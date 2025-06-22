package com.example.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun Schedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
) {
    val dayWidth = 256.dp
    val hourHeight = 64.dp
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    
    Column(modifier = modifier.background(Color(0xFF1C1C1E)).padding(horizontal = 12.dp)) {
        BasicSchedule(
            events = events,
            eventContent = eventContent,
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