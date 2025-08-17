package com.kamiapps.deep.deep.di

import android.content.Context
import androidx.room.Room
import com.kamiapps.deep.deep.data.local.database.DeepWorkDatabase
import com.kamiapps.deep.deep.data.repository.AppRepositoryImpl
import com.kamiapps.deep.deep.data.repository.SessionsRepositoryImpl
import com.kamiapps.deep.deep.data.repository.TagsRepositoryImpl
import com.kamiapps.deep.deep.data.repository.UserPreferencesRepositoryImpl
import com.kamiapps.deep.deep.data.manager.PremiumManager
import com.kamiapps.deep.deep.data.manager.RevenueCatManager
import com.kamiapps.deep.deep.data.manager.ThemeManager
import com.kamiapps.deep.deep.domain.repository.AppRepository
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import com.kamiapps.deep.deep.domain.repository.TagsRepository
import com.kamiapps.deep.deep.domain.repository.UserPreferencesRepository
import com.kamiapps.deep.deep.util.helper.HapticFeedbackHelper
import com.kamiapps.deep.deep.domain.usecases.ChangeUserPreferencesUseCase
import com.kamiapps.deep.deep.domain.usecases.GetUserPreferencesUseCase
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

    @Provides
    @Singleton
    fun providePremiumManager(@ApplicationContext context: Context): PremiumManager {
        return PremiumManager(context)
    }

    @Provides
    @Singleton
    fun provideRevenueCatManager(
        @ApplicationContext context: Context
    ): RevenueCatManager {
        return RevenueCatManager(context)
    }

    @Provides
    @Singleton
    fun provideHapticFeedbackHelper(
        @ApplicationContext context: Context
    ): HapticFeedbackHelper {
        return HapticFeedbackHelper(context)
    }

    @Provides
    @Singleton
    fun provideThemeManager(
        getUserPreferencesUseCase: GetUserPreferencesUseCase,
        changeUserPreferencesUseCase: ChangeUserPreferencesUseCase
    ): ThemeManager {
        return ThemeManager(getUserPreferencesUseCase, changeUserPreferencesUseCase)
    }

}