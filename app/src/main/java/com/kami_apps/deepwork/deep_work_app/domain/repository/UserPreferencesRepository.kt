package com.kami_apps.deepwork.deep_work_app.domain.repository

import com.kami_apps.deepwork.deep_work_app.data.local.entities.UserPreferences

interface UserPreferencesRepository {
    suspend fun savePreferences(preferences: UserPreferences)
    suspend fun getUserPreferences(): UserPreferences?

}