package com.kami_apps.deepwork.deep_work_app.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kami_apps.deepwork.deep_work_app.domain.data.DigitModel
import com.kami_apps.deepwork.deep_work_app.domain.data.compareTo

@Composable
fun NumericTextTransition(secondCount: String = "00", minuteCount: String = "00") {

    Row(
        modifier = Modifier
            .animateContentSize()
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        minuteCount
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
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }
            }

        Text(
            ":",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 60.sp
        )

        secondCount
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
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 60.sp,
                            fontWeight = FontWeight.SemiBold
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