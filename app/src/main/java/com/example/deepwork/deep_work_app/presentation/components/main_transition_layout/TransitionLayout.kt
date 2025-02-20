import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deepwork.deep_work_app.presentation.components.circular_progress_indicator.CustomCircularProgressIndicator
import com.example.deepwork.deep_work_app.presentation.components.toggle_switch_bar.TimerToggleBar


data class Snack(
    val name: String,
    val description: String
)

private val listSnacks = listOf(
    Snack("Select a Tag", ""),
)

private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
fun AnimatedVisibilitySharedElementShortenedExample() {
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Progress Indicator - En üst katmanda ve merkezde
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CustomCircularProgressIndicator(
                    modifier = Modifier
                        .size(300.dp)
                        .background(Color.Transparent),
                    initialValue = 67,
                    primaryColor = Color.Blue,
                    secondaryColor = Color.DarkGray,
                    minValue = 0,
                    maxValue = 100,
                    circleRadiusStroke = 450f,
                    circleRadiusGradient = 270f,
                    onPositionChange = {}
                )
            }

            // Diğer içerikler
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 50.dp)
            ) {
                // Tag seçimi ve Timer Toggle Bar
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(listSnacks) { snack ->
                            AnimatedVisibility(
                                visible = snack != selectedSnack,
                                enter = fadeIn() + scaleIn(),
                                exit = fadeOut() + scaleOut(),
                                modifier = Modifier.animateItem()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .sharedBounds(
                                            sharedContentState = rememberSharedContentState(key = "${snack.name}-bounds"),
                                            animatedVisibilityScope = this,
                                            clipInOverlayDuringTransition = OverlayClip(
                                                shapeForSharedElement
                                            )
                                        )
                                        .background(Color.Transparent, shapeForSharedElement)
                                        .clip(shapeForSharedElement)
                                ) {
                                    SnackContents(
                                        snack = snack,
                                        modifier = Modifier.sharedElement(
                                            state = rememberSharedContentState(key = snack.name),
                                            animatedVisibilityScope = this@AnimatedVisibility
                                        ),
                                        onClick = {
                                            selectedSnack = snack
                                        },
                                        heightButton = 50,
                                        textColor = Color.White,
                                        emoji = "\uD83D\uDCCD"
                                    )
                                }
                            }
                        }
                    }

                    // Timer Toggle Bar - Tag seçiminin hemen altında
                    Spacer(modifier = Modifier.height(16.dp))
                    TimerToggleBar(
                        height = 50.dp,
                        circleButtonPadding = 4.dp,
                        circleBackgroundOnResource = Color(0xff5550e3),
                        circleBackgroundOffResource = Color.Black,
                        stateOn = 0,
                        stateOff = 1,
                        onCheckedChanged = {}
                    )
                }
            }
        }

        // Details overlay
        SnackEditDetails(
            snack = selectedSnack,
            onConfirmClick = {
                selectedSnack = null
            }
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SnackEditDetails(
    snack: Snack?,
    modifier: Modifier = Modifier,
    onConfirmClick: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = snack,
        transitionSpec = {
            (slideInVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOutQuart
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOutQuart
                )
            )) togetherWith (slideOutVertically(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOutQuart
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 500,
                    easing = EaseInOutQuart
                )
            ))
        },
        label = "SnackEditDetails"
    ) { targetSnack ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (targetSnack != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            onConfirmClick()
                        }
                        .background(Color.Black.copy(alpha = 0.6f))
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${targetSnack.name}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                        )
                        .background(Color.Black.copy(alpha = 0.6f), shapeForSharedElement)
                        .graphicsLayer {
                            alpha = 0.9f
                            shadowElevation = 5.dp.toPx()
                        }
                        .clip(shapeForSharedElement)
                        .animateEnterExit(
                            enter = slideInVertically(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = EaseInOutQuart
                                )
                            ),
                            exit = slideOutVertically(
                                animationSpec = tween(
                                    durationMillis = 500,
                                    easing = EaseInOutQuart
                                )
                            )
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // İçerik (Buton ve Başlık)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF28303B)),
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    Icons.Outlined.Add,
                                    contentDescription = "addButton",
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                            Text(
                                text = "Details",
                                color = Color(0xFFb6bcc6),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            Box(
                                modifier = Modifier
                                    .size(25.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF28303B))
                                    .clickable {
                                        onConfirmClick()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Clear,
                                    contentDescription = "closeButton",
                                    tint = Color.White, modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .padding(bottom = 25.dp)
                                    .sharedElement(
                                        state = rememberSharedContentState(key = targetSnack.name),
                                        animatedVisibilityScope = this@AnimatedContent,
                                    ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    "\uD83D\uDCCD", modifier = Modifier.padding(end = 10.dp),
                                    fontSize = 20.sp
                                )
                                Text("Select a Tag", color = Color.White, fontSize = 30.sp)
                            }
                            Icon(
                                Icons.Outlined.Tab,
                                contentDescription = "tagIcon",
                                tint = Color(0xFF5c626a),
                                modifier = Modifier.size(60.dp)
                            )
                            Text(
                                "No Tags Yet",
                                color = Color.White,
                                fontSize = 20.sp,
                                modifier = Modifier.padding(top = 15.dp)
                            )
                            Text(
                                "Tap + to add a tag",
                                color = Color(0xFF5c626a),
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 15.dp)
                            )

                        }

                    }
                }
            }
        }
    }
}

@Composable
fun SnackContents(
    snack: Snack,
    modifier: Modifier,
    onClick: () -> Unit,
    heightButton: Int,
    textColor: Color,
    emoji: String,
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(0.7f) // Genişliği sınırlandır
            .height(heightButton.dp)
            .clip(RoundedCornerShape(heightButton.dp))
            .background(Color(0xFF1C1E22))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onClick()
            },
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
                emoji, color = textColor, modifier = Modifier.padding(end = 5.dp)
            )
            Text(snack.name, color = textColor)
        }
    }
}