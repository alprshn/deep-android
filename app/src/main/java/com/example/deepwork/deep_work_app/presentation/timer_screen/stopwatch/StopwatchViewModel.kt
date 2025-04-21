package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.deepwork.deep_work_app.data.UiState
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import com.example.deepwork.deep_work_app.domain.usecases.AddTagUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StartFocusSessionUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StopFocusSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val stopwatchManager: StopwatchManager,
    private val addTag: AddTagUseCase,
    private val startFocusSession: StartFocusSessionUseCase,
    private val stopFocusSession: StopFocusSessionUseCase
): ViewModel(), StopwatchActions {

    private val _uiState = MutableStateFlow<UiState>(UiState.Success(stopwatchManager.stopwatchState))
    val uiState : StateFlow<UiState> = _uiState

    val stopwatchState = stopwatchManager.stopwatchState.asLiveData()

    val lapTimes = stopwatchManager.lapTimes

    override fun start(session: Sessions) {
        viewModelScope.launch {
            startFocusSession.invoke(session)
            stopwatchManager.start()

        }

    }

    override fun stop(tags: Tags) {
        viewModelScope.launch {
            stopwatchManager.stop()
            addTag.invoke(tags)
            //stopFocusSession.invoke(sessions = )
            stopwatchManager.reset()
        }
    }

    override fun lap() {
        stopwatchManager.stop()
    }

    override fun clear() {
        stopwatchManager.clear()
    }

    override fun reset() {
        stopwatchManager.reset()
    }
}