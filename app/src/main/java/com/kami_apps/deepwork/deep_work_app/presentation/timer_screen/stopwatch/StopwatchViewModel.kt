package com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.UiState
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.data.manager.StopwatchManager
import com.kami_apps.deepwork.deep_work_app.data.service.AppBlockingService
import com.kami_apps.deepwork.deep_work_app.domain.usecases.AddTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.StartFocusSessionUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.StopFocusSessionUseCase
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.TimerUiState
import com.kami_apps.deepwork.deep_work_app.util.PermissionHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getAllTagsUseCase: GetAllTagsUseCase,
    @ApplicationContext private val context: Context
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
            val currentState = _timerUIState.value
            
            if (currentState.stopWatchIsStarted && currentState.startTime != null) {
                // This is a RESUME action (after lap/pause)
                Log.d("Stopwatch", "Resuming paused stopwatch")
                _timerUIState.value = currentState.copy(stopWatchIsStarted = true)
                stopwatchManager.start()
                
                // Resume app blocking if permissions are granted
                if (PermissionHelper.hasAllPermissions(context)) {
                    startAppBlocking()
                }
            } else {
                // This is a NEW START action
                val now = Date()
                Log.e("Date Start", now.toString())
                _timerUIState.value = _timerUIState.value.copy(
                    startTime = now,
                    stopWatchIsStarted = true
                )
                stopwatchManager.start()
                
                // Start app blocking if permissions are granted
                if (PermissionHelper.hasAllPermissions(context)) {
                    startAppBlocking()
                }
            }
        }
    }

    override fun stop() {
        val finish = Date()
        val currentState = _timerUIState.value
        val updatedState = currentState.copy(
            finishTime = finish,
            stopWatchIsStarted = false
        )
        _timerUIState.value = updatedState
        
        Log.e("Date Start", updatedState.startTime!!.time.toString())
        Log.e("Date Finish", finish.time.toString())
        val tagId = updatedState.tagId
        val startTime = updatedState.startTime
        val finishTime = updatedState.finishTime
        val duration = stopwatchState.value!!.minute + ": " + stopwatchState.value!!.second
        val selectedTagEmoji = updatedState.selectedTagEmoji
        
        viewModelScope.launch(Dispatchers.IO) {
            stopwatchManager.stop()
            
            // Stop app blocking
            stopAppBlocking()
            
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
        _timerUIState.value = _timerUIState.value.copy(stopWatchIsStarted = false)
        
        // Stop app blocking when pausing
        stopAppBlocking()
    }

    override fun clear() {
        stopwatchManager.clear()
    }

    override fun reset() {
        stopwatchManager.reset()
        _timerUIState.value = _timerUIState.value.copy(
            stopWatchIsStarted = false,
            startTime = null,
            finishTime = null
        )
        
        // Stop app blocking when resetting
        stopAppBlocking()
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
    
    // App Blocking Helper Methods
    private fun startAppBlocking() {
        try {
            val intent = Intent(context, AppBlockingService::class.java).apply {
                action = AppBlockingService.ACTION_START_BLOCKING
            }
            context.startForegroundService(intent)
            Log.d("StopwatchViewModel", "App blocking service started")
        } catch (e: Exception) {
            Log.e("StopwatchViewModel", "Failed to start app blocking service", e)
        }
    }
    
    private fun stopAppBlocking() {
        try {
            val intent = Intent(context, AppBlockingService::class.java).apply {
                action = AppBlockingService.ACTION_STOP_BLOCKING
            }
            context.startService(intent)
            Log.d("StopwatchViewModel", "App blocking service stopped")
        } catch (e: Exception) {
            Log.e("StopwatchViewModel", "Failed to stop app blocking service", e)
        }
    }
}