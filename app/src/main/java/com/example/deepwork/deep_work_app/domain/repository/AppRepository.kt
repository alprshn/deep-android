package com.example.deepwork.deep_work_app.domain.repository

import com.example.deepwork.deep_work_app.domain.data.InstalledApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getInstalledApps(): Flow<List<InstalledApp>>
}