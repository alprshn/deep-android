package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import java.util.Date

interface StopwatchActions {
    fun start() {}
    fun stop() {}
    fun lap() {}
    fun clear() {}
    fun reset() {}
}