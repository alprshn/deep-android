package com.kami_apps.deepwork.deep_work_app.data.repository

import com.kami_apps.deepwork.deep_work_app.data.local.dao.UserPreferencesDao
import com.kami_apps.deepwork.deep_work_app.data.local.entities.UserPreferences
import com.kami_apps.deepwork.deep_work_app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(private val userPreferencesDao: UserPreferencesDao) :
    UserPreferencesRepository {
    override suspend fun savePreferences(preferences: UserPreferences) = userPreferencesDao.savePreferences(preferences)
    override suspend fun getUserPreferences(): UserPreferences? = userPreferencesDao.getUserPreferences()
}
