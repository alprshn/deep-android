package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

data class HourlyFocusData(
    val hour: Int, // 0-23
    val totalMinutes: Int
)

class GetHourlyFocusDataUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository
) {
    suspend operator fun invoke(date: LocalDate, tagId: Int? = null): Flow<List<HourlyFocusData>> {
        // Convert LocalDate to Date range for that day
        val startOfDay = java.util.Date.from(date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endOfDay = java.util.Date.from(date.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        
        return if (tagId == null || tagId == 0) {
            // All tags
            sessionsRepository.getSessionsByDate(startOfDay, endOfDay).map { sessions ->
                groupSessionsByHour(sessions)
            }
        } else {
            // Specific tag
            sessionsRepository.getSessionsByTag(tagId).map { allTagSessions ->
                val filteredSessions = allTagSessions.filter { session ->
                    session.startTime?.let { startTime ->
                        startTime.after(startOfDay) && startTime.before(endOfDay)
                    } ?: false
                }
                groupSessionsByHour(filteredSessions)
            }
        }
    }
    
    private fun groupSessionsByHour(sessions: List<com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions>): List<HourlyFocusData> {
        val hourlyData = mutableMapOf<Int, Int>()
        
        // Initialize all hours with 0
        for (hour in 0..23) {
            hourlyData[hour] = 0
        }
        
        // Group sessions by hour and sum up durations
        sessions.forEach { session ->
            session.startTime?.let { startTime ->
                val calendar = java.util.Calendar.getInstance()
                calendar.time = startTime
                val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
                val durationMinutes = convertDurationToMinutes(session.duration) // Convert duration to minutes
                hourlyData[hour] = (hourlyData[hour] ?: 0) + durationMinutes
            }
        }
        
        return hourlyData.map { (hour, minutes) ->
            HourlyFocusData(hour, minutes)
        }.sortedBy { it.hour }
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