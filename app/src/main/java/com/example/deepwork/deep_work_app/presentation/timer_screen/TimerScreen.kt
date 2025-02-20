package com.example.deepwork.deep_work_app.presentation.timer_screen

import AnimatedVisibilitySharedElementShortenedExample
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar
import com.example.deepwork.deep_work_app.presentation.statistics_screen.StatisticsScreen

@Composable
fun TimerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush
                    .radialGradient(
                        listOf(Color.Blue.copy(0.4f), Color.Black)
                    )
            ), verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibilitySharedElementShortenedExample()
    }
}


@Preview
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}