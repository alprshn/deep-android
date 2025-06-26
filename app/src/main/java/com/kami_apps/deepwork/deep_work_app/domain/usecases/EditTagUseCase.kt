package com.kami_apps.deepwork.deep_work_app.domain.usecases

import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.repository.TagsRepository
import javax.inject.Inject

class EditTagUseCase @Inject constructor (private val tagsRepository: TagsRepository) {
    suspend operator fun invoke(tags:Tags) = tagsRepository.updateTag(tags)
}