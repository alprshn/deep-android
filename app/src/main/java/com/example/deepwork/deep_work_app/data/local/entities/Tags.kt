package com.example.deepwork.deep_work_app.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags_table")
data class Tags(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "tag_id") val tagId: Int = 0,
    @ColumnInfo(name = "tag_name") val tagName: String,
    @ColumnInfo(name = "tag_emoji") val tagEmoji: String,
    @ColumnInfo(name = "tag_color") val tagColor: String,
    //@ColumnInfo(name = "tag_order") val tagOrder: Int
)

