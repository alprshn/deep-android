package com.kami_apps.deepwork.deep_work_app.data.workManager.worker

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.kami_apps.deepwork.deep_work_app.data.manager.TimerManager
import com.kami_apps.deepwork.deep_work_app.util.helper.MediaPlayerHelper
import com.kami_apps.deepwork.deep_work_app.util.helper.TIMER_COMPLETED_NOTIFICATION_ID
import com.kami_apps.deepwork.deep_work_app.util.helper.TimerNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class TimerCompletedWorker @AssistedInject constructor(
     private val mediaPlayerHelper: MediaPlayerHelper,
     private val timerNotificationHelper: TimerNotificationHelper,
     private val timerManager: TimerManager,
    @Assisted ctx: Context,
    @Assisted params: WorkerParameters,
) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            mediaPlayerHelper.prepare()
            mediaPlayerHelper.start()

            val foregroundInfo = ForegroundInfo(
                TIMER_COMPLETED_NOTIFICATION_ID,
                timerNotificationHelper.showTimerCompletedNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
            )
            setForeground(foregroundInfo)

            // Ensure the work lasts at least 5 seconds for the notification to be shown
            // because shorter durations may not provide enough time for the notification to be visible.
            timerManager.timerState.collectLatest {}

            Result.success()
        } catch (e: CancellationException) {
            mediaPlayerHelper.release()
            timerNotificationHelper.removeTimerCompletedNotification()
            Result.failure()
        }
    }
}

const val TIMER_COMPLETED_TAG = "timerCompletedTag"