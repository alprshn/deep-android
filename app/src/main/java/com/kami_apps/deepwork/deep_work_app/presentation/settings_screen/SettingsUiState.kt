package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen

import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp

data class SettingsUiState(
    val installedApps: List<InstalledApp> = emptyList()
)