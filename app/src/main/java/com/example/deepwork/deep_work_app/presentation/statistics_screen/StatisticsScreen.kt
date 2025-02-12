package com.example.deepwork.deep_work_app.presentation.statistics_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.deepwork.deep_work_app.presentation.navigation.bottom_bar.BottomBarTab

@Composable
fun StatisticsScreen(){
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Text("Statistics", color = Color.White)
    }
}