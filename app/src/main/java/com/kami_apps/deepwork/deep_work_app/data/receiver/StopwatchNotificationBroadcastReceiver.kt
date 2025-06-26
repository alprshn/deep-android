package com.kami_apps.deepwork.deep_work_app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.kami_apps.deepwork.deep_work_app.data.manager.StopwatchManager
import com.kami_apps.deepwork.deep_work_app.util.helper.StopwatchNotificationHelper
import com.kami_apps.deepwork.deep_work_app.util.safeLet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class StopwatchNotificationBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var stopwatchManager: StopwatchManager

    @Inject
    lateinit var stopwatchNotificationHelper: StopwatchNotificationHelper

    private val broadcastReceiverScope = CoroutineScope(SupervisorJob())


    override fun onReceive(context: Context?, intent: Intent?) {
        val pendingResult: PendingResult = goAsync()
        broadcastReceiverScope.launch(Dispatchers.Default) {
            try {
                val isPlaying = intent?.getBooleanExtra(STOPWATCH_IS_PLAYING, false)
                val time = intent?.getStringExtra(STOPWATCH_TIME)
                val lastLapIndex = intent?.getIntExtra(STOPWATCH_LAST_INDEX, 0)
                val action = intent?.action

                action?.let {
                    when (it) {
                        STOPWATCH_RESET_ACTION -> {
                            stopwatchManager.clear()
                            stopwatchManager.reset()
                        }
                        STOPWATCH_LAP_ACTION -> stopwatchManager.lap()
                    }
                }

                safeLet(
                    time,
                    isPlaying,
                    lastLapIndex,
                ) { safeTime, safeIsPlaying, safeLastIndex ->
                    stopwatchNotificationHelper.updateStopwatchWorkerNotification(
                        isPlaying = safeIsPlaying,
                        time = safeTime,
                        lastLapIndex = safeLastIndex,
                    )
                    if (safeIsPlaying) {
                        stopwatchManager.stop()
                    } else {
                        stopwatchManager.start()
                    }
                }
            } finally {
                pendingResult.finish()
                broadcastReceiverScope.cancel()
            }
        }
    }


}


const val STOPWATCH_TIME = "STOPWATCH_TIME"
const val STOPWATCH_IS_PLAYING = "STOPWATCH_IS_PLAYING"
const val STOPWATCH_RESET_ACTION = "STOPWATCH_RESET_ACTION"
const val STOPWATCH_LAP_ACTION = "STOPWATCH_LAP_ACTION"
const val STOPWATCH_LAST_INDEX = "STOPWATCH_LAST_INDEX"