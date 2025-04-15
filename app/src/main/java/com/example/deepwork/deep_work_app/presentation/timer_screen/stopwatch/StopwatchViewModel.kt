package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val stopwatchManager: StopwatchManager,
) {
}