package com.example.deepwork.deep_work_app.data.workManager.worker

import androidx.hilt.work.HiltWorker
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import com.example.deepwork.deep_work_app.util.STOPWATCH_WORKER_NOTIFICATION_ID
import com.example.deepwork.deep_work_app.util.StopwatchNotificationHelper
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
            val foregroundInfo = ForegroundInfo(
                STOPWATCH_WORKER_NOTIFICATION_ID,
                stopwatchNotificationHelper.getStopwatchBaseNotification().build(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK else 0
            )

            setForeground(foregroundInfo)

            stopwatchManager.stopwatchState.collectLatest {
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
}

const val STOPWATCH_TAG = "stopwatchTag"
