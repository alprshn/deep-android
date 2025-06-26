package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import javax.inject.Inject

class GetSessionCountUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(): Int {
        return sessionsRepository.getSessionCount()
    }}