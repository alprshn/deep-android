package com.kami_apps.deepwork.deep.data.service

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
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
    lateinit var appRepository: com.kami_apps.deepwork.deep.domain.repository.AppRepository

    private var serviceJob: Job? = null
    private var isBlocking = false
    private var blockedApps = setOf<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var checkRunnable: Runnable? = null
    private var overlayView: View? = null
    private var windowManager: WindowManager? = null
    private var currentBlockedPackage: String? = null

    // Performans optimizasyonları
    private var lastCheckedApp: String? = null
    private var lastCheckTime = 0L
    private var preCreatedOverlay: View? = null // Önceden oluşturulan overlay
    private var packageNameCache = mutableMapOf<String, String>() // App isim cache
    private var usageStatsManager: UsageStatsManager? = null

    companion object {
        const val ACTION_START_BLOCKING = "start_blocking"
        const val ACTION_STOP_BLOCKING = "stop_blocking"
        const val NOTIFICATION_ID = 2001
        const val CHANNEL_ID = "APP_BLOCKING_CHANNEL"
        const val CHECK_INTERVAL = 300L // 300ms'ye düşürdük (daha hızlı kontrol)
        const val MIN_CHECK_INTERVAL = 100L // Minimum interval
        const val CACHE_CLEANUP_INTERVAL = 60000L // 1 dakika
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("AppBlockingService", "Service created")
        createNotificationChannel()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        // Overlay'i önceden oluştur
        preCreateOverlay()
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
        removeBlockOverlay()
        preCreatedOverlay = null
        packageNameCache.clear()
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
                Log.d("AppBlockingService", "Blocked apps loaded: ${blockedApps.size} apps -> $blockedApps")

                // Ana thread'e geç ve monitoring'i başlat
                withContext(Dispatchers.Main) {
                    startAppMonitoring()
                }
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
                    val currentTime = System.currentTimeMillis()

                    // Hızlı kontrol - çok yakın zamanda kontrol edildi mi?
                    if (currentTime - lastCheckTime > MIN_CHECK_INTERVAL) {
                        checkCurrentApp()
                        lastCheckTime = currentTime
                    }

                    // Dinamik interval - eğer blocked app tespit edilirse daha hızlı kontrol et
                    val interval = if (currentBlockedPackage != null) {
                        CHECK_INTERVAL / 2 // Blocked app varsa daha hızlı
                    } else {
                        CHECK_INTERVAL
                    }

                    handler.postDelayed(this, interval)
                } else {
                    Log.d("AppBlockingService", "Monitoring stopped - isBlocking is false")
                }
            }
        }
        handler.post(checkRunnable!!)
    }

    private fun checkCurrentApp() {
        val currentApp = getCurrentForegroundApp()

        // Aynı app'i tekrar kontrol etme optimizasyonu
        if (currentApp == lastCheckedApp) {
            return
        }

        lastCheckedApp = currentApp
        Log.d("AppBlockingService", "Current foreground app: $currentApp")

        if (currentApp != null) {
            if (blockedApps.contains(currentApp)) {
                Log.w("AppBlockingService", "BLOCKED APP DETECTED: $currentApp - Showing overlay")
                showBlockOverlay(currentApp)
            } else {
                // Non-blocked app'e geçiş
                if (currentBlockedPackage != null && currentBlockedPackage != currentApp) {
                    Log.d("AppBlockingService", "User switched from blocked app to allowed app, removing overlay")
                    removeBlockOverlay()
                }
            }
        }
    }

    private fun getCurrentForegroundApp(): String? {
        try {
            if (usageStatsManager == null) return null

            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 1000 // 1 saniye pencere (daha küçük)

            val usageEvents = usageStatsManager!!.queryEvents(beginTime, endTime)
            if (usageEvents == null) return null

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

            return latestPackage
        } catch (e: Exception) {
            Log.e("AppBlockingService", "Error getting foreground app", e)
            return null
        }
    }

    private fun showBlockOverlay(packageName: String) {
        try {
            Log.i("AppBlockingService", "Attempting to show block overlay for: $packageName")

            // Aynı app için overlay zaten gösteriliyorsa, tekrar oluşturma
            if (currentBlockedPackage == packageName && overlayView != null) {
                Log.d("AppBlockingService", "Overlay already showing for $packageName")
                return
            }

            // Mevcut overlay'i kaldır
            removeBlockOverlay()
            currentBlockedPackage = packageName

            // Önceden oluşturulan overlay'i kullan veya yeni bir tane oluştur
            overlayView = preCreatedOverlay?.let { view ->
                updateOverlayContent(view, packageName)
                view
            } ?: createNativeOverlayView(packageName, getAppName(packageName))

            // Window parametreleri
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
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN or
                        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                PixelFormat.TRANSLUCENT

            ).apply {
                gravity = Gravity.TOP or Gravity.START
            }

            // Overlay'i ekle
            windowManager?.addView(overlayView, params)
            Log.i("AppBlockingService", "Block overlay added successfully for $packageName")


            // Overlay ekledikten sonra immersive mode'u ayarla
            setImmersiveMode()

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

    private fun preCreateOverlay() {
        // XML layout'u önceden oluştur
        preCreatedOverlay = LayoutInflater.from(this).inflate(R.layout.overlay_block_screen, null)

        // Button listeners'ı ayarla
        val homeButton = preCreatedOverlay?.findViewById<Button>(R.id.home_button)


        homeButton?.setOnClickListener {
            removeBlockOverlay()
            goToHomeScreen()

        }
        preCreatedOverlay?.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // System UI görünür hale gelirse tekrar gizle
                handler.postDelayed({
                    setImmersiveMode()
                }, 500)
            }
        }
    }

    private fun updateOverlayContent(view: View, packageName: String) {
        // Sadece app ismini güncelle
        val appNameText = view.findViewById<TextView>(R.id.app_name_text)
        appNameText?.text = getAppName(packageName)
    }

    private fun createNativeOverlayView(packageName: String, appName: String): View {
        // XML layout'u inflate et
        val overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_block_screen, null)

        // View'ları bul ve içeriği ayarla
        val appNameText = overlayView.findViewById<TextView>(R.id.app_name_text)
        val homeButton = overlayView.findViewById<Button>(R.id.home_button)
        val focusTimeText = overlayView.findViewById<TextView>(R.id.focus_time_text)

        // İçeriği ayarla
        appNameText.text = appName



        homeButton.setOnClickListener {
            removeBlockOverlay()
            goToHomeScreen()
        }

        // Focus time bilgisini ayarla (isteğe bağlı)
        focusTimeText.text = "Focus session active"
// Overlay'in tam ekran olması için sistem UI'yi gizle
        overlayView.setOnSystemUiVisibilityChangeListener { visibility ->
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
                // System UI görünür hale gelirse tekrar gizle
                handler.postDelayed({
                    setImmersiveMode()
                }, 500)
            }
        }
        return overlayView
    }

    private fun getAppName(packageName: String): String {
        // Cache'den kontrol et
        packageNameCache[packageName]?.let { return it }

        return try {
            val packageManager = packageManager
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            val appName = packageManager.getApplicationLabel(applicationInfo).toString()

            // Cache'e ekle
            packageNameCache[packageName] = appName
            appName
        } catch (e: Exception) {
            Log.w("AppBlockingService", "Could not get app name for $packageName", e)
            packageName
        }
    }

    private fun goToHomeScreen() {
        try {
            val homeIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
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

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
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

    private fun setImmersiveMode() {
        try {
            overlayView?.let { view ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    // Android 11+ için yeni API
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                } else {
                    // Eski Android sürümleri için
                    view.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                }
            }
        } catch (e: Exception) {
            Log.e("AppBlockingService", "Error setting immersive mode", e)
        }
    }
}
