package com.kami_apps.deepwork.deep.data.manager

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class PremiumManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val TAG = "PremiumManager"

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private lateinit var revenueCatManager: RevenueCatManager
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun initialize() {
        try {
            revenueCatManager = RevenueCatManager(context)

            // RevenueCat subscription state'ini dinle
            scope.launch {
                revenueCatManager.subscriptionState.collect { subscriptionState ->
                    Log.d(TAG, "RevenueCat subscription state updated: isPremium=${subscriptionState.isPremium}")

                    _isPremium.value = subscriptionState.isPremium
                    _isLoading.value = subscriptionState.isLoading

                    // Debug log için customer info
                    subscriptionState.customerInfo?.let { customerInfo ->
                        Log.d(TAG, "Active entitlements: ${customerInfo.entitlements.active.keys}")
                        Log.d(TAG, "All entitlements: ${customerInfo.entitlements.all.keys}")
                    }
                }
            }

            // İlk yüklemede customer info'yu refresh et
            revenueCatManager.refreshCustomerInfo()

            Log.d(TAG, "PremiumManager initialized and listening to RevenueCat")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PremiumManager", e)
            _isPremium.value = false
        }
    }

    fun getCurrentPremiumStatus(): Boolean {
        return _isPremium.value
    }

    fun setPremiumStatus(isPremium: Boolean) {
        Log.d(TAG, "Manually setting premium status: $isPremium")
        _isPremium.value = isPremium
    }

    fun refreshPremiumStatus() {
        if (::revenueCatManager.isInitialized) {
            Log.d(TAG, "Refreshing premium status from RevenueCat")
            revenueCatManager.refreshCustomerInfo()
        } else {
            Log.w(TAG, "RevenueCatManager not initialized, cannot refresh")
        }
    }

    // Premium feature kısıtlamaları
    fun canAddMoreTags(currentTagCount: Int): Boolean {
        return if (_isPremium.value) {
            true // Premium users unlimited tags
        } else {
            currentTagCount < 1 // Free users max 1 tag
        }
    }

    fun canUseAppIcon(iconId: String): Boolean {
        return if (_isPremium.value) {
            true // Premium users can use all icons
        } else {
            iconId == "original" // Free users only original icon
        }
    }

    fun canViewRealStatistics(): Boolean {
        return _isPremium.value
    }

    fun getMaxTagsForFreeUser(): Int = 1

    fun canAccessPremiumFeatures(): Boolean {
        return _isPremium.value
    }

    // Debug fonksiyonu
    fun debugPremiumStatus() {
        Log.d(TAG, "=== PREMIUM STATUS DEBUG ===")
        Log.d(TAG, "Current premium status: ${_isPremium.value}")
        Log.d(TAG, "Is loading: ${_isLoading.value}")

        if (::revenueCatManager.isInitialized) {
            val currentState = revenueCatManager.subscriptionState.value
            Log.d(TAG, "RevenueCat state: $currentState")

            currentState.customerInfo?.let { customerInfo ->
                Log.d(TAG, "Customer ID: ${customerInfo.originalAppUserId}")
                Log.d(TAG, "Active entitlements: ${customerInfo.entitlements.active}")
                Log.d(TAG, "All entitlements: ${customerInfo.entitlements.all}")

                // Specific entitlement check
                val proEntitlement = customerInfo.entitlements.active[ENTITLEMENT_PRO]
                if (proEntitlement != null) {
                    Log.d(TAG, "Pro entitlement found: ${proEntitlement.isActive}")
                    Log.d(TAG, "Product identifier: ${proEntitlement.productIdentifier}")
                } else {
                    Log.d(TAG, "Pro entitlement not found in active entitlements")
                }
            } ?: Log.d(TAG, "No customer info available")
        } else {
            Log.d(TAG, "RevenueCatManager not initialized")
        }
        Log.d(TAG, "=== END DEBUG ===")
    }
}