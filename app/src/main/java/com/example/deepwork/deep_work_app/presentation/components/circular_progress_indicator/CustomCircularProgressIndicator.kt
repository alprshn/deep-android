package com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomCircularProgressIndicator(
    modifier: Modifier = Modifier,
    initialValue: Int,
    primaryColor: Color,
    secondaryColor: Color,
    minValue: Int,
    maxValue: Int,
    circleRadiusStroke: Float,
    circleRadiusGradient: Float,
    colorBackgroundGradient: Color = Color.Blue,
    colorBackgroundGradientValue: Float = 0.2f,
    onPositionChange: (Int) -> Unit
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var positionValue by remember { mutableStateOf(initialValue) }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val circleThickness = width / 30f
            circleCenter = Offset(x = width / 2, y = height / 2)

            // Radial Gradient Effect
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(colorBackgroundGradient.copy(colorBackgroundGradientValue), Color.Transparent),
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

            // Text in the Center
            drawContext.canvas.nativeCanvas.apply {
                drawIntoCanvas {
                    drawText(
                        "$positionValue",
                        circleCenter.x,
                        circleCenter.y + 45.dp.toPx() / 3f,
                        Paint().apply {
                            textSize = 38.sp.toPx()
                            color = Color.White.toArgb()
                            textAlign = Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }

}


@Composable
@Preview
fun CustomCircularProgressIndicatorPreview() {
    CustomCircularProgressIndicator(
        modifier = Modifier
            .size(600.dp)
            .background(Color.Transparent),
        initialValue = 67,
        primaryColor = Color.Blue,
        secondaryColor = Color.DarkGray,
        minValue = 0,
        maxValue = 100,
        circleRadiusStroke = 400f,
        circleRadiusGradient = 700f,
        onPositionChange = {

        }
    )
}