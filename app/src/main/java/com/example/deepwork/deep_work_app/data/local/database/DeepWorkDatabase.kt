package com.example.deepwork.deep_work_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.deepwork.deep_work_app.data.local.dao.SessionsDao
import com.example.deepwork.deep_work_app.data.local.dao.TagsDao
import com.example.deepwork.deep_work_app.data.local.entities.LiveActivities
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.data.local.entities.UserPreferences

@Database(
    entities = [Tags::class, Sessions::class, UserPreferences::class, LiveActivities::class],
    version = 1,
    exportSchema = false
)
abstract class DeepWorkDatabase : RoomDatabase() {
    abstract fun tagsDao(): TagsDao
    abstract fun sessionsDao(): SessionsDao

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