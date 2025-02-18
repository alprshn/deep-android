package com.example.deepwork.deep_work_app.presentation.components.main_transition_layout

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Tab
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailsTagButton(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    textTagTitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        with(sharedTransitionScope) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(500.dp)
                    .align(Alignment.BottomCenter) // Navigation Bar'ın altında kalacak şekilde hizalandı
                    .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                    )
            ) {
                // **ARKA PLAN (BULANIK)**
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.1f))
                        .blur(radius = 25.dp)
                        .graphicsLayer {
                            alpha = 0.8f
                            shadowElevation = 5.dp.toPx()
                        }
                )

                // **ÖN PLAN (BULANIK)**
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.05f))
                        .blur(radius = 25.dp) // Ön plan da bulanık
                )
                // **İÇERİK (BULANIK OLMAYACAK)**
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
                            text = textTagTitle,
                            color = Color(0xFFb6bcc6),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFF28303B))
                                .clickable { onBack() },
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
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 15.dp).padding(bottom = 25.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "❌", modifier = Modifier.padding(end = 10.dp),
                                fontSize = 20.sp
                            )
                            Text(textTagTitle, color = Color.White, fontSize = 30.sp)
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



@Composable
@Preview
fun DetailsTagButton() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(topEnd = 16.dp, topStart = 16.dp))
        ) {
            // Arka Plan (Bulanık)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.1f))
                    .blur(radius = 25.dp)
                    .graphicsLayer {
                        alpha = 0.8f
                        shadowElevation = 5.dp.toPx()
                    }
            )

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
                            .background(Color(0xFF28303B)),
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
                    horizontalAlignment = Alignment.CenterHorizontally
                    , verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 15.dp).padding(bottom = 25.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "❌", modifier = Modifier.padding(end = 10.dp),
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
                    Text("No Tags Yet", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(top = 15.dp))
                    Text("Tap + to add a tag", color =  Color(0xFF5c626a), fontSize = 15.sp, modifier = Modifier.padding(top = 15.dp))

                }

            }
        }

    }
}
