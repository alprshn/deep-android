package com.kami_apps.deepwork.deep_work_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {

    @Query("SELECT * FROM tags_table")
    fun getAllTags():Flow<List<Tags>>

    @Insert
    suspend fun insertTag(tag: Tags)

    @Update
    suspend fun updateTag(tag:Tags)

    @Delete
    suspend fun deleteTag(tag:Tags)
}