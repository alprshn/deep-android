package com.kamiapps.deep.deep.presentation.paywall_screen

import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Package

data class PaywallUiState(
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val customerInfo: CustomerInfo? = null,
    val availablePackages: List<Package> = emptyList(),
    val weeklyPackage: Package? = null,
    val yearlyPackage: Package? = null,
    val isFreeTrial: Boolean = true,
    val configError: Boolean = false,
    val showInfoDialog: Boolean = false,
    val selectedPlan: PlanType = PlanType.WEEKLY
)

enum class PlanType {
    WEEKLY,
    YEARLY
} 