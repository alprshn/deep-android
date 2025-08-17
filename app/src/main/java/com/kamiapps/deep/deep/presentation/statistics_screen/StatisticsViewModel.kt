package com.kamiapps.deep.deep.presentation.statistics_screen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kamiapps.deep.deep.domain.usecases.GetAllTagsUseCase
import com.kamiapps.deep.deep.domain.usecases.GetAverageFocusTimeByTagUseCase
import com.kamiapps.deep.deep.domain.usecases.GetAverageFocusTimeUseCase
import com.kamiapps.deep.deep.domain.usecases.GetDailyFocusDataUseCase
import com.kamiapps.deep.deep.domain.usecases.GetHourlyFocusDataUseCase
import com.kamiapps.deep.deep.domain.usecases.GetMonthlyFocusDataUseCase
import com.kamiapps.deep.deep.domain.usecases.GetYearlyFocusDataUseCase
import com.kamiapps.deep.deep.domain.usecases.GetWeekdayFocusDataUseCase
import com.kamiapps.deep.deep.domain.usecases.GetSessionCountByTagUseCase
import com.kamiapps.deep.deep.domain.usecases.GetTopTagsBySessionCountUseCase
import com.kamiapps.deep.deep.domain.usecases.GetTotalFocusTimeByTagUseCase
import com.kamiapps.deep.deep.domain.usecases.GetTotalFocusTimeUseCase
import com.kamiapps.deep.deep.domain.usecases.GetTotalSessionCountUseCase
import com.kamiapps.deep.deep.domain.usecases.GetRecentSessionsUseCase
import java.time.LocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import com.kamiapps.deep.deep.data.manager.PremiumManager
import com.kamiapps.deep.deep.domain.usecases.HourlyFocusData
import kotlinx.coroutines.flow.first

