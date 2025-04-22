package com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import com.example.deepwork.deep_work_app.data.util.darken
import com.example.deepwork.deep_work_app.data.util.lighten
import com.example.deepwork.deep_work_app.presentation.components.NumericTextTransition
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    initialValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    maxValue: Int,
    minValue: Int = 0,
    circleRadiusStroke: Float,
    circleRadiusGradient: Float,
    colorBackgroundGradient: Color = Color.Blue,
    colorBackgroundGradientValue: Float = 0.2f,
    baseColor: Color = Color(0xFF1278FF), // Varsayılan mavi ton
    positionValue: Int = initialValue,
    circleCenter: Offset = Offset.Zero,
    onDragStart: (Offset) -> Unit,                 // parent’e bildir
    onDrag: (PointerInputChange) -> Unit,
    onDragEnd: () -> Unit,

    ) {
    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()//State
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = { offset -> onDragStart(offset) },
                        onDrag = { change, _ -> onDrag(change) },
                        onDragEnd = { onDragEnd() }
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 25f
            // Radial Gradient Effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorBackgroundGradient.copy(colorBackgroundGradientValue),
                        Color.Transparent
                    ),
                    center = circleCenter,
                    radius = circleRadiusGradient
                ),
                radius = circleRadiusGradient,
                center = circleCenter
            )

            // Secondary Circle
            drawCircle(
                style = Stroke(width = circleThickness),
                color = secondaryColor,
                radius = circleRadiusStroke,
                center = circleCenter
            )

            // Primary Progress Arc
            drawArc(
                color = primaryColor,
                startAngle = 90f,
                sweepAngle = (360f / maxValue) * positionValue,
                style = Stroke(width = circleThickness, cap = StrokeCap.Round),
                useCenter = false,
                size = Size(
                    width = circleRadiusStroke * 2,
                    height = circleRadiusStroke * 2
                ),
                topLeft = Offset(
                    (width - circleRadiusStroke * 2) / 2,
                    (height - circleRadiusStroke * 2) / 2
                )
            )
            val endAngle = 90f + (360f / maxValue) * positionValue  // Yayın son açısı

            val angleInRadians = Math.toRadians(endAngle.toDouble())

            val dotX = center.x + circleRadiusStroke * cos(angleInRadians).toFloat()
            val dotY = center.y + circleRadiusStroke * sin(angleInRadians).toFloat()
            drawCircle(
                brush = Brush.linearGradient(colors = gradientColors),
                radius = circleThickness,  // Dot boyutu
                center = Offset(dotX, dotY)
            )
        }
        NumericTextTransition(count = positionValue)

    }

}

