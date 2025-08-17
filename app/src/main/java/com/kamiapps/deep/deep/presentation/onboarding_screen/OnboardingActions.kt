package com.kamiapps.deep.deep.presentation.onboarding_screen

sealed class OnboardingActions {
    object NextPage : OnboardingActions()
    object PreviousPage : OnboardingActions()
    data class GoToPage(val page: Int) : OnboardingActions()
    object CompleteOnboarding : OnboardingActions()
    object ShowButtons : OnboardingActions() // İlk sayfa animasyonu için
    object RequestScreenTimePermission : OnboardingActions()
    object RequestNotificationPermission : OnboardingActions()
    object MaybeLater : OnboardingActions()
} 