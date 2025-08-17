package com.kami_apps.deepwork.deep.presentation.components.toggle_switch_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kami_apps.deepwork.deep.data.util.parseTagColor

@Composable
fun TimerToggleBar(
    height: Dp,
    circleButtonPadding: Dp,
    circleBackgroundOnResource: String = "18402806360702976000",
    selectedState: Boolean,                       // true: Stopwatch, false: Timer
    onCheckedChanged: (isStopwatch: Boolean) -> Unit, // Callback for state change
    toggleTimerUiState: Boolean // Is currently running
) {
    val buttonColor = parseTagColor(circleBackgroundOnResource)

    Row(
        modifier = Modifier
            .wrapContentSize()
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .animateContentSize( // Animasyonlu küçülme/büyüme
                animationSpec = androidx.compose.animation.core.tween(
                    durationMillis = 500,
                    easing = androidx.compose.animation.core.EaseInOutQuart
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Timer Button (left side) - sadece timer modu değilse ve çalışmıyorsa göster
        AnimatedVisibility(
            visible = !toggleTimerUiState || !selectedState, // Çalışmıyorsa veya timer aktifse göster
            enter = androidx.compose.animation.fadeIn(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideInHorizontally(
                animationSpec = androidx.compose.animation.core.tween(300)
            ),
            exit = androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideOutHorizontally(
                animationSpec = androidx.compose.animation.core.tween(300),
                targetOffsetX = { -it }
            )
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(height)
                    .padding(circleButtonPadding)
                    .clip(RoundedCornerShape(50))
                    .background(if (!selectedState) buttonColor else MaterialTheme.colorScheme.surfaceContainer) // Active when selectedState is false (Timer mode)
                    .clickable {
                        if (!toggleTimerUiState) { // Only allow change when not running
                            onCheckedChanged(false) // Switch to Timer mode
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        Icons.Filled.AvTimer,
                        contentDescription = "Timer",
                        tint = if (!selectedState)Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Timer", color = if (!selectedState)Color.White else MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        // Stopwatch Button (right side) - sadece stopwatch modu değilse ve çalışmıyorsa göster
        AnimatedVisibility(
            visible = !toggleTimerUiState || selectedState, // Çalışmıyorsa veya stopwatch aktifse göster
            enter = androidx.compose.animation.fadeIn(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideInHorizontally(
                animationSpec = androidx.compose.animation.core.tween(300)
            ),
            exit = androidx.compose.animation.fadeOut(
                animationSpec = androidx.compose.animation.core.tween(300)
            ) + androidx.compose.animation.slideOutHorizontally(
                animationSpec = androidx.compose.animation.core.tween(300),
                targetOffsetX = { it }
            )
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(height)
                    .padding(circleButtonPadding)
                    .clip(RoundedCornerShape(50))
                    .background(if (selectedState) buttonColor else MaterialTheme.colorScheme.surfaceContainer) // Active when selectedState is true (Stopwatch mode)
                    .clickable {
                        if (!toggleTimerUiState) { // Only allow change when not running
                            onCheckedChanged(true) // Switch to Stopwatch mode
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(10.dp)
                ) {
                    Icon(
                        Icons.Outlined.Timer,
                        contentDescription = "Stopwatch",
                        tint = if (selectedState)Color.White else MaterialTheme.colorScheme.onPrimary
                    )
                    Text("Stopwatch", color = if (selectedState)Color.White else MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

