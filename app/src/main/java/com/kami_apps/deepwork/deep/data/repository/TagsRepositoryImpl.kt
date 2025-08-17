package com.kami_apps.deepwork.deep.data.repository

import com.kami_apps.deepwork.deep.data.local.dao.TagsDao
import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.domain.repository.TagsRepository
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TagsRepositoryImpl @Inject constructor(
    private val tagsDao: TagsDao
) : TagsRepository {
    override suspend fun insertTag(tags: Tags) {
        tagsDao.insertTag(tags)
    }

    override suspend fun updateTag(tags: Tags) {
        tagsDao.updateTag(tags)
    }

    override suspend fun deleteTag(tags: Tags) {
        tagsDao.deleteTag(tags)
    }

    override fun getAllTags(): Flow<List<Tags>> {
        return tagsDao.getAllTags()
    }

    override fun getTopTagsBySessionCount(): Flow<List<TopTag>> {
        return tagsDao.getTopTagsBySessionCount()
    }


}