package com.kamiapps.deep.deep.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kamiapps.deep.deep.data.local.dao.SessionsDao
import com.kamiapps.deep.deep.data.local.dao.TagsDao
import com.kamiapps.deep.deep.data.local.dao.UserPreferencesDao
import com.kamiapps.deep.deep.data.local.entities.LiveActivities
import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.data.local.entities.UserPreferences
import com.kamiapps.deep.deep.data.util.DateConverter

@Database(
    entities = [Tags::class, Sessions::class, UserPreferences::class, LiveActivities::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun tagsDao(): TagsDao
    abstract fun sessionsDao(): SessionsDao
    abstract fun userPreferencesDao(): UserPreferencesDao
    companion object {
        @Volatile
        private var INSTANCE: DeepWorkDatabase? = null

        fun getDatabase(context: Context): DeepWorkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DeepWorkDatabase::class.java,
                    "deepwork_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}