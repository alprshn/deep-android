package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import android.util.Log
import androidx.compose.material3.MaterialTheme

@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    onEventClick: ((Event) -> Unit)? = null,
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
) {
    Log.d("Schedule", "Schedule composable called with ${events.size} events")
    events.forEach { event ->
        Log.d("Schedule", "Event: ${event.name}, start: ${event.start}, end: ${event.end}")
    }
    
    val dayWidth = 256.dp
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Column(modifier = modifier.background(MaterialTheme.colorScheme.surfaceDim).padding(horizontal = 12.dp)) {
        BasicSchedule(
            events = events,
            eventContent = { event ->
                BasicEvent(
                    event = event,
                    onClick = if (onEventClick != null) { { onEventClick(event) } } else null
                )
            },
            minDate = minDate,
            maxDate = maxDate,
            dayWidth = dayWidth,
            hourHeight = hourHeight,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(verticalScrollState)
                .horizontalScroll(horizontalScrollState)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SchedulePreview() {
    Schedule(sampleEvents)
} 