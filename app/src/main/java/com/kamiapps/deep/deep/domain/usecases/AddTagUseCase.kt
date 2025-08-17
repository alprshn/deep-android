package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.data.local.entities.Tags
import com.kamiapps.deep.deep.domain.repository.TagsRepository
import javax.inject.Inject

class AddTagUseCase @Inject constructor(private val tagsRepository: TagsRepository){
    suspend operator fun invoke(tags: Tags){
            tagsRepository.insertTag(tags)
    }
}