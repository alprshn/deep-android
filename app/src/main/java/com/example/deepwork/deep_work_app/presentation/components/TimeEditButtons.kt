package com.example.deepwork.deep_work_app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TimeEditButtons(
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp) // Butonun boyutu
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6A5ACD), Color(0xFF00BFFF))
                ),
                shape = CircleShape
            )
            .fillMaxSize()
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(40.dp) // İkon boyutu
        )
    }
}

@Composable
@Preview
fun TimeEditButtonsPreview() {
    StartButton(onClick = {
        // Buton tıklandığında yapılacak işlem
    })
}