package com.kami_apps.deepwork.deep.presentation.statistics_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import android.os.Build
import androidx.annotation.RequiresApi

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.deep.data.util.parseTagColor
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.DateSelector
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.ModernSegmentedControl
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.SegmentedControlColors
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.SummaryCardsSection
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.FocusStatistics
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.HourlyFocusChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.DailyFocusChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.WeeklyPeakChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.MonthlyFocusChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.YearlyFocusChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.WeekdayAnalysisChart
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.TopTagsCard
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.SessionLogsCard
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.TopTag
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.SessionLog
import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.presentation.components.PremiumCard


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel = hiltViewModel(),
    onShowPaywall: (() -> Unit)? = null
) {
    val statisticsState by statisticsViewModel.uiState.collectAsStateWithLifecycle()
    val items = listOf("Day", "Week", "Month", "Year")
    var sampleStatistics by remember { mutableStateOf(FocusStatistics()) } // Örnek
    val interactionSource = remember { MutableInteractionSource() }
    // Load data when screen first loads
    LaunchedEffect(Unit) {
        statisticsViewModel.loadAllTags()
        statisticsViewModel.loadTopTags()
        statisticsViewModel.loadStatisticsForSelectedTag()
        statisticsViewModel.loadChartData()
        statisticsViewModel.loadSessionLogs() // Load session logs

    }



    val scrollState = rememberScrollState()



    LaunchedEffect(scrollState.value) {
        statisticsViewModel.updateBlurAlpha((scrollState.value / 300f).coerceIn(0f, 1f))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
                .padding(bottom = if (!statisticsState.isPremium) 80.dp else 0.dp), // Add bottom padding for PremiumCard
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Text(
                    text = "Statistics",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(bottom = 12.dp),
                ) {
                    // All Tags - Her zaman başta olacak
                    item {
                        val isSelected = statisticsState.selectedTagId == 0

                        Card(
                            modifier = Modifier
                                .wrapContentWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                            ),
                            onClick = {
                                statisticsViewModel.updateTagId(0)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "\uD83C\uDFF7\uFE0E",
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = "All Tags",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    // User created tags - with dummy data for testing
                    val dummyTags = listOf(
                        Tags(1, "Programming", "💻", "18402806360702976000"),  // Red
                        Tags(2, "Reading", "📚", "18402806360702976000"),     // Teal
                        Tags(3, "Writing", "✍️", "18402806360702976000"),     // Blue
                        Tags(4, "Research", "🔍", "18402806360702976000"),    // Green
                        Tags(5, "Learning", "🎓", "18402806360702976000"),     // Orange
                        Tags(6, "Design", "🎨", "18402806360702976000"),      // Pink
                        Tags(7, "Music", "🎵", "18402806360702976000"),       // Light Blue
                        Tags(8, "Exercise", "💪", "18402806360702976000"),   // Purple
                        Tags(9, "Meditation", "🧘", "18402806360702976000"),  // Cyan
                        Tags(10, "Cooking", "👨‍🍳", "18402806360702976000,,,")   // Yellow
                    )

                    val tagsToShow =
                        if (!statisticsState.isPremium) dummyTags else statisticsState.allTags // Show dummy tags for non-premium users

                    items(tagsToShow) { tags ->
                        val tagColor = parseTagColor(tags.tagColor)

                        val isSelected = statisticsState.selectedTagId == tags.tagId

                        Card(
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .wrapContentWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected)
                                    tagColor
                                else
                                    tagColor.copy(alpha = 0.2f)
                            ),
                            onClick = {
                                statisticsViewModel.updateTagId(tags.tagId)
                            }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = tags.tagEmoji,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = tags.tagName,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
                ModernSegmentedControl(
                    items = items,
                    selectedIndex = statisticsState.selectedTimeIndex,
                    onItemSelected = { timeIndex ->
                        statisticsViewModel.updateTimeIndex(timeIndex)
                    },
                    showSeparators = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    colors = SegmentedControlColors(
                        containerBackground = Color(0xff767680).copy(alpha = 0.2f),
                        selectedBackground = MaterialTheme.colorScheme.surfaceTint,
                        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                        separatorColor = Color.Gray.copy(alpha = 0.2f)
                    )
                )
                // Date selector - Updates all charts when date is changed
                DateSelector(
                    selectedTimeIndex = statisticsState.selectedTimeIndex,
                    dayDate = statisticsState.selectedDayDate,
                    weekDate = statisticsState.selectedWeekDate,
                    monthDate = statisticsState.selectedMonthDate,
                    yearDate = statisticsState.selectedYearDate,
                    onDateChanged = { selectedDate ->
                        when (statisticsState.selectedTimeIndex) {
                            0 -> statisticsViewModel.setDayDate(selectedDate)
                            1 -> statisticsViewModel.setWeekDate(selectedDate)
                            2 -> statisticsViewModel.setMonthDate(selectedDate)
                            3 -> statisticsViewModel.setYearDate(selectedDate)
                        }
                    },
                )
            }
            // Summary Cards Section - Use dummy data for non-premium users
            sampleStatistics = if (!statisticsState.isPremium) {
                FocusStatistics(
                    totalFocusTime = "178h 45m",
                    totalSessions = 156,
                    averageDuration = "2h 18m"
                )
            } else {
                FocusStatistics(
                    totalFocusTime = statisticsState.totalFocusTime,
                    totalSessions = statisticsState.totalSessionCount,
                    averageDuration = statisticsState.averageFocusTime
                )
            }


            SummaryCardsSection(
                statistics = sampleStatistics,
            )

            // Charts based on selected time period
            when (statisticsState.selectedTimeIndex) {
                0 -> { // Day view
                    HourlyFocusChart(
                        hourlyFocusData = statisticsState.hourlyFocusData,
                        totalFocusTime = statisticsState.totalFocusTime
                    )
                }

                1 -> { // Week view
                    DailyFocusChart(
                        dailyFocusData = statisticsState.dailyFocusData,
                        totalFocusTime = statisticsState.totalFocusTime
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WeeklyPeakChart(
                        hourlyFocusData = statisticsState.hourlyFocusData,
                        peakHour = statisticsState.peakHour
                    )
                }

                2 -> { // Month view
                    MonthlyFocusChart(
                        monthlyFocusData = statisticsState.monthlyFocusData,
                        totalFocusTime = statisticsState.totalFocusTime
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WeeklyPeakChart(
                        hourlyFocusData = statisticsState.hourlyFocusData,
                        peakHour = statisticsState.peakHour
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WeekdayAnalysisChart(
                        weekdayFocusData = statisticsState.weekdayFocusData,
                        peakWeekday = statisticsState.peakWeekday
                    )
                }

                3 -> { // Year view
                    YearlyFocusChart(
                        yearlyFocusData = statisticsState.yearlyFocusData,
                        totalFocusTime = statisticsState.totalFocusTime
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WeeklyPeakChart(
                        hourlyFocusData = statisticsState.hourlyFocusData,
                        peakHour = statisticsState.peakHour
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    WeekdayAnalysisChart(
                        weekdayFocusData = statisticsState.weekdayFocusData,
                        peakWeekday = statisticsState.peakWeekday
                    )
                }
            }

            // Top Tags with dummy data for non-premium users
            TopTagsCard(
                topTags = if (!statisticsState.isPremium) {
                    listOf(
                        TopTag("Programming", "💻", 89),
                        TopTag("Reading", "📚", 67),
                        TopTag("Writing", "✍️", 45),
                        TopTag("Research", "🔍", 34),
                        TopTag("Learning", "🎓", 28),
                        TopTag("Design", "🎨", 23),
                        TopTag("Music", "🎵", 18),
                        TopTag("Exercise", "💪", 15)
                    )
                } else {
                    statisticsState.topTags
                }
            )

            // Session Logs with premium control - use real data for premium users
            SessionLogsCard(
                sessionLogs = if (!statisticsState.isPremium) {
                    // Dummy data for non-premium users
                    listOf(
                        SessionLog("Programming", "💻", "19.07.2025", "09:15", "3h 45m"),
                        SessionLog("Reading", "📚", "19.07.2025", "14:30", "1h 20m"),
                        SessionLog("Writing", "✍️", "18.07.2025", "11:00", "2h 15m"),
                        SessionLog("Research", "🔍", "18.07.2025", "16:45", "1h 55m"),
                        SessionLog("Programming", "💻", "17.07.2025", "08:30", "4h 30m"),
                        SessionLog("Design", "🎨", "17.07.2025", "13:20", "2h 40m"),
                        SessionLog("Learning", "🎓", "16.07.2025", "10:15", "3h 10m"),
                        SessionLog("Music Practice", "🎵", "16.07.2025", "19:00", "1h 30m"),
                        SessionLog("Exercise", "💪", "15.07.2025", "07:00", "1h 15m"),
                        SessionLog("Programming", "💻", "15.07.2025", "15:30", "2h 50m"),
                        SessionLog("Reading", "📚", "14.07.2025", "12:00", "1h 40m"),
                        SessionLog("Writing", "✍️", "14.07.2025", "17:15", "2h 25m"),
                        SessionLog("Research", "🔍", "13.07.2025", "09:45", "3h 05m"),
                        SessionLog("Programming", "💻", "13.07.2025", "14:10", "4h 20m"),
                        SessionLog("Design", "🎨", "12.07.2025", "11:30", "2h 10m")
                    )
                } else {
                    // Real data for premium users
                    statisticsState.sessionLogs
                }
            )
        }

        // Premium Card Overlay - show only if not premium
        if (!statisticsState.isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                PremiumCard(
                    onTryFreeClick = {
                        onShowPaywall?.invoke()
                    }
                )
            }
        }
    }
}


@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // All Tags - Selected/Active state
        Card(
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF8B5A3C) // Kahverengi/turuncu ton
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color.White, CircleShape)
                )
                Text(
                    text = "All Tags",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Art Tag
        Card(
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A) // Koyu gri
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "🎨",
                    fontSize = 16.sp
                )
                Text(
                    text = "Art",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Coding Tag
        Card(
            modifier = Modifier.wrapContentWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A) // Koyu gri
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "💻",
                    fontSize = 16.sp
                )
                Text(
                    text = "Coding",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

}


