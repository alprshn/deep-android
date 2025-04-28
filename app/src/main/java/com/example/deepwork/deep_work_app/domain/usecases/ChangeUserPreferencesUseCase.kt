package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.UserPreferences
import com.example.deepwork.deep_work_app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ChangeUserPreferencesUseCase @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(theme: String, haptic:Boolean) {
        val preferences = UserPreferences(theme = theme, haptic = haptic)
        userPreferencesRepository.savePreferences(preferences)
    }
}