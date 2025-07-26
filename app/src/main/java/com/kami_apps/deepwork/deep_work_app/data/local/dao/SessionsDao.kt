package com.kami_apps.deepwork.deep_work_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SessionsDao{
    @Query("SELECT * FROM sessions_table")
    fun getAllSessions(): Flow<List<Sessions>>

    @Insert
    suspend fun insertSession(session: Sessions)

    @Update
    suspend fun updateSessions(session:Sessions)

    @Delete
    suspend fun deleteSessions(session:Sessions)

    @Query("SELECT * FROM sessions_table WHERE session_id = :sessionId")
    suspend fun getSessionsById(sessionId: Int): Sessions

    @Query("SELECT * FROM sessions_table WHERE tag_id = :tagId")
     fun getSessionsByTag(tagId: Int): Flow<List<Sessions>>

    @Query("SELECT * FROM sessions_table WHERE start_time >= :startDate AND start_time <= :endDate ORDER BY start_time ASC")
     fun getSessionsByDate(startDate: Date, endDate: Date): Flow<List<Sessions>>

    @Query("SELECT duration FROM sessions_table")
    suspend fun getTotalFocusTime(): String

    @Query("SELECT COUNT(*) FROM sessions_table")
    suspend fun getSessionCount(): Int

    @Query("SELECT COUNT(session_id) FROM sessions_table WHERE tag_id = :tagId")
    fun getSessionCountByTag(tagId: Int): Flow<Int>

    @Query("SELECT AVG(duration) FROM sessions_table")
    suspend fun getAverageSessionDuration(): Double
}