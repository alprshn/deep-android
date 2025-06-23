package com.example.deepwork.deep_work_app.di

import android.content.Context
import androidx.room.Room
import com.example.deepwork.deep_work_app.data.local.database.DeepWorkDatabase
import com.example.deepwork.deep_work_app.data.repository.AppRepositoryImpl
import com.example.deepwork.deep_work_app.data.repository.SessionsRepositoryImpl
import com.example.deepwork.deep_work_app.data.repository.TagsRepositoryImpl
import com.example.deepwork.deep_work_app.data.repository.UserPreferencesRepositoryImpl
import com.example.deepwork.deep_work_app.domain.repository.AppRepository
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository
import com.example.deepwork.deep_work_app.domain.repository.TagsRepository
import com.example.deepwork.deep_work_app.domain.repository.UserPreferencesRepository
import dagger.Binds
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
    fun provideDeepWorkDatabase(@ApplicationContext context: Context): DeepWorkDatabase {
        return Room.databaseBuilder(
            context,
            DeepWorkDatabase::class.java,
            "deepwork_database.sqlite"
        ).build()

    }

    @Provides
    fun provideTagsRepository(database: DeepWorkDatabase): TagsRepository {
        return TagsRepositoryImpl(database.tagsDao())
    }

    @Provides
    fun provideSessionsRepository(database: DeepWorkDatabase): SessionsRepository {
        return SessionsRepositoryImpl(database.sessionsDao())
    }

    @Provides
    fun provideUserPreferencesRepository(database: DeepWorkDatabase): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(database.userPreferencesDao())
    }
    @Provides
    fun provideAppRepository(@ApplicationContext context: Context): AppRepository {
        return AppRepositoryImpl(context)
    }


 }