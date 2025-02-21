package com.example.deepwork.deep_work_app.data.util

import androidx.compose.ui.graphics.Color

// Extension fonksiyonu: Rengi açar
fun Color.lighten(factor: Float): Color {
    val red = (red + (1 - red) * factor).coerceIn(0f, 1f)
    val green = (green + (1 - green) * factor).coerceIn(0f, 1f)
    val blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f)
    return Color(red, green, blue, alpha)
}

// Extension fonksiyonu: Rengi koyulaştırır
fun Color.darken(factor: Float): Color {
    val red = (red * (1 - factor)).coerceIn(0f, 1f)
    val green = (green * (1 - factor)).coerceIn(0f, 1f)
    val blue = (blue * (1 - factor)).coerceIn(0f, 1f)
    return Color(red, green, blue, alpha)
}