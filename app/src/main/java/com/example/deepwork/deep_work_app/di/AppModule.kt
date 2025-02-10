package com.example.deepwork.deep_work_app.di

import android.content.Context
import androidx.room.Room
import com.example.deepwork.deep_work_app.data.local.database.DeepWorkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn (SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideDeepWorkDatabase(@ApplicationContext context:Context): DeepWorkDatabase {
        return Room.databaseBuilder(
            context,
            DeepWorkDatabase::class.java,
            "deepwork_database"
        ).build()

    }

}