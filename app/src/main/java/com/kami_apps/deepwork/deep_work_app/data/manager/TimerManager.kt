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
    private val progressFlow = MutableStateFlow(0f)
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
        timeInMillisFlow.value =
            (hourFlow.value.hours + minuteFlow.value.minutes + secondFlow.value.seconds).inWholeMilliseconds
        countDownTimerHelper = object : CountDownTimerHelper(timeInMillisFlow.value, 1000) {
            override fun onTimerTick(millisUntilFinished: Long) {
                val progressValue = millisUntilFinished.toFloat() / timeInMillisFlow.value
                handleTimerValues(true, millisUntilFinished.formatTime(), progressValue)
            }
            override fun onTimerFinish() {
                workRequestManager.enqueueWorker<TimerCompletedWorker>(TIMER_COMPLETED_TAG)
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
        handleTimerValues(false, timeInMillisFlow.value.formatTime(), 0f)
        isDoneFlow.value = true
        workRequestManager.cancelWorker(TIMER_RUNNING_TAG)
    }

    fun start() {
        countDownTimerHelper?.start()
        isPlayingFlow.value = true
        if (isDoneFlow.value) {
            progressFlow.value = 1f
            workRequestManager.enqueueWorker<TimerRunningWorker>(TIMER_RUNNING_TAG)
            isDoneFlow.value = false
        }
    }

    private fun handleTimerValues(
        isPlaying: Boolean,
        text: String,
        progress: Float,
    ) {
        isPlayingFlow.value = isPlaying
        timeTextFlow.value = text
        progressFlow.value = progress
    }

    fun Long.formatTime(): String = String.format(
        TIME_FORMAT,
        TimeUnit.MILLISECONDS.toHours(this),
        TimeUnit.MILLISECONDS.toMinutes(this) % 60,
        TimeUnit.MILLISECONDS.toSeconds(this) % 60,
    )
}