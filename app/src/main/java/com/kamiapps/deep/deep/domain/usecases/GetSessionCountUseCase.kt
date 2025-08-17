package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import javax.inject.Inject

class GetTotalSessionCountUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(): Int {
        return sessionsRepository.getSessionCount()
    }
}