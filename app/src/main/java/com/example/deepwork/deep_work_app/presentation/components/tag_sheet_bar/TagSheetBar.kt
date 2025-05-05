package com.example.deepwork.deep_work_app.presentation.components.tag_sheet_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.EmojiViewItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBottomSheet(
    showBottomSheet: Boolean,
) {
    val listState = rememberLazyListState()

    var selectedEmoji by remember { mutableStateOf("😊") } // Başlangıç emojisi
    var showEmojiPicker by remember { mutableStateOf(false) }
    // Renk listesi
    val colors = listOf(
        Color.Transparent,
        Color(255, 69, 58, 255),
        Color(255, 159, 10, 255),
        Color(255, 214, 10, 255),
        Color(48, 209, 88, 255),
        Color(99, 230, 226, 255),
        Color(64, 200, 224, 255),
        Color(100, 210, 255, 255),
        Color(10, 132, 255, 255),
        Color(94, 92, 230, 255),
        Color(191, 90, 242, 255),
        Color(255, 55, 95, 255),
        Color(172, 142, 104, 255),
        Color.Transparent,
    )

    ModalBottomSheet(
        onDismissRequest = { showBottomSheet },
        containerColor = Color(0xff1d1a1f),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xff1d1a1f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Add New Tag", color = Color.White)
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(25.dp))
                    .background(Color.Blue.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .border(shape = RoundedCornerShape(25.dp), width = 2.dp, color = Color.Blue),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    text = "\uD83D\uDE0A",
                    modifier = Modifier
                        .padding(vertical = 25.dp)
                        .padding(start = 20.dp),
                    fontSize = 26.sp
                )
                Text(
                    text = "New Tag",
                    modifier = Modifier
                        .padding(vertical = 25.dp)
                        .padding(start = 10.dp),
                    fontSize = 22.sp,
                    color = Color.White
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                        .background(Color.Green),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = ":D", modifier = Modifier.clickable {
                        showEmojiPicker = true
                    })
                }
                OutlinedTextField(
                    value = "", onValueChange = {},
                    Modifier
                        .padding(10.dp)
                        .height(30.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(7.dp),
                    placeholder = { Text("Enter Tag Name") },
                    colors = TextFieldDefaults.colors(unfocusedContainerColor = Color.Gray)
                )
            }

            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 30.dp,
                    vertical = 40.dp
                ) // Kart genişliğinin yarısı
            ) {
                itemsIndexed(colors) { index, color ->
                    Box(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .border(1.dp, Color.White, shape = CircleShape)
                            .size(30.dp)
                            .background(color)
                            .clickable {

                            }
                    )
                }
            }

        }

        // Emoji picker dialogu
        if (showEmojiPicker) {
            AlertDialog(
                containerColor = Color(0xFF201e1e),
                onDismissRequest = { showEmojiPicker = false },
                text = {
                    AndroidView(
                        factory = { ctx ->
                            EmojiPickerView(ctx).apply {
                                setOnEmojiPickedListener { emojiViewItem: EmojiViewItem ->
                                    selectedEmoji = emojiViewItem.emoji
                                    showEmojiPicker = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                },
                confirmButton = {}
            )
        }

    }
}


@Preview
@Composable
fun TagBottomSheetPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff1d1a1f)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add New Tag", color = Color.White)
        Row(
            modifier = Modifier
                .padding(10.dp)
                .clip(shape = RoundedCornerShape(25.dp))
                .background(Color.Blue.copy(alpha = 0.2f))
                .fillMaxWidth()
                .border(shape = RoundedCornerShape(25.dp), width = 2.dp, color = Color.Blue),
            verticalAlignment = Alignment.CenterVertically,
        )
        {
            Text(
                text = "\uD83D\uDE0A",
                modifier = Modifier
                    .padding(vertical = 25.dp)
                    .padding(start = 20.dp),
                fontSize = 26.sp
            )
            Text(
                text = "New Tag",
                modifier = Modifier
                    .padding(vertical = 25.dp)
                    .padding(start = 10.dp),
                fontSize = 22.sp,
                color = Color.White
            )
        }



        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(10.dp))
                    .size(45.dp)
                    .background(Color(0xff29272c))
                    .clickable {

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "\uD83D\uDE0A")
            }
            OutlinedTextField(
                value = "", onValueChange = {},
                Modifier
                    .padding(10.dp)
                    .height(45.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                placeholder = { Text("Enter Tag Name", color = Color.Black) },
                colors = TextFieldDefaults.colors(unfocusedContainerColor = Color(0xff29272c)),
            )
        }

        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .border(1.dp, Color.White, shape = CircleShape)
                    .size(30.dp)
                    .background(Color.Blue)
                    .clickable {

                    })
        }

    }
}
