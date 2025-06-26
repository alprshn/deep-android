package com.kami_apps.deepwork.deep_work_app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab
import com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.SettingsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components.SelectBlockAppsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.StatisticsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.TimelineScreen
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.TimerScreen

@Composable
fun RootNavigationGraph(navController: NavHostController, innerPadding: PaddingValues){
    NavHost(
        navController = navController,
        startDestination = BottomBarTab.Statistics.title,
        modifier = Modifier.padding(innerPadding)
    ){
        composable(BottomBarTab.Statistics.title){StatisticsScreen()}
        composable(BottomBarTab.Timer.title){TimerScreen()}
        composable(BottomBarTab.Timeline.title){TimelineScreen()}
        composable(BottomBarTab.Settings.title){SettingsScreen(navController)}
        composable("SelectBlockApps") { SelectBlockAppsScreen(navController) }
   }
}