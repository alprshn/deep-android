package com.example.deepwork.deep_work_app.presentation.timer_screen

import SnackContents
import SnackEditDetails
import android.util.Log
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.deepwork.deep_work_app.data.model.StopwatchState
import com.example.deepwork.deep_work_app.presentation.components.tag_sheet_bar.TagBottomSheet
import com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchActions
import com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchViewModel

data class Snack(
    val name: String,
    val description: String
)

private val listSnacks = listOf(
    Snack("Select a Tag", ""),
)

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    stopWatchViewModel: StopwatchViewModel = hiltViewModel(),
) {
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }
    var isStarted by remember { mutableStateOf(false) }
    var colorBackgroundGradientValue by remember { mutableStateOf(0.2f) }
    val stopwatchState by stopWatchViewModel.stopwatchState.observeAsState(
        StopwatchState(
            second = "0",
            minute = "0",
            hour = "0",
            isPlaying = false,
            isReset = false
        )
    )

    var selectedState by remember { mutableStateOf(0) }


    var initialValue by remember { mutableStateOf(stopwatchState.second.toInt()) }
    var positionValue by remember { mutableStateOf(initialValue) }
    var oldPositionValue by remember { mutableStateOf(initialValue) }
    val maxValue by remember { mutableStateOf(60) }
    val minValue by remember { mutableStateOf(0) }
    Log.d("TAG", "TimerScreen: $initialValue")

    var visibleTagItems by remember { mutableStateOf(true) }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }


    initialValue = stopwatchState.second.toInt()
    Log.e("TAG", "Second: ${stopwatchState.second.toString()}")

// Removed Animate circle radius - handled internally
    // Animate background gradient
    val animatedColorBackgroundGradientValue by animateFloatAsState(
        targetValue = colorBackgroundGradientValue,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutQuart
        ), label = "backgroundGradientAlpha"
    )


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////Add Tag Layout //////////////////////////////////////////////////////
    val listState = rememberLazyListState()

    var selectedEmoji by remember { mutableStateOf("😊") } // Başlangıç emojisi
    var showEmojiPicker by remember { mutableStateOf(false) }
    val tagGeneratorSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, // Sheet'in kay  dırılmasını engeller
        confirmValueChange = { newValue ->
            newValue != SheetValue.Expanded
        }
    )
    val tagTextField = remember { mutableStateOf("") }

    val tagName by remember { mutableStateOf(tagTextField) } // Başlangıç emojisi

    // Renk listesi
    val colors = listOf(
        Color(255, 69, 58, 255),
        Color(255, 159, 10, 255),
        Color(255, 214, 10, 255),
        Color(48, 209, 88, 255),
        Color(99, 230, 226, 255),
        Color(64, 200, 224, 255),
        Color(100, 210, 255, 255),
        Color(10, 132, 255, 255),
        Color(172, 142, 104, 255),
    )
    var selectedIndex by remember { mutableIntStateOf(7) }

    var tagColor by remember { mutableStateOf(colors[selectedIndex]) }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    if (showBottomSheet) {
        TagBottomSheet(
            addTagDismiss = { showBottomSheet = false },
            listState = listState,
            selectedEmoji = selectedEmoji,
            showEmojiPicker = showEmojiPicker,
            tagGeneratorSheetState = tagGeneratorSheetState,
            tagTextField = tagTextField.value,
            tagName = tagName.value,
            colors = colors,
            selectedIndex = selectedIndex,
            tagColor = tagColor,
            emojiPickerBox = {
                showEmojiPicker = true
            },
            textFieldValueChange = { it ->
                tagTextField.value = it
            },
            clickColorBox = { index, color ->
                selectedIndex = index               // seçili state güncellenir
                tagColor = color
                Log.e("TAG Color", "TagBottomSheet: $tagColor")

                Log.e("Index Number: $index", "TagBottomSheet: $color")
            },
            setOnEmojiPickedListener = { emojiViewItem ->
                selectedEmoji = emojiViewItem.emoji
                showEmojiPicker = false
            },
            onDismissRequestAlertDialog = {
                showEmojiPicker = false
            }
        )
    }
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
                            modifier = Modifier.animateItem()
                        ) {
                            Box(
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${snack.name}-bounds"),//State
                                        animatedVisibilityScope = this,

                                        )
                                    .background(Color.Transparent)
                                    .clip(shapeForSharedElement)
                            ) {
                                SnackContents(
                                    snack = snack,
                                    modifier = Modifier.sharedElement(
                                        sharedContentState = rememberSharedContentState(key = snack.name),//State
                                        animatedVisibilityScope = this@AnimatedVisibility,
                                    ),
                                    onClick = {
                                        selectedSnack = snack
                                    },
                                    heightButton = 50,
                                    textColor = Color.White,
                                    emoji = "\uD83D\uDCCD",
                                    modifierText = Modifier.sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${snack.name}-text"),//State
                                        animatedVisibilityScope = this@AnimatedVisibility
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TimerToggleBar(
                    height = 50.dp,
                    circleButtonPadding = 4.dp,
                    circleBackgroundOnResource = Color(0xff5550e3),
                    circleBackgroundOffResource = Color(0xFF1C1E22),
                    stateOn = 0,
                    stateOff = 1,
                    onCheckedChanged = { isSelected ->
                        selectedState = if (isSelected) {
                            0
                        } else {
                            1
                        }
                    },
                    selectedState = selectedState
                )

                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .padding(vertical = 50.dp)
                        .size(350.dp)
                        .background(Color.Transparent),
                    currentValue = initialValue,
                    primaryColor = Color.Blue, // Replace with your theme colors
                    secondaryColor = Color.DarkGray, // Replace with your theme colors
                    maxValue = maxValue,
                    minValue = minValue,
                    colorBackgroundGradientValue = animatedColorBackgroundGradientValue, // Pass the animated value
                    onValueChange = { newValue ->
                        // Indicator'dan gelen yeni değeri kendi state'imize kaydediyoruz
                        initialValue = newValue

                    }

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
                            onClick = {
                                // Handle replay/reset click
                                positionValue = initialValue // Reset timer value
                                oldPositionValue = initialValue
                                // Optionally stop if started
                                isStarted = false
                                colorBackgroundGradientValue = 0.2f
                                stopWatchViewModel.reset()
                            },
                            baseColor = Color.Gray,
                            icon = Icons.Filled.Replay
                        )
                    }
                    if (isStarted) {
                        StartButton(
                            onClick = {
                                isStarted = !isStarted // Toggle start state
                                colorBackgroundGradientValue = 0.4f//0.2f
                                stopWatchViewModel.lap()
                            },
                            imageVector = Icons.Filled.Pause
                        )
                    } else {
                        StartButton(
                            onClick = {
                                isStarted = !isStarted // Toggle start state
                                colorBackgroundGradientValue = 0.2f
                                stopWatchViewModel.start()
                            },
                            imageVector = Icons.Filled.PlayArrow
                        )
                    }


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
                            onClick = {
                                // Handle stop click
                                isStarted = false // Stop timer
                                colorBackgroundGradientValue = 0.2f // Reset gradient alpha
                                // Optionally reset positionValue as well
                                // positionValue = initialValue
                                // oldPositionValue = initialValue
                            },
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
                },
                visibleTagItems = visibleTagItems,
                addTagClick = {
                    showBottomSheet = true
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