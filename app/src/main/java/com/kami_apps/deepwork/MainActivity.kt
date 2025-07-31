package com.kami_apps.deepwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.RootNavigationGraph
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTabs
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.tabs
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchViewModel
import com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen.PaywallScreen
import androidx.compose.foundation.layout.Box
import com.kami_apps.deepwork.ui.theme.DeepWorkTheme
import dagger.hilt.android.AndroidEntryPoint
import com.kami_apps.deepwork.deep_work_app.data.manager.PremiumManager
import com.kami_apps.deepwork.deep_work_app.data.manager.ThemeManager
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.kami_apps.deepwork.deep_work_app.presentation.components.PremiumCard
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTabs
import com.kami_apps.deepwork.deep_work_app.presentation.navigation.bottom_bar.tabs
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.StatisticsViewModel
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.timer.TimerViewModel
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import jakarta.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeManager: ThemeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Status-bar ikonlarını açık (beyaz) yap


        setContent {
            val navController: NavHostController = rememberNavController()
            var selectedIndex by remember { mutableIntStateOf(0) }
            var showPaywall by remember { mutableStateOf(false) }
            val stopWatchViewModel: StopwatchViewModel = hiltViewModel()
            val timerViewModel: TimerViewModel = hiltViewModel()

            val stopwatchState by stopWatchViewModel.stopwatchState.observeAsState()
            val timerState by timerViewModel.timerState.observeAsState()
            // Theme management
            val userTheme by themeManager.currentTheme.collectAsState()

            val timerUiState by stopWatchViewModel.timerUIState.collectAsState()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val hazeState = rememberHazeState(HazeDefaults.blurEnabled())


            val statisticsViewModel: StatisticsViewModel = hiltViewModel()
            val statisticsState by statisticsViewModel.uiState.collectAsStateWithLifecycle()

            val isTimerOrStopwatchRunning =
                stopwatchState?.isPlaying == true || timerState?.isPlaying == true



            DeepWorkTheme(
                userTheme = userTheme,
                dynamicColor = false  // Disable dynamic color to use our custom colors
            ) {

                val style =
                    HazeMaterials.ultraThin(containerColor = MaterialTheme.colorScheme.background)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Onboarding ekranında bottom bar'ı gösterme
                        if (currentRoute != "onboarding") {
                            Box(
                                modifier = Modifier .hazeEffect(state = hazeState, style = style) {
                                    progressive = HazeProgressive.verticalGradient(
                                        startIntensity = 1f,
                                        endIntensity = 0f
                                    )
                                }
                                    .animateContentSize(
                                        animationSpec = tween(
                                            durationMillis = 500
                                        )
                                    ),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                AnimatedVisibility(
                                    visible = !isTimerOrStopwatchRunning,
                                    enter = slideInVertically(
                                        animationSpec = tween(durationMillis = 500),
                                        initialOffsetY = { it }
                                    ) + fadeIn(tween(durationMillis = 500)),
                                    exit = slideOutVertically(
                                        animationSpec = tween(durationMillis = 500),
                                        targetOffsetY = { it }
                                    ) + fadeOut(tween(durationMillis = 500))
                                )
                                {
                                    BottomBarTabs(
                                        tabs = tabs, selectedTab = selectedIndex,
                                        onTabSelected = {
                                            selectedIndex = tabs.indexOf(it)
                                            navController.navigate(it.title) {
                                                popUpTo(navController.graph.startDestinationId) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        modifier = Modifier

                                            .fillMaxWidth(),
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    RootNavigationGraph(
                        navController = navController,
                        innerPadding = innerPadding,
                        onShowPaywall = { showPaywall = true },
                        modifier = Modifier.hazeSource(hazeState) // <-- BURAYA EKLE

                    )
                }

                // Paywall Overlay
                if (showPaywall) {
                    PaywallScreen(
                        onDismiss = { showPaywall = false },
                        onSubscribe = { showPaywall = false }
                    )
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeepWorkTheme(userTheme = "Default") {
    }
}