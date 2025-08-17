package com.kamiapps.deep.deep.data.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Offering
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreTransaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

private const val TAG = "RevenueCatManager"

// Define the Pro subscription entitlement ID and product IDs
const val ENTITLEMENT_PRO = "Premium"
const val WEEKLY_PRODUCT_ID = "deep_premium:deep-premium-weekly"
const val YEARLY_PRODUCT_ID = "deep_premium:deep-premium-yearly"

/**
 * Represents the subscription state
 */
data class SubscriptionState(
    val isPremium: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val customerInfo: CustomerInfo? = null,
    val availablePackages: List<Package> = emptyList()
)

/**
 * Manager class for RevenueCat subscriptions
 */
class RevenueCatManager(context: Context) : UpdatedCustomerInfoListener {
    
    // Use StateFlow to emit subscription state changes
    private val _subscriptionState = MutableStateFlow(SubscriptionState(isLoading = true))
    val subscriptionState: StateFlow<SubscriptionState> = _subscriptionState.asStateFlow()
    
    init {
        // Add this instance as a listener for customer info updates
        Purchases.sharedInstance.updatedCustomerInfoListener = this
        
        // Load initial customer info
        refreshCustomerInfo()
    }
    
    /**
     * Initialize RevenueCat SDK with your public API key.
     * Call this in your Application class's onCreate method.
     */
    companion object {
        fun init(context: Context) {
            // Set log level for debugging in development
            Purchases.logLevel = LogLevel.DEBUG
            
            // Your actual RevenueCat API key for Google Play Store
            val apiKey = "goog_JnsJRDKENfBqnSixEBtTHoiquAZ" // TODO: Replace with actual API key
            
            // Configure Purchases with your API key
            Purchases.configure(PurchasesConfiguration.Builder(context, apiKey).build())
            
            Log.d(TAG, "RevenueCat SDK initialized with API Key: $apiKey")
        }
    }
    
    /**
     * Refresh customer info from RevenueCat
     */
    fun refreshCustomerInfo() {
        _subscriptionState.value = _subscriptionState.value.copy(isLoading = true, error = null)
        
        Purchases.sharedInstance.getCustomerInfo(object : ReceiveCustomerInfoCallback {
            override fun onError(error: PurchasesError) {
                _subscriptionState.value = _subscriptionState.value.copy(
                    isLoading = false,
                    error = error.toString()
                )
                Log.e(TAG, "Error fetching customer info: $error")
            }
            
            override fun onReceived(customerInfo: CustomerInfo) {
                processCustomerInfo(customerInfo)
                loadOfferings()
            }
        })
    }
    
    /**
     * Updates subscription state based on customer info
     */
    /**
     * Updates subscription state based on customer info
     */
    private fun processCustomerInfo(customerInfo: CustomerInfo) {
        // Entitlement kontrolü
        val isPremium = customerInfo.entitlements.active.containsKey(ENTITLEMENT_PRO)

        Log.d(TAG, "=== PROCESSING CUSTOMER INFO ===")
        Log.d(TAG, "Customer ID: ${customerInfo.originalAppUserId}")
        Log.d(TAG, "Active entitlements: ${customerInfo.entitlements.active.keys}")
        Log.d(TAG, "All entitlements: ${customerInfo.entitlements.all.keys}")
        Log.d(TAG, "Calculated premium status: $isPremium")

        // Eğer premium değilse, tüm entitlement'ları kontrol et
        if (!isPremium) {
            Log.d(TAG, "Premium false, checking all entitlements:")
            customerInfo.entitlements.all.forEach { (key, entitlement) ->
                Log.d(TAG, "Entitlement: $key, Active: ${entitlement.isActive}, Product: ${entitlement.productIdentifier}")
            }
        }

        // Active purchases da kontrol et
        Log.d(TAG, "Active purchases: ${customerInfo.activeSubscriptions}")
        Log.d(TAG, "All purchased product IDs: ${customerInfo.allPurchasedProductIds}")

        _subscriptionState.value = _subscriptionState.value.copy(
            isPremium = isPremium,
            isLoading = false,
            error = null,
            customerInfo = customerInfo
        )

        Log.d(TAG, "Subscription state updated. Premium: $isPremium")
        Log.d(TAG, "=== END PROCESSING ===")
    }
    
