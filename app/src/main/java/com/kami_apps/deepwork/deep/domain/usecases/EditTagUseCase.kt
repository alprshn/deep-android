package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.data.local.entities.Tags
import com.kami_apps.deepwork.deep.domain.repository.TagsRepository
import javax.inject.Inject

class EditTagUseCase @Inject constructor (private val tagsRepository: TagsRepository) {
    suspend operator fun invoke(tags:Tags) = tagsRepository.updateTag(tags)
}