package com.kami_apps.deepwork.deep.presentation.settings_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep.domain.repository.AppRepository
import com.kami_apps.deepwork.deep.data.manager.PremiumManager
import com.kami_apps.deepwork.deep.data.manager.RevenueCatManager
import com.kami_apps.deepwork.deep.data.manager.ThemeManager
import com.kami_apps.deepwork.deep.domain.usecases.GetUserPreferencesUseCase
import com.kami_apps.deepwork.deep.domain.usecases.ChangeUserPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.util.Log

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val appRepository: AppRepository,
    private val premiumManager: PremiumManager,
    private val revenueCatManager: RevenueCatManager,
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val changeUserPreferencesUseCase: ChangeUserPreferencesUseCase,
    private val themeManager: ThemeManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _restoreMessage = MutableStateFlow<String?>(null)
    val restoreMessage: StateFlow<String?> = _restoreMessage.asStateFlow()
    
    private val _isRestoring = MutableStateFlow(false)
    val isRestoring: StateFlow<Boolean> = _isRestoring.asStateFlow()
    
    // Haptic feedback state
    private val _isHapticEnabled = MutableStateFlow(true)
    val isHapticEnabled: StateFlow<Boolean> = _isHapticEnabled.asStateFlow()
    
    // Premium status
    val isPremium: StateFlow<Boolean> = premiumManager.isPremium

    companion object {
        private const val PAGE_SIZE = 20
        private const val TAG = "SettingsViewModel"
    }
    
    init {
        // Observe premium status
        viewModelScope.launch {
            premiumManager.isPremium.collectLatest { isPremium ->
                _uiState.update { currentState ->
                    currentState.copy(isPremium = isPremium)
                }
            }
        }
        
        // Observe theme changes from ThemeManager
        viewModelScope.launch {
            themeManager.currentTheme.collectLatest { theme ->
                _uiState.update { currentState ->
                    currentState.copy(currentTheme = theme)
                }
            }
        }
        
        // Load user preferences
        loadUserPreferences()
        
        // Observe blocked apps changes
        viewModelScope.launch {
            appRepository.getBlockedAppsFlow().collect { blockedApps ->
                _uiState.update { state ->
                    val updatedApps = state.installedApps.map { app ->
                        app.copy(isSelected = blockedApps.contains(app.packageName))
                    }
                    state.copy(
                        installedApps = updatedApps,
                        blockedApps = blockedApps
                    )
                }
            }
        }
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
        viewModelScope.launch {
            try {
                val isCurrentlyBlocked = appRepository.isAppBlocked(packageName)
                if (isCurrentlyBlocked) {
                    appRepository.removeBlockedApp(packageName)
                } else {
                    appRepository.addBlockedApp(packageName)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message)
                }
            }
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
    
    // App Blocking Functions
    fun loadBlockedApps() {
        viewModelScope.launch {
            try {
                val blockedApps = appRepository.getBlockedApps()
                _uiState.update { 
                    it.copy(blockedApps = blockedApps)
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message)
                }
            }
        }
    }
    
    fun clearAllBlockedApps() {
        viewModelScope.launch {
            try {
                appRepository.clearAllBlockedApps()
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message)
                }
            }
        }
    }

    fun restorePurchases() {
        _isRestoring.value = true
        _restoreMessage.value = null
        
        revenueCatManager.restorePurchases(
            onSuccess = { customerInfo ->
                _isRestoring.value = false
                val isPremium = customerInfo.entitlements.active.containsKey("deepwork_premium")
                
                if (isPremium) {
                    premiumManager.setPremiumStatus(true)
                    _restoreMessage.value = "✅ Purchases restored successfully! You now have premium access."
                } else {
                    _restoreMessage.value = "ℹ️ No active purchases found to restore."
                }
            },
            onError = { error ->
                _isRestoring.value = false
                _restoreMessage.value = "❌ Failed to restore purchases: ${error.message}"
            }
        )
    }
    
    fun clearRestoreMessage() {
        _restoreMessage.value = null
    }

    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                val userPreferences = getUserPreferencesUseCase()
                _isHapticEnabled.value = userPreferences?.haptic ?: false
                Log.d(TAG, "Loaded haptic feedback setting: ${_isHapticEnabled.value}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load user preferences", e)
                _isHapticEnabled.value = false // Default to false on error
            }
        }
    }

    fun toggleHapticFeedback() {
        viewModelScope.launch {
            try {
                val newHapticEnabled = !_isHapticEnabled.value
                _isHapticEnabled.value = newHapticEnabled
                
                // Update user preferences with current theme and new haptic setting
                val currentPrefs = getUserPreferencesUseCase()
                val currentTheme = currentPrefs?.theme ?: "Default"
                changeUserPreferencesUseCase(currentTheme, newHapticEnabled)
                
                Log.d(TAG, "Haptic feedback toggled to: $newHapticEnabled")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to change haptic feedback", e)
                // Revert the state on error
                _isHapticEnabled.value = !_isHapticEnabled.value
            }
        }
    }

    // Theme Management Functions
    fun changeTheme(newTheme: String) {
        themeManager.changeTheme(newTheme)
    }

    fun getAvailableThemes(): List<String> {
        return themeManager.getAvailableThemes()
    }
}