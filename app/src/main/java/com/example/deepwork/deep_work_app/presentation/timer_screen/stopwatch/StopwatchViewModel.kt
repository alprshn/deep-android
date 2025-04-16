package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val stopwatchManager: StopwatchManager,
): ViewModel(), StopwatchActions {

    val stopwatchState = stopwatchManager.stopwatchState.asLiveData()

    val lapTimes = stopwatchManager.lapTimes

    override fun start() {
        stopwatchManager.start()

    }

    override fun stop() {
        stopwatchManager.stop()
    }

    override fun lap() {
        stopwatchManager.lap()
    }

    override fun clear() {
        stopwatchManager.clear()
    }

    override fun reset() {
        stopwatchManager.reset()
    }
}