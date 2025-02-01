package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class GetSessionsByDateUseCase(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(startDate: Date, endDate: Date): Flow<List<Sessions>> {
        return sessionsRepository.getSessionsByDate(startDate, endDate)
    }

}