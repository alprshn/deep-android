package com.kamiapps.deep.deep.domain.repository

import com.kamiapps.deep.deep.data.local.entities.UserPreferences

interface UserPreferencesRepository {
    suspend fun savePreferences(preferences: UserPreferences)
    suspend fun getUserPreferences(): UserPreferences?

}