package com.kamiapps.deep.deep.util

import android.app.PendingIntent
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object GlobalProperties {

    const val TIME_FORMAT = "%02d:%02d:%02d"

    @RequiresApi(Build.VERSION_CODES.O)
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE,MMMdd")
    @RequiresApi(Build.VERSION_CODES.O)
    val nextDay: LocalDateTime = LocalDateTime.now().plus(1, ChronoUnit.DAYS)

    const val pendingIntentFlags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
}