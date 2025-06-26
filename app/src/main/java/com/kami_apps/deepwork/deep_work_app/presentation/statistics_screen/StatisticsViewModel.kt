package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase
) : ViewModel(), StatisticsActions {

    private val _allTagList = MutableStateFlow(emptyList<Tags>())
    val allTagList = _allTagList.asStateFlow()

    fun getAllTag() {
        viewModelScope.launch {
            getAllTagsUseCase.invoke().collectLatest {
                _allTagList.tryEmit(it)
            }
        }
    }
}