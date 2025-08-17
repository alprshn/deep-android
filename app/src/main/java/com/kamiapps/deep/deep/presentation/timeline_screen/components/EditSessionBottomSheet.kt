package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kamiapps.deep.deep.data.util.parseTagColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSessionBottomSheet(
    sessionDetails: SessionDetails,
    onDismiss: () -> Unit,
    onSave: (SessionDetails) -> Unit = {},
    onDatePicker: (LocalDate, (LocalDate) -> Unit) -> Unit = { _, _ -> },
    onTimePicker: (LocalTime, (LocalTime) -> Unit) -> Unit = { _, _ -> }
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var editedDate by remember { mutableStateOf(sessionDetails.date.toLocalDate()) }
    var editedStartTime by remember { mutableStateOf(sessionDetails.startTime.toLocalTime()) }
    var editedDurationMinutes by remember { mutableStateOf(parseDurationToMinutes(sessionDetails.duration)) }
    var durationText by remember { mutableStateOf(editedDurationMinutes.toString()) }

    // Calculate end time whenever start time or duration changes
    val editedEndTime = remember(editedStartTime, editedDurationMinutes) {
        editedStartTime.plusMinutes(editedDurationMinutes.toLong())
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        dragHandle = null // ✅ Bu satırı ekle, çizgiyi kaldırır

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                        RoundedCornerShape(12.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {


                EditFieldItem(
                    icon = sessionDetails.tagEmoji,
                    label = sessionDetails.tagName,
                    value = "",
                    tagNameBar = true,
                    tagNameColor = parseTagColor(sessionDetails.tagColor)
                )

                // Date row
                EditFieldItem(
                    label = "Date",
                    value = editedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                    onClick = {
                        onDatePicker(editedDate) { newDate -> editedDate = newDate }
                    },
                    useCard = true
                )

                // Time row
                EditFieldItem(
                    label = "Start Time",
                    value = editedStartTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                    onClick = {
                        onTimePicker(editedStartTime) { newTime -> editedStartTime = newTime }
                    },
                    useCard = true

                )

                // End Time (computed)
                EditFieldItem(
                    label = "End Time",
                    value = editedEndTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                )

                // Duration
                SessionDetailTextField(
                    value = durationText,
                    onValueChange = { newText ->
                        durationText = newText
                        // Parse as integer directly if it's a number
                        editedDurationMinutes = newText.toIntOrNull() ?: editedDurationMinutes
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    val updated = sessionDetails.copy(
                        date = editedDate.atTime(editedStartTime),
                        startTime = editedDate.atTime(editedStartTime),
                        endTime = editedDate.atTime(editedEndTime),
                        duration = formatDuration(editedDurationMinutes)
                    )
                    onSave(updated)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(
                        1.dp,
                        Color(0xFF30D158),
                        RoundedCornerShape(24.dp)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF30D158).copy(alpha = 0.1f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF30D158),
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    "Save Changes", color = Color(0xFF30D158),
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun EditFieldItem(
    icon: String? = null,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    divider: Boolean = true,
    onClick: () -> Unit = {},
    useCard: Boolean = false, // <-- yeni parametre
    tagNameBar: Boolean = false,
    tagNameColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                if (tagNameBar) tagNameColor.copy(alpha = 0.1f) else Color.Transparent,
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                )
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Text(
                    text = it,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
            Text(text = label, color = if (tagNameBar) tagNameColor else MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
        }

        if (useCard) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
                shape = RoundedCornerShape(8.dp),
                onClick = onClick
            ) {
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)

                )
            }
        } else {
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)

            )
        }
    }
    if (divider) {
        Divider(
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


private fun parseDurationToMinutes(duration: String): Int {
    return try {
        when {
            duration.contains("h") && duration.contains("m") -> {
                val parts = duration.split("h", "m")
                val hours = parts[0].trim().toInt()
                val minutes = parts[1].trim().toInt()
                hours * 60 + minutes
            }

            duration.contains("h") -> {
                duration.replace("h", "").trim().toInt() * 60
            }

            duration.contains("min") -> {
                duration.replace("min", "").trim().toInt()
            }

            duration.contains("m") -> {
                duration.replace("m", "").trim().toInt()
            }

            else -> {
                duration.toIntOrNull() ?: 0
            }
        }
    } catch (e: Exception) {
        0
    }
}

private fun formatDuration(minutes: Int): String {
    return when {
        minutes >= 60 -> {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes > 0) "${hours}h ${remainingMinutes}m" else "${hours}h"
        }

        minutes > 0 -> "${minutes} min"
        else -> "0 min"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun EditSessionBottomSheetSimplifiedPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .background(Color(0xFF3A3A3C), RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Tag row
        EditFieldItem(
            icon = "\uD83D\uDCD3",
            label = "No Tag",
            value = ""
        )

        // Date row
        EditFieldItem(
            label = "Date",
            value = "26 Jul, 2025",
            useCard = true

        )

        // Time row
        EditFieldItem(
            label = "Start Time",
            value = "12:05",
            useCard = true

        )

        // End Time (computed)
        EditFieldItem(
            label = "End Time",
            value = "13:02",
        )
        SessionDetailTextField(
            value = "0",
            onValueChange = {}
        )

    }
}


@Composable
fun SessionDetailTextField(
    value: String = "0",
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Duration",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange, // Bu callback'i düzgün kullan
                modifier = Modifier
                    .width(80.dp)
                    .height(32.dp)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterEnd // 👈 sağa hizalanmış
                    ) {
                        innerTextField()
                    }
                }
            )

            Text(
                text = "minutes",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
    }
}