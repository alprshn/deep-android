package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.*
import javax.inject.Inject

data class DailyFocusData(
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday (ISO standard)
    val dayName: String, // "Mon", "Tue", etc.
    val totalMinutes: Int,
    val date: LocalDate
)

class GetDailyFocusDataUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(weekStartDate: LocalDate, tagId: Int? = null): Flow<List<DailyFocusData>> {
        val weekFields = WeekFields.of(Locale.getDefault())
        val startOfWeek = weekStartDate.with(weekFields.dayOfWeek(), 1)
        val endOfWeek = startOfWeek.plusDays(6)
        
        // Convert to Date range for the week
        val startOfWeekDate = java.util.Date.from(startOfWeek.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endOfWeekDate = java.util.Date.from(endOfWeek.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        
        return if (tagId == null || tagId == 0) {
            // All tags
            sessionsRepository.getSessionsByDate(startOfWeekDate, endOfWeekDate).map { sessions ->
                groupSessionsByDay(sessions, startOfWeek)
            }
        } else {
            // Specific tag
            sessionsRepository.getSessionsByTag(tagId).map { allTagSessions ->
                val filteredSessions = allTagSessions.filter { session ->
                    session.startTime?.let { startTime ->
                        startTime.after(startOfWeekDate) && startTime.before(endOfWeekDate)
                    } ?: false
                }
                groupSessionsByDay(filteredSessions, startOfWeek)
            }
        }
    }
    
    private fun groupSessionsByDay(
        sessions: List<com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions>,
        weekStartDate: LocalDate
    ): List<DailyFocusData> {
        val dailyData = mutableMapOf<LocalDate, Int>()
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        
        // Initialize all days of the week with 0
        for (i in 0..6) {
            val date = weekStartDate.plusDays(i.toLong())
            dailyData[date] = 0
        }
        
        // Group sessions by day and sum up durations
        sessions.forEach { session ->
            session.startTime?.let { startTime ->
                val sessionDate = startTime.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
            
                if (dailyData.containsKey(sessionDate)) {
                    val durationMinutes = convertDurationToMinutes(session.duration) // Convert duration to minutes
                    dailyData[sessionDate] = (dailyData[sessionDate] ?: 0) + durationMinutes
                }
            }
        }
        
        return dailyData.map { (date, minutes) ->
            val dayOfWeek = date.dayOfWeek.value // 1 = Monday, 7 = Sunday
            val dayName = dayNames[(dayOfWeek - 1) % 7]
            DailyFocusData(dayOfWeek, dayName, minutes, date)
        }.sortedBy { it.dayOfWeek }
    }
    
    private fun convertDurationToMinutes(duration: Any): Int {
        return when (duration) {
            is Long -> (duration / 60000L).toInt() // If it's milliseconds
            is Int -> (duration / 60000).toInt() // If it's milliseconds as Int
            is String -> {
                try {
                    // Try to parse as "mm:ss" format
                    val parts = duration.split(":")
                    if (parts.size == 2) {
                        val minutes = parts[0].toIntOrNull() ?: 0
                        val seconds = parts[1].toIntOrNull() ?: 0
                        minutes + (seconds / 60) // Convert to total minutes
                    } else {
                        // Try to parse as plain number (milliseconds)
                        (duration.toLongOrNull()?.div(60000L) ?: 0).toInt()
                    }
                } catch (e: Exception) {
                    0 // Return 0 if parsing fails
                }
            }
            else -> 0
        }
    }
} 