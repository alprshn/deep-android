package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor (private val tagsRepository: TagsRepository)  {
    fun invoke(): Flow<List<Tags>> = tagsRepository.getAllTags()
}