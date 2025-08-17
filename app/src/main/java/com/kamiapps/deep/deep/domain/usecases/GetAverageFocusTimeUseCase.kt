package com.kamiapps.deep.deep.domain.usecases

import android.util.Log
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAverageFocusTimeUseCase @Inject constructor (private val sessionsRepository: SessionsRepository) {

    operator fun invoke(): Flow<String> {
        return sessionsRepository.getAllSessions()
            .map { sessions ->
                val durations = sessions.map { it.duration }
                Log.e("calculateAverageTotalTime", calculateAverageTotalTime(durations))

                calculateAverageTotalTime(durations)

            }

    }

    private fun calculateAverageTotalTime(durations: List<String>): String {
        var totalSeconds = 0
        var totalMinutes = 0

        durations.forEach { duration ->
            val parts = duration.split(":")
            val minute = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: 0
            val second = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 0
            totalMinutes += minute
            totalSeconds += second
        }

        totalMinutes += totalSeconds / 60
        totalMinutes /= durations.size
        // val remainingSeconds = totalSeconds % 60
        val hours = totalMinutes / 60
        val remainingMinutes = totalMinutes % 60

        return buildString {
            if (hours > 0) append("$hours h ")
            if (remainingMinutes > 0) append("${remainingMinutes}m ")
        }.trim()
    }

}