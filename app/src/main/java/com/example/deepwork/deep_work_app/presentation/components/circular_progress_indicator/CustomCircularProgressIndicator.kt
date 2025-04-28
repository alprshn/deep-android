package com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.data.util.darken
import com.example.deepwork.deep_work_app.data.util.lighten
import com.example.deepwork.deep_work_app.presentation.components.NumericTextTransition
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.min
import kotlin.math.roundToInt

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    currentValue: Int, // DEĞERİ DIŞARIDAN ALIYORUZ
    primaryColor: Color,
    secondaryColor: Color,
    maxValue: Int,
    minValue: Int = 0,
    colorBackgroundGradient: Color = Color.Blue,
    colorBackgroundGradientValue: Float = 0.2f,
    baseColor: Color = Color(0xFF1278FF), // Varsayılan mavi ton
    onValueChange: (newValue: Int) -> Unit // Değer değiştiğinde dışarıya bildirilecek lambda


) {
    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )

    // Calculate radius and thickness based on the available size
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true) {
                    detectDragGestures(
                        onDragStart = { offset ->
                        },
                        //Parmağınızı ekrana koyup sürüklemeye başladığınız ilk an çalışır. (Sürükleme Başladığında)
                        //
                        onDrag = { change: PointerInputChange, dragAmount: Offset ->
                            val currentTouchOffset = change.position
                            val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                            val vector = currentTouchOffset - canvasCenter
                            val angleRadians = atan2(vector.y, vector.x)
                            var angleDegrees = Math.toDegrees(angleRadians.toDouble()).toFloat()

                            // Açıyı 12 o'clock (90 derece) sıfır kabul edip saat yönünde ilerleyen
                            // bir 0-360 aralığına eşle
                            var sweepAngle = (angleDegrees - 90f + 360f) % 360f

                            // Hesaplanan açıya karşılık gelen yeni değeri bul
                            val newValue =
                                ((sweepAngle / 360f) * (maxValue - minValue) + minValue).roundToInt()
                                    .coerceIn(minValue, maxValue)

                            // Değeri KENDİ state'ini GÜNCELLEMEK yerine, dışarıya bildir!
                            onValueChange(newValue)
                        },
                        onDragEnd = {    }
                        //onDragEnd: Kullanıcı parmağını ekrandan kaldırdığında, yani sürükleme hareketi bittiğinde tam bir kez tetiklenir.
                    )
                }
        ) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 25f

            // Calculate the drawing radius based on the canvas size
            val drawingRadius = (min(
                width,
                height
            ) / 2f) - (circleThickness / 2f) // Radius should fit within the bounds

            // Use the actual center of the Canvas for drawing
            val canvasCenter = center // This is correct inside DrawScope

            // Radial Gradient Effect
            // Adjust gradient radius to be relative to the canvas size or drawing radius
            val gradientRadius = maxOf(width, height) / 2f // Fill the canvas
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colorBackgroundGradient.copy(colorBackgroundGradientValue),
                        Color.Transparent
                    ),
                    center = canvasCenter, // Use Canvas center
                    radius = gradientRadius
                ),
                radius = gradientRadius,
                center = canvasCenter // Use Canvas center
            )

            // Secondary Circle
            drawCircle(
                style = Stroke(width = circleThickness),
                color = secondaryColor,
                radius = drawingRadius, // Use calculated radius
                center = canvasCenter // Use Canvas center
            )

            // Primary Progress Arc
            val sweepAngle = (360f / (maxValue - minValue)) * (currentValue - minValue)
            val arcSize = Size(drawingRadius * 2, drawingRadius * 2)
            val arcTopLeft = Offset(
                (width - arcSize.width) / 2f,
                (height - arcSize.height) / 2f
            )
            drawArc(
                color = primaryColor,
                startAngle = 90f,
                sweepAngle = sweepAngle,
                style = Stroke(width = circleThickness, cap = StrokeCap.Round),
                useCenter = false,
                size = arcSize, // Use calculated size based on radius
                topLeft = arcTopLeft // Calculated to center the arc
            )

            // Dot on the arc
            val angleInRadians = Math.toRadians(90f + sweepAngle.toDouble()) // Start angle + sweep
            val dotRadius = circleThickness // Dot size relative to stroke thickness

            val dotX =
                canvasCenter.x + drawingRadius * cos(angleInRadians).toFloat() // Use calculated radius
            val dotY =
                canvasCenter.y + drawingRadius * sin(angleInRadians).toFloat() // Use calculated radius

            drawCircle(
                brush = Brush.linearGradient(colors = gradientColors),
                radius = dotRadius,
                center = Offset(dotX, dotY)
            )
        }
        // Numeric text is centered in the Box, which is centered in the Column
        NumericTextTransition(count = currentValue)
    }
}


@Preview(showBackground = true)
@Composable
fun CustomCircularProgressIndicatorPreview() {
    // state’i preview içinde yönetiyoruz
    var progress by remember { mutableStateOf(25) }

    MaterialTheme {
        CustomCircularProgressIndicator(
            modifier = Modifier.size(200.dp),
            currentValue = progress, // Parent state'ini veriyoruz
            primaryColor = Color(0xFF3DDC84),
            secondaryColor = Color(0xFFE0E0E0),
            maxValue = 100,
            minValue = 0,
            colorBackgroundGradient = Color(0xFF3DDC84),
            colorBackgroundGradientValue = 0.2f,
            baseColor = Color(0xFF3DDC84),
            onValueChange = { newValue ->
                // Indicator'dan gelen değeri parent state'ine kaydediyoruz
                progress = newValue
            }
        )
    }
}

