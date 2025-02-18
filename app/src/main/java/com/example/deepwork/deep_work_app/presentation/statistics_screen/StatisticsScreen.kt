package com.example.deepwork.deep_work_app.presentation.statistics_screen

import AnimatedVisibilitySharedElementShortenedExample
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab

@Composable
fun StatisticsScreen(){
    Column(modifier = Modifier.fillMaxSize().background(Color.Black), verticalArrangement = Arrangement.Center) {
        AnimatedVisibilitySharedElementShortenedExample()
        Text("Statistics", color = Color.White)

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


@Preview
@Composable
fun StatisticsScreenPreview(){
    StatisticsScreen()
}