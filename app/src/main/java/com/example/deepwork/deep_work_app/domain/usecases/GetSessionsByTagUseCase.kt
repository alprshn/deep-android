package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsByTagUseCase @Inject constructor (private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(tagId: Int): Flow<List<Sessions>> {
        return sessionsRepository.getSessionsByTag(tagId)
    }

}