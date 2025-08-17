package com.kamiapps.deep.deep.presentation.timer_screen.stopwatch

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kamiapps.deep.deep.data.UiState
import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.data.manager.StopwatchManager
import com.kamiapps.deep.deep.data.service.AppBlockingService
import com.kamiapps.deep.deep.domain.usecases.AddTagUseCase
import com.kamiapps.deep.deep.domain.usecases.EditTagUseCase
import com.kamiapps.deep.deep.domain.usecases.DeleteTagUseCase
import com.kamiapps.deep.deep.domain.usecases.GetAllTagsUseCase
import com.kamiapps.deep.deep.domain.usecases.StartFocusSessionUseCase
import com.kamiapps.deep.deep.domain.usecases.StopFocusSessionUseCase
import com.kamiapps.deep.deep.domain.usecases.GetUserPreferencesUseCase
import com.kamiapps.deep.deep.presentation.timer_screen.TimerUiState
import com.kamiapps.deep.deep.util.PermissionHelper
import com.kamiapps.deep.deep.util.helper.HapticFeedbackHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import com.kamiapps.deep.deep.data.manager.PremiumManager
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
    private val editTag: EditTagUseCase,
    private val deleteTag: DeleteTagUseCase,
    private val startFocusSession: StartFocusSessionUseCase,
    private val stopFocusSession: StopFocusSessionUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase,
    @ApplicationContext private val context: Context,
    private val premiumManager: PremiumManager,
    private val hapticFeedbackHelper: HapticFeedbackHelper,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase
) : ViewModel(), StopwatchActions {

    private val _uiState =
        MutableStateFlow<UiState>(UiState.Success(stopwatchManager.stopwatchState))
    val uiState: StateFlow<UiState> = _uiState

    private val _allTagList = MutableStateFlow(emptyList<Tags>())
    val allTagList = _allTagList.asStateFlow()

    private val _timerUIState = MutableStateFlow(TimerUiState())
    val timerUIState: StateFlow<TimerUiState> = _timerUIState.asStateFlow()

    val stopwatchState = stopwatchManager.stopwatchState.asLiveData()

    // Haptic feedback state
    private val _isHapticEnabled = MutableStateFlow(false)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()

    init {
        // Observe premium status
        viewModelScope.launch {
            premiumManager.isPremium.collectLatest { isPremium ->
                _timerUIState.value = _timerUIState.value.copy(isPremium = isPremium)
            }
        }

        // Load haptic feedback preference
        loadHapticPreference()
    }

    private fun loadHapticPreference() {
        viewModelScope.launch {
            try {
                val userPreferences = getUserPreferencesUseCase()
                _isHapticEnabled.value = userPreferences?.haptic ?: false
            } catch (e: Exception) {
                Log.e("StopwatchViewModel", "Failed to load haptic preference", e)
                _isHapticEnabled.value = false
            }
        }
    }

    private fun performHapticFeedback(feedbackType: StopwatchHapticFeedbackType) {
        if (_isHapticEnabled.value) {
            when (feedbackType) {
                StopwatchHapticFeedbackType.BUTTON_CLICK -> hapticFeedbackHelper.performButtonClick()
                StopwatchHapticFeedbackType.IMPORTANT_ACTION -> hapticFeedbackHelper.performImportantAction()
                StopwatchHapticFeedbackType.MODE_SELECTION -> hapticFeedbackHelper.performModeSelection()
            }
        }
    }

    private enum class StopwatchHapticFeedbackType {
        BUTTON_CLICK,
        IMPORTANT_ACTION,
        MODE_SELECTION
    }

    override fun start() {
        // Haptic feedback for start button
        performHapticFeedback(StopwatchHapticFeedbackType.BUTTON_CLICK)

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
        // Haptic feedback for stop button (important action)
        performHapticFeedback(StopwatchHapticFeedbackType.IMPORTANT_ACTION)

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
        // Haptic feedback for lap/pause button
        performHapticFeedback(StopwatchHapticFeedbackType.BUTTON_CLICK)

        stopwatchManager.stop()
        _timerUIState.value = _timerUIState.value.copy(stopWatchIsStarted = false)

        // Stop app blocking when pausing
        stopAppBlocking()
    }

    override fun clear() {
        stopwatchManager.clear()
    }

    override fun reset() {
        // Haptic feedback for reset button (important action)
        performHapticFeedback(StopwatchHapticFeedbackType.IMPORTANT_ACTION)

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

    fun editTag(tag: Tags) {
        viewModelScope.launch {
            editTag.invoke(tag)
            getAllTag() // Refresh the list
        }
    }

    fun deleteTag(tag: Tags) {
        viewModelScope.launch {
            deleteTag.invoke(tag)
            getAllTag() // Refresh the list
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