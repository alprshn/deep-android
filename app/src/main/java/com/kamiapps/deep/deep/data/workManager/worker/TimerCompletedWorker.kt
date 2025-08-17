package com.kamiapps.deep.deep.data.workManager.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.kamiapps.deep.deep.data.manager.TimerManager
import com.kamiapps.deep.deep.util.helper.HapticFeedbackHelper
import com.kamiapps.deep.deep.util.helper.MediaPlayerHelper
import com.kamiapps.deep.deep.util.helper.TIMER_COMPLETED_NOTIFICATION_ID
import com.kamiapps.deep.deep.util.helper.TimerNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class TimerCompletedWorker @AssistedInject constructor(
    // ESKİ: private val mediaPlayerHelper: MediaPlayerHelper,  <-- KALDIR
    private val timerNotificationHelper: TimerNotificationHelper,
    private val timerManager: TimerManager,
    // YENİ: uzun titreşim için helper
    private val hapticFeedbackHelper: HapticFeedbackHelper,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            // 🔇 Ses yok, sadece uzun titreşim
            hapticFeedbackHelper.performTimerCompletionVibration()

            val foregroundInfo = ForegroundInfo(
                TIMER_COMPLETED_NOTIFICATION_ID,
                timerNotificationHelper.showTimerCompletedNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK // İstersen 0 bırakabilirsin; işlevi etkilemez.
                else 0
            )
            setForeground(foregroundInfo)

            // Bildirim görünür kalsın diye kısa bekleme yeterli
            // (daha önce sonsuza kadar collect ediyordu, gereksiz)
            kotlinx.coroutines.delay(5000)

            Result.success()
        } catch (e: CancellationException) {
            // güvenlik: herhangi bir şey çalıyorsa dursun, bildirim kalksın
            timerNotificationHelper.removeTimerCompletedNotification()
            Result.failure()
        }
    }
}


const val TIMER_COMPLETED_TAG = "timerCompletedTag"