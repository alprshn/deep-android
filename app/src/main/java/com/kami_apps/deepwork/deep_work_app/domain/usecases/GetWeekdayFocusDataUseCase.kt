package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

data class WeekdayFocusData(
    val dayOfWeek: Int, // 1 = Monday, 7 = Sunday
    val dayName: String, // "Mon", "Tue", etc.
    val totalMinutes: Int
)

class GetWeekdayFocusDataUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(startDate: LocalDate, endDate: LocalDate, tagId: Int? = null): Flow<List<WeekdayFocusData>> {
        // Convert to Date range
        val startDateConverted = java.util.Date.from(startDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endDateConverted = java.util.Date.from(endDate.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        
        return if (tagId == null || tagId == 0) {
            // All tags
            sessionsRepository.getSessionsByDate(startDateConverted, endDateConverted).map { sessions ->
                groupSessionsByWeekday(sessions)
            }
        } else {
            // Specific tag
            sessionsRepository.getSessionsByTag(tagId).map { allTagSessions ->
                val filteredSessions = allTagSessions.filter { session ->
                    session.startTime?.let { startTime ->
                        startTime.after(startDateConverted) && startTime.before(endDateConverted)
                    } ?: false
                }
                groupSessionsByWeekday(filteredSessions)
            }
        }
    }
    
    private fun groupSessionsByWeekday(
        sessions: List<com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions>
    ): List<WeekdayFocusData> {
        val weekdayData = mutableMapOf<Int, Int>()
        val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        
        // Initialize all weekdays with 0
        for (i in 1..7) {
            weekdayData[i] = 0
        }
        
        // Group sessions by weekday and sum up durations
        sessions.forEach { session ->
            session.startTime?.let { startTime ->
                val sessionDate = startTime.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate()
                
                val dayOfWeek = sessionDate.dayOfWeek.value // 1 = Monday, 7 = Sunday
                val durationMinutes = convertDurationToMinutes(session.duration)
                weekdayData[dayOfWeek] = (weekdayData[dayOfWeek] ?: 0) + durationMinutes
            }
        }
        
        return weekdayData.map { (dayOfWeek, totalMinutes) ->
            val dayName = dayNames[(dayOfWeek - 1) % 7]
            WeekdayFocusData(dayOfWeek, dayName, totalMinutes)
        }.sortedBy { it.dayOfWeek }
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