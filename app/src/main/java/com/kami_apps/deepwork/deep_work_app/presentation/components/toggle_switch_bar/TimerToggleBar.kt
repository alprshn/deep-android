package com.kami_apps.deepwork.deep_work_app.presentation.components.toggle_switch_bar

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor

@Composable
fun TimerToggleBar(
    height: Dp,
    circleButtonPadding: Dp,
    circleBackgroundOnResource: String = "18402806360702976000",
    selectedState: Boolean,                       // ← Dışarıdan gelen seçili durum
    onCheckedChanged: (isOn: Boolean) -> Unit, // ← Dışarıdan tetiklenen callback
    toggleTimerUiState: Boolean
) {
//    var selectedState by remember { mutableStateOf(stateOn) }

    val buttonColor = parseTagColor(circleBackgroundOnResource)


    Row(
        modifier = Modifier
            .wrapContentSize() // İçeriğe göre genişlik ve yükseklik
            .height(height)
            .clip(RoundedCornerShape(height))
            .background(Color(0xFF1C1E22)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = if (toggleTimerUiState && selectedState == true) false else true,
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(height)
                    .padding(circleButtonPadding)
                    .clip(RoundedCornerShape(50))
                    .background(if (selectedState == false) buttonColor else Color(0xFF1C1E22))
                    .clickable {
                        onCheckedChanged(true)
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
                        contentDescription = stringResource(R.string.timer_icon_description),
                        tint = Color.White
                    )
                    Text("Timer", color = Color.White)
                }
            }
        }

        AnimatedVisibility(
            visible =  if (toggleTimerUiState && selectedState == false) false else true,
        ){
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(height)
                    .padding(circleButtonPadding)
                    .clip(RoundedCornerShape(50))
                    .background(if (selectedState == true) buttonColor else Color(0xFF1C1E22)) // Seçili duruma göre renk
                    .clickable {
                        onCheckedChanged(false)
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
                        contentDescription = stringResource(R.string.timer_icon_description),
                        tint = Color.White
                    )
                    Text("Stopwatch", color = Color.White)
                }
            }
        }

    }
}


@Preview
@Composable
fun TimerToggleBarPreview() {


}