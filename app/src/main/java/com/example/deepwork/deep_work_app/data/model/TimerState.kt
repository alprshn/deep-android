package com.example.deepwork.deep_work_app.data.model

data class TimerState(
    val timeInMillis: Long = 0L,
    val timeText: String = "00:00:00",
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val progress: Float = 0f,
    val isPlaying: Boolean = false,
    val isDone: Boolean = true,
)

// Bu class ui da gördüğümüz durumu izlemek için oluşturuldu timer için

