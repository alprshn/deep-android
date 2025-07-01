package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    companion object {
        private const val PAGE_SIZE = 20
    }

    // App Installation Functions
    fun loadInstalledApps() {
        resetPagination()
        loadNextPage()
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoading || currentState.isLoadingMore || !currentState.hasNextPage) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        isLoading = it.currentPage == 0,
                        isLoadingMore = it.currentPage > 0,
                        error = null
                    )
                }

                val newApps = appRepository.getInstalledApps(
                    page = currentState.currentPage,
                    pageSize = PAGE_SIZE
                )

                val totalCount = appRepository.getTotalAppCount()
                val currentLoadedCount = currentState.installedApps.size + newApps.size
                val hasNextPage = currentLoadedCount < totalCount

                _uiState.update { state ->
                    state.copy(
                        installedApps = state.installedApps + newApps,
                        currentPage = state.currentPage + 1,
                        hasNextPage = hasNextPage,
                        isLoading = false,
                        isLoadingMore = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun toggleAppSelection(packageName: String) {
        _uiState.update { state ->
            val updated = state.installedApps.map {
                if (it.packageName == packageName) it.copy(isSelected = !it.isSelected)
                else it
            }
            state.copy(installedApps = updated)
        }
    }

    private fun resetPagination() {
        _uiState.update { 
            it.copy(
                installedApps = emptyList(),
                currentPage = 0,
                hasNextPage = true,
                isLoading = false,
                isLoadingMore = false,
                error = null
            )
        }
    }

    // App Icon Functions
    fun loadAppIcons() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isIconLoading = true, iconError = null) }
                
                val availableIcons = appRepository.getAvailableAppIcons()
                val currentIcon = appRepository.getCurrentAppIcon()
                
                _uiState.update { 
                    it.copy(
                        availableIcons = availableIcons,
                        currentAppIcon = currentIcon,
                        isIconLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isIconLoading = false,
                        iconError = e.message
                    )
                }
            }
        }
    }

    fun changeAppIcon(iconId: String) {
        viewModelScope.launch {
            try {
                _uiState.update { 
                    it.copy(
                        isIconLoading = true, 
                        iconError = null,
                        iconChangeSuccess = false
                    )
                }
                
                val success = appRepository.changeAppIcon(iconId)
                
                if (success) {
                    // Reload icons to update selection state
                    val updatedIcons = appRepository.getAvailableAppIcons()
                    val newCurrentIcon = appRepository.getCurrentAppIcon()
                    
                    _uiState.update { 
                        it.copy(
                            availableIcons = updatedIcons,
                            currentAppIcon = newCurrentIcon,
                            isIconLoading = false,
                            iconChangeSuccess = true
                        )
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isIconLoading = false,
                            iconError = "Failed to change app icon"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isIconLoading = false,
                        iconError = e.message
                    )
                }
            }
        }
    }

    fun clearIconMessages() {
        _uiState.update { 
            it.copy(
                iconChangeSuccess = false,
                iconError = null
            )
        }
    }
}