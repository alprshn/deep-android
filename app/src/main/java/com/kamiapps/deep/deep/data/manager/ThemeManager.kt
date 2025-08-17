package com.kamiapps.deep.deep.data.manager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log
import com.kamiapps.deep.deep.domain.usecases.GetUserPreferencesUseCase
import com.kamiapps.deep.deep.domain.usecases.ChangeUserPreferencesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class ThemeManager @Inject constructor(
    private val getUserPreferencesUseCase: GetUserPreferencesUseCase,
    private val changeUserPreferencesUseCase: ChangeUserPreferencesUseCase
) {
    private val _currentTheme = MutableStateFlow("Default")
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()
    
    private val managerScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val TAG = "ThemeManager"
    }
    
    init {
        loadTheme()
    }
    
    private fun loadTheme() {
        managerScope.launch {
            try {
                val userPreferences = getUserPreferencesUseCase()
                val theme = userPreferences?.theme ?: "Default"
                _currentTheme.value = theme
                Log.d(TAG, "Theme loaded: '$theme'")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load theme, using default", e)
                _currentTheme.value = "Default"
            }
        }
    }
    
    fun changeTheme(newTheme: String) {
        managerScope.launch {
            try {
                Log.d(TAG, "Changing theme from '${_currentTheme.value}' to '$newTheme'")
                
                // Get current haptic setting
                val userPreferences = getUserPreferencesUseCase()
                val currentHaptic = userPreferences?.haptic ?: false
                
                // Save new theme
                changeUserPreferencesUseCase(newTheme, currentHaptic)
                _currentTheme.value = newTheme
                
                Log.d(TAG, "Theme successfully changed to: '$newTheme'")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to change theme to '$newTheme'", e)
            }
        }
    }
    
    fun getAvailableThemes(): List<String> {
        return listOf("Light", "Dark", "Default")
    }
} 