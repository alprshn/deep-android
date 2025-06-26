package com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ViewTimeline
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarTab(val title: String, val icon: ImageVector, val color: Color) {
    data object Statistics : BottomBarTab(
        title = "Statistics",
        icon = Icons.Rounded.BarChart,
        color = Color(0xFFFFA574)
    )
    data object Timer : BottomBarTab(
        title = "Timer",
        icon = Icons.Default.AvTimer,
        color = Color(0xFFFA6FFF)
    )
    data object Timeline : BottomBarTab(
        title = "Timeline",
        icon = Icons.Rounded.ViewTimeline,
        color = Color(0xFFADFF64)
    )
    data object Settings : BottomBarTab(
        title = "Settings",
        icon = Icons.Rounded.Settings,
        color = Color(0xFFADFF64)
    )
}

val tabs = listOf(
    BottomBarTab.Statistics,
    BottomBarTab.Timer,
    BottomBarTab.Timeline,
    BottomBarTab.Settings
)