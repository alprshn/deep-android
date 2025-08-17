package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.domain.repository.TagsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTagsUseCase @Inject constructor (private val tagsRepository: TagsRepository)  {
    fun invoke(): Flow<List<Tags>> = tagsRepository.getAllTags()
}