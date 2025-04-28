package com.example.deepwork.deep_work_app.domain.usecases

import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.domain.repository.TagsRepository
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor (private val tagsRepository: TagsRepository)  {
    suspend operator fun invoke(tags:Tags) = tagsRepository.deleteTag(tags)
}