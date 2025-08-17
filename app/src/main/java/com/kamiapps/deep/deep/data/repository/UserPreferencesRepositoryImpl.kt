package com.kamiapps.deep.deep.data.repository

import com.kamiapps.deep.deep.data.local.dao.UserPreferencesDao
import com.kamiapps.deep.deep.data.local.entities.UserPreferences
import com.kamiapps.deep.deep.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class UserPreferencesRepositoryImpl @Inject constructor(private val userPreferencesDao: UserPreferencesDao) :
    UserPreferencesRepository {
    override suspend fun savePreferences(preferences: UserPreferences) = userPreferencesDao.savePreferences(preferences)
    override suspend fun getUserPreferences(): UserPreferences? = userPreferencesDao.getUserPreferences()
}