    /**
     * Called when RevenueCat provides updated customer info
     */
    override fun onReceived(customerInfo: CustomerInfo) {
        processCustomerInfo(customerInfo)
    }
    
    /**
     * Load available subscription offerings
     */
    private fun loadOfferings() {
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onError(error: PurchasesError) {
                Log.e(TAG, "Error fetching offerings: $error")
                
                // If it's a network error, create fallback test packages
                if (error.code.name == "NETWORK_ERROR") {
                    Log.d(TAG, "Network error detected, creating fallback test packages")
                    createFallbackTestPackages()
                } else {
                    _subscriptionState.value = _subscriptionState.value.copy(
                        error = "Failed to load offerings: $error"
                    )
                }
            }
            
            override fun onReceived(offerings: Offerings) {
                val defaultOffering = offerings.current
                if (defaultOffering != null) {
                    Log.d(TAG, "Offerings loaded: ${defaultOffering.identifier} with ${defaultOffering.availablePackages.size} packages")
                    
                    // Log available packages to help with debugging
                    defaultOffering.availablePackages.forEach { pkg ->
                        Log.d(TAG, "Package available: ${pkg.identifier}, type: ${pkg.packageType}, productId: ${pkg.product.id}")
                    }
                    
                    _subscriptionState.value = _subscriptionState.value.copy(
                        availablePackages = defaultOffering.availablePackages
                    )
                } else {
                    Log.w(TAG, "No offerings available, creating fallback test packages")
                    createFallbackTestPackages()
                }
            }
        })
    }
    
    /**
     * Create fallback test packages when network is unavailable
     */
    private fun createFallbackTestPackages() {
        Log.d(TAG, "Creating fallback test packages for offline mode")
        // For now, just set empty packages to prevent crashes
        // In a real app, you might want to create mock packages or handle this differently
        _subscriptionState.value = _subscriptionState.value.copy(
            availablePackages = emptyList(),
            error = null,
            isLoading = false
        )
    }
    
    /**
     * Purchase a package
     */
    fun purchasePackage(
        activity: Activity,
        packageToPurchase: Package,
        onSuccess: (CustomerInfo) -> Unit,
        onError: (PurchasesError) -> Unit
    ) {
        _subscriptionState.value = _subscriptionState.value.copy(isLoading = true, error = null)
        
        Log.d(TAG, "Starting purchase for package: ${packageToPurchase.identifier}, product: ${packageToPurchase.product.id}")
        
        try {
            val params = PurchaseParams.Builder(activity, packageToPurchase).build()
            
            Purchases.sharedInstance.purchase(
                params,
                object : PurchaseCallback {
                    override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                        onSuccess(customerInfo)
                        processCustomerInfo(customerInfo)
                        Log.d(TAG, "Purchase successful for: ${storeTransaction.productIds}")
                    }
                    
                    override fun onError(error: PurchasesError, userCancelled: Boolean) {
                        if (!userCancelled) {
                            onError(error)
                            _subscriptionState.value = _subscriptionState.value.copy(
                                isLoading = false,
                                error = error.toString()
                            )
                            Log.e(TAG, "Purchase error: $error")
                        } else {
                            _subscriptionState.value = _subscriptionState.value.copy(
                                isLoading = false,
                                error = null
                            )
                            Log.d(TAG, "Purchase cancelled by user")
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception during purchase setup: ${e.message}", e)
            _subscriptionState.value = _subscriptionState.value.copy(
                isLoading = false,
                error = "Error: ${e.message}"
            )
        }
    }
    
    /**
     * Restore purchases
     */
    fun restorePurchases(
        onSuccess: (CustomerInfo) -> Unit,
        onError: (PurchasesError) -> Unit
    ) {
        _subscriptionState.value = _subscriptionState.value.copy(isLoading = true, error = null)
        
        Log.d(TAG, "Attempting to restore purchases")
        
        Purchases.sharedInstance.restorePurchases(object : ReceiveCustomerInfoCallback {
            override fun onError(error: PurchasesError) {
                onError(error)
                _subscriptionState.value = _subscriptionState.value.copy(
                    isLoading = false,
                    error = error.toString()
                )
                Log.e(TAG, "Restore error: $error")
            }
            
            override fun onReceived(customerInfo: CustomerInfo) {
                onSuccess(customerInfo)
                processCustomerInfo(customerInfo)
                val isPremium = customerInfo.entitlements.active.containsKey(ENTITLEMENT_PRO)
                Log.d(TAG, "Restore successful. Premium status: $isPremium")
            }
        })
    }
    
    /**
     * Suspending function to get a weekly subscription package
     */
    suspend fun getWeeklyPackage(): Package? {
        try {
            val offerings = getOfferings()
            val weeklyPackage = offerings?.availablePackages?.find { it.product.id == WEEKLY_PRODUCT_ID }
                ?: offerings?.availablePackages?.find { it.packageType == PackageType.WEEKLY }
            
            if (weeklyPackage == null) {
                Log.w(TAG, "Weekly package not found - check product configuration")
            } else {
                Log.d(TAG, "Weekly package found: ${weeklyPackage.product.id}")
            }
            
            return weeklyPackage
        } catch (e: Exception) {
            Log.e(TAG, "Error getting weekly package: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Suspending function to get a yearly subscription package
     */
    suspend fun getYearlyPackage(): Package? {
        try {
            val offerings = getOfferings()
            val yearlyPackage = offerings?.availablePackages?.find { it.product.id == YEARLY_PRODUCT_ID }
                ?: offerings?.availablePackages?.find { it.packageType == PackageType.ANNUAL }
            
            if (yearlyPackage == null) {
                Log.w(TAG, "Yearly package not found - check product configuration")
            } else {
                Log.d(TAG, "Yearly package found: ${yearlyPackage.product.id}")
            }
            
            return yearlyPackage
        } catch (e: Exception) {
            Log.e(TAG, "Error getting yearly package: ${e.message}", e)
            return null
        }
    }
    
    /**
     * Helper to get current offerings
     */
    private suspend fun getOfferings() = suspendCancellableCoroutine<Offering?> { continuation ->
        Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
            override fun onError(error: PurchasesError) {
                Log.e(TAG, "Error fetching offerings: $error")
                continuation.resume(null)
            }
            
            override fun onReceived(offerings: Offerings) {
                continuation.resume(offerings.current)
            }
        })
    }
    
    /**
     * Get the main offering with weekly and yearly options.
     */
    suspend fun getMainOffering(): Pair<Package?, Package?> {
        return suspendCancellableCoroutine { continuation ->
            Purchases.sharedInstance.getOfferings(object : ReceiveOfferingsCallback {
                override fun onError(error: PurchasesError) {
                    Log.e(TAG, "Error fetching offerings: $error")
                    continuation.resume(Pair(null, null))
                }
                
                override fun onReceived(offerings: Offerings) {
                    val defaultOffering = offerings.current
                    
                    if (defaultOffering == null) {
                        Log.e(TAG, "No default offering found - check RevenueCat dashboard configuration")
                        continuation.resume(Pair(null, null))
                        return
                    }
                    
                    if (defaultOffering.availablePackages.isEmpty()) {
                        Log.e(TAG, "No available packages in default offering - check product configuration")
                        continuation.resume(Pair(null, null))
                        return
                    }
                    
                    // Get weekly and yearly packages by product ID first, then fallback to package type
                    val weeklyPackage = defaultOffering.availablePackages.find { pkg -> pkg.product.id == WEEKLY_PRODUCT_ID }
                        ?: defaultOffering.availablePackages.find { pkg -> pkg.packageType == PackageType.WEEKLY }
                    
                    val yearlyPackage = defaultOffering.availablePackages.find { pkg -> pkg.product.id == YEARLY_PRODUCT_ID }
                        ?: defaultOffering.availablePackages.find { pkg -> pkg.packageType == PackageType.ANNUAL }
                    
                    if (weeklyPackage != null) {
                        Log.d(TAG, "Found weekly package: ${weeklyPackage.product.id}, price: ${weeklyPackage.product.price}")
                    } else {
                        Log.w(TAG, "Weekly package not found - check product ID: $WEEKLY_PRODUCT_ID")
                    }
                    
                    if (yearlyPackage != null) {
                        Log.d(TAG, "Found yearly package: ${yearlyPackage.product.id}, price: ${yearlyPackage.product.price}")
                    } else {
                        Log.w(TAG, "Yearly package not found - check product ID: $YEARLY_PRODUCT_ID")
                    }
                    
                    continuation.resume(Pair(weeklyPackage, yearlyPackage))
                }
            })
        }
    }
} 