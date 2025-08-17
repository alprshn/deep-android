package com.kami_apps.deepwork.deep.data.workManager.worker

import androidx.hilt.work.HiltWorker
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep.data.manager.StopwatchManager
import com.kami_apps.deepwork.deep.util.helper.STOPWATCH_WORKER_NOTIFICATION_ID
import com.kami_apps.deepwork.deep.util.helper.StopwatchNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.collectLatest

@HiltWorker
class StopwatchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val stopwatchManager: StopwatchManager,
    private val stopwatchNotificationHelper: StopwatchNotificationHelper,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            val foregroundInfo = createForegroundInfo()
            setForeground(foregroundInfo)

            stopwatchManager.stopwatchState.collectLatest {
                              // her saniye tek emission
                if (!it.isReset) {
                    stopwatchNotificationHelper.updateStopwatchWorkerNotification(
                        time = "${it.minute}:${it.second}",
                        isPlaying = it.isPlaying,
                        lastLapIndex = stopwatchManager.lapTimes.lastIndex,
                    )
                }
            }

            Result.success()
        } catch (e: CancellationException) {
            stopwatchNotificationHelper.removeStopwatchNotification()
            Result.failure()
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = stopwatchNotificationHelper
            .getStopwatchBaseNotification()
            .setSmallIcon(R.drawable.ic_stopwatch)   // ← Bkz. ikon notu
            .build()

        val fgsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            // API 34+  → specialUse
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            // API 29-33 → mediaPlayback en yakın ve her cihazda tanımlı
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        }

        return ForegroundInfo(STOPWATCH_WORKER_NOTIFICATION_ID, notification, fgsType)
    }
}

const val STOPWATCH_TAG = "stopwatchTag"