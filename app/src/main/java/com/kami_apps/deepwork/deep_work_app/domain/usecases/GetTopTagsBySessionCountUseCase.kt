package com.kami_apps.deepwork.deep_work_app.domain.usecases

import android.util.Log
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.repository.SessionsRepository
import com.kami_apps.deepwork.deep_work_app.domain.repository.TagsRepository
import com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components.TopTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

    class GetTopTagsBySessionCountUseCase@Inject constructor (private val tagsRepository: TagsRepository) {
    fun invoke(): Flow<List<TopTag>> = tagsRepository.getTopTagsBySessionCount()

}