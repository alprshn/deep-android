package com.kami_apps.deepwork.deep_work_app.domain.repository

import com.kami_apps.deepwork.deep_work_app.domain.data.AppIcon
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    suspend fun getInstalledApps(page: Int, pageSize: Int): List<InstalledApp>
    suspend fun getTotalAppCount(): Int
    
    // App Icon Management
    suspend fun getAvailableAppIcons(): List<AppIcon>
    suspend fun getCurrentAppIcon(): AppIcon
    suspend fun changeAppIcon(iconId: String): Boolean
}