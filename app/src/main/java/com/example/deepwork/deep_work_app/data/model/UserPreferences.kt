package com.example.deepwork.deep_work_app.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userPreferences_table")
data class UserPreferences(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "preference_id") val preferenceId: Int,
    @ColumnInfo(name = "theme_id") val theme: String,
    @ColumnInfo(name = "haptic") val haptic: Boolean
)