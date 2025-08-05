package com.kami_apps.deepwork.deep_work_app.di

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.kami_apps.deepwork.deep_work_app.data.manager.RevenueCatManager
import com.kami_apps.deepwork.deep_work_app.data.manager.PremiumManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DeepWorkApp : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var premiumManager: PremiumManager

    override fun onCreate() {
        super.onCreate()
        
        // Initialize RevenueCat first
        RevenueCatManager.init(this)
        
        // Initialize PremiumManager after RevenueCat is ready
        premiumManager.initialize()
        Log.d("DeepWorkApp", "Application initialized with Premium and RevenueCat")

        // Manuel initialize etmeyin - sadece Configuration.Provider implement edin
        // WorkManager kendi kendine başlatılacak ve sizin configuration'ınızı kullanacak
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}
