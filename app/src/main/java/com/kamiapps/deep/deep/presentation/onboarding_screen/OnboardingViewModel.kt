package com.kamiapps.deep.deep.presentation.onboarding_screen

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material.icons.outlined.ScreenLockPortrait
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.ui.graphics.Color

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    private val onboardingPages = listOf(
        OnboardingPage(
            title = "Ultra Focus with Deep",
            description = "Plan your day, block distractions\nand build better focus",
            showFloatingIcons = true,
            floatingIcons = listOf(
                Icons.Default.HourglassEmpty,
                Icons.Default.Schedule,
                Icons.Outlined.PersonPin,
                Icons.Default.BarChart,
                Icons.Outlined.ScreenLockPortrait,
                Icons.Rounded.CalendarMonth
            )
        ),
        OnboardingPage(
            title = "Focus Sessions",
            description = "Create customizable focus sessions\nwith timers and breaks",
            showFloatingIcons = false
        ),
        OnboardingPage(
            title = "Block Distractions",
            description = "Block apps and websites\nthat distract you during focus time",
            showFloatingIcons = false,
            backgroundColor = Color.White
        ),
        OnboardingPage(
            title = "Screen Time",
            description = "This lets us block distracting apps.\nYour data is private and never\nleaves your phone.",
            showFloatingIcons = false,
            backgroundColor = Color.Black
        ),
        OnboardingPage(
            title = "Stay on top of your schedule",
            description = "We'll keep notifications minimal. Deep is\nmade to help you focus, not\ndistract you.",
            showFloatingIcons = false,
            backgroundColor = Color.Black
        )
    )

    fun handleAction(action: OnboardingActions) {
        when (action) {
            is OnboardingActions.NextPage -> {
                viewModelScope.launch {
                    val currentPage = _uiState.value.currentPage
                    if (currentPage < _uiState.value.totalPages - 1) {
                        _uiState.value = _uiState.value.copy(
                            currentPage = currentPage + 1,
                            showButtons = false // Sayfa değiştiğinde buton animasyonunu sıfırla
                        )
                    } else {
                        completeOnboarding()
                    }
                }
            }
            is OnboardingActions.PreviousPage -> {
                viewModelScope.launch {
                    val currentPage = _uiState.value.currentPage
                    if (currentPage > 0) {
                        _uiState.value = _uiState.value.copy(currentPage = currentPage - 1)
                    }
                }
            }
            is OnboardingActions.GoToPage -> {
                viewModelScope.launch {
                    if (action.page in 0 until _uiState.value.totalPages) {
                        _uiState.value = _uiState.value.copy(
                            currentPage = action.page,
                            showButtons = false // Sayfa değiştiğinde buton animasyonunu sıfırla
                        )
                    }
                }
            }
            is OnboardingActions.CompleteOnboarding -> {
                completeOnboarding()
            }
            is OnboardingActions.ShowButtons -> {
                viewModelScope.launch {
                    _uiState.value = _uiState.value.copy(showButtons = true)
                }
            }
            is OnboardingActions.RequestScreenTimePermission -> {
                // Screen time permission logic handled in the composable
            }
            is OnboardingActions.RequestNotificationPermission -> {
                // Notification permission logic handled in the composable
            }
            is OnboardingActions.MaybeLater -> {
                // Skip to next page or complete onboarding
                handleAction(OnboardingActions.NextPage)
            }
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            // SharedPreferences'a onboarding tamamlandı bilgisini kaydet
            val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()
            
            _uiState.value = _uiState.value.copy(
                isCompleted = true,
                isLoading = false
            )
        }
    }

    fun getCurrentPage(): OnboardingPage = onboardingPages[_uiState.value.currentPage]
    
    fun getPageAt(index: Int): OnboardingPage? = 
        if (index in onboardingPages.indices) onboardingPages[index] else null
} 