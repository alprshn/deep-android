package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSessionsByTagUseCase @Inject constructor (private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(tagId: Int): Flow<List<Sessions>> {
        return sessionsRepository.getSessionsByTag(tagId)
    }

}