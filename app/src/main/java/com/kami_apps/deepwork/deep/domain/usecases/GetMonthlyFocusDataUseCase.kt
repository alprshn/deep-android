package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

data class MonthlyFocusData(
    val dayOfMonth: Int, // 1-31
    val totalMinutes: Int,
    val date: LocalDate
)

class GetMonthlyFocusDataUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(monthDate: LocalDate, tagId: Int? = null): Flow<List<MonthlyFocusData>> {
        val startOfMonth = monthDate.withDayOfMonth(1)
        val endOfMonth = startOfMonth.plusMonths(1).minusDays(1)
        
        // Convert to Date range for the month
        val startOfMonthDate = java.util.Date.from(startOfMonth.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endOfMonthDate = java.util.Date.from(endOfMonth.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        
        return if (tagId == null || tagId == 0) {
            // All tags
            sessionsRepository.getSessionsByDate(startOfMonthDate, endOfMonthDate).map { sessions ->
                groupSessionsByDayOfMonth(sessions, startOfMonth)
            }
        } else {
            // Specific tag
            sessionsRepository.getSessionsByTag(tagId).map { allTagSessions ->
                val filteredSessions = allTagSessions.filter { session ->
                    session.startTime?.let { startTime ->
                        startTime.after(startOfMonthDate) && startTime.before(endOfMonthDate)
                    } ?: false
                }
                groupSessionsByDayOfMonth(filteredSessions, startOfMonth)
            }
        }
    }
    
    private fun groupSessionsByDayOfMonth(
        sessions: List<com.kami_apps.deepwork.deep.data.local.entities.Sessions>,
        monthStartDate: LocalDate
    ): List<MonthlyFocusData> {
        val monthlyData = mutableMapOf<LocalDate, Int>()
        
        // Initialize all days of the month with 0
        val daysInMonth = monthStartDate.lengthOfMonth()
        for (i in 1..daysInMonth) {
            val date = monthStartDate.withDayOfMonth(i)
            monthlyData[date] = 0
        }
        
        // Group sessions by day and sum up durations
        sessions.forEach { session ->
            session.startTime?.let { startTime ->
                val sessionDate = startTime.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                
                if (monthlyData.containsKey(sessionDate)) {
                    val durationMinutes = convertDurationToMinutes(session.duration)
                    monthlyData[sessionDate] = (monthlyData[sessionDate] ?: 0) + durationMinutes
                }
            }
        }
        
        return monthlyData.map { (date, minutes) ->
            MonthlyFocusData(date.dayOfMonth, minutes, date)
        }.sortedBy { it.dayOfMonth }
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