package com.example.deepwork.deep_work_app.data.repository

import com.example.deepwork.deep_work_app.data.local.dao.UserPreferencesDao
import com.example.deepwork.deep_work_app.data.local.entities.UserPreferences
import com.example.deepwork.deep_work_app.domain.repository.UserPreferencesRepository

class UserPreferencesRepositoryImpl(private val userPreferencesDao: UserPreferencesDao) :
    UserPreferencesRepository {
    override suspend fun savePreferences(preferences: UserPreferences) = userPreferencesDao.savePreferences(preferences)
    override suspend fun getUserPreferences(): UserPreferences? = userPreferencesDao.getUserPreferences()
}
