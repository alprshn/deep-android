package com.kami_apps.deepwork.deep.presentation.onboarding_screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 5, // Increased to 4 for permissions page
    val showButtons: Boolean = false, // ilk sayfa için button animasyonu
    val isOnboardingCompleted: Boolean = false,
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconResource: Int? = null, // Kullanıcı icon'ları ekleyecek
    val backgroundColor: Color = Color.Black,
    val showFloatingIcons: Boolean = false,
    val floatingIcons: List<ImageVector> = emptyList() // Material Icons olarak değiştirildi
) 