package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalDateTime
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

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color(0xFF1C1C1E),
        contentColor = Color.White,
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
                    .fillMaxWidth().padding(vertical = 16.dp)
                    .background(Color(0xFF3A3A3C), RoundedCornerShape(12.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {


                EditFieldItem(
                    icon = sessionDetails.tagEmoji,
                    label = sessionDetails.tagName,
                    value = ""
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
                    value = editedStartTime.plusMinutes(editedDurationMinutes.toLong())
                        .format(DateTimeFormatter.ofPattern("HH:mm")),
                    isClickable = false
                )

                // Duration
                EditFieldItem(
                    label = "Duration",
                    value = formatDuration(editedDurationMinutes),
                    onClick = {

                    },
                    divider = false
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    val updated = sessionDetails.copy(
                        date = editedDate.atTime(editedStartTime),
                        startTime = editedDate.atTime(editedStartTime),
                        endTime = editedDate.atTime(editedStartTime)
                            .plusMinutes(editedDurationMinutes.toLong()),
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
    isClickable: Boolean = true,
    onClick: () -> Unit = {},
    useCard: Boolean = false, // <-- yeni parametre

) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable, onClick = onClick)
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
            Text(text = label, color = Color.LightGray, fontSize = 16.sp)
        }

        if (useCard) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(5.dp),
            ) {
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        } else {
            Text(
                text = value,
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            )
        }
    }
    if (divider) {
        Divider(
            color = Color.Gray.copy(alpha = 0.2f),
            thickness = 1.dp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DurationEditField(
    label: String,
    durationMinutes: Int,
    onDurationChange: (Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var hours by remember { mutableStateOf(durationMinutes / 60) }
    var minutes by remember { mutableStateOf(durationMinutes % 60) }

    EditFieldItem(
        label = label,
        value = formatDuration(durationMinutes),
        isClickable = true,
        onClick = { showDialog = true }
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor = Color(0xFF2C2C2E),
            title = {
                Text(
                    text = "Set Duration",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hours
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Hours", color = Color.Gray, fontSize = 14.sp)
                        OutlinedTextField(
                            value = hours.toString(),
                            onValueChange = {
                                hours = it.toIntOrNull()?.coerceIn(0, 23) ?: 0
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF30D158),
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }

                    Text(":", color = Color.White, fontSize = 24.sp)

                    // Minutes
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Minutes", color = Color.Gray, fontSize = 14.sp)
                        OutlinedTextField(
                            value = minutes.toString(),
                            onValueChange = {
                                minutes = it.toIntOrNull()?.coerceIn(0, 59) ?: 0
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFF30D158),
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDurationChange(hours * 60 + minutes)
                        showDialog = false
                    }
                ) {
                    Text("OK", color = Color(0xFF30D158))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel", color = Color.Gray)
                }
            }
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
            value = "26 Jul, 2025",
            useCard = true

        )

        // End Time (computed)
        EditFieldItem(
            label = "End Time",
            value = "13:02",
            isClickable = true
        )

        // Duration
        EditFieldItem(
            label = "Duration",
            value = "0 minutes",
            onClick = {

            },
            divider = false
        )
    }
}
