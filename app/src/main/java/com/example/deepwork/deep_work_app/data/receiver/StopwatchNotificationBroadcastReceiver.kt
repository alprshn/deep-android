package com.example.deepwork.deep_work_app.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import com.example.deepwork.deep_work_app.util.StopwatchNotificationHelper
import javax.inject.Inject

class StopwatchNotificationBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var stopwatchManager: StopwatchManager

    @Inject
    lateinit var stopwatchNotificationHelper: StopwatchNotificationHelper


    override fun onReceive(context: Context?, intent: Intent?) {
        TODO("Not yet implemented")
    }


}


const val STOPWATCH_TIME = "STOPWATCH_TIME"
const val STOPWATCH_IS_PLAYING = "STOPWATCH_IS_PLAYING"
const val STOPWATCH_RESET_ACTION = "STOPWATCH_RESET_ACTION"
const val STOPWATCH_LAP_ACTION = "STOPWATCH_LAP_ACTION"
const val STOPWATCH_LAST_INDEX = "STOPWATCH_LAST_INDEX"