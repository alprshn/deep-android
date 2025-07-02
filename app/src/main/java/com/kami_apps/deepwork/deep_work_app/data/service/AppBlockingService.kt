package com.kami_apps.deepwork.deep_work_app.data.service

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class AppBlockingService : Service() {

    @Inject
    lateinit var appRepository: com.kami_apps.deepwork.deep_work_app.domain.repository.AppRepository

    private var serviceJob: Job? = null
    private var isBlocking = false
    private var blockedApps = setOf<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var currentBlockedPackage: String? = null

    companion object {
        const val ACTION_START_BLOCKING = "start_blocking"
        const val ACTION_STOP_BLOCKING = "stop_blocking"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "APP_BLOCKING_CHANNEL"
        const val CHECK_INTERVAL = 1000L // 1 saniye
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppBlockingService", "Service created")
        createNotificationChannel()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("AppBlockingService", "onStartCommand called with action: ${intent?.action}")
        when (intent?.action) {
            ACTION_START_BLOCKING -> {
                Log.d("AppBlockingService", "Starting blocking...")
                startBlocking()
            }

            ACTION_STOP_BLOCKING -> {
                Log.d("AppBlockingService", "Stopping blocking...")
                stopBlocking()
            }

            else -> {
                Log.d("AppBlockingService", "Unknown or null action received")
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AppBlockingService", "Service onDestroy called")
        removeBlockOverlay() // Clean up any active overlay
    }

    private fun startBlocking() {
        Log.d("AppBlockingService", "startBlocking() called, isBlocking: $isBlocking")
        if (isBlocking) {
            Log.d("AppBlockingService", "Already blocking, returning")
            return
        }

        isBlocking = true
        Log.d("AppBlockingService", "Starting foreground service")
        startForeground(NOTIFICATION_ID, createNotification())

        serviceJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("AppBlockingService", "Loading blocked apps from repository")
                blockedApps = appRepository.getBlockedApps().toSet()
                Log.d(
                    "AppBlockingService",
                    "Blocked apps loaded: ${blockedApps.size} apps -> $blockedApps"
                )
                startAppMonitoring()
            } catch (e: Exception) {
                Log.e("AppBlockingService", "Error in startBlocking coroutine", e)
            }
        }
    }

    private fun stopBlocking() {
        Log.d("AppBlockingService", "stopBlocking() called")
        isBlocking = false
        serviceJob?.cancel()
        checkRunnable?.let {
            handler.removeCallbacks(it)
            Log.d("AppBlockingService", "Monitoring callbacks removed")
        }

        // Remove any active overlay
        removeBlockOverlay()

        stopForeground(STOP_FOREGROUND_REMOVE)
        Log.d("AppBlockingService", "Foreground service stopped")
        stopSelf()
        Log.d("AppBlockingService", "Service stopped")
    }

    private fun startAppMonitoring() {
        Log.d("AppBlockingService", "Starting app monitoring")
        checkRunnable = object : Runnable {
            override fun run() {
                if (isBlocking) {
                    checkCurrentApp()
                    handler.postDelayed(this, CHECK_INTERVAL)
                } else {
                    Log.d("AppBlockingService", "Monitoring stopped - isBlocking is false")
                }
            }
        }
        handler.post(checkRunnable!!)
    }

    private fun checkCurrentApp() {
        val currentApp = getCurrentForegroundApp()
        Log.d("AppBlockingService", "Current foreground app: $currentApp")

        if (currentApp != null) {
            if (blockedApps.contains(currentApp)) {
                Log.w("AppBlockingService", "BLOCKED APP DETECTED: $currentApp - Showing overlay")
                showBlockOverlay(currentApp)
            } else {
                // If user switched to a non-blocked app, remove overlay
                if (currentBlockedPackage != null && currentBlockedPackage != currentApp) {
                    Log.d(
                        "AppBlockingService",
                        "User switched from blocked app to allowed app, removing overlay"
                    )
                    removeBlockOverlay()
                }

                // Only log every 10th check to reduce spam
                if (System.currentTimeMillis() % 10000 < 1000) {
                    Log.d("AppBlockingService", "App $currentApp is not blocked")
                }
            }
        } else {
            // Only log every 10th check to reduce spam
            if (System.currentTimeMillis() % 10000 < 1000) {
                Log.d("AppBlockingService", "No foreground app detected")
            }
        }
    }

    private fun getCurrentForegroundApp(): String? {
        try {
            val usageStatsManager =
                getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 2000 // 2 seconds window

            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)

            if (usageEvents == null) {
                // Only log every 10th check to reduce spam
                if (System.currentTimeMillis() % 10000 < 1000) {
                    Log.w("AppBlockingService", "No usage events available - check permissions")
                }
                return null
            }

            var latestPackage: String? = null
            var latestTimestamp = 0L

            while (usageEvents.hasNextEvent()) {
                val event = android.app.usage.UsageEvents.Event()
                usageEvents.getNextEvent(event)

                if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (event.timeStamp > latestTimestamp) {
                        latestTimestamp = event.timeStamp
                        latestPackage = event.packageName
                    }
                }
            }

            // If no events in small window, try a larger window (up to 30 seconds)
            if (latestPackage == null) {
                val largerBeginTime = endTime - 30000 // 30 seconds window
                val largerUsageEvents = usageStatsManager.queryEvents(largerBeginTime, endTime)

                if (largerUsageEvents != null) {
                    while (largerUsageEvents.hasNextEvent()) {
                        val event = android.app.usage.UsageEvents.Event()
                        largerUsageEvents.getNextEvent(event)

                        if (event.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            if (event.timeStamp > latestTimestamp) {
                                latestTimestamp = event.timeStamp
                                latestPackage = event.packageName
                            }
                        }
                    }
                }
            }

            return latestPackage
        } catch (e: Exception) {
            Log.e("AppBlockingService", "Error getting foreground app", e)
            return null
        }
    }

    private fun showBlockOverlay(packageName: String) {
        try {
            Log.i("AppBlockingService", "Attempting to show block overlay for: $packageName")

            // If overlay is already showing for the same app, don't recreate it
            if (currentBlockedPackage == packageName && overlayView != null) {
                Log.d("AppBlockingService", "Overlay already showing for $packageName")
                return
            }

            // Remove any existing overlay first
            removeBlockOverlay()

            currentBlockedPackage = packageName

            // Create native Android View overlay (no Compose complications)
            overlayView = createNativeOverlayView(packageName, getAppName(packageName))

            // Window parameters for overlay
            val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                overlayType,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }

            // Add overlay to window
            windowManager?.addView(overlayView, params)
            Log.i("AppBlockingService", "Block overlay added successfully for $packageName")

        } catch (e: Exception) {
            Log.e("AppBlockingService", "Failed to show block overlay", e)
            currentBlockedPackage = null
            overlayView = null
        }
    }

    private fun removeBlockOverlay() {
        try {
            overlayView?.let { view ->
                windowManager?.removeView(view)
                Log.d("AppBlockingService", "Block overlay removed")
            }
        } catch (e: Exception) {
            Log.e("AppBlockingService", "Error removing overlay", e)
        } finally {
            overlayView = null
            currentBlockedPackage = null
        }
    }
    
    private fun createNativeOverlayView(packageName: String, appName: String): View {
        // Create main container
        val mainLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xF0000000.toInt()) // Semi-transparent black
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        
        // Title text
        val titleText = TextView(this).apply {
            text = "Stay Focused!"
            textSize = 24f
            setTextColor(0xFFFFFFFF.toInt()) // White
            gravity = Gravity.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }
        
        // Message text
        val messageText = TextView(this).apply {
            text = "$appName is blocked during your focus session"
            textSize = 18f
            setTextColor(0xFFCCCCCC.toInt()) // Light gray
            gravity = Gravity.CENTER
            setPadding(0, 32, 0, 16)
        }
        
        // Subtitle text
        val subtitleText = TextView(this).apply {
            text = "Keep working towards your goals!"
            textSize = 16f
            setTextColor(0xFFCCCCCC.toInt()) // Light gray
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 48)
        }
        
        // Return to focus button
        val returnButton = Button(this).apply {
            text = "Return to Focus"
            textSize = 16f
            setBackgroundColor(0xFF2196F3.toInt()) // Blue
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(32, 16, 32, 16)
            setOnClickListener { removeBlockOverlay() }
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            layoutParams = params
        }
        
        // Go to home button
        val homeButton = Button(this).apply {
            text = "Go to Home"
            textSize = 16f
            setBackgroundColor(0xFF666666.toInt()) // Gray
            setTextColor(0xFFFFFFFF.toInt())
            setPadding(32, 16, 32, 16)
            setOnClickListener { 
                removeBlockOverlay()
                goToHomeScreen()
            }
            
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
        
        // Add all views to main layout
        mainLayout.addView(titleText)
        mainLayout.addView(messageText)
        mainLayout.addView(subtitleText)
        mainLayout.addView(returnButton)
        mainLayout.addView(homeButton)
        
        return mainLayout
    }

    private fun getAppName(packageName: String): String {
        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: Exception) {
            Log.w("AppBlockingService", "Could not get app name for $packageName", e)
            packageName // Fallback to package name
        }
    }

    private fun goToHomeScreen() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(homeIntent)
        } catch (e: Exception) {
            Log.e("AppBlockingService", "Failed to go to home screen", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Blocking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows when apps are being blocked during focus sessions"
                setShowBadge(false)
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Focus Mode Active")
            .setContentText("Apps are being blocked during your focus session")
            .setSmallIcon(R.drawable.ic_stopwatch)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}