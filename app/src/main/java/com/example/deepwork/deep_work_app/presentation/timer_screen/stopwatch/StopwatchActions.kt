package com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch

import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags

interface StopwatchActions {
    fun start(session: Sessions) {}
    fun stop(tags: Tags) {}
    fun lap() {}
    fun clear() {}
    fun reset() {}
}