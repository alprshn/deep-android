package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.CalendarApp
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.Schedule
import java.time.LocalDate

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF101012))) {
        // Calendar at the top
        CalendarApp(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            onDateSelected = { date ->
                viewModel.onDateSelected(date)
            }
        )
        
        // Schedule content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                uiState.events.isEmpty() -> {
                    Text(
                        text = "No focus sessions found for this date",
                        color = Color.Gray,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                
                else -> {
                    // Schedule showing events for selected date
                    Schedule(
                        events = uiState.events,
                        minDate = uiState.selectedDate,
                        maxDate = uiState.selectedDate,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}