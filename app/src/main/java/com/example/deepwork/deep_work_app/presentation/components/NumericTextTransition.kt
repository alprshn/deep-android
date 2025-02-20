package com.example.deepwork.deep_work_app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NumericTextTransition() {
    var count by remember { mutableIntStateOf(0) }
    var shakeOffset by remember { mutableStateOf(0f) }

    // Her sayaç değiştiğinde titreme efekti başlat
    LaunchedEffect (count) {
        // Yukarı çık
        animate(
            initialValue = 0f,
            targetValue = -10f,
            animationSpec = tween(durationMillis = 100)
        ) { value, _ ->
            shakeOffset = value
        }
        // Aşağı in
        animate(
            initialValue = -10f,
            targetValue = 5f,
            animationSpec = tween(durationMillis = 100)
        ) { value, _ ->
            shakeOffset = value
        }
        // Yerine sabitle
        animate(
            initialValue = 5f,
            targetValue = 0f,
            animationSpec = tween(durationMillis = 100)
        ) { value, _ ->
            shakeOffset = value
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = count,
            transitionSpec = {
                (slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300))) with
                        (slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(300)))
            },
            label = "BouncingCounterAnimation"
        ) { targetCount ->
            Text(
                text = targetCount.toString(),
                color = Color.White,
                fontSize = 64.sp,
                modifier = Modifier
                    .graphicsLayer(translationY = shakeOffset)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { count++ }) {
            Text("Sayaç Arttır")
        }
    }
}


@Composable
@Preview
fun NumericTextTransitionPreview() {
    NumericTextTransition()
}