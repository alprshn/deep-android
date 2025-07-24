package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAverageFocusTimeByTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAverageFocusTimeUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetDailyFocusDataUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetHourlyFocusDataUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetMonthlyFocusDataUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetYearlyFocusDataUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetWeekdayFocusDataUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetSessionCountByTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTopTagsBySessionCountUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalFocusTimeByTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalFocusTimeUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalSessionCountUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetRecentSessionsUseCase
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import com.kami_apps.deepwork.deep_work_app.data.manager.PremiumManager

// TODO: Set to false when real data is ready
private const val USE_DUMMY_DATA = true


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getTotalFocusTimeUseCase: GetTotalFocusTimeUseCase,
    private val getTotalSessionCountUseCase: GetTotalSessionCountUseCase,
    private val getSessionCountByTagUseCase: GetSessionCountByTagUseCase,
    private val getTotalFocusTimeByTagUseCase: GetTotalFocusTimeByTagUseCase,
    private val getAverageFocusTimeByTagUseCase: GetAverageFocusTimeByTagUseCase,
    private val getAverageFocusTimeUseCase: GetAverageFocusTimeUseCase,
    private val getTopTagsBySessionCountUseCase: GetTopTagsBySessionCountUseCase,
    private val getHourlyFocusDataUseCase: GetHourlyFocusDataUseCase,
    private val getDailyFocusDataUseCase: GetDailyFocusDataUseCase,
    private val getMonthlyFocusDataUseCase: GetMonthlyFocusDataUseCase,
    private val getYearlyFocusDataUseCase: GetYearlyFocusDataUseCase,
    private val getWeekdayFocusDataUseCase: GetWeekdayFocusDataUseCase,
    private val getRecentSessionsUseCase: GetRecentSessionsUseCase,
    private val premiumManager: PremiumManager
) : ViewModel(), StatisticsActions {


    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )

    init {
        // Observe premium status
        viewModelScope.launch {
            premiumManager.isPremium.collectLatest { isPremium ->
                _uiState.update { currentState ->
                    currentState.copy(isPremium = isPremium)
                }
            }
        }
    }



    fun loadTopTags() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                getTopTagsBySessionCountUseCase.invoke().collectLatest {
                    _uiState.value = _uiState.value.copy(topTags = it)
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }

            }

        }
    }


    fun loadAllTags() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                getAllTagsUseCase.invoke().collectLatest {
                    _uiState.value = _uiState.value.copy(allTags = it)
                }



                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }

            }

        }
    }


    fun updateTagId(tagId: Int) {
        _uiState.update { it.copy(selectedTagId = tagId) }
        loadStatisticsForSelectedTag()
        loadChartData()
    }

    fun updateTimeIndex(timeIndex: Int) {
        _uiState.update { it.copy(selectedTimeIndex = timeIndex) }
        loadStatisticsForSelectedTag()
        loadChartData()
    }

    fun updateSelectedDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date) }
        // Only reload chart data when date changes, not basic statistics
        loadChartData()
    }

    fun loadChartData() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val tagId = if (currentState.selectedTagId == 0) null else currentState.selectedTagId
            
            // TODO: Remove dummy data when real data is ready
            if (USE_DUMMY_DATA) {
                loadDummyChartData(currentState.selectedTimeIndex)
                return@launch
            }
            
            when (currentState.selectedTimeIndex) {
                0 -> { // Day view
                    getHourlyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { hourlyData ->
                        _uiState.value = _uiState.value.copy(
                            hourlyFocusData = hourlyData,
                            peakHour = findPeakHour(hourlyData)
                        )
                    }
                }
                1 -> { // Week view
                    coroutineScope {
                        // Load daily data for column chart
                        launch {
                            getDailyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { dailyData ->
                                _uiState.value = _uiState.value.copy(dailyFocusData = dailyData)
                            }
                        }
                        // Load hourly data for line chart (peak analysis) - aggregate for the whole week
                        launch {
                            // For week view, we want to show the overall weekly peak pattern
                            // So we load hourly data for each day of the week and combine them
                            val weekFields = java.time.temporal.WeekFields.of(java.util.Locale.getDefault())
                            val startOfWeek = currentState.selectedDate.with(weekFields.dayOfWeek(), 1)
                            
                            // For now, just use the first day of the week to show peak pattern
                            // TODO: Could be improved to aggregate across all days of the week
                            getHourlyFocusDataUseCase(startOfWeek, tagId).collectLatest { hourlyData ->
                                _uiState.value = _uiState.value.copy(
                                    hourlyFocusData = hourlyData,
                                    peakHour = findPeakHour(hourlyData)
                                )
                            }
                        }
                    }
                }
                2 -> { // Month view
                    coroutineScope {
                        // Load monthly data for column chart
                        launch {
                            getMonthlyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { monthlyData ->
                                _uiState.value = _uiState.value.copy(monthlyFocusData = monthlyData)
                            }
                        }
                        // Load hourly data for peak analysis
                        launch {
                            getHourlyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { hourlyData ->
                                _uiState.value = _uiState.value.copy(
                                    hourlyFocusData = hourlyData,
                                    peakHour = findPeakHour(hourlyData)
                                )
                            }
                        }
                        // Load weekday data for weekday analysis
                        launch {
                            val startOfMonth = currentState.selectedDate.withDayOfMonth(1)
                            val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)
                            getWeekdayFocusDataUseCase(startOfMonth, endOfMonth, tagId).collectLatest { weekdayData ->
                                _uiState.value = _uiState.value.copy(
                                    weekdayFocusData = weekdayData,
                                    peakWeekday = findPeakWeekday(weekdayData)
                                )
                            }
                        }
                    }
                }
                3 -> { // Year view
                    coroutineScope {
                        // Load yearly data for column chart
                        launch {
                            getYearlyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { yearlyData ->
                                _uiState.value = _uiState.value.copy(yearlyFocusData = yearlyData)
                            }
                        }
                        // Load hourly data for peak analysis
                        launch {
                            getHourlyFocusDataUseCase(currentState.selectedDate, tagId).collectLatest { hourlyData ->
                                _uiState.value = _uiState.value.copy(
                                    hourlyFocusData = hourlyData,
                                    peakHour = findPeakHour(hourlyData)
                                )
                            }
                        }
                        // Load weekday data for weekday analysis  
                        launch {
                            val startOfYear = currentState.selectedDate.withDayOfYear(1)
                            val endOfYear = startOfYear.plusYears(1).minusDays(1)
                            getWeekdayFocusDataUseCase(startOfYear, endOfYear, tagId).collectLatest { weekdayData ->
                                _uiState.value = _uiState.value.copy(
                                    weekdayFocusData = weekdayData,
                                    peakWeekday = findPeakWeekday(weekdayData)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun findPeakHour(hourlyData: List<com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData>): String {
        val peakData = hourlyData.maxByOrNull { it.totalMinutes }
        return if (peakData != null && peakData.totalMinutes > 0) {
            String.format("%02d:00", peakData.hour)
        } else {
            "12:00" // Default if no data
        }
    }
    
    private fun findPeakWeekday(weekdayData: List<com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData>): String {
        val peakData = weekdayData.maxByOrNull { it.totalMinutes }
        return if (peakData != null && peakData.totalMinutes > 0) {
            peakData.dayName
        } else {
            "Monday" // Default if no data
        }
    }
    
    private fun loadDummyChartData(selectedTimeIndex: Int) {
        when (selectedTimeIndex) {
            0 -> { // Day view
                val dummyHourlyData = generateDummyHourlyData()
                _uiState.value = _uiState.value.copy(
                    hourlyFocusData = dummyHourlyData,
                    peakHour = findPeakHour(dummyHourlyData)
                )
            }
            1 -> { // Week view
                val dummyDailyData = generateDummyDailyData()
                val dummyHourlyData = generateDummyHourlyData()
                _uiState.value = _uiState.value.copy(
                    dailyFocusData = dummyDailyData,
                    hourlyFocusData = dummyHourlyData,
                    peakHour = findPeakHour(dummyHourlyData)
                )
            }
            2 -> { // Month view
                val dummyMonthlyData = generateDummyMonthlyData()
                val dummyHourlyData = generateDummyHourlyData()
                val dummyWeekdayData = generateDummyWeekdayData()
                _uiState.value = _uiState.value.copy(
                    monthlyFocusData = dummyMonthlyData,
                    hourlyFocusData = dummyHourlyData,
                    weekdayFocusData = dummyWeekdayData,
                    peakHour = findPeakHour(dummyHourlyData),
                    peakWeekday = findPeakWeekday(dummyWeekdayData)
                )
            }
            3 -> { // Year view
                val dummyYearlyData = generateDummyYearlyData()
                val dummyHourlyData = generateDummyHourlyData()
                val dummyWeekdayData = generateDummyWeekdayData()
                _uiState.value = _uiState.value.copy(
                    yearlyFocusData = dummyYearlyData,
                    hourlyFocusData = dummyHourlyData,
                    weekdayFocusData = dummyWeekdayData,
                    peakHour = findPeakHour(dummyHourlyData),
                    peakWeekday = findPeakWeekday(dummyWeekdayData)
                )
            }
        }
    }
    
    private fun generateDummyHourlyData(): List<com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData> {
        return listOf(
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(6, 25),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(7, 45),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(8, 75),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(9, 120),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(10, 95),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(11, 85),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(12, 65),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(13, 45),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(14, 110),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(15, 135),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(16, 90),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(17, 70),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(18, 50),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(19, 30),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(20, 40),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(21, 25),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(22, 15),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.HourlyFocusData(23, 10)
        )
    }
    
    private fun generateDummyDailyData(): List<com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData> {
        return listOf(
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(1, "Mon", 180, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(2, "Tue", 150, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(1)),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(3, "Wed", 220, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(2)),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(4, "Thu", 165, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(3)),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(5, "Fri", 240, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(4)),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(6, "Sat", 90, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(5)),
            com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData(7, "Sun", 120, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(6))
        )
    }
    
    private fun generateDummyMonthlyData(): List<com.kami_apps.deepwork.deep_work_app.domain.usecases.MonthlyFocusData> {
        return (1..31).map { day ->
            val minutes = when {
                day % 7 == 0 || day % 7 == 6 -> (30..90).random() // Weekend - less focus
                day % 5 == 0 -> (180..300).random() // Every 5th day - high focus
                else -> (60..180).random() // Regular days
            }
            com.kami_apps.deepwork.deep_work_app.domain.usecases.MonthlyFocusData(
                day, 
                minutes, 
                LocalDate.now().withDayOfMonth(minOf(day, LocalDate.now().lengthOfMonth()))
            )
        }
    }
    
    private fun generateDummyYearlyData(): List<com.kami_apps.deepwork.deep_work_app.domain.usecases.YearlyFocusData> {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return monthNames.mapIndexed { index, monthName ->
            val hours = when (index + 1) {
                1, 2, 12 -> (40..80).random() // Winter months - moderate
                6, 7, 8 -> (20..50).random() // Summer months - vacation time
                3, 4, 5, 9, 10, 11 -> (60..120).random() // Other months - high productivity
                else -> 60
            }
            com.kami_apps.deepwork.deep_work_app.domain.usecases.YearlyFocusData(
                index + 1, 
                monthName, 
                hours, 
                LocalDate.now().withMonth(index + 1).withDayOfMonth(1)
            )
        }
    }
    
    private fun generateDummyWeekdayData(): List<com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData> {
        return listOf(
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(1, "Mon", 180), // Monday blues
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(2, "Tue", 220), // Getting into rhythm
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(3, "Wed", 280), // Midweek peak
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(4, "Thu", 240), // Still productive
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(5, "Fri", 160), // Friday slowdown
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(6, "Sat", 90),  // Weekend
            com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData(7, "Sun", 120)  // Sunday prep
        )
    }

    fun loadStatisticsForSelectedTag() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                if (_uiState.value.selectedTagId == 0) {
                    // All tags - get total statistics
                    _uiState.update { it.copy(totalSessionCount = getTotalSessionCountUseCase.invoke()) }

                    getTotalFocusTimeUseCase.invoke().collectLatest { result ->
                        Log.e("UI Log", "All Tags - Toplam Süre (UI): $result")
                        _uiState.value = _uiState.value.copy(totalFocusTime = result)
                    }

                    getAverageFocusTimeUseCase.invoke().collectLatest { result ->
                        _uiState.value = _uiState.value.copy(averageFocusTime = result)
                    }



                } else {
                    // Specific tag - get statistics for that tag
                    val selectedTagId = _uiState.value.selectedTagId
                    
                    // Collect both flows concurrently
                    coroutineScope {
                        launch {
                            getSessionCountByTagUseCase(selectedTagId).collectLatest { count ->
                                Log.e("UI Log", "Session Count for Tag $selectedTagId: $count")
                                _uiState.value = _uiState.value.copy(totalSessionCount = count)
                            }
                        }
                        
                        launch {
                            getTotalFocusTimeByTagUseCase(selectedTagId).collectLatest { result ->
                                Log.e("UI Log", "Tag $selectedTagId - Toplam Süre (UI): $result")
                                _uiState.value = _uiState.value.copy(totalFocusTime = result)
                            }
                        }

                        launch {
                            getAverageFocusTimeByTagUseCase(selectedTagId).collectLatest { result ->
                                _uiState.value = _uiState.value.copy(averageFocusTime = result)
                            }
                        }
                    }
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error loading statistics: ${e.message}", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun loadSessionLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getRecentSessionsUseCase.invoke(limit = 15).collectLatest { sessionLogs ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            sessionLogs = sessionLogs,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error loading session logs: ${e.message}", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage = "Session logs yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }
            }
        }
    }







    fun InitializeTotalSessionCount() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                _uiState.update { it.copy(totalSessionCount = getTotalSessionCountUseCase.invoke()) }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }

            }

        }
    }


}