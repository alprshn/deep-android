package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData
import com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData
import com.kami_apps.deepwork.deep_work_app.domain.usecases.MonthlyFocusData
import com.kami_apps.deepwork.deep_work_app.domain.usecases.YearlyFocusData
import com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.TopTag
import java.time.LocalDate

data class StatisticsUiState(
    val isPremium: Boolean = false,
    val allTags: List<Tags> = emptyList(),
    val topTags: List<TopTag> = emptyList(),
    val selectedTagId: Int = 0, // 0 = All Tags
    val selectedTimeIndex: Int = 0, // 0 = Day, 1 = Week, 2 = Month, 3 = Year
    val selectedDate: LocalDate = LocalDate.now(),
    val totalSessionCount: Int = 0,
    val totalFocusTimeOverall: String = "0h 0m", // Genel odaklanma süresi
    val totalFocusTime: String = "0h 0m", // Seçili taga özel odaklanma süresi
    val averageFocusTime: String = "0h 0m", // Genel ortalama süre
    // Chart data for different time periods
    val hourlyFocusData: List<HourlyFocusData> = emptyList(), // For Day view and line charts
    val dailyFocusData: List<DailyFocusData> = emptyList(), // For Week view
    val monthlyFocusData: List<MonthlyFocusData> = emptyList(), // For Month view
    val yearlyFocusData: List<YearlyFocusData> = emptyList(), // For Year view
    val weekdayFocusData: List<WeekdayFocusData> = emptyList(), // For Month and Year weekday analysis
    val peakHour: String = "12:00", // Most focused hour of the day
    val peakWeekday: String = "Monday", // Most focused day of the week
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)