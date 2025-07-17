package com.kami_apps.deepwork.deep_work_app.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kami_apps.deepwork.deep_work_app.domain.data.AppIcon
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp
import com.kami_apps.deepwork.deep_work_app.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep_work_app.data.model.AppIconConfig
import java.io.InputStreamReader

class AppRepositoryImpl(
    private val context: Context
) : AppRepository {

    private val packageManager = context.packageManager
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_blocking_prefs", Context.MODE_PRIVATE)
    private val _blockedAppsFlow = MutableStateFlow<List<String>>(emptyList())
    
    companion object {
        private const val BASE_PACKAGE = "com.kami_apps.deepwork"
        private const val DEFAULT_ALIAS = "$BASE_PACKAGE.MainActivityDefault"
        private const val BLUE_ALIAS = "$BASE_PACKAGE.MainActivityBlue" 
        private const val WHITE_ALIAS = "$BASE_PACKAGE.MainActivityRed"
        private const val BLOCKED_APPS_KEY = "blocked_apps"
    }
    
    init {
        // Initialize blocked apps flow
        _blockedAppsFlow.value = getBlockedAppsFromPrefs()
    }

    private suspend fun getAllInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val currentPackageName = context.packageName
        
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter {
                // Sisteme ait olmayan (yani kullanıcı tarafından yüklenen) uygulamaları getir
                (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                // Kendi uygulamamızı listeden çıkar
                it.packageName != currentPackageName
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
        val gson = Gson()
        val inputStream = context.resources.openRawResource(R.raw.app_icons)
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<AppIconConfig>>() {}.type
        val iconConfigs: List<AppIconConfig> = gson.fromJson(reader, type)

        iconConfigs.map { config ->
            val iconResId = context.resources.getIdentifier(
                config.iconResName, "mipmap", context.packageName
            )
            AppIcon(
                id = config.id,
                name = config.name,
                activityAlias = config.activityAlias,
                iconRes = iconResId, // Dynamically get the resource ID
                isSelected = currentIcon.id == config.id
            )
        }
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

    // App Blocking Methods
    override suspend fun getBlockedApps(): List<String> = withContext(Dispatchers.IO) {
        getBlockedAppsFromPrefs()
    }

    override suspend fun addBlockedApp(packageName: String) = withContext(Dispatchers.IO) {
        val currentBlocked = getBlockedAppsFromPrefs().toMutableSet()
        currentBlocked.add(packageName)
        saveBlockedAppsToPrefs(currentBlocked.toList())
        _blockedAppsFlow.value = currentBlocked.toList()
    }

    override suspend fun removeBlockedApp(packageName: String) = withContext(Dispatchers.IO) {
        val currentBlocked = getBlockedAppsFromPrefs().toMutableSet()
        currentBlocked.remove(packageName)
        saveBlockedAppsToPrefs(currentBlocked.toList())
        _blockedAppsFlow.value = currentBlocked.toList()
    }

    override suspend fun isAppBlocked(packageName: String): Boolean = withContext(Dispatchers.IO) {
        getBlockedAppsFromPrefs().contains(packageName)
    }

    override suspend fun clearAllBlockedApps() = withContext(Dispatchers.IO) {
        saveBlockedAppsToPrefs(emptyList())
        _blockedAppsFlow.value = emptyList()
    }

    override fun getBlockedAppsFlow(): Flow<List<String>> = _blockedAppsFlow.asStateFlow()

    private fun getBlockedAppsFromPrefs(): List<String> {
        val blockedAppsString = sharedPreferences.getString(BLOCKED_APPS_KEY, "") ?: ""
        return if (blockedAppsString.isEmpty()) {
            emptyList()
        } else {
            blockedAppsString.split(",").filter { it.isNotBlank() }
        }
    }

    private fun saveBlockedAppsToPrefs(blockedApps: List<String>) {
        sharedPreferences.edit()
            .putString(BLOCKED_APPS_KEY, blockedApps.joinToString(","))
            .apply()
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