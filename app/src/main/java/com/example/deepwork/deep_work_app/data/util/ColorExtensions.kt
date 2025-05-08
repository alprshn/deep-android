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


fun parseTagColor(raw: String): Color = try {
    // ❶ ULong formatı → doğrudan Color(value)
    Color(raw.toULong())
} catch (e: NumberFormatException) {
    // ❷ "Color(r,g,b,a,…)" formatı varsa regex ile al
    val re = Regex("""Color\(([\d.]+),\s*([\d.]+),\s*([\d.]+),\s*([\d.]+)""")
    val (r, g, b, a) = re.find(raw)!!.destructured
    Color(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
}