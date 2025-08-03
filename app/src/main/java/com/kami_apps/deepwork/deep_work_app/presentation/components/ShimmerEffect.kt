package com.kami_apps.deepwork.deep_work_app.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape // Shape import'ını ekledik
import androidx.compose.foundation.shape.RoundedCornerShape // Örnek olarak RoundedCornerShape ekledik
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp


@Composable
fun ShimmerEffectOnImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    widthOfShadowBrush: Int = 300,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 2500,
    baseColor: Color = Color.Transparent,
    shimmerColor: Color = Color.White,
    contentScale: ContentScale = ContentScale.Crop,
    description: String? = null,
    shimmerTravelDistance: Float = 600f // daha yavaş hareket için sabit mesafe belirliyoruz

) {

    val transition = rememberInfiniteTransition(label = "shimmer")
    val animatedOffset = transition.animateFloat(
        initialValue = 0f,
        targetValue = shimmerTravelDistance,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    val shimmerColors = listOf(
        baseColor.copy(alpha = 0.3f),
        shimmerColor.copy(alpha = 0.5f),
        shimmerColor.copy(alpha = 1f),
        shimmerColor.copy(alpha = 0.5f),
        baseColor.copy(alpha = 0.3f),
    )

    val shimmerBrush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(animatedOffset.value - widthOfShadowBrush, 0f),
        end = Offset(animatedOffset.value, angleOfAxisY)
    )

    Box(modifier = modifier) {
        Image(
            painter = painter,
            contentDescription = description,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer(alpha = 0.99f) // zorla Layer açar
                .drawWithCache {
                    onDrawWithContent {
                        drawContent() // orijinal görseli çiz
                        drawRect(
                            brush = shimmerBrush,
                            blendMode = BlendMode.SrcAtop // sadece mevcut alpha alanına uygula
                        )
                    }
                },
            contentScale = contentScale
        )
    }
}



@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier,
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 2000,
    baseColor: Color = Color.Transparent,
    shimmerColor: Color = Color.White,
    // Yeni eklenen parametre: Shimmer'ın uygulanacağı şekil
    shimmerShape: Shape = RoundedCornerShape(0.dp), // Varsayılan olarak dikdörtgen, yuvarlak köşeleri buradan ayarlayacağız
    content: @Composable () -> Unit
) {
    val shimmerColors = listOf(
        baseColor.copy(alpha = 0.3f),
        shimmerColor.copy(alpha = 0.5f),
        shimmerColor.copy(alpha = 1.0f),
        shimmerColor.copy(alpha = 0.5f),
        baseColor.copy(alpha = 0.3f),
    )

    val transition = rememberInfiniteTransition(label = "ShimmerEffectTransition")

    val translateAnimation = transition.animateFloat(
        initialValue = 0f,
        targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = durationMillis,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "Shimmer loading animation",
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
        end = Offset(x = translateAnimation.value, y = angleOfAxisY),
    )

    Box(
        modifier = modifier
            // Shimmer fırçasını ve şeklini doğrudan background modifikatörüne uyguluyoruz
            .background(
                brush = brush,
                shape = shimmerShape // Buraya yeni şekil parametresini ekledik
            )
    ) {
        content()
    }
}
