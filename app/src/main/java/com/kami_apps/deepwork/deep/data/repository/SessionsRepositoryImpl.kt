package com.kami_apps.deepwork.deep.data.repository

import com.kami_apps.deepwork.deep.data.local.dao.SessionsDao
import com.kami_apps.deepwork.deep.data.local.entities.Sessions
import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class SessionsRepositoryImpl @Inject constructor(
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

    override suspend fun getSessionsById(sessionId: Int): Sessions? {
        return sessionsDao.getSessionsById(sessionId)
    }

    override fun getSessionsByTag(tagId: Int): Flow<List<Sessions>> {
        return sessionsDao.getSessionsByTag(tagId)
    }

    override suspend fun getSessionsByDate(startDate: Date, endDate: Date): Flow<List<Sessions>> {
       return sessionsDao.getSessionsByDate(startDate,endDate)
    }

    override suspend fun getTotalFocusTime(): String {
        return sessionsDao.getTotalFocusTime()
    }

    override suspend fun getSessionCountByTag(tagId: Int): Flow<Int> {
        return sessionsDao.getSessionCountByTag(tagId)
    }

    override suspend fun getSessionCount(): Int {
        return sessionsDao.getSessionCount()
    }

    override suspend fun getAverageSessionDuration(): Double {
        return sessionsDao.getAverageSessionDuration()
    }

}