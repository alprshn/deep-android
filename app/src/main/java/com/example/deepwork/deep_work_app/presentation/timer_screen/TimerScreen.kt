package com.example.deepwork.deep_work_app.presentation.timer_screen

import AnimatedVisibilitySharedElementShortenedExample
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimerScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
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