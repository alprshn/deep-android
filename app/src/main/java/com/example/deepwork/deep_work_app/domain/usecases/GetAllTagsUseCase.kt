package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor (private val tagsRepository: TagsRepository)  {
    fun invoke(): Flow<List<Tags>> = tagsRepository.getAllTags()
}