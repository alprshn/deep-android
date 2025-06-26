package com.kami_apps.deepwork.deep_work_app.data.manager


import com.kami_apps.deepwork.deep_work_app.data.model.TimerState
import com.kami_apps.deepwork.deep_work_app.util.GlobalProperties.TIME_FORMAT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import com.kami_apps.deepwork.deep_work_app.util.helper.CountDownTimerHelper
import com.kami_apps.deepwork.deep_work_app.data.workManager.worker.TimerRunningWorker
import com.kami_apps.deepwork.deep_work_app.data.workManager.worker.TimerCompletedWorker
import com.kami_apps.deepwork.deep_work_app.data.workManager.worker.TIMER_RUNNING_TAG
import com.kami_apps.deepwork.deep_work_app.data.workManager.worker.TIMER_COMPLETED_TAG


@Singleton
class TimerManager @Inject constructor(
    private val workRequestManager: WorkRequestManager,
) {

    private val timeInMillisFlow = MutableStateFlow(0L)
    private val timeTextFlow = MutableStateFlow("00:00:00")
    private val hourFlow = MutableStateFlow(0)
    private val minuteFlow = MutableStateFlow(0)
    private val secondFlow = MutableStateFlow(0)
    private val progressFlow = MutableStateFlow(1f) // Başlangıçta tam dolu
    private val isPlayingFlow = MutableStateFlow(false)
    private val isDoneFlow = MutableStateFlow(true)

    val timerState : Flow<TimerState> = combine(
        timeInMillisFlow,
        timeTextFlow,
        hourFlow,
        minuteFlow,
        secondFlow,
        progressFlow,
        isPlayingFlow,
        isDoneFlow,
    ) { args -> // Burayı değiştirdik: artık doğrudan destructuring yok, 'args' dizisi var
        // Değerlere index üzerinden erişin ve doğru tipe dönüştürün
        TimerState(
            timeInMillis = args[0] as Long,
            hour = args[2] as Int,
            minute = args[3] as Int,
            second = args[4] as Int,
            timeText = args[1] as String,
            progress = args[5] as Float,
            isPlaying = args[6] as Boolean,
            isDone = args[7] as Boolean,
        )
    }

    private var countDownTimerHelper: CountDownTimerHelper? = null

    fun setTHour(hour: Int) {
        hourFlow.value = hour
    }

    fun setMinute(minute: Int) {
        minuteFlow.value = minute
    }

    fun setSecond(second: Int) {
        secondFlow.value = second
    }

    fun setCountDownTimer() {
        val totalTimeInMillis = (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds
        
        if (totalTimeInMillis <= 0) {
            android.util.Log.e("TimerManager", "Cannot create timer with 0 or negative time: $totalTimeInMillis ms")
            return
        }
        
        timeInMillisFlow.value = totalTimeInMillis
        android.util.Log.d("TimerManager", "Setting countdown timer for ${totalTimeInMillis}ms (${hourFlow.value}h ${minuteFlow.value}m ${secondFlow.value}s)")
        
        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
            override fun onTimerTick(millisUntilFinished: Long) {
                val progressValue = if (timeInMillisFlow.value > 0) {
                    (millisUntilFinished.toFloat() / timeInMillisFlow.value).coerceIn(0f, 1f)
                } else {
                    0f
                }
                android.util.Log.d("TimerManager", "Timer tick: ${millisUntilFinished}ms remaining, progress: $progressValue")
                handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)
            }
            override fun onTimerFinish() {
                android.util.Log.d("TimerManager", "Timer finished!")
                handleTimerValues(false, "00:00:00", 0f)
                // Timer bittiğinde completed worker'ı başlat
                android.util.Log.d("TimerManager", "Enqueueing TimerCompletedWorker")
                // workRequestManager.enqueueWorker<TimerCompletedWorker>(TIMER_COMPLETED_TAG) // Geçici olarak devre dışı
                reset()
            }
        }
    }

    fun pause() {
        countDownTimerHelper?.pause()
        isPlayingFlow.value = false
    }

    fun reset() {
        countDownTimerHelper?.restart()
        // Reset edildiğinde orijinal süreyi göster
        val originalTimeText = timeInMillisFlow.value.formatTime()
        handleTimerValues(false, originalTimeText, 1f) // progress = 1 (tam dolu)
        isDoneFlow.value = true
        // workRequestManager.cancelWorker(TIMER_RUNNING_TAG) // Geçici olarak devre dışı
        android.util.Log.d("TimerManager", "Timer reset to: $originalTimeText (worker disabled)")
    }

    fun start() {
        android.util.Log.d("TimerManager", "Starting timer...")
        
        if (countDownTimerHelper == null) {
            android.util.Log.e("TimerManager", "CountDownTimerHelper is null! Call setCountDownTimer() first")
            return
        }
        
        if (timeInMillisFlow.value <= 0) {
            android.util.Log.e("TimerManager", "Cannot start timer with time: ${timeInMillisFlow.value}ms")
            return
        }
        
        countDownTimerHelper?.start()
        isPlayingFlow.value = true
        
        if (isDoneFlow.value) {
            // Timer başladığında running worker'ı başlat (tamamlama worker'ı değil)
            android.util.Log.d("TimerManager", "Enqueueing TimerRunningWorker")
            // workRequestManager.enqueueWorker<TimerRunningWorker>(TIMER_RUNNING_TAG) // Geçici olarak devre dışı
            isDoneFlow.value = false
        }
        
        android.util.Log.d("TimerManager", "Timer started successfully")
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
    ) {
        isPlayingFlow.value = isPlaying
        timeTextFlow.value = text
        progressFlow.value = progress
        
        // timeText'ten minute ve second değerlerini çıkart ve flow'ları güncelle
        val timeParts = text.split(":")
        if (timeParts.size >= 3) {
            hourFlow.value = timeParts[0].toIntOrNull() ?: 0
            minuteFlow.value = timeParts[1].toIntOrNull() ?: 0
            secondFlow.value = timeParts[2].toIntOrNull() ?: 0
        }
    }

    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
    )
}