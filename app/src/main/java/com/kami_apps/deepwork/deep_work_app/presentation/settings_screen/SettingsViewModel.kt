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

    fun loadInstalledApps() {
        viewModelScope.launch {
            appRepository.getInstalledApps().collect { apps ->
                _uiState.update { it.copy(installedApps = apps) }
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
}