package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalendarContent(
    data: CalendarUiModel,
    onDateClickListener: (CalendarUiModel.Date) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "2025", color = Color.Gray)
            Text(text = "Jan", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        VerticalDivider(
            color = Color.Gray,
            modifier = Modifier
                .padding(vertical = 4.dp, horizontal = 12.dp)
                .width(0.75.dp)
                .height(42.dp) // Sabit yükseklik, Card'ların yüksekliği kadar
        )
        LazyRow {
            // pass the visibleDates to the UI
            items(items = data.visibleDates) { date ->
                ContentItem(
                    date = date,
                    onClickListener = onDateClickListener
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentItem(
    date: CalendarUiModel.Date,
    onClickListener: (CalendarUiModel.Date) -> Unit,
) {
    Column(
        Modifier
            .padding(end = 6.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = date.day.first().toString(), // day "M", "T", "W" tek harf
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray

        )


        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(shape = RoundedCornerShape(12.dp))

                .background(
                    if (date.isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        Color.Transparent
                    }
                )
                .clickable {
                    onClickListener(date)
                },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.date.dayOfMonth.toString(), // date "15", "16"
                style = MaterialTheme.typography.bodyMedium,
                color = if (date.isSelected) {
                    MaterialTheme.colorScheme.background
                } else {
                    MaterialTheme.colorScheme.onPrimary
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }


    }


}


@Preview
@Composable
fun CalendarContentPreview() {
    val dataSource = CalendarDataSource()

    var calendarUiModel by remember {
        mutableStateOf(dataSource.getData(lastSelectedDate = dataSource.today))
    }

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
        }
    )
}