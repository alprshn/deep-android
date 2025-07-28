package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor
import com.kami_apps.deepwork.deep_work_app.presentation.components.tag_sheet_bar.TagBottomSheet
import com.kami_apps.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchViewModel
import androidx.compose.foundation.border
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTagsScreen(
    navController: NavHostController? = null,
    viewModel: StopwatchViewModel = hiltViewModel()
) {
    val allTags by viewModel.allTagList.collectAsState()

    var showBottomSheet by remember { mutableStateOf(false) }
    var editingTag by remember { mutableStateOf<Tags?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var tagToDelete by remember { mutableStateOf<Tags?>(null) }

    // For bottom sheet state
    var selectedEmoji by remember { mutableStateOf("😊") }
    var showEmojiPicker by remember { mutableStateOf(false) }
    var tagTextField by remember { mutableStateOf("") }
    var selectedColorIndex by remember { mutableStateOf(7) }
    var tagColor by remember { mutableStateOf(com.kami_apps.deepwork.ui.theme.TagColors[selectedColorIndex]) }

    LaunchedEffect(Unit) {
        viewModel.getAllTag()
    }

    // Set fields when editing existing tag
    LaunchedEffect(editingTag) {
        editingTag?.let { tag ->
            tagTextField = tag.tagName
            selectedEmoji = tag.tagEmoji
            // Find color index from tag color
            val colorValue = parseTagColor(tag.tagColor).value
            selectedColorIndex = com.kami_apps.deepwork.ui.theme.TagColors.indexOfFirst {
                it.value == colorValue
            }.takeIf { it >= 0 } ?: 7
            tagColor = com.kami_apps.deepwork.ui.theme.TagColors[selectedColorIndex]
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController?.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                "Manage Tags",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tags List
        if (allTags.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tags available to edit.\nCreate tags from the Timer screen first.",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allTags) { tag ->
                    TagItem(
                        tag = tag,
                        onEditClick = {
                            editingTag = tag
                            showBottomSheet = true
                        },
                        onDeleteClick = {
                            tagToDelete = tag
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && tagToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                tagToDelete = null
            },
            title = {
                Text(
                    text = "Delete Tag",
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${tagToDelete?.tagName}'? This action cannot be undone.",
                    color = Color.Gray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        tagToDelete?.let { viewModel.deleteTag(it) }
                        showDeleteDialog = false
                        tagToDelete = null
                    }
                ) {
                    Text(
                        text = "Delete",
                        color = Color.Red
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        tagToDelete = null
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = Color.White
                    )
                }
            },
            containerColor = Color(0xFF1C1C1E)
        )
    }

    // Tag Edit Bottom Sheet
    if (showBottomSheet && editingTag != null) {
        TagBottomSheet(
            title = "Edit Tag",
            addTagDismiss = {
                showBottomSheet = false
                editingTag = null
            },
            selectedEmoji = selectedEmoji,
            showEmojiPicker = showEmojiPicker,
            tagTextField = tagTextField,
            tagName = tagTextField,
            selectedIndex = selectedColorIndex,
            tagColor = tagColor,
            emojiPickerBox = { showEmojiPicker = true },
            textFieldValueChange = { tagTextField = it },
            clickColorBox = { index, color ->
                selectedColorIndex = index
                tagColor = color
            },
            setOnEmojiPickedListener = { emojiViewItem ->
                selectedEmoji = emojiViewItem.emoji
                showEmojiPicker = false
            },
            onDismissRequestAlertDialog = { showEmojiPicker = false },
            addTag = {
                if (tagTextField.isNotBlank()) {
                    editingTag?.let { existing ->
                        // Edit existing tag
                        val updatedTag = existing.copy(
                            tagName = tagTextField,
                            tagColor = tagColor.value.toString(),
                            tagEmoji = selectedEmoji
                        )
                        viewModel.editTag(updatedTag)
                    }
                    showBottomSheet = false
                    editingTag = null
                }
            }
        )
    }
}

@Composable
private fun TagItem(
    tag: Tags,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = parseTagColor(tag.tagColor).copy(alpha = 0.15f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            VerticalDivider(
                color = parseTagColor(tag.tagColor),
                modifier = Modifier
                    .height(32.dp) // ← görünür hale getirir
                    .clip(CircleShape),
                thickness = 4.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tag Emoji
            Text(
                text = tag.tagEmoji.takeIf { it.isNotBlank() } ?: "📖",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tag Name
            Text(
                text = tag.tagName,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )


            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // kare ve yuvarlatılmış köşe
                    .background(parseTagColor(tag.tagColor).copy(alpha = 0.15f))
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Tag",
                    tint = parseTagColor(tag.tagColor),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Delete Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // kare ve yuvarlatılmış köşe
                    .background(Color(0XFFda4844).copy(alpha = 0.30f))
                    .clickable { onDeleteClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Tag",
                    tint = Color(0XFFda4844),
                    modifier = Modifier.size(24.dp)
                )
            }


        }
    }
}


@Composable
@Preview
fun TagItemPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Red.copy(alpha = 0.20f)
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            VerticalDivider(
                color = Color.Red,
                modifier = Modifier
                    .height(32.dp) // ← görünür hale getirir
                    .clip(CircleShape),
                thickness = 4.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tag Emoji
            Text(
                text = "📖",
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Tag Name
            Text(
                text = "Deneme",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )



            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // kare ve yuvarlatılmış köşe
                    .background(Color.Red.copy(alpha = 0.15f))
                    .clickable { /* tıklanma aksiyonu */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Tag",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Delete Button
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp)) // kare ve yuvarlatılmış köşe
                    .background(Color.Red.copy(alpha = 0.15f))
                    .clickable { /* tıklanma aksiyonu */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Tag",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
