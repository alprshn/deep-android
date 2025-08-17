package com.kami_apps.deepwork.deep.presentation.timeline_screen.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun CalendarHeader(
    data: CalendarUiModel,
    onPrevClickListener: (LocalDate) -> Unit,
    onNextClickListener: (LocalDate) -> Unit,
) {
    Row {
        Text(
            // show "Today" if user selects today's date
            // else, show the full format of the date
            text = if (data.selectedDate.isToday) {
                "Today"
            } else {
                data.selectedDate.date.format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
                )
            },
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically),
            color = MaterialTheme.colorScheme.onPrimary
        )
        IconButton(onClick = {
            // invoke previous callback when its button clicked
            onPrevClickListener(data.startDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.ChevronLeft,
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(onClick = {
            // invoke next callback when this button is clicked
            onNextClickListener(data.endDate.date)
        }) {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onPrimary

            )
        }
    }
}

@Composable
@Preview
fun CalendarHeaderPreview(){
    val dataSource = CalendarDataSource()
    // we use `mutableStateOf` and `remember` inside composable function to schedules recomposition
    var calendarUiModel by remember {
        mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
    }
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
}