package com.example.deepwork.deep_work_app.presentation.timer_screen

import SnackContents
import SnackEditDetails
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.presentation.components.StartButton
import com.example.deepwork.deep_work_app.presentation.components.TimeEditButtons
import com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator.CustomCircularProgressIndicator
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

data class Snack(
    val name: String,
    val description: String
)

private val listSnacks = listOf(
    Snack("Select a Tag", ""),
)

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TimerScreen() {
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }
    var isStarted by remember { mutableStateOf(false) }
    var circleRadiusStroke by remember { mutableStateOf(400f) }
    var colorBackgroundGradientValue by remember { mutableStateOf(0.2f) }
    var selectedState by remember { mutableStateOf(0) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var initialValue by remember { mutableStateOf(0) }
    var positionValue by remember { mutableStateOf(initialValue) }
    var oldPositionValue by remember { mutableStateOf(initialValue) }
    var changeAngle by remember {
        mutableStateOf(0f)
    }
    var dragStartedAngle by remember {
        mutableStateOf(180f)
    }

    val maxValue by remember {
        mutableStateOf(100)
    }
    val minValue by remember {
        mutableStateOf(0)
    }


// Animate circle radius
    val animatedCircleRadius by animateFloatAsState(
        targetValue = circleRadiusStroke,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutQuart
        )
    )
    // Animate background gradient
    val animatedColorBackgroundGradientValue by animateFloatAsState(
        targetValue = colorBackgroundGradientValue,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutQuart
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(listSnacks) { snack ->
                        AnimatedVisibility(
                            visible = snack != selectedSnack, //State
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut(),
                            modifier = Modifier.animateItem()
                        ) {
                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${snack.name}-bounds"),//State
                                        animatedVisibilityScope = this,
                                        clipInOverlayDuringTransition = OverlayClip(
                                            shapeForSharedElement //State
                                        )
                                    )
                                    .background(Color.Transparent, shapeForSharedElement)
                                    .clip(shapeForSharedElement)
                            ) {
                                SnackContents(
                                    snack = snack,
                                    modifier = Modifier.sharedElement(
                                        state = rememberSharedContentState(key = snack.name),//State
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    ),
                                    onClick = {

                                    },
                                    heightButton = 50,
                                    textColor = Color.White,
                                    emoji = "\uD83D\uDCCD"
                                )
                            }
                        }
                    }
                }

                // Timer Toggle Bar - Tag seçiminin hemen altında
                Spacer(modifier = Modifier.height(16.dp))

                TimerToggleBar(
                    height = 50.dp,
                    circleButtonPadding = 4.dp,
                    circleBackgroundOnResource = Color(0xff5550e3),
                    circleBackgroundOffResource = Color(0xFF1C1E22),
                    stateOn = 0,
                    stateOff = 1,
                    onCheckedChanged = {},
                    selectedState = selectedState
                )

                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 50.dp)
                        .size(350.dp)
                        .background(Color.Transparent),
                    initialValue = initialValue,
                    primaryColor = Color.Blue,
                    secondaryColor = Color.DarkGray,
                    maxValue = maxValue,
                    minValue = minValue,
                    circleRadiusStroke = animatedCircleRadius,//State
                    circleRadiusGradient = 700f,
                    colorBackgroundGradientValue = animatedColorBackgroundGradientValue,//State
                    circleCenter = circleCenter,
                    positionValue = positionValue,
                    onDragEnd = {
                        oldPositionValue = positionValue
                    },
                    onDragStart = { offset ->
                        dragStartedAngle = -atan2(
                            x = circleCenter.y - offset.y,
                            y = circleCenter.x - offset.x
                        ) * (180f / PI).toFloat()
                        dragStartedAngle = (dragStartedAngle + 180f).mod(360f)
                    },
                    onDrag = { change: PointerInputChange ->
                        var touchAngle = -atan2(
                            x = circleCenter.y - change.position.y,
                            y = circleCenter.x - change.position.x
                        ) * (180f / PI).toFloat()
                        touchAngle = (touchAngle + 180f).mod(360f)

                        val currentAngle = oldPositionValue * 360f / (maxValue - minValue)
                        changeAngle = (touchAngle - currentAngle).toFloat()

                        val lowerThreshold = currentAngle - (360f / (maxValue - minValue) * 5)
                        val higherThreshold = currentAngle + (360f / (maxValue - minValue) * 5)

                        if (dragStartedAngle in lowerThreshold..higherThreshold) {
                            positionValue =
                                (oldPositionValue + (changeAngle / (360f / (maxValue - minValue))).roundToInt())
                        }
                    },
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = isStarted,//State
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutQuart
                            )
                        ) + scaleIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutQuart
                            ),
                            initialScale = 0.7f
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOutQuart
                            )
                        ) + scaleOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOutQuart
                            ),
                            targetScale = 0.7f
                        )
                    ) {
                        TimeEditButtons(
                            onClick = { /* Tıklama işlemi */ },//State
                            baseColor = Color.Gray,
                            icon = Icons.Filled.Replay
                        )
                    }

                    StartButton(onClick = {
                        isStarted = !isStarted//State
                        circleRadiusStroke = if (isStarted) 450f else 400f//State
                        colorBackgroundGradientValue = if (isStarted) 0.4f else 0.2f//State
                    })

                    AnimatedVisibility(
                        visible = isStarted,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutQuart
                            )
                        ) + scaleIn(
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = EaseInOutQuart
                            ),
                            initialScale = 0.7f
                        ),
                        exit = fadeOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOutQuart
                            )
                        ) + scaleOut(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = EaseInOutQuart
                            ),
                            targetScale = 0.7f
                        )
                    ) {
                        TimeEditButtons(
                            onClick = { /* Tıklama işlemi */ },//State
                            baseColor = Color.Gray,
                            icon = Icons.Default.Stop
                        )
                    }
                }
            }

            SnackEditDetails(
                snack = selectedSnack,//State
                onConfirmClick = {
                    selectedSnack = null//State
                }
            )
        }
    }
}


@Preview
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}