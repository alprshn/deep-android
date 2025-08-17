package com.kami_apps.deepwork.deep.presentation.timeline_screen

import com.kami_apps.deepwork.deep.presentation.timeline_screen.components.Event
import java.time.LocalDate

data class TimelineUiState(
    val events: List<Event> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val isLoading: Boolean = false,
    val error: String? = null
) 