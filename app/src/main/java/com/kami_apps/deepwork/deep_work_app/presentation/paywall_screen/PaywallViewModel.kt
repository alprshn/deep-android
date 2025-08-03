package com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kami_apps.deepwork.deep_work_app.data.manager.RevenueCatManager
import com.kami_apps.deepwork.deep_work_app.data.manager.ENTITLEMENT_PRO
import com.revenuecat.purchases.Package
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext

@HiltViewModel
class PaywallViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val revenueCatManager = RevenueCatManager(context)
    
    private val _uiState = MutableStateFlow(PaywallUiState())
    val uiState: StateFlow<PaywallUiState> = _uiState.asStateFlow()
    
    init {
        // Observe subscription state changes from RevenueCat
        viewModelScope.launch {
            combine(
                revenueCatManager.subscriptionState,
                _uiState
            ) { subscriptionState, currentUiState ->
                currentUiState.copy(
                    isPremium = subscriptionState.isPremium,
                    isLoading = subscriptionState.isLoading,
                    error = subscriptionState.error,
                    customerInfo = subscriptionState.customerInfo,
                    availablePackages = subscriptionState.availablePackages
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
        
        // Load packages initially
        handleAction(PaywallActions.LoadPackages)
    }
    
    fun handleAction(action: PaywallActions) {
        when (action) {
            is PaywallActions.LoadPackages -> loadPackages()
            is PaywallActions.ToggleFreeTrial -> toggleFreeTrial()
            is PaywallActions.SelectPlan -> selectPlan(action.planType)
            is PaywallActions.ShowInfoDialog -> showInfoDialog()
            is PaywallActions.HideInfoDialog -> hideInfoDialog()
            is PaywallActions.StartPurchase -> startPurchase()
            is PaywallActions.RestorePurchases -> restorePurchases()
            is PaywallActions.RefreshCustomerInfo -> refreshCustomerInfo()
            is PaywallActions.DismissError -> dismissError()
            is PaywallActions.OpenPrivacyPolicy -> openPrivacyPolicy()
            is PaywallActions.OpenTermsOfService -> openTermsOfService()
        }
    }
    
    private fun loadPackages() {
        viewModelScope.launch {
            try {
                val weeklyPackage = revenueCatManager.getWeeklyPackage()
                val yearlyPackage = revenueCatManager.getYearlyPackage()
                
                Log.d("PaywallViewModel", "Weekly package: ${weeklyPackage?.identifier}, Yearly package: ${yearlyPackage?.identifier}")
                
                val configError = weeklyPackage == null && yearlyPackage == null
                
                _uiState.value = _uiState.value.copy(
                    weeklyPackage = weeklyPackage,
                    yearlyPackage = yearlyPackage,
                    configError = configError
                )
                
                if (configError) {
                    Log.w("PaywallViewModel", "No packages found - using test mode")
                }
            } catch (e: Exception) {
                Log.e("PaywallViewModel", "Error loading packages: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    configError = true,
                    error = "Error loading subscription options: ${e.message}"
                )
            }
        }
    }
    
    private fun toggleFreeTrial() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(
            isFreeTrial = !currentState.isFreeTrial,
            selectedPlan = if (!currentState.isFreeTrial) PlanType.WEEKLY else PlanType.YEARLY
        )
    }
    
    private fun selectPlan(planType: PlanType) {
        _uiState.value = _uiState.value.copy(
            selectedPlan = planType,
            isFreeTrial = planType == PlanType.WEEKLY
        )
    }
    
    private fun showInfoDialog() {
        _uiState.value = _uiState.value.copy(showInfoDialog = true)
    }
    
    private fun hideInfoDialog() {
        _uiState.value = _uiState.value.copy(showInfoDialog = false)
    }
    
    private fun startPurchase() {
        val currentState = _uiState.value
        val packageToPurchase = if (currentState.isFreeTrial) {
            currentState.weeklyPackage
        } else {
            currentState.yearlyPackage
        }
        
        Log.d("PaywallViewModel", "Attempting to purchase package: ${packageToPurchase?.identifier ?: "null"}")
        
        if (packageToPurchase == null) {
            if (currentState.configError) {
                // In test mode, simulate a successful purchase
                Log.d("PaywallViewModel", "TEST MODE: Simulating successful purchase")
                _uiState.value = currentState.copy(
                    error = "Test purchase completed",
                    isPremium = true
                )
            } else {
                _uiState.value = currentState.copy(
                    error = "Package not available. Please try again later."
                )
            }
            return
        }
        
        // This will be called from the UI with the activity context
        // The actual purchase logic is handled in the UI layer
    }
    
    fun purchasePackage(activity: Activity, packageToPurchase: Package) {
        viewModelScope.launch {
            try {
                Log.d("PaywallViewModel", "Starting purchase for: ${packageToPurchase.product.id}")
                revenueCatManager.purchasePackage(
                    activity = activity,
                    packageToPurchase = packageToPurchase,
                    onSuccess = { 
                        Log.d("PaywallViewModel", "Purchase successful!")
                        // State will be updated automatically through the subscription state flow
                    },
                    onError = { error ->
                        Log.e("PaywallViewModel", "Purchase failed: $error")
                        val errorMessage = if (error.underlyingErrorMessage?.contains("signed correctly") == true) {
                            "⚠️ Debug Mode: Use signed APK for real purchases"
                        } else {
                            "Purchase failed: ${error.message}"
                        }
                        _uiState.value = _uiState.value.copy(
                            error = errorMessage
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("PaywallViewModel", "Exception during purchase: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    error = "Error: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
    
    private fun restorePurchases() {
        viewModelScope.launch {
            revenueCatManager.restorePurchases(
                onSuccess = { customerInfo ->
                    if (customerInfo.entitlements.active.containsKey(ENTITLEMENT_PRO)) {
                        // Premium restored successfully
                        Log.d("PaywallViewModel", "Premium subscription restored")
                    } else {
                        _uiState.value = _uiState.value.copy(
                            error = "No active subscription found to restore"
                        )
                    }
                },
                onError = { error ->
                    Log.e("PaywallViewModel", "Restore failed: $error")
                    _uiState.value = _uiState.value.copy(
                        error = "Restore failed: $error"
                    )
                }
            )
        }
    }
    
    private fun refreshCustomerInfo() {
        revenueCatManager.refreshCustomerInfo()
    }
    
    private fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    private fun openPrivacyPolicy() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.kamiapps.com/page/privacy-policy") // TODO: Update with actual URL
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("PaywallViewModel", "Error opening privacy policy: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Unable to open privacy policy"
            )
        }
    }
    
    private fun openTermsOfService() {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.kamiapps.com/page/terms-conditions") // TODO: Update with actual URL
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("PaywallViewModel", "Error opening terms of service: ${e.message}")
            _uiState.value = _uiState.value.copy(
                error = "Unable to open terms of service"
            )
        }
    }
    
    fun getCurrentPackage(): Package? {
        val currentState = _uiState.value
        return if (currentState.isFreeTrial) {
            currentState.weeklyPackage
        } else {
            currentState.yearlyPackage
        }
    }
    
    fun calculateDiscountedPrice(originalPrice: String): String {
        return if (originalPrice != "000") {
            try {
                val numericRegex = "[0-9]+([,.][0-9]+)?".toRegex()
                val numericMatch = numericRegex.find(originalPrice)
                
                if (numericMatch != null) {
                    val numericPart = numericMatch.value
                    val numericValue = numericPart.replace(",", ".").toFloat()
                    val discountedValue = numericValue * 52f // 88% off
                    
                    val originalDecimals = if (numericPart.contains(".") || numericPart.contains(",")) {
                        val parts = numericPart.replace(",", ".").split(".")
                        if (parts.size > 1) parts[1].length else 0
                    } else {
                        0
                    }
                    
                    val formattedDiscount = String.format("%." + originalDecimals + "f", discountedValue)
                    originalPrice.replace(numericPart, formattedDiscount)
                } else {
                    originalPrice
                }
            } catch (e: Exception) {
                Log.e("PaywallViewModel", "Error calculating discount: ${e.message}")
                originalPrice
            }
        } else {
            originalPrice
        }
    }
} 