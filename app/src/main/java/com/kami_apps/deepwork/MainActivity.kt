package com.kami_apps.deepwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Status-bar ikonlarını açık (beyaz) yap
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false   // false = açık ikonlar
        }

        setContent {
            val navController: NavHostController = rememberNavController()
            var selectedIndex by remember { mutableIntStateOf(0) }
            var showPaywall by remember { mutableStateOf(false) }
            val stopWatchViewModel: StopwatchViewModel = hiltViewModel()

            val timerUiState by stopWatchViewModel.timerUIState.collectAsState()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            DeepWorkTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Black,
                        bottomBar = {
                            // Onboarding ekranında bottom bar'ı gösterme
                            if (currentRoute != "onboarding") {
                                BottomBarTabs(tabs = tabs, selectedTab = selectedIndex, onTabSelected = {
                                    selectedIndex = tabs.indexOf(it)
                                    navController.navigate(it.title) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                })
                            }
                        }) { innerPadding ->
                        RootNavigationGraph(
                            navController = navController, 
                            innerPadding = innerPadding,
                            onShowPaywall = { showPaywall = true }
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
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeepWorkTheme {
    }
}