package com.kami_apps.deepwork.deep.presentation.components.tag_sheet_bar

import android.util.Log
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.EmojiViewItem
import com.kami_apps.deepwork.ui.theme.darkColorsList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagBottomSheet(
    title: String = "Add New Tag", // ← burada varsayılanı ver
    addTagDismiss: () -> Unit = {},
    selectedEmoji: String,
    showEmojiPicker: Boolean,
    tagTextField: String,
    tagName: String,
    selectedIndex: Int,
    tagColor: Color,
    emojiPickerBox: () -> Unit,
    textFieldValueChange: (String) -> Unit,
    clickColorBox: (Int, Color) -> Unit,
    setOnEmojiPickedListener: (EmojiViewItem) -> Unit,
    onDismissRequestAlertDialog: () -> Unit,
    addTag: () -> Unit
) {
    val listState = rememberLazyListState()
    val tagGeneratorSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false, // Sheet'in kay  dırılmasını engeller
        confirmValueChange = { newValue ->
            newValue != SheetValue.Expanded
        }
    )

    ModalBottomSheet(
        onDismissRequest = addTagDismiss,
        sheetState = tagGeneratorSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        dragHandle = null,

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(vertical = 5.dp))
            Row(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(shape = RoundedCornerShape(25.dp))
                    .background(tagColor.copy(alpha = 0.2f))
                    .fillMaxWidth()
                    .border(shape = RoundedCornerShape(25.dp), width = 2.dp, color = tagColor),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                Text(
                    text = selectedEmoji,
                    modifier = Modifier
                        .padding(vertical = 25.dp)
                        .padding(start = 20.dp),
                    fontSize = 26.sp
                )
                Text(
                    text = tagName.trim().ifBlank { "New Tag" },
                    modifier = Modifier
                        .padding(vertical = 25.dp)
                        .padding(start = 10.dp),
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(10.dp))
                        .size(55.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                        .clickable {
                            emojiPickerBox()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = selectedEmoji, fontSize = 24.sp)
                }
                OutlinedTextField(
                    value = tagTextField, onValueChange = {

                        val normalized = it
                            .replace(Regex("^\\s+"), "")   // baştaki boşlukları sil
                            .replace(Regex("\\s{2,}"), " ") // art arda boşluk → tek boşluk

                        textFieldValueChange(normalized)
                    },
                    Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("Enter Tag Name", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        // arka plan
                        unfocusedContainerColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.onPrimary,
                        // kenarlıkları şeffaf yap
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    textStyle = TextStyle(fontSize = 18.sp),
                    singleLine = true
                )
            }


            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(
                    horizontal = 25.dp,
                    vertical = 30.dp
                ), // Kart genişliğinin yarısı
                horizontalArrangement = Arrangement.spacedBy(6.dp)   // her öğe arası eşit 12 dp

            ) {
                itemsIndexed(darkColorsList) { index, color ->

                    val borderStroke = if (selectedIndex == index)
                        BorderStroke(2.dp, MaterialTheme.colorScheme.onPrimary)
                    else
                        BorderStroke(1.dp, Color.Transparent)

                    Box(
                        modifier = Modifier
                            .clip(shape = CircleShape)
                            .size(32.dp)
                            .background(color)
                            .border(borderStroke, shape = CircleShape)
                            .clickable {
                                clickColorBox(index, color)
                                Log.e("TAG Color", "TagBottomSheet: $tagColor")
                                Log.e("Index Number: $index", "TagBottomSheet: $color")
                            }
                    )
                }
            }

            Button(
                onClick = addTag,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(20.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (tagTextField.isNotBlank()) tagColor else Color.Gray,
                    contentColor = Color.White
                )
            ) { Text("Save", color = Color.White, fontSize = 20.sp) }

        }

        // Emoji picker dialogu
        if (showEmojiPicker) {
            AlertDialog(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                onDismissRequest = onDismissRequestAlertDialog,
                text = {
                    AndroidView(
                        factory = { ctx ->
                            EmojiPickerView(ctx).apply {
                                setOnEmojiPickedListener(setOnEmojiPickedListener)
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
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xff1d1a1f)),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = "Add New Tag", color = Color.White)
//        Row(
//            modifier = Modifier
//                .padding(10.dp)
//                .clip(shape = RoundedCornerShape(25.dp))
//                .background(Color(0xff0f79fe).copy(alpha = 0.2f))
//                .fillMaxWidth()
//                .border(shape = RoundedCornerShape(25.dp), width = 2.dp, color = Color.Blue),
//            verticalAlignment = Alignment.CenterVertically,
//        )
//        {
//            Text(
//                text = "\uD83D\uDE0A",
//                modifier = Modifier
//                    .padding(vertical = 25.dp)
//                    .padding(start = 20.dp),
//                fontSize = 26.sp
//            )
//            Text(
//                text = "New Tag",
//                modifier = Modifier
//                    .padding(vertical = 25.dp)
//                    .padding(start = 10.dp),
//                fontSize = 22.sp,
//                color = Color.White
//            )
//        }
//
//
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp)
//        ) {
//            Box(
//                modifier = Modifier
//                    .clip(shape = RoundedCornerShape(10.dp))
//                    .size(55.dp)
//                    .background(Color(0xff29272c))
//                    .clickable {
//
//                    },
//                contentAlignment = Alignment.Center
//            ) {
//                Text(text = "\uD83D\uDE0A", fontSize = 24.sp)
//            }
//            OutlinedTextField(
//                value = "", onValueChange = {},
//                Modifier
//                    .padding(start = 10.dp)
//                    .fillMaxWidth()
//                    .height(55.dp),
//                shape = RoundedCornerShape(10.dp),
//                placeholder = { Text("Enter Tag Name", color = Color.Gray) },
//                colors = TextFieldDefaults.colors(
//                    // arka plan
//                    unfocusedContainerColor = Color(0xFF29272C),
//                    focusedContainerColor = Color(0xFF29272C),
//                    focusedTextColor = Color.White,
//                    unfocusedTextColor = Color.White,
//
//                    // kenarlıkları şeffaf yap
//                    focusedIndicatorColor = Color.Transparent,
//                    unfocusedIndicatorColor = Color.Transparent,
//                    disabledIndicatorColor = Color.Transparent,
//                    errorIndicatorColor = Color.Transparent,
//                ),
//                textStyle = TextStyle(fontSize = 18.sp)
//            )
//        }
//
//        Row(
//            modifier = Modifier
//                .padding(10.dp)
//                .fillMaxWidth()
//        ) {
//            Box(
//                modifier = Modifier
//                    .clip(shape = CircleShape)
//                    .border(1.dp, Color.White, shape = CircleShape)
//                    .size(30.dp)
//                    .background(Color.Blue)
//                    .clickable {
//
//                    })
//        }
//
//
//        Button(
//            onClick = {
//            },
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(10.dp)
//                .height(55.dp),
//            shape = RoundedCornerShape(20.dp),
//            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
//                containerColor = Color.Gray,
//                contentColor = Color.White
//            )
//        ) { Text("Save", color = Color.White, fontSize = 20.sp) }
//
//    }

}
