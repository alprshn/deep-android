package com.kamiapps.deep.deep.data.repository

import com.kamiapps.deep.deep.data.local.dao.TagsDao
import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.domain.repository.TagsRepository
import com.kamiapps.deep.deep.presentation.statistics_screen.components.TopTag
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