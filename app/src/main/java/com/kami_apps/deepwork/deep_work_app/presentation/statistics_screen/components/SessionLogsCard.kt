package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class SessionLog(
    val tagName: String,
    val tagEmoji: String,
    val date: String,
    val time: String,
    val duration: String
)

@Composable
fun SessionLogsCard(
    modifier: Modifier = Modifier,
    sessionLogs: List<SessionLog> = emptyList()
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    , onClick = {
            isExpanded = !isExpanded
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header - Always visible
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Session Logs",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (sessionLogs.isEmpty()) {
                        Text(
                            text = "No session logs available",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        sessionLogs.forEachIndexed { index, session ->
                            SessionLogItem(
                                session = session,
                                showDivider = index < sessionLogs.size - 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionLogItem(
    session: SessionLog,
    showDivider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.tagEmoji,
                    fontSize = 20.sp
                )
                Column {
                    Text(
                        text = session.tagName,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${session.date}, ${session.time}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            
            Text(
                text = session.duration,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }
        
        if (showDivider) {
            HorizontalDivider(
                color = Color.Gray.copy(alpha = 0.2f),
                thickness = 0.5.dp
            )
        }
    }
}

@Composable
@Preview
fun SessionLogsCardPreview() {
    val sampleLogs = listOf(
        SessionLog("Coding", "💻", "24.05.2025", "11:10", "1h 54m"),
        SessionLog("Coding", "💻", "20.05.2025", "17:35", "0m"),
        SessionLog("Coding", "💻", "19.05.2025", "10:45", "1h 24m"),
        SessionLog("Physics", "⚛️", "14.05.2025", "23:16", "0m"),
        SessionLog("Coding", "💻", "11.05.2025", "13:25", "5h 9m"),
        SessionLog("Coding", "💻", "10.05.2025", "15:42", "1h 56m"),
        SessionLog("Coding", "💻", "8.05.2025", "09:23", "8h 14m"),
        SessionLog("Coding", "💻", "6.05.2025", "10:41", "4h 42m"),
        SessionLog("Coding", "💻", "5.05.2025", "19:01", "0m")
    )
    
    SessionLogsCard(
        sessionLogs = sampleLogs
    )
} 