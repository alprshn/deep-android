package com.kami_apps.deepwork.deep_work_app.domain.repository

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface SessionsRepository {

    suspend fun insertSession(session: Sessions)

    suspend fun updateSession(session: Sessions)

    suspend fun deleteSession(session: Sessions)

    fun getAllSessions(): Flow<List<Sessions>>

    suspend fun getSessionsById(sessionId: Int): Sessions?

    suspend fun getSessionsByTag(tagId: Int): Flow<List<Sessions>>

    suspend fun getSessionsByDate(startDate: Date, endDate: Date): Flow<List<Sessions>>

    suspend fun getTotalFocusTime(): String

    suspend fun getSessionCount(): Int

    suspend fun getAverageSessionDuration(): Double
}