package com.example.deepwork.deep_work_app.presentation.statistics_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedTimeIndex: Int,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().monthValue

    // Single container for all date selection UI
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF101012), RoundedCornerShape(12.dp))
            .border(
                shape = RoundedCornerShape(12.dp),
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.2f)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .animateContentSize()//Color(0xFF101012)
    ) {
        when (selectedTimeIndex) {
            0 -> DaySelector(
                currentDate = currentDate,
                onDateChanged = { newDate ->
                    currentDate = newDate
                    onDateChanged(newDate)
                }
            )

            1 -> WeekSelector(
                currentDate = currentDate,
                onPreviousWeek = {
                    currentDate = currentDate.minusWeeks(1)
                    onDateChanged(currentDate)
                },
                onNextWeek = {
                    currentDate = currentDate.plusWeeks(1)
                    onDateChanged(currentDate)
                }
            )

            2 -> MonthYearSelector(
                currentDate = currentDate,
                currentYear = currentYear,
                currentMonth = currentMonth,
                onDateChanged = { newDate ->
                    currentDate = newDate
                    onDateChanged(newDate)
                }
            )

            3 -> YearSelector(
                currentDate = currentDate,
                currentYear = currentYear,
                onYearChanged = { newDate ->
                    currentDate = newDate
                    onDateChanged(newDate)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DaySelector(
    currentDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDatePicker = true },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Select Day",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(Color(0xFF1C1C1E), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }

    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { selectedDateMillis ->
                selectedDateMillis?.let { millis ->
                    val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                    onDateChanged(selectedDate)
                }
            },
            onDismiss = { showDatePicker = false },
            initialSelectedDateMillis = currentDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WeekSelector(
    currentDate: LocalDate,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit
) {
    val weekFields = WeekFields.of(Locale.getDefault())
    val startOfWeek = currentDate.with(weekFields.dayOfWeek(), 1)
    val endOfWeek = startOfWeek.plusDays(6)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIos,
            contentDescription = "Previous Week",
            tint = Color.White,
            modifier = Modifier.clickable { onPreviousWeek() }
        )
        Text(
            text = "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${
                endOfWeek.format(
                    DateTimeFormatter.ofPattern("MMM dd")
                )
            }",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Next Week",
            tint = Color.White,
            modifier = Modifier.clickable { onNextWeek() }
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun MonthYearSelector(
    currentDate: LocalDate,
    currentYear: Int,
    currentMonth: Int,
    onDateChanged: (LocalDate) -> Unit
) {
    var monthExpanded by remember { mutableStateOf(false) }
    var yearExpanded by remember { mutableStateOf(false) }

    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    // Available years: current year back to 5 years ago
    val availableYears = (currentYear - 5..currentYear).toList().reversed()

    // Available months based on selected year
    val availableMonths = if (currentDate.year == currentYear) {
        months.take(currentMonth) // Only months up to current month for current year
    } else {
        months // All months for past years
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Month Selector
        Box {
            Row(
                modifier = Modifier.clickable { monthExpanded = true },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = months[currentDate.monthValue - 1],
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Month",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = Color(0xFF1C1C1E)
            ) {
                availableMonths.forEachIndexed { index, month ->
                    val isSelected = (index + 1) == currentDate.monthValue
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = month,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        onClick = {
                            val newDate = currentDate.withMonth(index + 1)
                            onDateChanged(newDate)
                            monthExpanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.White
                        )
                    )
                    if (index < availableMonths.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Year Selector
        Box {
            Row(
                modifier = Modifier.clickable { yearExpanded = true },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDate.year.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Year",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = Color(0xFF1C1C1E)
            ) {
                availableYears.forEachIndexed { index, year ->
                    val isSelected = year == currentDate.year
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = year.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        onClick = {
                            val newDate = currentDate.withYear(year)
                            onDateChanged(newDate)
                            yearExpanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.White
                        )
                    )
                    if (index < availableYears.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun YearSelector(
    currentDate: LocalDate,
    currentYear: Int,
    onYearChanged: (LocalDate) -> Unit
) {
    var yearExpanded by remember { mutableStateOf(false) }

    // Available years: current year back to 5 years ago
    val availableYears = (currentYear - 5..currentYear).toList().reversed()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box {
            Row(
                modifier = Modifier.clickable { yearExpanded = true },
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentDate.year.toString(),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Year",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = Color(0xFF1C1C1E)
            ) {
                availableYears.forEachIndexed { index, year ->
                    val isSelected = year == currentDate.year
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = year.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        onClick = {
                            val newDate = currentDate.withYear(year)
                            onYearChanged(newDate)
                            yearExpanded = false
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = Color.White
                        )
                    )
                    if (index < availableYears.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
    fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    initialSelectedDateMillis: Long? = null
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss, colors = DatePickerDefaults.colors(containerColor = Color(0xFF1C1C1E)),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF1C1C1E),
                titleContentColor = Color.White,
                headlineContentColor = Color.White,
                weekdayContentColor = Color.White.copy(alpha = 0.7f),
                subheadContentColor = Color.White,
                yearContentColor = Color.White,
                currentYearContentColor = Color.White,
                selectedYearContentColor = Color.Black,
                selectedYearContainerColor = Color.White,
                dayContentColor = Color.White,
                disabledDayContentColor = Color.White.copy(alpha = 0.3f),
                selectedDayContentColor = Color.White,
                disabledSelectedDayContentColor = Color.Gray,
                selectedDayContainerColor = Color.Gray,
                disabledSelectedDayContainerColor = Color.Gray.copy(alpha = 0.3f),
                todayContentColor = Color.White,
                todayDateBorderColor = Color.White.copy(alpha = 0.5f),
                dayInSelectionRangeContainerColor = Color.Gray.copy(alpha = 0.2f),
                dayInSelectionRangeContentColor = Color.White,
                dividerColor = Color.Gray.copy(alpha = 0.2f),
                navigationContentColor = Color.White,
            )
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(backgroundColor = 0xFF000000, showBackground = true)
@Composable
fun DateSelectorPreview() {
    var monthExpanded by remember { mutableStateOf(true) }
    val currentDate = LocalDate.now()
    val months = listOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    val availableMonths = months.take(currentDate.monthValue)
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Column {
            // Month selector with dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF101012), RoundedCornerShape(12.dp))
                    .border(
                        shape = RoundedCornerShape(12.dp),
                        width = 1.dp,
                        color = Color.Gray.copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Row(
                        modifier = Modifier.clickable { monthExpanded = true },
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "May",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Icon(
                            imageVector = Icons.Default.UnfoldMore,
                            contentDescription = "Select Month",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = monthExpanded,
                        onDismissRequest = { monthExpanded = false },
                        modifier = Modifier.wrapContentSize(),
                        shape = RoundedCornerShape(8.dp),
                        containerColor = Color(0xFF1C1C1E)
                    ) {
                        availableMonths.forEachIndexed { index, month ->
                            val isSelected = (index + 1) == currentDate.monthValue
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = month,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal
                                        )
                                        if (isSelected) {
                                            Text(
                                                text = "✓",
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    monthExpanded = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = Color.White
                                )
                            )
                            if (index < availableMonths.size - 1) {
                                HorizontalDivider(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    thickness = 0.5.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}