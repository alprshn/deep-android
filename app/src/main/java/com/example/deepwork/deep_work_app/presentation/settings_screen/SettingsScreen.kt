package com.example.deepwork.deep_work_app.presentation.settings_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.deepwork.deep_work_app.presentation.components.main_transition_layout.MainApp

@Composable
fun SettingsScreen(){
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        MainApp()
    }
}


@Preview
@Composable
fun SettingsScreenPreview(){
    SettingsScreen()
}
