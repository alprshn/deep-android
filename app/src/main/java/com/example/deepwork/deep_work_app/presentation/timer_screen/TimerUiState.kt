package com.example.deepwork.deep_work_app.presentation.timer_screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.graphics.Color
import com.example.deepwork.ui.theme.TagColors

@OptIn(ExperimentalMaterial3Api::class)
data class TimerUiState(
    var stopWatchIsStarted: Boolean = false,
    var initialValueSecond: String = "",
    var initialValueMinutes: String = "",
    val maxValue: Int = 60,
    val minValue: Int = 0,
    val endThisSessionVisibility: Boolean = true,
    val visibleTagItems: Boolean = true,
    val animatedColorBackgroundGradientValue: Float = 0.2f,
    var selectedEmoji: String = "😊",
)