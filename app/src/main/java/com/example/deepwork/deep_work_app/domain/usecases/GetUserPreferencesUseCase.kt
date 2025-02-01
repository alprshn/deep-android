package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.UserPreferences
import com.example.deepwork.deep_work_app.domain.repository.UserPreferencesRepository

class GetUserPreferencesUseCase(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences? {
        return userPreferencesRepository.getUserPreferences()
    }
}