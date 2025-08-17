package com.kami_apps.deepwork.deep.presentation.timeline_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep.domain.usecases.GetTimelineEventsUseCase
import com.kami_apps.deepwork.deep.domain.usecases.DeleteSessionUseCase
import com.kami_apps.deepwork.deep.domain.usecases.EditSessionUseCase
import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import com.kami_apps.deepwork.deep.presentation.timeline_screen.components.SessionDetails
import com.kami_apps.deepwork.deep.data.manager.PremiumManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val editSessionUseCase: EditSessionUseCase,
    private val sessionsRepository: SessionsRepository,
    private val premiumManager: PremiumManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    // Expose premium status
    val isPremium = premiumManager.isPremium

    init {
        loadAllSessions() // Debug - load all sessions first
        loadEventsForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadEventsForDate(date)
    }

    fun deleteSession(sessionId: Int) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Get the session to delete
                    val session = sessionsRepository.getSessionsById(sessionId)
                    session?.let {
                        deleteSessionUseCase(it)
                        Log.d("TimelineViewModel", "Session deleted successfully: $sessionId")
                    }
                }
                // Refresh events for current date on main thread
                loadEventsForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "Error deleting session", e)
                _uiState.update { 
                    it.copy(error = "Failed to delete session: ${e.message}")
                }
            }
        }
    }

    fun updateSession(sessionDetails: SessionDetails) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Get the original session
                    val originalSession = sessionsRepository.getSessionsById(sessionDetails.id)
                    originalSession?.let { session ->
                        // Create updated session
                        val updatedSession = session.copy(
                            startTime = Date.from(
                                sessionDetails.startTime
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toInstant()
                            ),
                            finishTime = Date.from(
                                sessionDetails.endTime
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toInstant()
                            ),
                            duration = formatDurationFromDateTime(sessionDetails.startTime, sessionDetails.endTime)
                        )
                        
                        editSessionUseCase(updatedSession)
                        Log.d("TimelineViewModel", "Session updated successfully: ${sessionDetails.id}")
                    }
                }
                // Refresh events for current date on main thread
                loadEventsForDate(_uiState.value.selectedDate)
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "Error updating session", e)
                _uiState.update { 
                    it.copy(error = "Failed to update session: ${e.message}")
                }
            }
        }
    }

    private fun loadEventsForDate(date: LocalDate) {
        Log.d("TimelineViewModel", "loadEventsForDate called for date: $date")
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val startDate = date.toDateAtStartOfDay()
                val endDate = date.toDateAtEndOfDay()
                
                Log.d("TimelineViewModel", "Querying events between $startDate and $endDate")
                
                getTimelineEventsUseCase(startDate, endDate)
                    .catch { exception ->
                        Log.e("TimelineViewModel", "Error getting events", exception)
                        _uiState.update { 
                            it.copy(
                                isLoading = false, 
                                error = "Failed to load events: ${exception.message}"
                            )
                        }
                    }
                    .collect { events ->
                        Log.d("TimelineViewModel", "Received ${events.size} events from UseCase")
                        val roundedEvents = events.map { event ->
                            event.copy(
                                start = event.start.roundToNearestMinute(),
                                end = event.end.roundToNearestMinute()
                            )
                        }
                        
                        Log.d("TimelineViewModel", "Updating UI with ${roundedEvents.size} rounded events")
                        _uiState.update { 
                            it.copy(
                                events = roundedEvents,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "Exception in loadEventsForDate", e)
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to load events: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshEvents() {
        loadEventsForDate(_uiState.value.selectedDate)
    }

    // Debug method to load all sessions
    fun loadAllSessions() {
        viewModelScope.launch {
            try {
                sessionsRepository.getAllSessions().collect { sessions ->
                    Log.d("TimelineViewModel", "All sessions count: ${sessions.size}")
                    sessions.forEach { session ->
                        Log.d("TimelineViewModel", "Session ${session.sessionId}: start=${session.startTime}, end=${session.finishTime}, duration=${session.duration}")
                    }
                }
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "Error loading all sessions", e)
            }
        }
    }

    private fun formatDurationFromDateTime(startTime: java.time.LocalDateTime, endTime: java.time.LocalDateTime): String {
        val duration = java.time.Duration.between(startTime, endTime)
        val totalMinutes = duration.toMinutes()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        
        return when {
            hours > 0L -> "${hours}:${minutes.toString().padStart(2, '0')}"
            minutes > 0L -> "0:${minutes.toString().padStart(2, '0')}"
            else -> "0:01" // Minimum 1 minute
        }
    }
}

// Extension functions for date conversion and rounding
private fun LocalDate.toDateAtStartOfDay(): Date {
    return Date.from(
        this.atStartOfDay()
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
    )
}

private fun LocalDate.toDateAtEndOfDay(): Date {
    return Date.from(
        this.atTime(LocalTime.MAX)
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
    )
}

private fun java.time.LocalDateTime.roundToNearestMinute(): java.time.LocalDateTime {
    val seconds = this.second
    val nanos = this.nano
    
    // Round to nearest minute: if seconds >= 30, round up, otherwise round down
    return if (seconds >= 30 || nanos >= 500_000_000) {
        this.withSecond(0).withNano(0).plusMinutes(1)
    } else {
        this.withSecond(0).withNano(0)
    }
} 