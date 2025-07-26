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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.TimePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.CalendarApp
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.Schedule
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.Event
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.SessionDetails
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.SessionDetailsBottomSheet
import com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components.EditSessionBottomSheet
import com.kami_apps.deepwork.deep_work_app.presentation.components.PremiumCard
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.DatePickerModal
import java.time.LocalDate
import java.time.LocalTime
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = hiltViewModel(),
    onShowPaywall: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    
    var selectedSession by remember { mutableStateOf<SessionDetails?>(null) }
    var showEditSheet by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<SessionDetails?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var datePickerCallback by remember { mutableStateOf<((LocalDate) -> Unit)?>(null) }
    var timePickerCallback by remember { mutableStateOf<((LocalTime) -> Unit)?>(null) }
    
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF101012))) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    .padding(top = 16.dp) // Add bottom padding for PremiumCard
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
                            onEventClick = { event ->
                                selectedSession = mapEventToSessionDetails(event)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
        
        // Premium Card Overlay - show only if not premium
        if (!isPremium) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                PremiumCard(
                    onTryFreeClick = {
                        onShowPaywall?.invoke()
                    }
                )
            }
        }
        
        // Session Details Bottom Sheet
        selectedSession?.let { session ->
            SessionDetailsBottomSheet(
                sessionDetails = session,
                onDismiss = { selectedSession = null },
                onEdit = { sessionDetails ->
                    sessionToEdit = sessionDetails
                    showEditSheet = true
                },
                onDelete = { sessionDetails ->
                    viewModel.deleteSession(sessionDetails.id)
                }
            )
        }
        
        // Edit Session Bottom Sheet
        if (showEditSheet && sessionToEdit != null) {
            EditSessionBottomSheet(
                sessionDetails = sessionToEdit!!,
                onDismiss = { 
                    showEditSheet = false
                    sessionToEdit = null
                },
                onSave = { editedSession ->
                    viewModel.updateSession(editedSession)
                },
                onDatePicker = { currentDate, callback ->
                    datePickerCallback = callback
                    showDatePicker = true
                },
                onTimePicker = { currentTime, callback ->
                    timePickerCallback = callback
                    showTimePicker = true
                }
            )
        }
        
        // Date Picker Dialog
        if (showDatePicker) {
            DatePickerModal(
                onDateSelected = { millis ->
                    val date = millis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    date?.let { datePickerCallback?.invoke(it) }
                    showDatePicker = false
                    datePickerCallback = null
                },
                onDismiss = {
                    showDatePicker = false
                    datePickerCallback = null
                },
                initialSelectedDateMillis = System.currentTimeMillis() // veya başka bir tarih
            )
        }
        
        // Time Picker Dialog
        if (showTimePicker) {
            val timePickerState = rememberTimePickerState(
                initialHour = 12,
                initialMinute = 0
            )
            
            TimePickerDialog(
                onDismissRequest = { 
                    showTimePicker = false
                    timePickerCallback = null
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val time = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            timePickerCallback?.invoke(time)
                            showTimePicker = false
                            timePickerCallback = null
                        }
                    ) {
                        Text("OK", color = Color(0xFF30D158))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showTimePicker = false
                            timePickerCallback = null
                        }
                    ) {
                        Text("Cancel", color = Color.Gray)
                    }
                }
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = androidx.compose.material3.TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF2C2C2E),
                        selectorColor = Color(0xFF30D158)
                    )
                )
            }
        }
    }
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = confirmButton,
        dismissButton = dismissButton,
        text = content,
        containerColor = Color(0xFF2C2C2E)
    )
}

private fun mapEventToSessionDetails(event: Event): SessionDetails {
    return SessionDetails(
        id = event.sessionId, // Use the real session ID
        tagName = event.name,
        tagColor = "Color(${event.color.red}, ${event.color.green}, ${event.color.blue}, ${event.color.alpha}, sRGB)",
        tagEmoji = event.emoji ?: "📖",
        date = event.start,
        startTime = event.start,
        endTime = event.end,
        duration = calculateDurationString(event.start, event.end)
    )
}

private fun calculateDurationString(start: java.time.LocalDateTime, end: java.time.LocalDateTime): String {
    val duration = java.time.Duration.between(start, end)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes} min"
        else -> "< 1 min"
    }
}