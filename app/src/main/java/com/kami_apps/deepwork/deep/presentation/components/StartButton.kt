package com.kami_apps.deepwork.deep.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kami_apps.deepwork.deep.data.util.darken
import com.kami_apps.deepwork.deep.data.util.lighten
import com.kami_apps.deepwork.deep.data.util.parseTagColor

@Composable
fun StartButton(
    onClick: () -> Unit,
    baseColor: String = "18402806360702976000", // Varsayılan mavi ton
    imageVector: ImageVector = Icons.Filled.PlayArrow,
) {
    val buttonColor = parseTagColor(baseColor)

    val gradientColors = listOf(
        buttonColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        buttonColor.copy(alpha = 1f),                // Orijinal renk
        buttonColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(horizontal = 15.dp)
            .size(80.dp)
            .background(
                brush = Brush.linearGradient(colors = gradientColors),
                shape = CircleShape
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = "Play",
            tint = Color.White,
            modifier = Modifier.size(40.dp)
        )
    }
}


@Composable
@Preview
fun StartButtonPreview() {
    StartButton(
        onClick = { /* Tıklama işlemi */ },
        // baseColor = buttonColor // Örnek: Yeşil ton
    )
}