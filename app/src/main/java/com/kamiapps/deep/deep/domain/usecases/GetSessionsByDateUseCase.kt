package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Sessions
import com.kamiapps.deep.deep.domain.repository.SessionsRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetSessionsByDateUseCase @Inject constructor (private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(startDate: Date, endDate: Date): Flow<List<Sessions>> {
        return sessionsRepository.getSessionsByDate(startDate, endDate)
    }

}