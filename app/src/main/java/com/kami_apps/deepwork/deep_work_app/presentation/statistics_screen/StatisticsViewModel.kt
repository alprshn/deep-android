package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetSessionCountByTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalFocusTimeByTagUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalFocusTimeUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetTotalSessionCountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getTotalFocusTimeUseCase: GetTotalFocusTimeUseCase,
    private val getTotalSessionCountUseCase: GetTotalSessionCountUseCase,
    private val getSessionCountByTagUseCase: GetSessionCountByTagUseCase,
    private val getTotalFocusTimeByTagUseCase: GetTotalFocusTimeByTagUseCase
) : ViewModel(), StatisticsActions {


    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StatisticsUiState()
    )


    fun loadAllTags() {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                getAllTagsUseCase.invoke().collectLatest {
                    _uiState.value = _uiState.value.copy(allTags = it)
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }

            }

        }
    }


    fun updateTagId(tagId: Int) {
        _uiState.update { it.copy(selectedTagId = tagId) }
        loadStatisticsForSelectedTag()
    }

    fun loadStatisticsForSelectedTag() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                if (_uiState.value.selectedTagId == 0) {
                    // All tags - get total statistics
                    _uiState.update { it.copy(totalSessionCount = getTotalSessionCountUseCase.invoke()) }
                    getTotalFocusTimeUseCase.invoke().collectLatest { result ->
                        Log.e("UI Log", "All Tags - Toplam Süre (UI): $result")
                        _uiState.value = _uiState.value.copy(totalFocusTime = result)
                    }
                } else {
                    // Specific tag - get statistics for that tag
                    val selectedTagId = _uiState.value.selectedTagId
                    
                    // Collect both flows concurrently
                    kotlinx.coroutines.coroutineScope {
                        launch {
                            getSessionCountByTagUseCase(selectedTagId).collectLatest { count ->
                                Log.e("UI Log", "Session Count for Tag $selectedTagId: $count")
                                _uiState.value = _uiState.value.copy(totalSessionCount = count)
                            }
                        }
                        
                        launch {
                            getTotalFocusTimeByTagUseCase(selectedTagId).collectLatest { result ->
                                Log.e("UI Log", "Tag $selectedTagId - Toplam Süre (UI): $result")
                                _uiState.value = _uiState.value.copy(totalFocusTime = result)
                            }
                        }
                    }
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e("StatisticsViewModel", "Error loading statistics: ${e.message}", e)
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }
            }
        }
    }


    fun InitializeTotalSessionCount() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true, errorMessage = null
                )
            } // Yükleme başladı, hata yok
            try {
                _uiState.update { it.copy(totalSessionCount = getTotalSessionCountUseCase.invoke()) }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false, // Yükleme bitti
                        errorMessage = "Etiketler yüklenirken bir hata oluştu: ${e.localizedMessage}"
                    )
                }

            }

        }
    }


}