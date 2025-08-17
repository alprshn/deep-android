package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetSessionCountByTagUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(tagId: Int): Flow<Int> {
        return sessionsRepository.getSessionCountByTag(tagId)
    }
}