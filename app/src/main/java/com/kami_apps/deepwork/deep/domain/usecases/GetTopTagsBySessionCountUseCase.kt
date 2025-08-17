package com.kami_apps.deepwork.deep.domain.usecases

import com.kami_apps.deepwork.deep.domain.repository.TagsRepository
import com.kami_apps.deepwork.deep.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

    class GetTopTagsBySessionCountUseCase@Inject constructor (private val tagsRepository: TagsRepository) {
    fun invoke(): Flow<List<TopTag>> = tagsRepository.getTopTagsBySessionCount()

}