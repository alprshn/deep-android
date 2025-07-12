package com.kami_apps.deepwork.deep_work_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TagsDao {

    @Query("SELECT * FROM tags_table")
    fun getAllTags(): Flow<List<Tags>>

    @Insert
    suspend fun insertTag(tag: Tags)

    @Update
    suspend fun updateTag(tag: Tags)

    @Delete
    suspend fun deleteTag(tag: Tags)

    @Query(
        """
    SELECT 
        t.tag_name AS name, 
        t.tag_emoji AS emoji, 
        COUNT(s.session_id) AS sessionCount
    FROM tags_table t
    LEFT JOIN sessions_table s ON t.tag_id = s.tag_id
    GROUP BY t.tag_id
    ORDER BY sessionCount DESC
    LIMIT 3
"""
    )
    fun getTopTagsBySessionCount(): Flow<List<TopTag>>

}