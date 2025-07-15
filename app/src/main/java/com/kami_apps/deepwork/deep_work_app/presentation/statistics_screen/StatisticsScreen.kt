package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen

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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
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
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.DateSelector
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.ModernSegmentedControl
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.SegmentedControlColors
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.SummaryCardsSection
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.FocusStatistics
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.JetpackComposeElectricCarSales
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.JetpackComposeRockMetalRatios
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.TopTagsCard
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.SessionLogsCard
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.TopTag
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.SessionLog


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel = hiltViewModel(),
) {
    val statisticsState by statisticsViewModel.uiState.collectAsStateWithLifecycle()
    val items = listOf("Day", "Week", "Month", "Year")
    var selectedSegmentIndex by remember { mutableIntStateOf(0) }
    var sampleStatistics by remember { mutableStateOf(FocusStatistics()) } // Örnek


    // Tagları yükle
    LaunchedEffect(Unit) {
        statisticsViewModel.loadAllTags()
        statisticsViewModel.loadTopTags()
        statisticsViewModel.loadStatisticsForSelectedTag()
    }


    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Statistics",
                color = Color.White,
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
                            .wrapContentWidth()
                            .clickable {
                                statisticsViewModel.updateTagId(0)
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                Color.Gray
                            else
                                Color.Gray.copy(alpha = 0.2f)
                        ),
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "\uD83C\uDFF7\uFE0E",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "All Tags",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                // Kullanıcı tarafından oluşturulan taglar
                items(statisticsState.allTags) { tags ->
                    val tagColor = parseTagColor(tags.tagColor)

                    val isSelected = statisticsState.selectedTagId == tags.tagId

                    Card(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable {
                                statisticsViewModel.updateTagId(tags.tagId)
                            },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                tagColor
                            else
                                tagColor.copy(alpha = 0.2f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = tags.tagEmoji,
                                fontSize = 20.sp
                            )
                            Text(
                                text = tags.tagName,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }
            ModernSegmentedControl(
                items = items,
                selectedIndex = selectedSegmentIndex,
                onItemSelected = { selectedSegmentIndex = it },
                showSeparators = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = SegmentedControlColors(
                    containerBackground = Color(0xFF1C1C1E),
                    selectedBackground = Color.Gray,
                    selectedTextColor = Color.White,
                    unselectedTextColor = Color.White,
                    separatorColor = Color.Gray.copy(alpha = 0.2f)
                )
            )
            DateSelector(
                selectedTimeIndex = selectedSegmentIndex,
                onDateChanged = { selectedDate ->
                    // Burada seçilen tarihe göre veri filtreleme yapılabilir
                    // Örneğin: statisticsViewModel.filterByDate(selectedDate, selectedTagId)
                },
            )
        }
// Summary Cards Section

        sampleStatistics = FocusStatistics(
            totalFocusTime = statisticsState.totalFocusTime,
            totalSessions = statisticsState.totalSessionCount,
            averageDuration = statisticsState.averageFocusTime
        )


        SummaryCardsSection(
            statistics = sampleStatistics,
        )

        JetpackComposeElectricCarSales()

        JetpackComposeRockMetalRatios()





        TopTagsCard(
            topTags = statisticsState.topTags
        )

        // Sample data for Session Logs
        val sampleSessionLogs = listOf(
            SessionLog("Coding", "💻", "24.05.2025", "11:10", "1h 54m"),
            SessionLog("Coding", "💻", "20.05.2025", "17:35", "0m"),
            SessionLog("Coding", "💻", "19.05.2025", "10:45", "1h 24m"),
            SessionLog("Physics", "⚛️", "14.05.2025", "23:16", "0m"),
            SessionLog("Coding", "💻", "11.05.2025", "13:25", "5h 9m"),
            SessionLog("Coding", "💻", "10.05.2025", "15:42", "1h 56m"),
            SessionLog("Coding", "💻", "8.05.2025", "09:23", "8h 14m"),
            SessionLog("Coding", "💻", "6.05.2025", "10:41", "4h 42m"),
            SessionLog("Coding", "💻", "5.05.2025", "19:01", "0m")
        )

        SessionLogsCard(
            sessionLogs = sampleSessionLogs
        )
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


