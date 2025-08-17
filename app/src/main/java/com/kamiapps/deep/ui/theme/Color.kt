package com.kamiapps.deep.ui.theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Light Theme Colors
val LightBackground = Color(255, 255, 255) // Beyaz background light theme için
val LightSurface = Color(0xFFFFFFFF)
val LightOnBackground = Color(0xFF1C1B1F)
val LightOnSurface = Color(0xFF1C1B1F)
val LightPrimary = Color(0xFF0A84FF)
val LightOnPrimary = Color(0xFFFFFFFF)

val LightGray = Color(142, 142, 147)
val LightGray2 = Color(174, 174, 178)
val LightGray3 = Color(199, 199, 204)
val LightGray4 = Color(209, 209, 214)
val LightGray5 = Color(229, 229, 234)
val LightGray6 = Color(242, 242, 247)

val LightRed = Color(1f, 59 / 255f, 48 / 255f)
val LightOrange = Color(1f, 149 / 255f, 0f)
val LightYellow = Color(1f, 204 / 255f, 0f)
val LightGreen = Color(52 / 255f, 199 / 255f, 89 / 255f)
val LightMint = Color(0f, 199 / 255f, 190 / 255f)
val LightTeal = Color(48 / 255f, 176 / 255f, 199 / 255f)
val LightCyan = Color(50 / 255f, 173 / 255f, 230 / 255f)
val LightBlue = Color(0f, 136 / 255f, 255 / 255f)
val LightIndigo = Color(88 / 255f, 86 / 255f, 214 / 255f)
val LightPurple = Color(175 / 255f, 82 / 255f, 222 / 255f)
val LightPink = Color(1f, 45 / 255f, 85 / 255f)
val LightBrown = Color(172 / 255f, 142 / 255f, 104 / 255f)

// Dark Theme Colors
val DarkBackground = Color(0, 0, 0) // Siyah background dark theme için
val DarkSurface = Color(0xFF1C1C1E)
val DarkOnBackground = Color(0xFFFFFFFF)
val DarkOnSurface = Color(0xFFFFFFFF)
val DarkPrimary = Color(0xFF0A84FF)
val DarkOnPrimary = Color(0xFFFFFFFF)

val DarkGray = Color(142, 142, 147)
val DarkGray2 = Color(99, 99, 102)
val DarkGray3 = Color(72, 72, 74)
val DarkGray4 = Color(58, 58, 60)
val DarkGray5 = Color(44, 44, 46)
val DarkGray6 = Color(28, 28, 30)

val DarkRed = Color(1f, 69 / 255f, 58 / 255f)
val DarkOrange = Color(1f, 159 / 255f, 10 / 255f)
val DarkYellow = Color(1f, 214 / 255f, 10 / 255f)
val DarkGreen = Color(48 / 255f, 209 / 255f, 88 / 255f)
val DarkMint = Color(102 / 255f, 212 / 255f, 207 / 255f)
val DarkTeal = Color(64 / 255f, 200 / 255f, 224 / 255f)
val DarkCyan = Color(100 / 255f, 210 / 255f, 1f)
val DarkBlue = Color(0f, 145 / 255f, 255 / 255f)
val DarkIndigo = Color(94 / 255f, 92 / 255f, 230 / 255f)
val DarkPurple = Color(191 / 255f, 90 / 255f, 242 / 255f)
val DarkPink = Color(1f, 55 / 255f, 95 / 255f)
val DarkBrown = Color(181 / 255f, 148 / 255f, 105 / 255f)

// Renk listesi - TagColors için (mevcut uyumluluk)
val TagColors = listOf(
    Color(0xFFFF453A),
    Color(0xFFFF9F0A),
    Color(0xFFFFD60A),
    Color(0xFF30D158),
    Color(0xFF63E6E2),
    Color(0xFF40C8E0),
    Color(0xFF64D2FF),
    Color(0xFF0A84FF),
    Color(0xFFAC8E68),
)

// Light Theme için yeni renk listesi
val lightColorsList = listOf(
    LightRed,
    LightOrange,
    LightYellow,
    LightGreen,
    LightMint,
    LightTeal,
    LightCyan,
    LightBlue,
    LightIndigo,
    LightPurple,
    LightPink,
    LightBrown
)

// Dark Theme için yeni renk listesi
val darkColorsList = listOf(
    DarkRed,
    DarkOrange,
    DarkYellow,
    DarkGreen,
    DarkMint,
    DarkTeal,
    DarkCyan,
    DarkBlue,
    DarkIndigo,
    DarkPurple,
    DarkPink,
    DarkBrown
)

/**
 * Theme-aware renk seçimi için utility fonksiyonlar
 */
// Tag renkleri için theme'e göre renk listesi döndür
fun getTagColorsForTheme(isDarkTheme: Boolean): List<Color> {
    return if (isDarkTheme) darkColorsList else lightColorsList
}

// Belirli bir index için theme'e göre renk döndür  
fun getTagColorByIndex(index: Int, isDarkTheme: Boolean): Color {
    val colors = getTagColorsForTheme(isDarkTheme)
    return colors.getOrElse(index % colors.size) {
        if (isDarkTheme) DarkBlue else LightBlue
    }
}

// Surface renkleri için theme'e göre gradient döndür
fun getSurfaceGradientColors(isDarkTheme: Boolean): List<Color> {
    return if (isDarkTheme) {
        listOf(DarkGray6, DarkGray5, DarkGray4)
    } else {
        listOf(LightGray6, LightGray5, LightGray4)
    }
}

// Accent renkleri için theme'e göre seçim
fun getAccentColor(accentType: AccentType, isDarkTheme: Boolean): Color {
    return when (accentType) {
        AccentType.SUCCESS -> if (isDarkTheme) DarkGreen else LightGreen
        AccentType.WARNING -> if (isDarkTheme) DarkOrange else LightOrange
        AccentType.ERROR -> if (isDarkTheme) DarkRed else LightRed
        AccentType.INFO -> if (isDarkTheme) DarkBlue else LightBlue
        AccentType.HIGHLIGHT -> if (isDarkTheme) DarkCyan else LightCyan
    }
}

enum class AccentType {
    SUCCESS, WARNING, ERROR, INFO, HIGHLIGHT
}
