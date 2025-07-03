package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTimelineEventsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
import javax.inject.Inject
import kotlin.math.roundToInt
import android.util.Log

@HiltViewModel
class TimelineViewModel @Inject constructor(
    private val getTimelineEventsUseCase: GetTimelineEventsUseCase,
    private val sessionsRepository: SessionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimelineUiState())
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        loadAllSessions() // Debug - load all sessions first
        loadEventsForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        loadEventsForDate(date)
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