package com.example.deepwork.deep_work_app.data.repository

import com.example.deepwork.deep_work_app.data.local.dao.SessionsDao
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow

class SessionsRepositoryImpl(
    private val sessionsDao: SessionsDao
): SessionsRepository {
    override suspend fun insertSession(session: Sessions) {
        sessionsDao.insertSession(session)
    }

    override suspend fun updateSession(session: Sessions) {
        sessionsDao.updateSessions(session)
    }

    override suspend fun deleteSession(session: Sessions) {
        sessionsDao.deleteSessions(session)
    }

    override fun getAllSessions(): Flow<List<Sessions>> {
        return sessionsDao.getAllSessions()
    }

}