package com.example.deepwork.deep_work_app.domain.repository

import com.example.deepwork.deep_work_app.data.local.entities.Tags
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    suspend fun insertTag(tags: Tags)

    suspend fun updateTag(tags: Tags)

    suspend fun deleteTag(tags: Tags)

    fun getAllTags(): Flow<List<Tags>>

}