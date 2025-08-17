package com.kami_apps.deepwork.deep.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kami_apps.deepwork.deep.domain.data.AppIcon
import com.kami_apps.deepwork.deep.domain.data.InstalledApp
import com.kami_apps.deepwork.deep.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep.data.model.AppIconConfig
import java.io.IOException
import java.io.InputStreamReader

class AppRepositoryImpl(
    private val context: Context
) : AppRepository {

    private val packageManager = context.packageManager
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_blocking_prefs", Context.MODE_PRIVATE)
    private val _blockedAppsFlow = MutableStateFlow<List<String>>(emptyList())


    // Store the loaded icon configurations
    private var allAppIconConfigs: List<AppIconConfig> = emptyList()

    companion object {
        private const val BLOCKED_APPS_KEY = "blocked_apps"
    }
    
    init {
        // Initialize blocked apps flow
        _blockedAppsFlow.value = getBlockedAppsFromPrefs()

        // Load app icon configurations during initialization
        // This should be done carefully, potentially in a background thread
        // For simplicity here, we'll do it synchronously for now, but
        // a better approach for production might be lazy loading or
        // loading on a separate coroutine if startup time is critical.
        loadAppIconConfigs()

    }

    // New private function to load icon configurations
    private fun loadAppIconConfigs() {
        try {
            val gson = Gson()
            val inputStream = context.resources.openRawResource(R.raw.app_icons)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<AppIconConfig>>() {}.type
            allAppIconConfigs = gson.fromJson(reader, type)
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle error: log it, maybe provide a default set of icons
            allAppIconConfigs = emptyList() // Or a default list if critical
        } catch (e: Exception) { // Catching other potential exceptions from Gson
            e.printStackTrace()
            allAppIconConfigs = emptyList()
        }
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

        allAppIconConfigs.map { config ->
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
        // Iterate through all known app icon configurations to find the currently enabled one
        for (config in allAppIconConfigs) {
            try {
                val enabledState = packageManager.getComponentEnabledSetting(
                    android.content.ComponentName(context, config.activityAlias)
                )
                if (enabledState == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                    val iconResId = context.resources.getIdentifier(
                        config.iconResName, "mipmap", context.packageName
                    )
                    return@withContext AppIcon(config.id, config.name, config.activityAlias, iconResId, true)
                }
            } catch (e: Exception) {
                // Component not found or other issue, continue to next
                e.printStackTrace()
            }
        }

        // If no specific alias is enabled, assume the "original" or default one is active.
        // Find the "original" config or return a sensible default.
        val originalConfig = allAppIconConfigs.firstOrNull { it.id == "original" }
        if (originalConfig != null) {
            val iconResId = context.resources.getIdentifier(
                originalConfig.iconResName, "mipmap", context.packageName
            )
            AppIcon(originalConfig.id, originalConfig.name, originalConfig.activityAlias, iconResId, true)
        } else {
            // Fallback if "original" isn't found in config or if config loading failed
            AppIcon("default_fallback", "Default", "${context.packageName}.MainActivityDefault", R.mipmap.ic_launcher, true)
        }
    }



    override suspend fun changeAppIcon(iconId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            // Önce tüm alias'ları disable et
            disableAllAliases()

            // Seçilen icon'u enable et
            val targetAliasConfig = allAppIconConfigs.firstOrNull { it.id == iconId }

            if (targetAliasConfig != null) {
                packageManager.setComponentEnabledSetting(
                    android.content.ComponentName(context, targetAliasConfig.activityAlias),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                true
            } else {
                // If iconId not found in configs, cannot change
                false
            }
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

    // Dynamically get all aliases from the loaded configurations
    private fun disableAllAliases() {
        // Get all alias strings from the loaded configurations
        val aliases = allAppIconConfigs.map { it.activityAlias }

        aliases.forEach { alias ->
            try {
                packageManager.setComponentEnabledSetting(
                    android.content.ComponentName(context, alias),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            } catch (e: Exception) {
                // Ignore if the component is not found or other issues.
                // This can happen if an alias was removed from config but is still "enabled" from a prior run.
                // Log for debugging: e.printStackTrace()
            }
        }
    }
}