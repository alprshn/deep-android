package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags

data class StatisticsUiState(
    val allTags: List<Tags> = emptyList(),
    val selectedTagId: Int = 0, // 0 = All Tags
    val totalSessionCount: Int = 0,
    val totalFocusTimeOverall: String = "0h 0m", // Genel odaklanma süresi
    val totalFocusTime: String = "0h 0m", // Seçili taga özel odaklanma süresi
    val averageFocusTime: String = "0h 0m", // Genel ortalama süre
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)