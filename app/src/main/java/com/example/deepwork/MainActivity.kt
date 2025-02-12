package com.example.deepwork

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar
import com.example.deepwork.deep_work_app.presentation.navigation.RootNavigationGraph
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTabs
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.tabs
import com.example.deepwork.ui.theme.DeepWorkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController: NavHostController = rememberNavController()
            var selectedIndex by remember { mutableIntStateOf(0) }
            DeepWorkTheme {
                Scaffold(modifier = Modifier.fillMaxSize().background(Color.Black),  bottomBar = {
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
                }) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).background(color = Color.Black)) { }
                    RootNavigationGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TimerToggleBar(
            height = 50.dp,
            circleButtonPadding = 4.dp,
            circleBackgroundOnResource = Color(0xff5550e3),
            circleBackgroundOffResource = Color.Black,
            stateOn = 0,
            stateOff = 1,
            onCheckedChanged = {}
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DeepWorkTheme {
        Greeting("Android")
    }
}