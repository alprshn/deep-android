package com.example.deepwork.deep_work_app.domain.repository

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import kotlinx.coroutines.flow.Flow

interface SessionsRepository {

    suspend fun insertSession(session: Sessions)

    suspend fun updateSession(session: Sessions)

    suspend fun deleteSession(session: Sessions)

    fun getAllSessions(): Flow<List<Sessions>>
}