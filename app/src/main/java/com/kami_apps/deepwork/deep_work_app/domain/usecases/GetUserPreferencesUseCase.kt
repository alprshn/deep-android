package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.data.local.entities.UserPreferences
import com.kami_apps.deepwork.deep_work_app.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor (private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences? {
        return userPreferencesRepository.getUserPreferences()
    }
}