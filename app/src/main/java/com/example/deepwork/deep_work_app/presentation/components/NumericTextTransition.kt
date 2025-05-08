package com.example.deepwork.deep_work_app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import com.example.deepwork.deep_work_app.domain.data.DigitModel
import com.example.deepwork.deep_work_app.domain.data.compareTo

@Composable
fun NumericTextTransition(secondCount: String = "00", minuteCount: String = "00" ) {

    Row (
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        secondCount.toString()
            .mapIndexed { index, c -> DigitModel(c, secondCount.toInt(), index) }
            .forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }
                ) { digit ->
                    Row {
                        Text(
                            "${digit.digitChar}",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 54.sp
                        )
                    }

                }
            }
        Text(
            ":",
            textAlign = TextAlign.Center,
            color = Color.White,
            fontSize = 54.sp
        )
        minuteCount.toString()
            .mapIndexed { index, c -> DigitModel(c, minuteCount.toInt(), index) }
            .forEach { digit ->
                AnimatedContent(
                    targetState = digit,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInVertically { -it } togetherWith slideOutVertically { it }
                        } else {
                            slideInVertically { it } togetherWith slideOutVertically { -it }
                        }
                    }
                ) { digit ->
                    Row {
                        Text(
                            "${digit.digitChar}",
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            fontSize = 54.sp
                        )
                    }

                }
            }
    }

}


@Composable
@Preview
fun NumericTextTransitionPreview() {
    NumericTextTransition()
}