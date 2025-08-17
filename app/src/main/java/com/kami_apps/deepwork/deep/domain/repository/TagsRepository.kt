package com.kami_apps.deepwork.deep.domain.repository

import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    suspend fun insertTag(tags: Tags)

    suspend fun updateTag(tags: Tags)

    suspend fun deleteTag(tags: Tags)

    fun getAllTags(): Flow<List<Tags>>

    fun getTopTagsBySessionCount(): Flow<List<TopTag>>
}