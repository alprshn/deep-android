package com.kami_apps.deepwork.deep.data.util

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


fun parseTagColor(raw: String?): Color {
    if (raw.isNullOrBlank()) return Color.Gray // ⬅️ Null veya boş kontrolü

    return try {
        Color(raw.toULong())
    } catch (e: NumberFormatException) {
        val re = Regex("""Color\(([\d.]+),\s*([\d.]+),\s*([\d.]+),\s*([\d.]+)""")
        val matchResult = re.find(raw)
        return if (matchResult != null) {
            val (r, g, b, a) = matchResult.destructured
            Color(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat())
        } else {
            Color.Gray // ⬅️ Hatalı format fallback
        }
    }
}
