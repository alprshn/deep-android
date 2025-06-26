package com.kami_apps.deepwork.deep_work_app.domain.repository

import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<InstalledApp>>
}