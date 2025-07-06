package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.local.entities.Tags
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetAllTagsUseCase
import com.kami_apps.deepwork.deep_work_app.domain.usecases.GetSessionCountByTagUseCase
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
import javax.inject.Inject


@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val getAllTagsUseCase: GetAllTagsUseCase,
    private val getTotalFocusTimeUseCase: GetTotalFocusTimeUseCase,
    private val getTotalSessionCountUseCase: GetTotalSessionCountUseCase,
    private val getSessionCountByTagUseCase: GetSessionCountByTagUseCase
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





    private val _sessionCountForSelectedTag = MutableStateFlow(0)
    val sessionCountForSelectedTag: StateFlow<Int> = _sessionCountForSelectedTag.asStateFlow()


    private var _selectedTagId = MutableStateFlow(0) // Internal olarak tutulan StateFlow
    val selectedTagId: StateFlow<Int> = _selectedTagId.asStateFlow() // UI'a açılan okuma kısmı









    fun loadSessionCountByTag(tagId: Int) {
        if (_selectedTagId.value == tagId) return

        // Yeni seçilen tagId'yi ViewModel'ın iç state'ine kaydet
        _selectedTagId.value = tagId
        viewModelScope.launch {
            // Use Case'i çağır ve Flow'u topla
            getSessionCountByTagUseCase(tagId).collectLatest { count ->
                _sessionCountForSelectedTag.value = count
            }
        }

    }


}