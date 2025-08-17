package com.kamiapps.deep.deep.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "sessions_table",
    foreignKeys = [
        ForeignKey(
            entity = Tags::class,
            parentColumns = ["tag_id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["tag_id"])        // ← burayı ekledik
    ]

)
data class Sessions(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "session_id") val sessionId: Int = 0,
    @ColumnInfo(name = "tag_id") val tagId: Int,
    @ColumnInfo(name = "start_time") val startTime: Date?,
    @ColumnInfo(name = "finish_time") val finishTime: Date?,
    @ColumnInfo(name = "duration") val duration: String,
    @ColumnInfo(name = "session_emoji") val sessionEmoji: String
    //Buraya finish time eklenebilir incele
)