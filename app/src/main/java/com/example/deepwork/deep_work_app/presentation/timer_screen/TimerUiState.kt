package com.example.deepwork.deep_work_app.presentation.timer_screen

import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
data class TimerUiState(
    var stopWatchIsStarted: Boolean = false,
    var initialValueSecond: String = "",
    var initialValueMinutes: String = "",
    val maxValue: Int = 60,
    val minValue: Int = 0,
    val visibleTagItems: Boolean = true,
    var startTime: Date? = null,
    var finishTime: Date? = null,
    var tagId: Int = 0,
    var selectedTagEmoji: String = "\uD83D\uDCCD"
)