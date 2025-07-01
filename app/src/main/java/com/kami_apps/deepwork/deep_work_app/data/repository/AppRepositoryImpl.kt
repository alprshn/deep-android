package com.kami_apps.deepwork.deep_work_app.data.repository

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.kami_apps.deepwork.deep_work_app.domain.data.AppIcon
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp
import com.kami_apps.deepwork.deep_work_app.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.kami_apps.deepwork.R

class AppRepositoryImpl(
    private val context: Context
) : AppRepository {

    private val packageManager = context.packageManager
    
    companion object {
        private const val BASE_PACKAGE = "com.kami_apps.deepwork"
        private const val DEFAULT_ALIAS = "$BASE_PACKAGE.MainActivityDefault"
        private const val BLUE_ALIAS = "$BASE_PACKAGE.MainActivityBlue" 
        private const val WHITE_ALIAS = "$BASE_PACKAGE.MainActivityRed"
    }

    private suspend fun getAllInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
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
    }

    override suspend fun getInstalledApps(page: Int, pageSize: Int): List<InstalledApp> {
        val allApps = getAllInstalledApps()
        val startIndex = page * pageSize
        val endIndex = minOf(startIndex + pageSize, allApps.size)
        
        return if (startIndex < allApps.size) {
            allApps.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    override suspend fun getTotalAppCount(): Int {
        return getAllInstalledApps().size
    }

    override suspend fun getAvailableAppIcons(): List<AppIcon> = withContext(Dispatchers.IO) {
        val currentIcon = getCurrentAppIcon()
        
        listOf(
            AppIcon(
                id = "original",
                name = "Original",
                activityAlias = DEFAULT_ALIAS,
                iconRes = R.mipmap.ic_launcher,
                isSelected = currentIcon.id == "original"
            ),
            AppIcon(
                id = "blue",
                name = "Blue",
                activityAlias = BLUE_ALIAS,
                iconRes = R.mipmap.ic_blue,
                isSelected = currentIcon.id == "blue"
            ),
            AppIcon(
                id = "white", 
                name = "White",
                activityAlias = WHITE_ALIAS,
                iconRes = R.mipmap.ic_white,
                isSelected = currentIcon.id == "white"
            )
        )
    }

    override suspend fun getCurrentAppIcon(): AppIcon = withContext(Dispatchers.IO) {
        val enabledState = try {
            packageManager.getComponentEnabledSetting(
                android.content.ComponentName(context, DEFAULT_ALIAS)
            )
        } catch (e: Exception) {
            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT
        }

        val blueEnabledState = try {
            packageManager.getComponentEnabledSetting(
                android.content.ComponentName(context, BLUE_ALIAS)
            )
        } catch (e: Exception) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        val whiteEnabledState = try {
            packageManager.getComponentEnabledSetting(
                android.content.ComponentName(context, WHITE_ALIAS)
            )
        } catch (e: Exception) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        when {
            blueEnabledState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> {
                AppIcon("blue", "Blue", BLUE_ALIAS, R.mipmap.ic_blue, true)
            }
            whiteEnabledState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> {
                AppIcon("white", "White", WHITE_ALIAS, R.mipmap.ic_white, true)
            }
            else -> {
                AppIcon("original", "Original", DEFAULT_ALIAS, R.mipmap.ic_launcher, true)
            }
        }
    }

    override suspend fun changeAppIcon(iconId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Önce tüm alias'ları disable et
            disableAllAliases()
            
            // Seçilen icon'u enable et
            val targetAlias = when (iconId) {
                "blue" -> BLUE_ALIAS
                "white" -> WHITE_ALIAS
                else -> DEFAULT_ALIAS
            }
            
            packageManager.setComponentEnabledSetting(
                android.content.ComponentName(context, targetAlias),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun disableAllAliases() {
        val aliases = listOf(DEFAULT_ALIAS, BLUE_ALIAS, WHITE_ALIAS)
        
        aliases.forEach { alias ->
            try {
                packageManager.setComponentEnabledSetting(
                    android.content.ComponentName(context, alias),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}