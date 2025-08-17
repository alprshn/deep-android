package com.kamiapps.deep.deep.domain.data

import android.graphics.drawable.Drawable

data class InstalledApp(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val isSelected: Boolean = false
)