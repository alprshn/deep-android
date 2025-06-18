package com.example.deepwork.deep_work_app.presentation.statistics_screen

import com.example.deepwork.deep_work_app.presentation.timer_screen.stopwatch.StopwatchActions




import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.deepwork.deep_work_app.data.UiState
import com.example.deepwork.deep_work_app.data.local.entities.Sessions
import com.example.deepwork.deep_work_app.data.local.entities.Tags
import com.example.deepwork.deep_work_app.data.manager.StopwatchManager
import com.example.deepwork.deep_work_app.domain.usecases.AddTagUseCase
import com.example.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StartFocusSessionUseCase
import com.example.deepwork.deep_work_app.domain.usecases.StopFocusSessionUseCase
import com.example.deepwork.deep_work_app.presentation.timer_screen.TimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
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