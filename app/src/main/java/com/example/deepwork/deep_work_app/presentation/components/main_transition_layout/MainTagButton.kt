package com.example.deepwork.deep_work_app.presentation.components.main_transition_layout

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier.padding(8.dp)
                .wrapContentWidth()
                .height(heightButton.dp)
                .clip(RoundedCornerShape(heightButton.dp))
                .background(Color.Gray)
                .sharedBounds(
                    rememberSharedContentState(key = "bounds"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                )
                .clickable { onShowDetails() }) {
            Row(
                modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
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