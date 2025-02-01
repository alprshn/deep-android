package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow

class GetTotalFocusTimeUseCase(private val sessionsRepository: SessionsRepository)  {
    suspend operator fun invoke(): Long = sessionsRepository.getTotalFocusTime()
}