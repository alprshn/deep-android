package com.kami_apps.deepwork.deep.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userPreferences_table")
data class UserPreferences(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id") val preferenceId: Int = 1 ,
    @ColumnInfo(name = "theme_id") val theme: String,
    @ColumnInfo(name = "haptic") val haptic: Boolean
)