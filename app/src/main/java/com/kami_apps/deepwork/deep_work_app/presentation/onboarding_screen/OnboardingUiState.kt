package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 5,
    val isLoading: Boolean = false,
    val isCompleted: Boolean = false,
    val showButtons: Boolean = false // İlk sayfa için buton animasyon kontrolü
)

data class OnboardingPage(
    val title: String,
    val description: String,
    val iconResource: Int? = null, // Kullanıcı icon'ları ekleyecek
    val backgroundColor: Color = Color.Black,
    val showFloatingIcons: Boolean = false,
    val floatingIcons: List<ImageVector> = emptyList() // Material Icons olarak değiştirildi
) 