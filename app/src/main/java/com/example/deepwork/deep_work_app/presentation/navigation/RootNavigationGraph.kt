package com.example.deepwork.deep_work_app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab
import com.example.deepwork.deep_work_app.presentation.settings_screen.SettingsScreen
import com.example.deepwork.deep_work_app.presentation.statistics_screen.StatisticsScreen
import com.example.deepwork.deep_work_app.presentation.timeline_screen.TimelineScreen
import com.example.deepwork.deep_work_app.presentation.timer_screen.TimerScreen

@Composable
fun RootNavigationGraph(navController : NavHostController){
    NavHost(
        navController = navController,
        startDestination = BottomBarTab.Statistics.title
    ){
        composable(BottomBarTab.Statistics.title){StatisticsScreen()}
        composable(BottomBarTab.Timer.title){TimerScreen()}
        composable(BottomBarTab.Timeline.title){TimelineScreen()}
        composable(BottomBarTab.Settings.title){SettingsScreen()}
   }
}