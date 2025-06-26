package com.kami_apps.deepwork.deep_work_app.util.helper

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class CountDownTimerHelper(
    private val millisInFuture: Long,
    private val countDownInterval: Long,
) {

    private var job: Job? = null
    private var remainingTime: Long = 0
    private var isTimerPaused: Boolean = true

    init {
        remainingTime = millisInFuture
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        if (isTimerPaused) {
            job = GlobalScope.launch {
                // İlk tick'i hemen çağır (başlangıç durumu)
                onTimerTick(remainingTime)
                
                while (remainingTime > 0) {
                    delay(countDownInterval)
                    remainingTime -= countDownInterval
                    
                    if (remainingTime <= 0) {
                        onTimerFinish()
                        break
                    } else {
                        onTimerTick(remainingTime)
                    }
                }
                restart()
            }
            isTimerPaused = false
        }
    }

    fun pause() {
        job?.cancel()
        isTimerPaused = true
    }

    fun restart() {
        job?.cancel()
        remainingTime = millisInFuture
        isTimerPaused = true
    }

    abstract fun onTimerTick(millisUntilFinished: Long)
    abstract fun onTimerFinish()
}