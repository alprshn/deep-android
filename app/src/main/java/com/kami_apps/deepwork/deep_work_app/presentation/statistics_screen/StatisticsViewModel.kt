package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalFocusTimeUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalSessionCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getTotalFocusTimeUseCase: GetTotalFocusTimeUseCase,
    private val getTotalSessionCountUseCase: GetTotalSessionCountUseCase
) : ViewModel(), StatisticsActions {

    private val _allTagList = MutableStateFlow(emptyList<Tags>())
    val allTagList = _allTagList.asStateFlow()

    private val _totalSessionCount = MutableStateFlow(0)
    val totalSessionCount = _totalSessionCount.asStateFlow()


    fun getAllTag() {
        viewModelScope.launch {
            getAllTagsUseCase.invoke().collectLatest {
                _allTagList.tryEmit(it)
            }
        }
    }


    fun loadStatistics() {
        viewModelScope.launch {
            _totalSessionCount.value = getTotalSessionCountUseCase.invoke()
        }
    }


}