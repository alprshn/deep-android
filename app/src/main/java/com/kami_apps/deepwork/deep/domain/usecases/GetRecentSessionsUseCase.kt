package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.Sessions
import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import com.kami_apps.deepwork.deep.domain.repository.TagsRepository
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.SessionLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GetRecentSessionsUseCase @Inject constructor(
    private val sessionsRepository: SessionsRepository,
    private val tagsRepository: TagsRepository
) {
    suspend operator fun invoke(limit: Int = 15): Flow<List<SessionLog>> {
        return combine(
            sessionsRepository.getAllSessions(),
            tagsRepository.getAllTags()
        ) { sessions, tags ->
            val tagsMap = tags.associateBy { it.tagId }
            
            sessions
                .filter { session -> 
                    // Filter out sessions with null or invalid data
                    session.startTime != null && 
                    session.finishTime != null && 
                    session.duration.isNotBlank() &&
                    tagsMap.containsKey(session.tagId)
                }
                .sortedByDescending { it.startTime } // Most recent first
                .take(limit)
                .mapNotNull { session ->
                    val tag = tagsMap[session.tagId]
                    if (tag != null && session.startTime != null) {
                        mapToSessionLog(session, tag)
                    } else null
                }
        }
    }
    
    private fun mapToSessionLog(session: Sessions, tag: Tags): SessionLog {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        
        val date = dateFormat.format(session.startTime!!)
        val time = timeFormat.format(session.startTime!!)
        val duration = formatDuration(session.duration)
        
        return SessionLog(
            tagName = tag.tagName,
            tagEmoji = tag.tagEmoji.takeIf { it.isNotBlank() } ?: "📖",
            date = date,
            time = time,
            duration = duration
        )
    }
    
    private fun formatDuration(duration: String): String {
        // Handle different duration formats
        return when {
            duration.contains(":") -> {
                // Format like "25:30" (mm:ss)
                val parts = duration.split(":")
                val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                val totalMinutes = minutes + (seconds / 60)
                val remainingSeconds = seconds % 60
                
                when {
                    totalMinutes >= 60 -> {
                        val hours = totalMinutes / 60
                        val mins = totalMinutes % 60
                        "${hours}h ${mins}m"
                    }
                    totalMinutes > 0 -> "${totalMinutes}m"
                    else -> "0m"
                }
            }
            duration.toIntOrNull() != null -> {
                // Pure number format (assuming minutes)
                val minutes = duration.toInt()
                when {
                    minutes >= 60 -> {
                        val hours = minutes / 60
                        val mins = minutes % 60
                        "${hours}h ${mins}m"
                    }
                    minutes > 0 -> "${minutes}m"
                    else -> "0m"
                }
            }
            else -> duration // Return as-is if unrecognized format
        }
    }
} 
 