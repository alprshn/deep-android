package com.kami_apps.deepwork.deep.domain.data

import androidx.annotation.DrawableRes

data class AppIcon(
    val id: String,
    val name: String,
    val activityAlias: String,
    @DrawableRes val iconRes: Int,
    val isSelected: Boolean = false,
    val isPremium: Boolean = false
) 