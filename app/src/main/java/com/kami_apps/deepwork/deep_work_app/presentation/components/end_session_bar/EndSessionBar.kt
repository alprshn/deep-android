package com.kami_apps.deepwork.deep_work_app.presentation.components.end_session_bar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EndSessionBar(
    endSession: () -> Unit,
    keepGoingButtonColor: String,
    onClickKeepGoing: () -> Unit = {},
) {

    var endThisSessionVisibility by remember { mutableStateOf(false) }

    val endSessionBarSheetState = rememberModalBottomSheetState(
        // Başta açık dursun
        skipPartiallyExpanded = true,               // half‑way yok
        confirmValueChange = { target ->
            target == SheetValue.Expanded // başka değere geçişi veto
        }
    )



    LaunchedEffect(Unit) {
        delay(
            1000
        )
        endThisSessionVisibility = true
    }

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = endSessionBarSheetState,
        containerColor = Color(0xFF1C1E22).copy(alpha = 0.9f),
        dragHandle = null,

        ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .height(300.dp)
                .animateContentSize(               // boy değişimini yavaşlat
                    animationSpec = tween(
                        durationMillis = 500,
                        easing = FastOutSlowInEasing
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Are you sure you want to end this session?",
                color = Color.White,
                fontSize = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            val buttonColor = parseTagColor(keepGoingButtonColor)

            Column(
                modifier = Modifier
                    .animateContentSize(               // boy değişimini yavaşlat
                        animationSpec = tween(
                            durationMillis = 500,
                            easing = FastOutSlowInEasing
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = onClickKeepGoing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = buttonColor,
                        contentColor = Color.White
                    )
                ) { Text("Keep going!", color = Color.White, fontSize = 20.sp) }

                AnimatedVisibility(
                    visible = endThisSessionVisibility,
                    enter = fadeIn(tween(durationMillis = 500)),
                    exit = fadeOut(tween(durationMillis = 500))
                ) {

                    Button(
                        onClick = endSession,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(60.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                    ) { Text("End this Session", color = Color.White, fontSize = 20.sp) }
                }
            }
        }
    }


}


@Preview
@Composable
fun EndSessionBarPreview() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff1d1a1f))
            .padding(top = 15.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Are you sure you want to end this session?",
            color = Color.White,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            )
        ) { Text("Keep going!", color = Color.White, fontSize = 20.sp) }

        Button(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(60.dp),
            shape = RoundedCornerShape(15.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.Gray,
                contentColor = Color.White
            ),
        ) { Text("End this Session", color = Color.White, fontSize = 20.sp) }

    }

}