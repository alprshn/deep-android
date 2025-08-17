package com.kamiapps.deep.deep.domain.repository

import com.kamiapps.deep.deep.domain.data.AppIcon
import com.kamiapps.deep.deep.domain.data.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getInstalledApps(page: Int, pageSize: Int): List<InstalledApp>
    suspend fun getTotalAppCount(): Int
    
    // App Icon Management
    suspend fun getAvailableAppIcons(): List<AppIcon>
    suspend fun getCurrentAppIcon(): AppIcon
    suspend fun changeAppIcon(iconId: String): Boolean
    
    // App Blocking Management
    suspend fun getBlockedApps(): List<String>
    suspend fun addBlockedApp(packageName: String)
    suspend fun removeBlockedApp(packageName: String)
    suspend fun isAppBlocked(packageName: String): Boolean
    suspend fun clearAllBlockedApps()
    fun getBlockedAppsFlow(): Flow<List<String>>
}