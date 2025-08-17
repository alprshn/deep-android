package com.kami_apps.deepwork.deep.util

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

object PermissionHelper {
    
    /**
     * Kullanım verilerine erişim iznini kontrol eder
     */
    fun hasUsageStatsPermission(context: Context): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    
    /**
     * Diğer uygulamaların üzerinde göster iznini kontrol eder
     */
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    /**
     * Pil optimizasyonu devre dışı bırakma iznini kontrol eder
     */
    fun isBatteryOptimizationDisabled(context: Context): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            powerManager.isIgnoringBatteryOptimizations(context.packageName)
        } else {
            true
        }
    }
    
    /**
     * Kullanım verilerine erişim ayarlarını açar
     */
    fun requestUsageStatsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }
    
    /**
     * Overlay izin ayarlarını açar
     */
    fun requestOverlayPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
    
    /**
     * Pil optimizasyonu ayarlarını açar
     */
    fun requestBatteryOptimizationDisable(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
    
    /**
     * Tüm izinlerin verilip verilmediğini kontrol eder
     */
    fun hasAllPermissions(context: Context): Boolean {
        return hasUsageStatsPermission(context) && 
               hasOverlayPermission(context) && 
               isBatteryOptimizationDisabled(context)
    }
    
    /**
     * Eksik izinlerin listesini döndürür
     */
    fun getMissingPermissions(context: Context): List<PermissionType> {
        val missing = mutableListOf<PermissionType>()
        
        if (!hasUsageStatsPermission(context)) {
            missing.add(PermissionType.USAGE_STATS)
        }
        
        if (!hasOverlayPermission(context)) {
            missing.add(PermissionType.OVERLAY)
        }
        
        if (!isBatteryOptimizationDisabled(context)) {
            missing.add(PermissionType.BATTERY_OPTIMIZATION)
        }
        
        return missing
    }
    
    enum class PermissionType(val displayName: String, val description: String) {
        USAGE_STATS(
            "Usage Data Access",
            "Required to detect when blocked apps are opened"
        ),
        OVERLAY(
            "Display Over Other Apps", 
            "Required to show blocking screen over other apps"
        ),
        BATTERY_OPTIMIZATION(
            "Battery Optimization",
            "Prevents the app from being killed in background"
        )
    }
} 