package com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.data.util.darken
import com.example.deepwork.deep_work_app.data.util.lighten
import com.example.deepwork.deep_work_app.presentation.components.NumericTextTransition
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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
    onPositionChange: (Int) -> Unit,
    baseColor: Color = Color(0xFF1278FF), // Varsayılan mavi ton
) {
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var positionValue by remember { mutableStateOf(initialValue) }
   // var count by remember { mutableStateOf(0) }
    var changeAngle by remember {
        mutableStateOf(0f)
    }

    var dragStartedAngle by remember {
        mutableStateOf(180f)
    }

    var oldPositionValue by remember {
        mutableStateOf(initialValue)
    }

    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(true){
                    detectDragGestures(
                        onDragStart = {offset ->
                            dragStartedAngle = -atan2(
                                x = circleCenter.y - offset.y,
                                y = circleCenter.x - offset.x
                            ) * (180f / PI).toFloat()
                            dragStartedAngle = (dragStartedAngle + 180f).mod(360f)
                        },
                        onDrag = { change, _ ->
                            var touchAngle = -atan2(
                                x = circleCenter.y - change.position.y,
                                y = circleCenter.x - change.position.x
                            ) * (180f / PI).toFloat()
                            touchAngle = (touchAngle + 180f).mod(360f)

                            val currentAngle = oldPositionValue*360f/(maxValue-minValue)
                            changeAngle = touchAngle - currentAngle

                            val lowerThreshold = currentAngle - (360f / (maxValue-minValue) * 5)
                            val higherThreshold = currentAngle + (360f / (maxValue-minValue) * 5)

                            if(dragStartedAngle in lowerThreshold .. higherThreshold){
                                positionValue = (oldPositionValue + (changeAngle/(360f/(maxValue-minValue))).roundToInt())
                            }

                        },
                        onDragEnd = {
                            oldPositionValue = positionValue
                            onPositionChange(positionValue)
                        }
                    )
                }
        ){
            val width = size.width
            val height = size.height
            val circleThickness = width / 25f
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