package com.example.deepwork.deep_work_app.presentation.components.timeline

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.deepwork.deep_work_app.data.source.CalendarDataSource

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HorizontalCalendar(modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize()) {
        val dataSource = CalendarDataSource()
        val calendarUiModel = dataSource.getData(lastSelectedDate = dataSource.today)
        Column(modifier = modifier.fillMaxSize()) {
            CalendarHead(data = calendarUiModel)
            Content(data = calendarUiModel)
        }    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun HorizontalCalendarPreview() {
    HorizontalCalendar(
        modifier = Modifier.padding(16.dp)
    )
}