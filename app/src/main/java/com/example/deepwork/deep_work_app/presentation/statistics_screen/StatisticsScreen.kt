package com.example.deepwork.deep_work_app.presentation.statistics_screen

import android.view.View
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import com.example.deepwork.deep_work_app.presentation.components.NumericTextTransition
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.PixelCopy
import android.view.Window
import androidx.annotation.RequiresApi
import kotlin.math.roundToInt

import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.AppBlocking
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.deepwork.deep_work_app.data.util.parseTagColor
import com.example.deepwork.deep_work_app.presentation.statistics_screen.components.DateSelector
import com.example.deepwork.deep_work_app.presentation.statistics_screen.components.ModernSegmentedControl
import com.example.deepwork.deep_work_app.presentation.statistics_screen.components.SegmentedControlColors
import com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun StatisticsScreen(
    statisticsViewModel: StatisticsViewModel = hiltViewModel(),
) {
    val tagContent by statisticsViewModel.allTagList.collectAsStateWithLifecycle()
    var selectedTagId by remember { mutableStateOf<Int>(0) } // 0 = All Tags seçili
    val items = listOf("Day", "Week", "Month", "Year")
    var selectedSegmentIndex by remember { mutableIntStateOf(0) }
    // Tagları yükle
    LaunchedEffect(Unit) {
        statisticsViewModel.getAllTag()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
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
                horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(bottom = 12.dp),
            ) {
                // All Tags - Her zaman başta olacak
                item {
                    val isSelected = selectedTagId == 0

                    Card(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable { selectedTagId = 0 },
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
                items(tagContent) { tags ->
                    val tagColor = parseTagColor(tags.tagColor)
                    val isSelected = selectedTagId == tags.tagId.toInt()

                    Card(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable { selectedTagId = tags.tagId.toInt() },
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
        }

        ModernSegmentedControl(
            items = items,
            selectedIndex = selectedSegmentIndex,
            onItemSelected = { selectedSegmentIndex = it },
            showSeparators = true,
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            colors = SegmentedControlColors(
                containerBackground = Color(0xFF1C1C1E),
                selectedBackground = Color.Gray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White,
                separatorColor = Color.Gray.copy(alpha = 0.2f)
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        DateSelector(
            selectedTimeIndex = selectedSegmentIndex,
            onDateChanged = { selectedDate ->
                // Burada seçilen tarihe göre veri filtreleme yapılabilir
                // Örneğin: statisticsViewModel.filterByDate(selectedDate, selectedTagId)
            },
            modifier = Modifier.padding(bottom = 16.dp)
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


