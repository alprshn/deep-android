package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import javax.inject.Inject

class StartFocusSessionUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(session: Sessions) = sessionsRepository.insertSession(session)
}