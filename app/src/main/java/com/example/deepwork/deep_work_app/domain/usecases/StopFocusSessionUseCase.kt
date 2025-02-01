package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.domain.repository.SessionsRepository

class StopFocusSessionUseCase(private val sessionsRepository: SessionsRepository) {
    suspend operator fun invoke(sessions:Sessions){
        val currentSession = sessionsRepository.getSessionsById(sessions.sessionId)

        if (currentSession != null){
            val endTime = System.currentTimeMillis()

            // 3️⃣ Geçen süreyi hesapla (milisaniye cinsinden)
            val newDuration = endTime - currentSession.startTime.time

            // 4️⃣ Yeni süreyi kaydederek oturumu güncelle
            val updatedSession = currentSession.copy(duration = newDuration)

            // 5️⃣ Güncellenmiş seansı veri tabanına kaydet
            sessionsRepository.updateSession(updatedSession)
        }
    }
}