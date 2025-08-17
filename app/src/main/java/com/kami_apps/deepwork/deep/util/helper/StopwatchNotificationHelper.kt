package com.kami_apps.deepwork.deep.util.helper

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.kami_apps.deepwork.MainActivity
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep.data.receiver.STOPWATCH_IS_PLAYING
import com.kami_apps.deepwork.deep.data.receiver.STOPWATCH_LAST_INDEX
import com.kami_apps.deepwork.deep.data.receiver.STOPWATCH_RESET_ACTION
import com.kami_apps.deepwork.deep.data.receiver.STOPWATCH_TIME
import com.kami_apps.deepwork.deep.data.receiver.StopwatchNotificationBroadcastReceiver
import com.kami_apps.deepwork.deep.util.GlobalProperties.pendingIntentFlags
import com.kami_apps.deepwork.deep.util.setIntentAction
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopwatchNotificationHelper @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
) {
    private val notificationManager = NotificationManagerCompat.from(applicationContext)
    private val stopwatchNotificationBroadcastReceiver =
        StopwatchNotificationBroadcastReceiver::class.java

    private val openStopwatchIntent = Intent(
        Intent.ACTION_VIEW,
        "https://www.clock.com/Stopwatch".toUri(),
        applicationContext,
        MainActivity::class.java,
    ).apply {
        flags = FLAG_ACTIVITY_SINGLE_TOP
    }

    private val openStopwatchPendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        openStopwatchIntent,
        pendingIntentFlags,
    )

    init {
        createStopwatchNotificationChannel()
    }


    fun getStopwatchBaseNotification() = //Bildirimi oluşturan fonskiyon
        NotificationCompat.Builder(applicationContext, STOPWATCH_WORKER_CHANNEL)
            .setContentTitle("Stopwatch")
            .setSmallIcon(R.drawable.ic_stopwatch).setOnlyAlertOnce(true)
            .setColor(ContextCompat.getColor(applicationContext, android.R.color.holo_blue_dark))
    .setContentIntent(openStopwatchPendingIntent)//Buraya tıklandığında açılacak intent yazılır.
    .setOngoing(true)//Burası çalışmaya devam etsin diye yazılır.
    .setAutoCancel(true)


    private fun createStopwatchNotificationChannel() {
        val stopwatchWorkerChannel = NotificationChannelCompat.Builder(
            STOPWATCH_WORKER_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
        )
            .setName(applicationContext.getString(R.string.stopwatch_channel))
            .setDescription(applicationContext.getString(R.string.stopwatch_worker_channel_description))
            .setSound(null, null)
            .build()

        notificationManager.createNotificationChannel(stopwatchWorkerChannel)
    }


    @SuppressLint("MissingPermission")
    fun updateStopwatchWorkerNotification(
        time: String,
        isPlaying: Boolean,
        lastLapIndex: Int,
    ) {
        val resetIntentAction = stopwatchNotificationBroadcastReceiver.setIntentAction(
            actionName = STOPWATCH_RESET_ACTION,
            requestCode = 3,
            context = applicationContext,
        )
        val stopResumeIntentAction = stopResumeIntentAction(time, isPlaying, lastLapIndex)

        val stopResumeLabel = if (isPlaying) "Stop" else "Resume"
        val stopResumeIcon = if (isPlaying) Icons.Default.Stop.hashCode() else Icons.Default.PlayArrow.hashCode()
        val lastLapIndexText = if (isPlaying && lastLapIndex != -1) "\nLap  $lastLapIndex" else ""
        val isPlayingText = if (!isPlaying) "\nPaused" else ""

        val notificationUpdate =
            getStopwatchBaseNotification()//getStopwatchBaseNotification'e ekleme yapıyor
                .setContentText("$time  $lastLapIndexText$isPlayingText")
                .addAction(stopResumeIcon, stopResumeLabel, stopResumeIntentAction)
                .addAction(Icons.Default.Timer.hashCode(), "Reset", resetIntentAction)
                .build()
        notificationManager.notify(STOPWATCH_WORKER_NOTIFICATION_ID, notificationUpdate)
    }

    private fun stopResumeIntentAction(
        time: String,
        isPlaying: Boolean,
        lastLapIndex: Int,
    ): PendingIntent {
        val broadcastIntent =
            Intent(applicationContext, stopwatchNotificationBroadcastReceiver).apply {
                putExtra(STOPWATCH_TIME, time)
                putExtra(STOPWATCH_IS_PLAYING, isPlaying)
                putExtra(STOPWATCH_LAST_INDEX, lastLapIndex)
            }
        return PendingIntent.getBroadcast(
            applicationContext,
            4,
            broadcastIntent,
            pendingIntentFlags,
        )
    }


    fun removeStopwatchNotification() {
        notificationManager.cancel(STOPWATCH_WORKER_NOTIFICATION_ID)
    }


}

private const val STOPWATCH_WORKER_CHANNEL = "stopwatch_worker_channel"
const val STOPWATCH_WORKER_NOTIFICATION_ID = 1