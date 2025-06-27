package com.kami_apps.deepwork.deep_work_app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.OnboardingScreen
import com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.SettingsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components.SelectBlockAppsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.StatisticsScreen
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.TimelineScreen
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.TimerScreen

@Composable
fun RootNavigationGraph(navController: NavHostController, innerPadding: PaddingValues){
    val context = LocalContext.current
    var startDestination by remember { mutableStateOf("onboarding") }
    
    // Onboarding durumunu kontrol et
    LaunchedEffect(Unit) {
        val sharedPrefs = context.getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val isOnboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
        startDestination = if (isOnboardingCompleted) {
            BottomBarTab.Statistics.title
        } else {
            "onboarding"
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ){
        // Onboarding ekranı
        composable("onboarding") {
            OnboardingScreen(
                onNavigateToHome = {
                    navController.navigate(BottomBarTab.Statistics.title) {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }
        
        // Ana uygulama ekranları
        composable(BottomBarTab.Statistics.title){StatisticsScreen()}
        composable(BottomBarTab.Timer.title){TimerScreen()}
        composable(BottomBarTab.Timeline.title){TimelineScreen()}
        composable(BottomBarTab.Settings.title){SettingsScreen(navController)}
        composable("SelectBlockApps") { SelectBlockAppsScreen(navController) }
   }
}