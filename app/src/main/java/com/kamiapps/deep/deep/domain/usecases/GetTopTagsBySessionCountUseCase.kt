package com.kamiapps.deep.deep.domain.usecases

import com.kamiapps.deep.deep.domain.repository.TagsRepository
import com.kamiapps.deep.deep.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

    class GetTopTagsBySessionCountUseCase@Inject constructor (private val tagsRepository: TagsRepository) {
    fun invoke(): Flow<List<TopTag>> = tagsRepository.getTopTagsBySessionCount()

}