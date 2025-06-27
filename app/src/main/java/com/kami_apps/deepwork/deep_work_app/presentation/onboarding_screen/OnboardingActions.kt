package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

sealed class OnboardingActions {
    object NextPage : OnboardingActions()
    object PreviousPage : OnboardingActions()
    data class GoToPage(val page: Int) : OnboardingActions()
    object CompleteOnboarding : OnboardingActions()
    object SkipOnboarding : OnboardingActions()
    object ShowButtons : OnboardingActions() // İlk sayfa animasyonu için
} 