package com.kami_apps.deepwork.deep_work_app.domain.usecases

import android.util.Log
import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import javax.inject.Inject

class GetTotalFocusTimeUseCase @Inject constructor (private val sessionsRepository: SessionsRepository)  {
    suspend operator fun invoke(): Long {
        Log.e("GetTotalFocusTimeUseCase", sessionsRepository.getTotalFocusTime().toString())
        Log.e("GetTotalFocusTimeUseCase Count", sessionsRepository.getSessionCount().toString())

    return 0
    }
}