package com.kamiapps.deep.deep.presentation.paywall_screen

sealed interface PaywallActions {
    object LoadPackages : PaywallActions
    object ToggleFreeTrial : PaywallActions
    data class SelectPlan(val planType: PlanType) : PaywallActions
    object ShowInfoDialog : PaywallActions
    object HideInfoDialog : PaywallActions
    object StartPurchase : PaywallActions
    object RestorePurchases : PaywallActions
    object RefreshCustomerInfo : PaywallActions
    object DismissError : PaywallActions
    object OpenPrivacyPolicy : PaywallActions
    object OpenTermsOfService : PaywallActions
} 