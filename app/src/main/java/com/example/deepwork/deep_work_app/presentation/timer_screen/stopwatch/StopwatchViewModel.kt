package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.deepwork.deep_work_app.data.UiState
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import com.example.deepwork.deep_work_app.domain.usecases.AddTagUseCase
import com.example.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StartFocusSessionUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StopFocusSessionUseCase
import com.example.deepwork.deep_work_app.presentation.timer_screen.TimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class StopwatchViewModel @Inject constructor(
    private val stopwatchManager: StopwatchManager,
    private val addTag: AddTagUseCase,
    private val startFocusSession: StartFocusSessionUseCase,
    private val stopFocusSession: StopFocusSessionUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase
) : ViewModel(), StopwatchActions {

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Success(stopwatchManager.stopwatchState))
    val uiState: StateFlow<UiState> = _uiState

    private val _allTagList = MutableStateFlow(emptyList<Tags>())
    val allTagList = _allTagList.asStateFlow()

    private val _timerUIState = MutableStateFlow(TimerUiState())
    val timerUIState: StateFlow<TimerUiState> = _timerUIState.asStateFlow()


    val stopwatchState = stopwatchManager.stopwatchState.asLiveData()


    val lapTimes = stopwatchManager.lapTimes


    override fun start() {
        viewModelScope.launch {
            val now = Date()
            Log.e("Date Start", now.toString())
            _timerUIState.value.startTime = now
            stopwatchManager.start()
        }

    }

    override fun stop() {
        val finish = Date()
        _timerUIState.value.finishTime = finish
        Log.e("Date Start", _timerUIState.value.startTime!!.time.toString())
        Log.e("Date Finish", finish.time.toString())
        val tagId = _timerUIState.value.tagId
        val startTime = _timerUIState.value.startTime
        val finishTime = _timerUIState.value.finishTime
        val duration = stopwatchState.value!!.minute + ": " + stopwatchState.value!!.second
        val selectedTagEmoji = _timerUIState.value.selectedTagEmoji
        viewModelScope.launch((Dispatchers.IO)) {
            stopwatchManager.stop()
            startFocusSession.invoke(
                Sessions(
                    tagId = tagId,
                    startTime = startTime,
                    finishTime = finishTime,
                    duration = duration,
                    sessionEmoji = selectedTagEmoji
                )
            )
            Log.e("Duration", duration.toString())
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

    fun addTag(tagName: String, tagColor: String, tagEmoji: String) {
        viewModelScope.launch {
            addTag.invoke(
                Tags(
                    tagName = tagName,
                    tagColor = tagColor,
                    tagEmoji = tagEmoji
                )
            )
        }
    }

    fun getAllTag() {
        viewModelScope.launch {
            getAllTagsUseCase.invoke().collectLatest {
                _allTagList.tryEmit(it)
            }
        }
    }
}