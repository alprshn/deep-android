package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.twotone.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kami_apps.deepwork.deep_work_app.data.util.parseTagColor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SessionDetails(
    val id: Int,
    val tagName: String,
    val tagColor: String,
    val tagEmoji: String,
    val date: LocalDateTime,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val duration: String // Already formatted like "45 min"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionDetailsBottomSheet(
    sessionDetails: SessionDetails,
    onDismiss: () -> Unit,
    onEdit: (SessionDetails) -> Unit = {},
    onDelete: (SessionDetails) -> Unit = {}
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        containerColor = Color(0xFF2C2C2E),
        contentColor = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .size(width = 32.dp, height = 4.dp)
                    .background(
                        Color.Gray.copy(alpha = 0.4f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Session Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            SessionDetailItem(
                emoji = sessionDetails.tagEmoji,
                label = if (sessionDetails.tagName == "No Tag") "No Tag" else sessionDetails.tagName,
                isTag = true,
            )

            // Date
            SessionDetailItem(
                icon = Icons.Default.CalendarMonth,
                label = "Date:",
                value = sessionDetails.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            )

            // Time Range
            SessionDetailItem(
                icon = Icons.Default.History,
                label = "Start:",
                value = sessionDetails.startTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                secondaryLabel = "End:",
                secondaryValue = sessionDetails.endTime.format(DateTimeFormatter.ofPattern("HH:mm"))
            )

            // Duration
            SessionDetailItem(
                icon = Icons.TwoTone.HourglassBottom,
                label = "Time:",
                value = sessionDetails.duration,
                isLast = true
            )

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Delete Button
                OutlinedButton(
                    onClick = {
                        onDelete(sessionDetails)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF453A),
                        containerColor = Color(0xFFFF453A).copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFF453A))
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(8.dp).size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete",fontSize = 18.sp)
                }

                // Edit Button
                Button(
                    onClick = {
                        onEdit(sessionDetails)
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF30D158),
                        containerColor = Color(0xFF30D158).copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.padding(8.dp).size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit",fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
private fun SessionDetailItem(
    icon: ImageVector? = null,
    emoji: String? = null,
    label: String,
    value: String? = null,
    secondaryLabel: String? = null,
    secondaryValue: String? = null,
    isTag: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 16.dp)
            .border(
                1.dp,
                Color.Gray,
                RoundedCornerShape(12.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF3A3A3C),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (emoji != null) {
                Text(
                    text = emoji,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(end = 12.dp)
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(30.dp)
                        .padding(end = 12.dp)
                )
            }

            // Content
            if (isTag) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = label,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            } else {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {


                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            value?.let {
                                Text(
                                    text = it,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }

                        if (secondaryLabel != null && secondaryValue != null) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = secondaryLabel,
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(end = 4.dp)

                                )
                                Text(
                                    text = secondaryValue,
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SessionDetailsBottomSheetPreview() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Delete Button
        OutlinedButton(
            onClick = {
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFFFF453A),
                containerColor = Color(0xFFFF453A).copy(alpha = 0.1f)
            ),
            border = BorderStroke(1.dp, Color(0xFFFF453A))
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.padding(8.dp).size(30.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Delete",fontSize = 18.sp)
        }

    }
}