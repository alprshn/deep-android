package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.UserPreferences
import com.kamiapps.deep.deep.domain.repository.UserPreferencesRepository
import javax.inject.Inject

class GetUserPreferencesUseCase @Inject constructor (private val userPreferencesRepository: UserPreferencesRepository) {
    suspend operator fun invoke(): UserPreferences? {
        return userPreferencesRepository.getUserPreferences()
    }
}