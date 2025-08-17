package com.kami_apps.deepwork.deep.presentation.timer_screen

import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
data class TimerUiState(
    // Premium status
    val isPremium: Boolean = false,
    // Genel state'ler
    var isTimerMode: Boolean = false, // true: Timer, false: Stopwatch
    var isStarted: Boolean = false,
    var startTime: Date? = null,
    var finishTime: Date? = null,
    var tagId: Int = 0,
    var selectedTagEmoji: String = "\uD83D\uDCCD",
    
    // Stopwatch için state'ler
    var stopWatchIsStarted: Boolean = false,
    var initialValueSecond: String = "",
    var initialValueMinutes: String = "",
    
    // Timer için state'ler
    var timerMinutes: Int = 25, // Varsayılan 25 dakika
    var timerSeconds: Int = 0,
    var timerIsRunning: Boolean = false,
    var timerProgress: Float = 0f,
    
    // Ortak state'ler
    val maxValue: Int = 60,
    val minValue: Int = 0,
    val visibleTagItems: Boolean = true
)