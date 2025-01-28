package com.example.deepwork.deep_work_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionsDao{
    @Query("SELECT * FROM sessions_table")
    fun getAllSessions(): Flow<List<Sessions>>

    @Insert
    fun insertSession(session: Sessions)

    @Update
    fun updateSessions(session:Sessions)

    @Delete
    fun deleteSessions(session:Sessions)
}