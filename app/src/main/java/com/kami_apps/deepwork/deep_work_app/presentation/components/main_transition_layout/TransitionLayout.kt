import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Sell
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.Snack


private val shapeForSharedElement = RoundedCornerShape(48.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SnackEditDetails(
    snack: Snack?,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit,
    visibleTagItems: Boolean,
    addTagClick: () -> Unit = {},
    tagContent: List<Tags>,
    onTagClick: (Tags) -> Unit,
    selectedTagEmoji: String = "\uD83D\uDCCD",
    selectedTagText: String = "Select a Tag",
) {

    AnimatedContent(
        modifier = modifier,
        targetState = snack,
        label = "SnackEditDetails",
        transitionSpec = {
            (fadeIn(
                tween(
                    1000,
                    easing = EaseInOutQuart
                )
            ) + slideInVertically(tween(1000))) togetherWith
                    (fadeOut(
                        tween(
                            1000,
                            easing = EaseInOutQuart
                        )
                    ) + slideOutVertically(tween(1000)))
        }

    ) { targetSnack ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (targetSnack != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${targetSnack.name}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            boundsTransform = { _, _ ->
                                tween(durationMillis = 700, easing = EaseInOutQuart)
                            },
                            clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement),

                            )
                        .background(Color(0xFF1C1E22).copy(alpha = 0.9f), shapeForSharedElement)
                        .graphicsLayer {
                            alpha = 0.9f
                            shadowElevation = 5.dp.toPx()
                        }
                        .clip(shapeForSharedElement),
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
                                .padding(horizontal = 20.dp).padding(bottom = 10.dp, top = 20.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF28303B))
                                    .clickable {
                                        addTagClick()
                                    },
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    Icons.Outlined.Add,
                                    contentDescription = "addButton",
                                    tint = Color.White, modifier = Modifier
                                        .size(20.dp)
                                )
                            }
                            Text(
                                text = "Selected Tag",
                                color = Color(0xFFb6bcc6),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                fontSize = 20.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(Color(0xFF28303B))
                                    .clickable {
                                        onCloseClick()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Clear,
                                    contentDescription = "closeButton",
                                    tint = Color.White, modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .sharedElement(
                                    sharedContentState = rememberSharedContentState(key = targetSnack.name),
                                    animatedVisibilityScope = this@AnimatedContent,
                                    boundsTransform = { _, _ ->
                                        tween(durationMillis = 700, easing = EaseInOutQuart)
                                    },
                                    placeHolderSize = SharedTransitionScope.PlaceHolderSize.contentSize,
                                    )
                        ) {

                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 15.dp)
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${targetSnack.name}-text"),
                                        animatedVisibilityScope = this@AnimatedContent,
                                        boundsTransform = { _, _ ->
                                            tween(durationMillis = 700, easing = EaseInOutQuart)
                                        },
                                    ),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedTagEmoji,
                                    modifier = Modifier.padding(end = 10.dp),
                                    fontSize = 28.sp
                                )
                                Text(
                                    text = selectedTagText,
                                    color = Color.White,
                                    fontSize = 38.sp,
                                    modifier = Modifier
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 10.dp)
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {

                                AnimatedVisibility(
                                    visible = visibleTagItems,
                                    enter = fadeIn(
                                        tween(
                                            600,
                                            easing = EaseInOutCubic
                                        )
                                    ) + slideInVertically(tween(600)),
                                ) {
                                    if (tagContent.isEmpty()) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Icon(
                                                Icons.Outlined.Sell,
                                                contentDescription = "tagIcon",
                                                tint = Color(0xFF5c626a),
                                                modifier = Modifier
                                                    .size(60.dp)
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
                                                fontSize = 18.sp,
                                                modifier = Modifier.padding(top = 15.dp)
                                            )
                                        }
                                    } else {
                                        LazyColumn(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 40.dp).padding(bottom = 40.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            items(tagContent.size) { tag ->
                                                val tags = tagContent[tag]
                                                tags.tagId
                                                val tagColor = parseTagColor(tags.tagColor)

                                                Row(
                                                    modifier = Modifier
                                                        .padding(
                                                            horizontal = 30.dp,
                                                            vertical = 5.dp
                                                        )
                                                        .clip(shape = RoundedCornerShape(15.dp))
                                                        .background(tagColor.copy(alpha = 0.35f))
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onTagClick(tags)
                                                        },
                                                    verticalAlignment = Alignment.CenterVertically,
                                                )
                                                {
                                                    Log.e(
                                                        "TAG Color",
                                                        "SnackEditDetails Color: ${tags.tagColor}"
                                                    )
                                                    Text(
                                                        text = tags.tagEmoji,
                                                        modifier = Modifier
                                                            .padding(vertical = 13.dp)
                                                            .padding(start = 20.dp),
                                                        fontSize = 20.sp
                                                    )
                                                    Text(
                                                        text = tags.tagName,
                                                        modifier = Modifier
                                                            .padding(vertical = 13.dp)
                                                            .padding(start = 10.dp),
                                                        fontSize = 18.sp,
                                                        color = tagColor,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }

                                }
                            }
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
    modifierText: Modifier
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .wrapContentWidth() // Genişliği sınırlandır
            .height(heightButton.dp)
            .clip(RoundedCornerShape(heightButton.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Row(
            modifierText
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


@Preview
@Composable
fun SnackContentsPreview() {
    Column(
        modifier = Modifier.height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF28303B))
                    .clickable {

                    },
                contentAlignment = Alignment.Center

            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "addButton",
                    tint = Color.White, modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Selected Tag",
                color = Color(0xFFb6bcc6),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterVertically),
                fontSize = 20.sp
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF28303B))
                    .clickable {
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Clear,
                    contentDescription = "closeButton",
                    tint = Color.White, modifier = Modifier.size(20.dp)
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            modifier = Modifier
                .fillMaxSize(),
        ) {

            Row(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "\uD83D\uDCCD", modifier = Modifier.padding(end = 10.dp),
                    fontSize = 28.sp
                )
                Text(
                    "Select a Tag",
                    color = Color.White,
                    fontSize = 38.sp,
                    modifier = Modifier
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        Icons.Outlined.Sell,
                        contentDescription = "tagIcon",
                        tint = Color(0xFF5c626a),
                        modifier = Modifier
                            .size(60.dp)
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
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 15.dp)
                    )
                }

            }
        }
    }
}