import java.util.Locale
import java.time.temporal.WeekFields
// TODO: Set to false when real data is ready


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
        viewModelScope.launch {
            premiumManager.isPremium.collectLatest { isPremium ->
                _uiState.update { it.copy(isPremium = isPremium) }

                // Premium açıldıysa gerçek verileri, değilse dummy'yi yükle
                if (isPremium) {
                    loadStatisticsForSelectedTag()
                    loadChartData()
                    loadSessionLogs()
                } else {
                    loadDummyChartData(_uiState.value.selectedTimeIndex)
                }
            }
        }
    }
    private fun shouldUseDummy(): Boolean = !_uiState.value.isPremium


    fun loadTopTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getTopTagsBySessionCountUseCase.invoke().collectLatest { list ->
                    // No tag ayıkla
                    val filtered = list.filter { !it.name.equals("No tag", ignoreCase = true) }
                    _uiState.value = _uiState.value.copy(topTags = filtered)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}") }
            }
        }
    }



    fun loadAllTags() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                getAllTagsUseCase.invoke().collectLatest { tags ->
                    val filtered = tags.filter { it.tagId != 0 && !it.tagName.equals("No tag", ignoreCase = true) }
                    _uiState.value = _uiState.value.copy(allTags = filtered)
                }
                _uiState.update { it.copy(isLoading = false, errorMessage = null) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}") }
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

    // Blur değerini güncelleyen fonksiyon
    fun updateBlurAlpha(alpha: Float) {
        _uiState.update { it.copy(blurAlpha = alpha.coerceIn(0f, 1f)) }
    }


    fun loadChartData() {
        viewModelScope.launch {
            val s = _uiState.value
            val tagId = if (s.selectedTagId == 0) null else s.selectedTagId

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                if (shouldUseDummy()) {
                    loadDummyChartData(s.selectedTimeIndex)
                    return@launch
                }

                when (s.selectedTimeIndex) {
                    // === 0) GÜN ===
                    0 -> {
                        // Seçili gün için SAATLİK dağılım
                        getHourlyFocusDataUseCase(s.selectedDate, tagId).collectLatest { hourly ->
                            _uiState.update {
                                it.copy(
                                    hourlyFocusData = hourly,
                                    peakHour = findPeakHour(hourly)
                                )
                            }
                        }
                    }

                    // === 1) HAFTA ===
                    1 -> {
                        coroutineScope {
                            // 1a) Haftanın GÜNLERİ (sütun grafiği)
                            launch {
                                getDailyFocusDataUseCase(s.selectedDate, tagId).collectLatest { daily ->
                                    _uiState.update { it.copy(dailyFocusData = daily) }
                                }
                            }
                            // 1b) Haftanın tamamı için SAATLİK (çizgi + peak)
                            launch {
                                val wf = WeekFields.of(Locale.getDefault())
                                val start = s.selectedDate.with(wf.dayOfWeek(), 1)
                                val end = start.plusDays(6)

                                val hourly = aggregateHourlyOverRange(start, end, tagId)
                                _uiState.update {
                                    it.copy(
                                        hourlyFocusData = hourly,
                                        peakHour = findPeakHour(hourly)
                                    )
                                }
                            }
                        }
                    }

                    // === 2) AY ===
                    2 -> {
                        coroutineScope {
                            val startOfMonth = s.selectedDate.withDayOfMonth(1)
                            val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)

                            // 2a) Günlere göre AYLIK (sütun)
                            launch {
                                getMonthlyFocusDataUseCase(s.selectedDate, tagId).collectLatest { monthly ->
                                    _uiState.update { it.copy(monthlyFocusData = monthly) }
                                }
                            }
                            // 2b) Ayın tamamı için SAATLİK toplam (çizgi + peak)
                            launch {
                                val hourly = aggregateHourlyOverRange(startOfMonth, endOfMonth, tagId)
                                _uiState.update {
                                    it.copy(
                                        hourlyFocusData = hourly,
                                        peakHour = findPeakHour(hourly)
                                    )
                                }
                            }
                            // 2c) Ay içinde HAFTANIN GÜNÜNE göre (weekday)
                            launch {
                                getWeekdayFocusDataUseCase(startOfMonth, endOfMonth, tagId).collectLatest { weekday ->
                                    _uiState.update {
                                        it.copy(
                                            weekdayFocusData = weekday,
                                            peakWeekday = findPeakWeekday(weekday)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // === 3) YIL ===
                    3 -> {
                        coroutineScope {
                            val startOfYear = s.selectedDate.withDayOfYear(1)
                            val endOfYear = startOfYear.plusYears(1).minusDays(1)

                            // 3a) Aylara göre YILLIK (sütun)
                            launch {
                                getYearlyFocusDataUseCase(s.selectedDate, tagId).collectLatest { yearly ->
                                    _uiState.update { it.copy(yearlyFocusData = yearly) }
                                }
                            }
                            // 3b) Yılın tamamı için SAATLİK toplam (çizgi + peak)
                            launch {
                                val hourly = aggregateHourlyOverRange(startOfYear, endOfYear, tagId)
                                _uiState.update {
                                    it.copy(
                                        hourlyFocusData = hourly,
                                        peakHour = findPeakHour(hourly)
                                    )
                                }
                            }
                            // 3c) Yıl içinde HAFTANIN GÜNÜNE göre (weekday)
                            launch {
                                getWeekdayFocusDataUseCase(startOfYear, endOfYear, tagId).collectLatest { weekday ->
                                    _uiState.update {
                                        it.copy(
                                            weekdayFocusData = weekday,
                                            peakWeekday = findPeakWeekday(weekday)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error loading chart data", e)
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Unknown error") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    private fun findPeakHour(hourlyData: List<com.kamiapps.deep.deep.domain.usecases.HourlyFocusData>): String {
        val peakData = hourlyData.maxByOrNull { it.totalMinutes }
        return if (peakData != null && peakData.totalMinutes > 0) {
            String.format("%02d:00", peakData.hour)
        } else {
            "12:00" // Default if no data
        }
    }
    
    private fun findPeakWeekday(weekdayData: List<com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData>): String {
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
    
    private fun generateDummyHourlyData(): List<com.kamiapps.deep.deep.domain.usecases.HourlyFocusData> {
        return listOf(
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(6, 25),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(7, 45),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(8, 75),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(9, 120),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(10, 95),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(11, 85),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(12, 65),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(13, 45),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(14, 110),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(15, 135),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(16, 90),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(17, 70),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(18, 50),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(19, 30),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(20, 40),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(21, 25),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(22, 15),
            com.kamiapps.deep.deep.domain.usecases.HourlyFocusData(23, 10)
        )
    }
    
    private fun generateDummyDailyData(): List<com.kamiapps.deep.deep.domain.usecases.DailyFocusData> {
        return listOf(
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(1, "Mon", 180, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(2, "Tue", 150, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(1)),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(3, "Wed", 220, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(2)),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(4, "Thu", 165, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(3)),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(5, "Fri", 240, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(4)),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(6, "Sat", 90, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(5)),
            com.kamiapps.deep.deep.domain.usecases.DailyFocusData(7, "Sun", 120, LocalDate.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).plusDays(6))
        )
    }
    
    private fun generateDummyMonthlyData(): List<com.kamiapps.deep.deep.domain.usecases.MonthlyFocusData> {
        return (1..31).map { day ->
            val minutes = when {
                day % 7 == 0 || day % 7 == 6 -> (30..90).random() // Weekend - less focus
                day % 5 == 0 -> (180..300).random() // Every 5th day - high focus
                else -> (60..180).random() // Regular days
            }
            com.kamiapps.deep.deep.domain.usecases.MonthlyFocusData(
                day, 
                minutes, 
                LocalDate.now().withDayOfMonth(minOf(day, LocalDate.now().lengthOfMonth()))
            )
        }
    }
    
    private fun generateDummyYearlyData(): List<com.kamiapps.deep.deep.domain.usecases.YearlyFocusData> {
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        return monthNames.mapIndexed { index, monthName ->
            val hours = when (index + 1) {
                1, 2, 12 -> (40..80).random() // Winter months - moderate
                6, 7, 8 -> (20..50).random() // Summer months - vacation time
                3, 4, 5, 9, 10, 11 -> (60..120).random() // Other months - high productivity
                else -> 60
            }
            com.kamiapps.deep.deep.domain.usecases.YearlyFocusData(
                index + 1, 
                monthName, 
                hours, 
                LocalDate.now().withMonth(index + 1).withDayOfMonth(1)
            )
        }
    }
    
    private fun generateDummyWeekdayData(): List<com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData> {
        return listOf(
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(1, "Mon", 180), // Monday blues
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(2, "Tue", 220), // Getting into rhythm
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(3, "Wed", 280), // Midweek peak
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(4, "Thu", 240), // Still productive
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(5, "Fri", 160), // Friday slowdown
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(6, "Sat", 90),  // Weekend
            com.kamiapps.deep.deep.domain.usecases.WeekdayFocusData(7, "Sun", 120)  // Sunday prep
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



    private suspend fun aggregateHourlyOverRange(
        startDate: LocalDate,
        endDate: LocalDate,
        tagId: Int?
    ): List<HourlyFocusData> {
        val totals = IntArray(24) { 0 }
        var d = startDate
        while (!d.isAfter(endDate)) {
            val day = getHourlyFocusDataUseCase(d, tagId).first()
            day.forEach { totals[it.hour] += it.totalMinutes }
            d = d.plusDays(1)
        }
        return (0..23).map { hour -> HourlyFocusData(hour, totals[hour]) }
    }



}