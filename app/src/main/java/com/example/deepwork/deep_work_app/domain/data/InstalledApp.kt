package com.example.deepwork.deep_work_app.domain.data

import android.graphics.drawable.Drawable

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val isSelected: Boolean = false
)