package com.kami_apps.deepwork.deep.data.model

data class StopwatchState(
    val second: String = "00",
    val minute: String = "00",
    val hour: String = "00",
    val isPlaying: Boolean = false,
    val isReset: Boolean = true,
)


// Bu class ui da gördüğümüz durumu izlemek için oluşturuldu stop watch için
