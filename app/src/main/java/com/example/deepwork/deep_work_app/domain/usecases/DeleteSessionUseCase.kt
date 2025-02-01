package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository

class DeleteSessionUseCase(private val sessionsRepository : SessionsRepository)  {
    suspend operator fun invoke(sessions:Sessions) = sessionsRepository.deleteSession(sessions)
}