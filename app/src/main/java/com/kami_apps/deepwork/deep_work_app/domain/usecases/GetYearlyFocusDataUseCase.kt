package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

data class YearlyFocusData(
    val month: Int, // 1-12
    val monthName: String, // "Jan", "Feb", etc.
    val totalHours: Int, // Total hours for the month
    val date: LocalDate
)

class GetYearlyFocusDataUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(yearDate: LocalDate, tagId: Int? = null): Flow<List<YearlyFocusData>> {
        val startOfYear = yearDate.withDayOfYear(1)
        val endOfYear = startOfYear.plusYears(1).minusDays(1)
        
        // Convert to Date range for the year
        val startOfYearDate = java.util.Date.from(startOfYear.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endOfYearDate = java.util.Date.from(endOfYear.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        
        return if (tagId == null || tagId == 0) {
            // All tags
            sessionsRepository.getSessionsByDate(startOfYearDate, endOfYearDate).map { sessions ->
                groupSessionsByMonth(sessions, startOfYear)
            }
        } else {
            // Specific tag
            sessionsRepository.getSessionsByTag(tagId).map { allTagSessions ->
                val filteredSessions = allTagSessions.filter { session ->
                    session.startTime?.let { startTime ->
                        startTime.after(startOfYearDate) && startTime.before(endOfYearDate)
                    } ?: false
                }
                groupSessionsByMonth(filteredSessions, startOfYear)
            }
        }
    }
    
    private fun groupSessionsByMonth(
        sessions: List<com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions>,
        yearStartDate: LocalDate
    ): List<YearlyFocusData> {
        val yearlyData = mutableMapOf<LocalDate, Int>()
        val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                               "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        
        // Initialize all months of the year with 0
        for (i in 1..12) {
            val date = yearStartDate.withMonth(i)
            yearlyData[date] = 0
        }
        
        // Group sessions by month and sum up durations
        sessions.forEach { session ->
            session.startTime?.let { startTime ->
                val sessionDate = startTime.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                
                val monthKey = sessionDate.withDayOfMonth(1) // First day of the month as key
                if (yearlyData.containsKey(monthKey)) {
                    val durationMinutes = convertDurationToMinutes(session.duration)
                    yearlyData[monthKey] = (yearlyData[monthKey] ?: 0) + durationMinutes
                }
            }
        }
        
        return yearlyData.map { (date, totalMinutes) ->
            val monthNumber = date.monthValue
            val monthName = monthNames[monthNumber - 1]
            val totalHours = totalMinutes / 60 // Convert minutes to hours
            YearlyFocusData(monthNumber, monthName, totalHours, date)
        }.sortedBy { it.month }
    }
    
    private fun convertDurationToMinutes(duration: Any): Int {
        return when (duration) {
            is Long -> (duration / 60000L).toInt()
            is Int -> (duration / 60000).toInt()
            is String -> {
                try {
                    val parts = duration.split(":")
                    if (parts.size == 2) {
                        val minutes = parts[0].toIntOrNull() ?: 0
                        val seconds = parts[1].toIntOrNull() ?: 0
                        minutes + (seconds / 60)
                    } else {
                        (duration.toLongOrNull()?.div(60000L) ?: 0).toInt()
                    }
                } catch (e: Exception) {
                    0
                }
            }
            else -> 0
        }
    }
} 