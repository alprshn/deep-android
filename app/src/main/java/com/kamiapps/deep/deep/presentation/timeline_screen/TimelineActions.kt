package com.kamiapps.deep.deep.presentation.timeline_screen

import java.time.LocalDate

sealed class TimelineActions {
    data class OnDateSelected(val date: LocalDate) : TimelineActions()
    data object RefreshEvents : TimelineActions()
} 