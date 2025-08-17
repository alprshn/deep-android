package com.kamiapps.deep.deep.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liveActivities_table")
data class LiveActivities(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activity_id") val activityId: Int,
    @ColumnInfo(name = "task_title") val taskTitle: String,
    @ColumnInfo(name = "task_name") val taskName: String,
    @ColumnInfo(name= "time_remaining") val timeRemaining: Int,
)