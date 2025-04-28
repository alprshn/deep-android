package com.example.deepwork.deep_work_app.data.repository

import com.example.deepwork.deep_work_app.data.local.dao.TagsDao
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.domain.repository.TagsRepository
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

}