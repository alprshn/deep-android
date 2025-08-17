package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import javax.inject.Inject

class DeleteSessionUseCase @Inject constructor(private val sessionsRepository : SessionsRepository)  {
    suspend operator fun invoke(sessions:Sessions) = sessionsRepository.deleteSession(sessions)
}