package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Column
import android.util.Log

private class EventDataModifier(
    val event: Event,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?) = event
}

private fun Modifier.eventData(event: Event) = this.then(EventDataModifier(event))

@Composable
fun BasicSchedule(
    events: List<Event>,
    modifier: Modifier = Modifier,
    eventContent: @Composable (event: Event) -> Unit = { BasicEvent(event = it) },
    minDate: LocalDate = events.minByOrNull(Event::start)?.start?.toLocalDate() ?: LocalDate.now(),
    maxDate: LocalDate = events.maxByOrNull(Event::end)?.end?.toLocalDate() ?: LocalDate.now(),
    dayWidth: Dp,
    hourHeight: Dp,
    sidebarLabel: @Composable (time: LocalTime) -> Unit = { BasicSidebarLabel(time = it) },
) {
    Log.d("BasicSchedule", "BasicSchedule called with ${events.size} events, minDate: $minDate, maxDate: $maxDate")
    
    val numDays = ChronoUnit.DAYS.between(minDate, maxDate).toInt() + 1
    val dividerColor = if (MaterialTheme.colorScheme.background == Color.White) Color.LightGray else Color.DarkGray
    var sidebarWidth by remember { mutableStateOf(0) }
    
    Row(
        modifier = modifier
            .drawBehind {
                repeat(23) {
                    drawLine(
                        dividerColor,
                        start = Offset(0f, (it + 1) * hourHeight.toPx()),
                        end = Offset(size.width, (it + 1) * hourHeight.toPx()),
                        strokeWidth = 0.50.dp.toPx()
                    )
                }
            }
    ) {
        // Sidebar
        Column(
            modifier = Modifier.onGloballyPositioned { sidebarWidth = it.size.width }
        ) {
            val startTime = LocalTime.MIN
            repeat(24) { i ->
                Box(modifier = Modifier.height(hourHeight)) {
                    sidebarLabel(startTime.plusHours(i.toLong()))
                }
            }
        }
        
        // Schedule content
        Layout(
            content = {
                events.sortedBy(Event::start).forEach { event ->
                    Box(modifier = Modifier.eventData(event)) {
                        eventContent(event)
                    }
                }
            },
            modifier = Modifier.weight(1f)
        ) { measureables, constraints ->
            val height = hourHeight.roundToPx() * 24
            val width = dayWidth.roundToPx() * numDays
            
            val placeablesWithEvents = measureables.map { measurable ->
                val event = measurable.parentData as Event
                val eventDurationMinutes = ChronoUnit.MINUTES.between(event.start, event.end)
                val calculatedHeight = ((eventDurationMinutes / 60f) * hourHeight.toPx()).roundToInt()
                val eventHeight = maxOf(calculatedHeight, 32) // Minimum 32dp height
                
                Log.d("BasicSchedule", "Measuring event '${event.name}': duration=${eventDurationMinutes}min, calculatedHeight=${calculatedHeight}px, finalHeight=${eventHeight}px")
                
                val placeable = measurable.measure(
                    constraints.copy(
                        minWidth = dayWidth.roundToPx(),
                        maxWidth = dayWidth.roundToPx(),
                        minHeight = eventHeight,
                        maxHeight = eventHeight
                    )
                )
                Pair(placeable, event)
            }
            
            layout(width, height) {
                Log.d("BasicSchedule", "Layout: width=$width, height=$height, placing ${placeablesWithEvents.size} events")
                placeablesWithEvents.forEach { (placeable, event) ->
                    val eventOffsetMinutes = ChronoUnit.MINUTES.between(LocalTime.MIN, event.start.toLocalTime().plusHours(1) )
                    val eventY = ((eventOffsetMinutes / 60f) * hourHeight.toPx()).roundToInt()
                    val eventOffsetDays = ChronoUnit.DAYS.between(minDate, event.start.toLocalDate()).toInt()
                    val eventX = eventOffsetDays * dayWidth.roundToPx()
                    
                    Log.d("BasicSchedule", "Placing event '${event.name}' at X=$eventX, Y=$eventY (time: ${event.start.toLocalTime()}, offsetMinutes: $eventOffsetMinutes)")
                    placeable.place(eventX, eventY)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BasicSchedulePreview() {
    BasicSchedule(
        events = sampleEvents,
        dayWidth = 256.dp,
        hourHeight = 64.dp
    )
}


