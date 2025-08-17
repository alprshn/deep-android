package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.Sessions
import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(session: Sessions) = sessionsRepository.insertSession(session)
}