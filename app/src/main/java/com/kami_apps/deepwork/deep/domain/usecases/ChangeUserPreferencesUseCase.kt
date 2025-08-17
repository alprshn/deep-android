package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.UserPreferences
import com.kami_apps.deepwork.deep.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class ChangeUserPreferencesUseCase @Inject constructor(private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(theme: String, haptic:Boolean) {
        val preferences = UserPreferences(theme = theme, haptic = haptic)
        userPreferencesRepository.savePreferences(preferences)
    }
}