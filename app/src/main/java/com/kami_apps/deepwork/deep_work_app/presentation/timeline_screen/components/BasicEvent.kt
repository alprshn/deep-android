package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
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

val EventTimeFormatter = DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun BasicEvent(
    event: Event,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(end = 2.dp, bottom = 2.dp)
            .background(event.color.copy(alpha = 0.35f), shape = RoundedCornerShape(10.dp))
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
                text = "⚽",
                style = MaterialTheme.typography.bodyLarge,
            )

            Text(
                text = event.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color.White ,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        Text(
            text = "30 min",
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
        name = "Morning Standup",
        color = Color(0xFF4CAF50),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(9).withMinute(30),
        description = "Daily team standup meeting to discuss progress and blockers."
    ),
    Event(
        name = "Code Review Session",
        color = Color(0xFF2196F3),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(10).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(11).withMinute(30),
        description = "Review pull requests and discuss code improvements."
    ),
    Event(
        name = "Lunch Break",
        color = Color(0xFFFF9800),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(12).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(13).withMinute(0),
        description = "Lunch time!"
    ),
    Event(
        name = "Feature Development",
        color = Color(0xFF9C27B0),
        start = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(14).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).withHour(17).withMinute(0),
        description = "Work on new calendar integration feature."
    ),

    // Tuesday Events
    Event(
        name = "Team Meeting",
        color = Color(0xFFE91E63),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(9).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(10).withMinute(30),
        description = "Weekly team sync meeting."
    ),
    Event(
        name = "Client Call",
        color = Color(0xFF607D8B),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(11).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(12).withMinute(0),
        description = "Discuss project requirements with client."
    ),
    Event(
        name = "Design Workshop",
        color = Color(0xFFFF5722),
        start = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(14).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.TUESDAY).withHour(16).withMinute(0),
        description = "UI/UX design brainstorming session."
    ),

    // Wednesday Events
    Event(
        name = "Deep Work Session",
        color = Color(0xFF795548),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(8).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(11).withMinute(0),
        description = "Focused coding time - no interruptions."
    ),
    Event(
        name = "Architecture Review",
        color = Color(0xFF3F51B5),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(13).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(15).withMinute(0),
        description = "Review system architecture and discuss improvements."
    ),
    Event(
        name = "1-on-1 with Manager",
        color = Color(0xFF009688),
        start = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(15).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.WEDNESDAY).withHour(16).withMinute(30),
        description = "Regular check-in meeting with team lead."
    ),

    // Thursday Events
    Event(
        name = "Bug Triage",
        color = Color(0xFFF44336),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(10).withMinute(0),
        description = "Review and prioritize bug reports."
    ),
    Event(
        name = "Learning Session",
        color = Color(0xFF00BCD4),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(10).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(12).withMinute(0),
        description = "Study new Android development techniques."
    ),
    Event(
        name = "Sprint Planning",
        color = Color(0xFF8BC34A),
        start = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(14).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.THURSDAY).withHour(16).withMinute(0),
        description = "Plan next sprint tasks and estimates."
    ),

    // Friday Events
    Event(
        name = "Demo Preparation",
        color = Color(0xFFCDDC39),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(9).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(11).withMinute(0),
        description = "Prepare demo for sprint review."
    ),
    Event(
        name = "Sprint Demo",
        color = Color(0xFFFFEB3B),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(11).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(12).withMinute(30),
        description = "Present completed work to stakeholders."
    ),
    Event(
        name = "Team Lunch",
        color = Color(0xFFFFC107),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(12).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(13).withMinute(30),
        description = "Team building lunch outing."
    ),
    Event(
        name = "Retrospective",
        color = Color(0xFF9E9E9E),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(14).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(15).withMinute(0),
        description = "Sprint retrospective - what went well and what to improve."
    ),
    Event(
        name = "Documentation",
        color = Color(0xFF673AB7),
        start = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(15).withMinute(30),
        end = LocalDateTime.now().with(java.time.DayOfWeek.FRIDAY).withHour(17).withMinute(0),
        description = "Update project documentation and wiki."
    ),

    // Weekend Events
    Event(
        name = "Personal Project",
        color = Color(0xFFE1BEE7),
        start = LocalDateTime.now().with(java.time.DayOfWeek.SATURDAY).withHour(10).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.SATURDAY).withHour(12).withMinute(0),
        description = "Work on personal Android app project."
    ),
    Event(
        name = "Gym Workout",
        color = Color(0xFFB39DDB),
        start = LocalDateTime.now().with(java.time.DayOfWeek.SATURDAY).withHour(16).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.SATURDAY).withHour(17).withMinute(30),
        description = "Strength training session."
    ),
    Event(
        name = "Family Time",
        color = Color(0xFFFFCDD2),
        start = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).withHour(11).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).withHour(14).withMinute(0),
        description = "Spend quality time with family."
    ),
    Event(
        name = "Reading",
        color = Color(0xFFC8E6C9),
        start = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).withHour(19).withMinute(0),
        end = LocalDateTime.now().with(java.time.DayOfWeek.SUNDAY).withHour(21).withMinute(0),
        description = "Read technical books and articles."
    ),

    // Today's events (current date)
    Event(
        name = "Daily Standup",
        color = Color(0xFF81C784),
        start = LocalDateTime.now().withHour(9).withMinute(15),
        end = LocalDateTime.now().withHour(9).withMinute(45),
        description = "Today's team standup meeting."
    ),
    Event(
        name = "Focus Time",
        color = Color(0xFF64B5F6),
        start = LocalDateTime.now().withHour(10).withMinute(0),
        end = LocalDateTime.now().withHour(12).withMinute(0),
        description = "Deep work session - implementing new features."
    ),
    Event(
        name = "Quick Meeting",
        color = Color(0xFFFFB74D),
        start = LocalDateTime.now().withHour(14).withMinute(30),
        end = LocalDateTime.now().withHour(15).withMinute(0),
        description = "Quick sync with design team."
    ),
    Event(
        name = "Code Review",
        color = Color(0xFFA1C181),
        start = LocalDateTime.now().withHour(16).withMinute(0),
        end = LocalDateTime.now().withHour(17).withMinute(30),
        description = "Review team's pull requests."
    )
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
    val name: String,
    val color: Color,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val description: String? = null,
)