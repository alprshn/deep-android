package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.Sessions
import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(private val sessionsRepository : SessionsRepository)  {
    suspend operator fun invoke(sessions:Sessions) = sessionsRepository.deleteSession(sessions)
}