package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions
import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(session: Sessions) = sessionsRepository.insertSession(session)
}