package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import android.util.Log

val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

fun calculateDuration(start: LocalDateTime, end: LocalDateTime): String {
    val duration = java.time.Duration.between(start, end)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "< 1m"
    }
}

@Composable
fun BasicEvent(
    event: Event,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Log.d("BasicEvent", "Rendering event: ${event.name} at ${event.start} with color: ${event.color}")
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(event.color.copy(alpha = 0.2f), shape = RoundedCornerShape(10.dp))
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        onClick = onClick,
                        indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                        interactionSource = remember { MutableInteractionSource() }
                    )
                } else Modifier
            )
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VerticalDivider(
                color = event.color,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .clip(CircleShape),
                thickness = 4.dp,

                )
            Text(
                text = event.emoji ?: "📖",
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimary ,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        Text(
            text = calculateDuration(event.start, event.end),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

public val sampleEvents = listOf(
    // Monday Events
    Event(
        sessionId = 1,
        name = "Morning Standup",
        color = Color(0xFF4CAF50),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(9).withMinute(30),
        emoji = "⚽",
    ),
    Event(
        sessionId = 2,
        name = "Code Review Session",
        color = Color(0xFF2196F3),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(10).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(11).withMinute(30),
        emoji = "⚽"
    ),
    Event(
        sessionId = 3,
        name = "Lunch Break",
        color = Color(0xFFFF9800),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(12).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(13).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 4,
        name = "Feature Development",
        color = Color(0xFF9C27B0),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(14).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(17).withMinute(0),
        emoji = "⚽"
    ),

    // Tuesday Events
    Event(
        sessionId = 5,
        name = "Team Meeting",
        color = Color(0xFFE91E63),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(9).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(10).withMinute(30),
        emoji = "⚽"
    ),
    Event(
        sessionId = 6,
        name = "Client Call",
        color = Color(0xFF607D8B),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(11).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(12).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 7,
        name = "Design Workshop",
        color = Color(0xFFFF5722),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(14).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(16).withMinute(0),
        emoji = "⚽"
    ),

    // Wednesday Events
    Event(
        sessionId = 8,
        name = "Deep Work Session",
        color = Color(0xFF795548),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(8).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(11).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 9,
        name = "Architecture Review",
        color = Color(0xFF3F51B5),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(13).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(15).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 10,
        name = "1-on-1 with Manager",
        color = Color(0xFF009688),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(15).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(16).withMinute(30),
        emoji = "⚽"
    ),

    // Thursday Events
    Event(
        sessionId = 11,
        name = "Bug Triage",
        color = Color(0xFFF44336),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(10).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 12,
        name = "Learning Session",
        color = Color(0xFF00BCD4),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(10).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(12).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 13,
        name = "Sprint Planning",
        color = Color(0xFF8BC34A),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(14).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(16).withMinute(0),
        emoji = "⚽"
    ),

    // Friday Events
    Event(
        sessionId = 14,
        name = "Demo Preparation",
        color = Color(0xFFCDDC39),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(11).withMinute(0),
        emoji = "⚽"
    ),
    Event(
        sessionId = 15,
        name = "Sprint Demo",
        color = Color(0xFFFFEB3B),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(11).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(12).withMinute(30),
        emoji = "⚽"
    ),

)

class EventsProvider : PreviewParameterProvider<Event> {
    override val values = sampleEvents.asSequence()
}

@Preview(showBackground = true)
@Composable
fun EventPreview(
    @PreviewParameter(EventsProvider::class) event: Event,
) {
    BasicEvent(event, modifier = Modifier.sizeIn(maxHeight = 64.dp))
}

data class Event(
    val sessionId: Int = 0, // Add session ID
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val emoji: String? = null,
)