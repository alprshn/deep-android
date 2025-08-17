package com.kami_apps.deepwork.deep.presentation.statistics_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
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
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateSelector(
    selectedTimeIndex: Int,
    dayDate: LocalDate,
    weekDate: LocalDate,
    monthDate: LocalDate,
    yearDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentYear = LocalDate.now().year
    val currentMonth = LocalDate.now().monthValue

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .border(
                shape = RoundedCornerShape(12.dp),
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .animateContentSize()
    ) {
        when (selectedTimeIndex) {
            0 -> DaySelector(
                currentDate = dayDate,
                onDateChanged = onDateChanged
            )
            1 -> WeekSelector(
                currentDate = weekDate,
                onPreviousWeek = { onDateChanged(weekDate.minusWeeks(1)) },
                onNextWeek = { onDateChanged(weekDate.plusWeeks(1)) }
            )
            2 -> MonthYearSelector(
                currentDate = monthDate,
                currentYear = currentYear,
                currentMonth = currentMonth,
                onDateChanged = onDateChanged
            )
            3 -> YearSelector(
                currentDate = yearDate,
                currentYear = currentYear,
                onYearChanged = onDateChanged
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
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Select Day",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { showDatePicker = true }
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = currentDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                color = MaterialTheme.colorScheme.onPrimary,
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
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.clickable { onPreviousWeek() }
        )
        Text(
            text = "${startOfWeek.format(DateTimeFormatter.ofPattern("MMM dd"))} - ${
                endOfWeek.format(
                    DateTimeFormatter.ofPattern("MMM dd")
                )
            }",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = "Next Week",
            tint = MaterialTheme.colorScheme.onPrimary,
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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Month",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                            textColor = MaterialTheme.colorScheme.onPrimary
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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Year",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                            textColor = MaterialTheme.colorScheme.onPrimary
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
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Year",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }

            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.onPrimary,
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
                            textColor = MaterialTheme.colorScheme.onPrimary
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
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK", color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                headlineContentColor = MaterialTheme.colorScheme.onPrimary,
                weekdayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                subheadContentColor = MaterialTheme.colorScheme.onPrimary,
                yearContentColor = MaterialTheme.colorScheme.onPrimary,
                currentYearContentColor = MaterialTheme.colorScheme.onPrimary,
                selectedYearContentColor = MaterialTheme.colorScheme.background,
                selectedYearContainerColor = MaterialTheme.colorScheme.onPrimary,
                dayContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledDayContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f),
                selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
                disabledSelectedDayContentColor = Color.Gray,
                selectedDayContainerColor = Color.Gray,
                disabledSelectedDayContainerColor = Color.Gray.copy(alpha = 0.3f),
                todayContentColor = MaterialTheme.colorScheme.onPrimary,
                todayDateBorderColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                dayInSelectionRangeContainerColor = Color.Gray.copy(alpha = 0.2f),
                dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onPrimary,
                dividerColor = Color.Gray.copy(alpha = 0.2f),
                navigationContentColor = MaterialTheme.colorScheme.onPrimary,
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
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
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