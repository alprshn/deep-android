package com.kami_apps.deepwork.deep_work_app.presentation.timer_screen

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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.kami_apps.deepwork.deep_work_app.presentation.components.StartButton
import com.kami_apps.deepwork.deep_work_app.presentation.components.TimeEditButtons
import com.kami_apps.deepwork.deep_work_app.presentation.components.circular_progress_indicator.CustomCircularProgressIndicator
import com.kami_apps.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.deep_work_app.data.model.StopwatchState
import com.kami_apps.deepwork.deep_work_app.data.model.TimerState
import com.kami_apps.deepwork.deep_work_app.data.manager.PremiumManager
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.kami_apps.deepwork.deep_work_app.presentation.components.end_session_bar.EndSessionBar
import com.kami_apps.deepwork.deep_work_app.presentation.components.tag_sheet_bar.TagBottomSheet
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchViewModel
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.timer.TimerViewModel
import com.kami_apps.deepwork.ui.theme.TagColors
import androidx.compose.material3.MaterialTheme

data class Snack(
    var name: String,
    val description: String
)

//UI Effect, UI State, UI Event -> MVI

private val listSnacks = listOf(
    Snack("Select a Tag", ""),
)

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimerScreen(
    stopWatchViewModel: StopwatchViewModel = hiltViewModel(),
    timerViewModel: TimerViewModel = hiltViewModel()
) {
    // Haptic feedback
    val hapticFeedback = LocalHapticFeedback.current
    val isHapticEnabled by timerViewModel.isHapticEnabled.collectAsState()
    
    // UI State'leri
    val stopwatchUiState by stopWatchViewModel.timerUIState.collectAsState()
    val timerUiState by timerViewModel.timerUIState.collectAsState()
    
    // Timer ve Stopwatch State'leri
    val stopwatchState by stopWatchViewModel.stopwatchState.observeAsState(
        StopwatchState(
            second = "00",
            minute = "00",
            hour = "00",
            isPlaying = false,
            isReset = false
        )
    )
    
    val timerState by timerViewModel.timerState.observeAsState(
        TimerState(
            timeInMillis = 0L,
            timeText = "00:00:00",
            hour = 0,
            minute = 25,
            second = 0,
            progress = 0f,
            isPlaying = false,
            isDone = true
        )
    )
    
    val tagContent by stopWatchViewModel.allTagList.collectAsStateWithLifecycle()

    // Local UI State'ler - MODE FIRST!
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showEndSessionBar by remember { mutableStateOf(false) }
    var selectedTagText by remember { mutableStateOf("Select a Tag") }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var tagTextField by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(7) }
    var tagColor by remember { mutableStateOf(TagColors[selectedColorIndex]) }
    var chosenTagColor by remember { mutableStateOf("18402806360702976000") }
    var selectedEmoji by remember { mutableStateOf("😊") }
    
    // Mode Selection State (false: Timer, true: Stopwatch) - MUST BE BEFORE isPremium!
    var isStopwatchMode by remember { mutableStateOf(false) }

    // Premium status from either viewmodel (isStopwatchMode now defined)
    val isPremium = if (isStopwatchMode) stopwatchUiState.isPremium else timerUiState.isPremium
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var colorBackgroundGradientValue by remember { mutableFloatStateOf(0.2f) }
    
    // Timer spesifik state'ler
    var timerMinutes by remember { mutableStateOf(25) }

    LaunchedEffect(Unit) {
        stopWatchViewModel.getAllTag()
        timerViewModel.getAllTag()
        // Varsayılan timer süresini ayarla (25 dakika)
        timerViewModel.setTimerFromMinutes(25)
    }

    // Derived state'ler - hangi moda göre UI state'i belirlenir
    val currentUiState = if (isStopwatchMode) stopwatchUiState else timerUiState
    val isCurrentlyStarted = if (isStopwatchMode) 
        stopwatchUiState.stopWatchIsStarted else timerState.isPlaying

    val animatedColorBackgroundGradientValue by animateFloatAsState(
        targetValue = colorBackgroundGradientValue,
        animationSpec = tween(
            durationMillis = 500,
            easing = EaseInOutQuart
        ), label = "backgroundGradientAlpha"
    )

    // Tag ekleme bottom sheet
    if (showBottomSheet) {
        TagBottomSheet(
            addTagDismiss = { showBottomSheet = false },
            selectedEmoji = selectedEmoji,
            showEmojiPicker = showEmojiPicker,
            tagTextField = tagTextField,
            tagName = tagTextField,
            selectedIndex = selectedColorIndex,
            tagColor = tagColor,
            emojiPickerBox = { showEmojiPicker = true },
            textFieldValueChange = { tagTextField = it },
            clickColorBox = { index, color ->
                selectedColorIndex = index
                tagColor = color
            },
            setOnEmojiPickedListener = { emojiViewItem ->
                selectedEmoji = emojiViewItem.emoji
                showEmojiPicker = false
            },
            onDismissRequestAlertDialog = { showEmojiPicker = false },
            addTag = {
                if (tagTextField.isNotBlank()) {
                    val currentTagCount = tagContent.size

                    // Premium kontrolü
                    if (!isPremium && currentTagCount >= 1) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                "⭐ Premium required! Free users can only create 1 tag. Upgrade to create unlimited tags."
                            )
                        }
                        return@TagBottomSheet
                    }

                    showBottomSheet = false
                    if (isStopwatchMode) {
                        stopWatchViewModel.addTag(
                            tagTextField,
                            tagColor.value.toString(),
                            selectedEmoji
                        )
                    } else {
                        timerViewModel.addTag(
                            tagTextField,
                            tagColor.value.toString(),
                            selectedEmoji
                        )
                    }
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier.animateContentSize(
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    )
                ) {
                    AnimatedVisibility(
                        visible = !isCurrentlyStarted,
                        enter = fadeIn(tween(durationMillis = 500)),
                        exit = fadeOut(tween(durationMillis = 500))
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
                                    visible = snack != selectedSnack,
                                    modifier = Modifier.animateItem()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .sharedBounds(
                                                    sharedContentState = rememberSharedContentState(
                                                        key = "${snack.name}-bounds"
                                                    ),
                                                animatedVisibilityScope = this,
                                            )
                                            .background(Color.Transparent)
                                            .clip(shapeForSharedElement)
                                    ) {
                                        SnackContents(
                                            snack = snack,
                                            modifier = Modifier.sharedElement(
                                                    sharedContentState = rememberSharedContentState(
                                                        key = snack.name
                                                    ),
                                                animatedVisibilityScope = this@AnimatedVisibility,
                                            ),
                                            onClick = { selectedSnack = snack },
                                            heightButton = 50,
                                            textColor = Color.White,
                                            emoji = currentUiState.selectedTagEmoji,
                                            modifierText = Modifier.sharedBounds(
                                                    sharedContentState = rememberSharedContentState(
                                                        key = "${snack.name}-text"
                                                    ),
                                                animatedVisibilityScope = this@AnimatedVisibility
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                // Timer/Stopwatch Toggle Bar
                TimerToggleBar(
                    height = 50.dp,
                    circleButtonPadding = 4.dp,
                    circleBackgroundOnResource = chosenTagColor,
                    onCheckedChanged = { isStopwatch ->
                        if (!isCurrentlyStarted) {
                            isStopwatchMode = isStopwatch
                            
                            // Haptic feedback for mode selection
                            if (isHapticEnabled) {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                    },
                    selectedState = isStopwatchMode,
                    toggleTimerUiState = isCurrentlyStarted
                )

                // Circular Progress Indicator
                if (isStopwatchMode) {
                    // Stopwatch Mode
                    CustomCircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 50.dp)
                            .size(350.dp)
                            .background(Color.Transparent),
                        minuteCurrentValue = stopwatchState.minute,
                        secondCurrentValue = stopwatchState.second,
                        maxValue = stopwatchUiState.maxValue,
                        minValue = stopwatchUiState.minValue,
                        colorBackgroundGradientValue = animatedColorBackgroundGradientValue,
                        onValueChange = { newValue ->
                            stopwatchUiState.initialValueMinutes = newValue.toString()
                        },
                        timerState = false,
                        colorBackgroundGradient = chosenTagColor,
                        progressTagEmoji = selectedEmoji,
                        progressTagName = selectedTagText,
                        progressStartState = stopwatchUiState.stopWatchIsStarted,
                        isTimerMode = false,
                        timerProgress = 0f,
                        isTimerRunning = false
                    )
                } else {
                    // Timer Mode
                    CustomCircularProgressIndicator(
                        modifier = Modifier
                            .padding(vertical = 50.dp)
                            .size(350.dp)
                            .background(Color.Transparent),
                        minuteCurrentValue = String.format("%02d", timerState.minute),
                        secondCurrentValue = String.format("%02d", timerState.second),
                        maxValue = 60,
                        minValue = 0,
                        colorBackgroundGradientValue = animatedColorBackgroundGradientValue,
                        onValueChange = { newValue ->
                            if (!timerState.isPlaying) {
                                timerMinutes = newValue
                                timerViewModel.setTimerFromMinutes(newValue)
                                Log.d("TimerScreen", "Timer minutes set to: $newValue")
                            }
                        },
                        timerState = false,
                        colorBackgroundGradient = chosenTagColor,
                        progressTagEmoji = selectedEmoji,
                        progressTagName = selectedTagText,
                        progressStartState = timerState.isPlaying,
                        isTimerMode = true,
                        timerProgress = timerState.progress, // TimerManager zaten kalan zamanın oranını veriyor
                        isTimerRunning = timerState.isPlaying
                    )
                }

                // Action Buttons
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Reset/Replay Button
                    AnimatedVisibility(
                        visible = isCurrentlyStarted,
                        enter = fadeIn(animationSpec = tween(500, easing = EaseInOutQuart)) + 
                                    scaleIn(
                                        animationSpec = tween(500, easing = EaseInOutQuart),
                                        initialScale = 0.7f
                                    ),
                        exit = fadeOut(animationSpec = tween(300, easing = EaseInOutQuart)) + 
                                    scaleOut(
                                        animationSpec = tween(300, easing = EaseInOutQuart),
                                        targetScale = 0.7f
                                    )
                    ) {
                        TimeEditButtons(
                            onClick = {
                                if (isStopwatchMode) {
                                    stopWatchViewModel.reset()
                                } else {
                                    timerViewModel.reset()
                                }
                                colorBackgroundGradientValue = 0.2f
                            },
                            baseColor = Color.Gray,
                            icon = Icons.Filled.Replay
                        )
                    }

                    // Play/Pause Button
                    if (!isCurrentlyStarted) {
                        StartButton(
                            onClick = {
                                if (currentUiState.tagId == 0) {
                                    Log.e("TAG", "Please select a tag")
                                } else {
                                    if (isStopwatchMode) {
                                        stopWatchViewModel.start()
                                    } else {
                                        timerViewModel.start()
                                    }
                                    colorBackgroundGradientValue = 0.4f
                                }
                            },
                            imageVector = Icons.Filled.PlayArrow,
                            baseColor = chosenTagColor
                        )
                    } else {
                        StartButton(
                            onClick = {
                                if (isStopwatchMode) {
                                    stopWatchViewModel.lap()
                                } else {
                                    timerViewModel.pause()
                                }
                                colorBackgroundGradientValue = 0.2f
                            },
                            imageVector = Icons.Filled.Pause,
                            baseColor = chosenTagColor
                        )
                    }

                    // Stop Button
                    AnimatedVisibility(
                        visible = isCurrentlyStarted,
                        enter = fadeIn(animationSpec = tween(500, easing = EaseInOutQuart)) + 
                                    scaleIn(
                                        animationSpec = tween(500, easing = EaseInOutQuart),
                                        initialScale = 0.7f
                                    ),
                        exit = fadeOut(animationSpec = tween(300, easing = EaseInOutQuart)) + 
                                    scaleOut(
                                        animationSpec = tween(300, easing = EaseInOutQuart),
                                        targetScale = 0.7f
                                    )
                    ) {
                        TimeEditButtons(
                            onClick = { showEndSessionBar = true },
                            baseColor = Color.Gray,
                            icon = Icons.Default.Stop
                        )
                    }
                }
            }

            // Tag Selection Dialog
            SnackEditDetails(
                snack = selectedSnack,
                onCloseClick = { selectedSnack = null },
                visibleTagItems = currentUiState.visibleTagItems,
                addTagClick = { showBottomSheet = true },
                tagContent = tagContent,
                onTagClick = { tag ->
                    if (isStopwatchMode) {
                        stopwatchUiState.selectedTagEmoji = tag.tagEmoji
                        stopwatchUiState.tagId = tag.tagId
                    } else {
                        timerUiState.selectedTagEmoji = tag.tagEmoji
                        timerUiState.tagId = tag.tagId
                    }
                    selectedTagText = tag.tagName
                    listSnacks[0].name = selectedTagText
                    chosenTagColor = tag.tagColor
                    selectedEmoji = tag.tagEmoji
                    Log.e("chosenTagColor", "TimerScreen: $chosenTagColor")
                },
                selectedTagEmoji = currentUiState.selectedTagEmoji,
                selectedTagText = selectedTagText,
            )

            // End Session Dialog
            if (showEndSessionBar) {
                EndSessionBar(
                    endSession = {
                        showEndSessionBar = false
                        if (isStopwatchMode) {
                            stopWatchViewModel.stop()
                        } else {
                            timerViewModel.completeSession()
                        }
                        colorBackgroundGradientValue = 0.2f
                    },
                    keepGoingButtonColor = chosenTagColor,
                    onClickKeepGoing = { showEndSessionBar = false }
                )
            }
        }

            // Snackbar for premium restrictions
            androidx.compose.material3.SnackbarHost(
                hostState = snackbarHostState,
            )
    }
}

@Preview
@Composable
fun TimerScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 70.dp)
                .clip(shape = RoundedCornerShape(16.dp))
                .background(Color.Blue.copy(alpha = 0.2f))
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "selectedEmoji",
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .padding(start = 20.dp),
                fontSize = 20.sp
            )
            Text(
                text = "New Tag",
                modifier = Modifier
                    .padding(vertical = 15.dp)
                    .padding(start = 10.dp),
                fontSize = 18.sp,
                color = Color.Blue,
                fontWeight = FontWeight.Bold
            )
            }
        }
    }
}