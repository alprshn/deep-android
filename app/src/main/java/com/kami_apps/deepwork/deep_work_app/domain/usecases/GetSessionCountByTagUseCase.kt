package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Sessions
import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetSessionCountByTagUseCase @Inject constructor(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(tagId: Int): Flow<Int> {
        return sessionsRepository.getSessionCountByTag(tagId)
    }
}