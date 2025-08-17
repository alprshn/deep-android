package com.kamiapps.deep.deep.domain.repository

import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow

interface TagsRepository {

    suspend fun insertTag(tags: Tags)

    suspend fun updateTag(tags: Tags)

    suspend fun deleteTag(tags: Tags)

    fun getAllTags(): Flow<List<Tags>>

    fun getTopTagsBySessionCount(): Flow<List<TopTag>>
}