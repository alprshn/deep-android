package com.kami_apps.deepwork.deep_work_app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp
import com.kami_apps.deepwork.deep_work_app.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AppRepositoryImpl(
    private val context: Context
) : AppRepository {

    override fun getInstalledApps(): Flow<List<InstalledApp>> = flow {
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                // Sisteme ait olmayan (yani kullanıcı tarafından yüklenen) uygulamaları getir
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            }
            .map {
                InstalledApp(
                    appName = pm.getApplicationLabel(it).toString(),
                    packageName = it.packageName,
                    icon = pm.getApplicationIcon(it)
                )
            }
            .sortedBy { it.appName } // Alfabetik sırala
        emit(apps)
    }.flowOn(Dispatchers.IO)
}