package com.kami_apps.deepwork.deep_work_app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kami_apps.deepwork.deep_work_app.data.util.darken
import com.kami_apps.deepwork.deep_work_app.data.util.lighten

@Composable
fun TimeEditButtons(
    onClick: () -> Unit,
    baseColor: Color = Color(0xFF1278FF),// Varsayılan mavi ton
    icon: ImageVector
) {
    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(60.dp)
            .background(
                brush = Brush.linearGradient(colors = gradientColors),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "icon",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}

@Composable
@Preview
fun TimeEditButtonsPreview() {
    TimeEditButtons(
        onClick = { /* Tıklama işlemi */ },
        baseColor = Color.Gray,
        icon = Icons.Filled.Replay
    )
}