package com.kami_apps.deepwork.deep_work_app.data.manager

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    
    private lateinit var revenueCatManager: RevenueCatManager
    
    fun initialize() {
        // RevenueCat zaten Application'da initialize edildi
        revenueCatManager = RevenueCatManager(context)
        
        // RevenueCat'den premium status'ü dinle
        // Bu gerçek implementasyonda RevenueCat'den gelecek
        // Şimdilik debug için false
        _isPremium.value = false
        
        Log.d(TAG, "PremiumManager initialized. Premium status: ${_isPremium.value}")
    }
    
    fun getCurrentPremiumStatus(): Boolean {
        return _isPremium.value
    }
    
    fun setPremiumStatus(isPremium: Boolean) {
        _isPremium.value = isPremium
        Log.d(TAG, "Premium status updated: $isPremium")
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
} 