package com.example.deepwork.deep_work_app.presentation.timeline_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun TimelineScreen(){
    Column (modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Text("Timeline", color = Color.White)
    }
}