package com.kami_apps.deepwork.deep.domain.repository

import com.kami_apps.deepwork.deep.data.local.entities.UserPreferences

interface UserPreferencesRepository {
    suspend fun savePreferences(preferences: UserPreferences)
    suspend fun getUserPreferences(): UserPreferences?

}