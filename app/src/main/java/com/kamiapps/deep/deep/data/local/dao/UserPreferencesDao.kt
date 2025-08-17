package com.kamiapps.deep.deep.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kamiapps.deep.deep.data.local.entities.UserPreferences

@Dao
interface UserPreferencesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreferences(preferences: UserPreferences)

    @Query("SELECT * FROM userPreferences_table LIMIT 1")
    suspend fun getUserPreferences(): UserPreferences?
}