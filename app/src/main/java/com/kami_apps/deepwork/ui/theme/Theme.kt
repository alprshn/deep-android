package com.kami_apps.deepwork.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = DarkBlue,
    onPrimary = Color.White,
    primaryContainer = DarkBlue.copy(alpha = 0.3f),
    onPrimaryContainer = Color.White,

    secondary = LightGray6,
    onSecondary = Color.White,
    secondaryContainer = DarkGreen,
    onSecondaryContainer = Color.White,

    tertiary = DarkPurple,
    onTertiary = Color.White,
    tertiaryContainer = DarkPurple.copy(alpha = 0.3f),
    onTertiaryContainer = Color.White,

    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkGray6,
    onSurfaceVariant = DarkGray,


    outline = DarkGray3,
    outlineVariant = DarkGray5,
    scrim = Color.Black.copy(alpha = 0.3f),

    inverseSurface = LightSurface,
    inverseOnSurface = LightOnSurface,
    inversePrimary = LightBlue,

    surfaceDim = DarkGray6,
    surfaceBright = DarkGray6,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkGray6,
    surfaceContainer = DarkGray5,
    surfaceContainerHigh = DarkGray4,
    surfaceContainerHighest = DarkGray3,
    surfaceTint = DarkGray2,
    error = DarkRed,
    onError = Color.White,
    errorContainer = DarkRed.copy(alpha = 0.3f),
    onErrorContainer = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    onPrimary = Color.Black,
    primaryContainer = LightBlue.copy(alpha = 0.1f),
    onPrimaryContainer = LightBlue,

    secondary = DarkGray,
    onSecondary = Color.White,
    secondaryContainer = LightGreen,
    onSecondaryContainer = LightGreen,

    tertiary = LightPurple,
    onTertiary = Color.White,
    tertiaryContainer = LightPurple.copy(alpha = 0.1f),
    onTertiaryContainer = LightPurple,

    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightGray6,
    onSurfaceVariant = LightGray,

    outline = LightGray3,
    outlineVariant = LightGray5,
    scrim = Color.White,


    inverseSurface = DarkSurface,
    inverseOnSurface = DarkOnSurface,
    inversePrimary = DarkBlue,

    surfaceDim = LightGray6,
    surfaceBright = Color.White,
    surfaceContainerLowest = LightGray6,
    surfaceContainerLow = LightGray6,
    surfaceContainer = LightGray5,
    surfaceContainerHigh = LightGray4,
    surfaceContainerHighest = LightGray3,
    surfaceTint = Color.White,

    error = LightRed,
    onError = Color.White,
    errorContainer = LightRed.copy(alpha = 0.1f),
    onErrorContainer = LightRed,
)

@Composable
fun DeepWorkTheme(
    userTheme: String = "Default", // User selected theme: "Light", "Dark", "Default"
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkTheme = when (userTheme) {
        "Light" -> false
        "Dark" -> true
        "Default" -> systemInDarkTheme
        else -> systemInDarkTheme
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}