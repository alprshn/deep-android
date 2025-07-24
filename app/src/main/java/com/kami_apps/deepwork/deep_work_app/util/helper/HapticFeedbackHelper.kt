package com.kami_apps.deepwork.deep_work_app.util.helper

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticFeedbackHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val vibrator: Vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }
    
    /**
     * Light haptic feedback for button clicks
     */
    fun performButtonClick(hapticFeedback: HapticFeedback? = null) {
        hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        performVibration(VibrationPattern.BUTTON_CLICK)
    }
    
    /**
     * Medium haptic feedback for mode selection
     */
    fun performModeSelection(hapticFeedback: HapticFeedback? = null) {
        hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        performVibration(VibrationPattern.MODE_SELECTION)
    }
    
    /**
     * Light haptic feedback for slider/progress changes
     */
    fun performSliderFeedback(hapticFeedback: HapticFeedback? = null) {
        hapticFeedback?.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        performVibration(VibrationPattern.SLIDER_TICK)
    }
    
    /**
     * Strong haptic feedback for important actions (stop, reset)
     */
    fun performImportantAction(hapticFeedback: HapticFeedback? = null) {
        hapticFeedback?.performHapticFeedback(HapticFeedbackType.LongPress)
        performVibration(VibrationPattern.IMPORTANT_ACTION)
    }
    
    private fun performVibration(pattern: VibrationPattern) {
        if (!vibrator.hasVibrator()) return
        
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (pattern) {
                    VibrationPattern.BUTTON_CLICK -> 
                        VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationPattern.MODE_SELECTION -> 
                        VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationPattern.SLIDER_TICK -> 
                        VibrationEffect.createOneShot(15, 50)
                    VibrationPattern.IMPORTANT_ACTION -> 
                        VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 100), -1)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                when (pattern) {
                    VibrationPattern.BUTTON_CLICK -> vibrator.vibrate(25)
                    VibrationPattern.MODE_SELECTION -> vibrator.vibrate(50)
                    VibrationPattern.SLIDER_TICK -> vibrator.vibrate(15)
                    VibrationPattern.IMPORTANT_ACTION -> vibrator.vibrate(longArrayOf(0, 100, 50, 100), -1)
                }
            }
        } catch (e: Exception) {
            // Vibration permission might not be granted
        }
    }
    
    private enum class VibrationPattern {
        BUTTON_CLICK,
        MODE_SELECTION,
        SLIDER_TICK,
        IMPORTANT_ACTION
    }
} 