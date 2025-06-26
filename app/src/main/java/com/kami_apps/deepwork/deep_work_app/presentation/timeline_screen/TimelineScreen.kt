package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.CalendarApp
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.Schedule
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.sampleEvents
import java.time.LocalDate

@Composable
fun TimelineScreen(){
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Filter events for the selected date
    val filteredEvents = sampleEvents.filter { event ->
        event.start.toLocalDate() == selectedDate
    }
    
    Column (modifier = Modifier.fillMaxSize().background(Color(0xFF101012))) {
        // Calendar at the top
        CalendarApp(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onDateSelected = { date ->
                selectedDate = date
            }
        )
        
        // Schedule below showing events for selected date
        Schedule(
            events = filteredEvents,
            minDate = selectedDate,
            maxDate = selectedDate,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f).padding(top = 16.dp)
        )
    }
}