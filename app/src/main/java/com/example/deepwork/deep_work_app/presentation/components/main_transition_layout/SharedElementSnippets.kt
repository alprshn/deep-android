package com.example.deepwork.deep_work_app.presentation.components.main_transition_layout

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainApp() {
    var showDetails by remember { mutableStateOf(false) } // Ekran durumunu yönetir
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SharedTransitionLayout {
            AnimatedContent(
                showDetails,
                label = "basic_transition",
            ) { targetState ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (!targetState) {
                        MainTagButton(
                            onShowDetails = {
                                showDetails = true
                            },
                            50, Color.White, "❌", "Select a Tag",
                            animatedVisibilityScope = this@AnimatedContent,
                            sharedTransitionScope = this@SharedTransitionLayout
                        )
                    }
                    if (targetState) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            DetailsTagButton(
                                onBack = {
                                    showDetails = false
                                },
                                animatedVisibilityScope = this@AnimatedContent,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                textTagTitle = "Select a Tag"
                            )
                        }
                    }
                }

            }
        }
    }
}


@Composable
@Preview
fun MainAppPreview() {
    MainApp()
}