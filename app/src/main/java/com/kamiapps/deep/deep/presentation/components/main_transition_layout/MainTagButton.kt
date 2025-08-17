package com.kamiapps.deep.deep.presentation.components.main_transition_layout

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainTagButton(
    onShowDetails: () -> Unit,
    heightButton: Int,
    textColor: Color,
    emoji: String,
    text: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    ) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .wrapContentWidth()
                .height(heightButton.dp)
                .clip(RoundedCornerShape(heightButton.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .sharedBounds(
                    rememberSharedContentState(key = "bounds"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(animationSpec = tween(durationMillis = 1500)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 1500)),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .clickable { onShowDetails() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    emoji, color = textColor, modifier = Modifier.padding(end = 5.dp),
                    fontSize = 10.sp
                )
                Text(text, color = textColor)
            }
        }
    }
}

@Composable
@Preview
fun MainTagButtonPreview() {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .wrapContentWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(60.dp))
            .background(Color(0xFF1C1E22)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center

    )
    {
        Row(
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                "❌", modifier = Modifier.padding(end = 10.dp),
                fontSize = 10.sp
            )
            Text("Select a Tag", color = Color.White)
        }
    }
}