package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.UserPreferences
import com.kami_apps.deepwork.deep.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor (private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences? {
        return userPreferencesRepository.getUserPreferences()
    }
}