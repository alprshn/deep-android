package com.example.deepwork.deep_work_app.presentation.timeline_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.presentation.statistics_screen.components.JetpackComposeElectricCarSales
import com.example.deepwork.deep_work_app.presentation.timeline_screen.components.CalendarApp
import com.example.deepwork.deep_work_app.presentation.timeline_screen.components.Schedule
import com.example.deepwork.deep_work_app.presentation.timeline_screen.components.sampleEvents
import java.time.LocalDate

@Composable
fun TimelineScreen(){
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    
    // Filter events for the selected date
    val filteredEvents = sampleEvents.filter { event ->
        event.start.toLocalDate() == selectedDate
    }
    
    Column (modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Text(
            text = "Timeline", 
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        )
        
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
                .weight(1f)
                .padding(16.dp)
        )
    }
}