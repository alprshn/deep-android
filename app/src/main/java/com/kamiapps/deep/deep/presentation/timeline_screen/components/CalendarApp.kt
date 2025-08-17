package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun CalendarApp(
    modifier: Modifier = Modifier,
    onDateSelected: (LocalDate) -> Unit = {}
) {
    val dataSource = CalendarDataSource()
    // we use `mutableStateOf` and `remember` inside composable function to schedules recomposition
    var calendarUiModel by remember { 
        mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today)) 
    }
    
    // Notify parent about initial selected date (today)
    LaunchedEffect(Unit) {
        onDateSelected(dataSource.today)
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        CalendarHeader(
            data = calendarUiModel,
            onPrevClickListener = { startDate ->
                // refresh the CalendarUiModel with new data 
                // by get data with new Start Date (which is the startDate-1 from the visibleDates)
                val finalStartDate = startDate.minusDays(1)
                calendarUiModel = dataSource.getData(
                    startDate = finalStartDate, 
                    lastSelectedDate = calendarUiModel.selectedDate.date
                )
            },
            onNextClickListener = { endDate ->
                // refresh the CalendarUiModel with new data 
                // by get data with new Start Date (which is the endDate+2 from the visibleDates)
                val finalStartDate = endDate.plusDays(2)
                calendarUiModel = dataSource.getData(
                    startDate = finalStartDate, 
                    lastSelectedDate = calendarUiModel.selectedDate.date
                )
            }
        )
        CalendarContent(
            data = calendarUiModel, 
            onDateClickListener = { date ->
                // refresh the CalendarUiModel with new data
                // by changing only the `selectedDate` with the date selected by User
                calendarUiModel = calendarUiModel.copy(
                    selectedDate = date,
                    visibleDates = calendarUiModel.visibleDates.map {
                        it.copy(
                            isSelected = it.date.isEqual(date.date)
                        )
                    }
                )
                // Notify parent about the selected date
                onDateSelected(date.date)
            }
        )
    }
}

@Preview( showBackground = true, backgroundColor = 0x000000)
@Composable
fun CalendarAppPreview() {
    CalendarApp(
        modifier = Modifier.padding(16.dp)
    )
} 