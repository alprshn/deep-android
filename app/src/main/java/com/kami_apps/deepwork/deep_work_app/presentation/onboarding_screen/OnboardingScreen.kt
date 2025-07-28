package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Security
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingPageContent
import com.kami_apps.deepwork.deep_work_app.util.ScreenTimePermissionHelper
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingButtonSection
import com.kami_apps.deepwork.deep_work_app.util.rememberScreenTimePermissionLauncher
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import com.kami_apps.deepwork.deep_work_app.util.PermissionHelper
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.content.Context
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { uiState.totalPages })
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Screen Time Permission Launcher
    val permissionLauncher = rememberScreenTimePermissionLauncher { granted ->
        Log.d("OnboardingScreen", "Permission launcher callback triggered! Granted: $granted")
        if (granted) {
            Log.d("OnboardingScreen", "Permission granted! Moving to next page...")
            // Permission verildiyse sonraki sayfaya geç
            viewModel.handleAction(OnboardingActions.NextPage)
        } else {
            Log.d("OnboardingScreen", "Permission denied, staying on current page")
        }
        // Permission verilmezse kullanıcı manuel olarak settings'den verebilir
    }

    // Notification Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        // Notification permission sonrası overlay permission iste
        requestOverlayPermission(context) {
            // Overlay permission sonrası battery optimization iste
            requestBatteryOptimization(context) {
                // Tüm permissionlar tamamlandıktan sonra onboarding'i bitir
            viewModel.handleAction(OnboardingActions.CompleteOnboarding)
            }
        }
    }

    // UI state değişikliklerini pager state ile senkronize et
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(
                uiState.currentPage,
                animationSpec = tween(300)
            )
        }
    }

    // Pager state değişikliklerini viewModel'e bildir
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            viewModel.handleAction(OnboardingActions.GoToPage(pagerState.currentPage))
        }
    }

    // Onboarding tamamlandığında ana sayfaya git
    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onNavigateToHome()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Sayfa içerikleri
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Disable swipe gestures
        ) { page ->
            viewModel.getPageAt(page)?.let { onboardingPage ->
                OnboardingPageContent(
                    page = onboardingPage,
                    modifier = Modifier.fillMaxSize(),
                    onShowButtons = {
                        if (page == 0) { // Sadece ilk sayfa için
                            viewModel.handleAction(OnboardingActions.ShowButtons)
                        }
                    },
                    onNextClick = {
                        viewModel.handleAction(OnboardingActions.NextPage)
                    },
                    onConnectScreenTime = {
                        Log.d("OnboardingScreen", "Connect Screen Time button clicked!")
                        Log.d("OnboardingScreen", "Current permission status: ${permissionLauncher.hasUsageAccessPermission()}")
                        permissionLauncher.requestUsageAccessPermission()
                        Log.d("OnboardingScreen", "Permission request sent!")
                    },
                    onRequestNotificationPermission = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            // Android 13 altında notification permission otomatik verilir
                            // Direkt olarak diğer permissionları iste
                            requestOverlayPermission(context) {
                                requestBatteryOptimization(context) {
                                    // Continue onboarding after all permissions
                                    viewModel.handleAction(OnboardingActions.NextPage)
                                }
                            }
                        }
                    },
                    onMaybeLater = {
                        viewModel.handleAction(OnboardingActions.NextPage)
                    },
                    onRequestOverlayPermission = {
                        requestOverlayPermission(context) {}
                    },
                    onRequestBatteryOptimization = {
                        requestBatteryOptimization(context) {}
                    }
                )
            }
        }

        // Alt kısım - Butonlar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Butonlar
            OnboardingButtonSection(
                currentPage = uiState.currentPage,
                totalPages = uiState.totalPages,
                currentPageData = viewModel.getPageAt(uiState.currentPage),
                onNextClick = {
                    viewModel.handleAction(OnboardingActions.NextPage)
                },
                onPreviousClick = {
                    viewModel.handleAction(OnboardingActions.PreviousPage)
                },
                onSkipClick = {
                    viewModel.handleAction(OnboardingActions.CompleteOnboarding)
                },
                isVisible = if (uiState.currentPage == 0) uiState.showButtons else true // İlk sayfa için animasyon kontrolü
            )
        }
    }
}

// Helper functions for other permissions
private fun requestOverlayPermission(context: Context, onComplete: () -> Unit) {
    if (PermissionHelper.hasOverlayPermission(context)) {
        onComplete()
        return
    }
    
    try {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
        context.startActivity(intent)
        // For simplicity, just call onComplete - in real app you'd need to check when user returns
        onComplete()
    } catch (e: Exception) {
        onComplete()
    }
}

private fun requestBatteryOptimization(context: Context, onComplete: () -> Unit) {
    if (PermissionHelper.isBatteryOptimizationDisabled(context)) {
        onComplete()
        return
    }
    
    try {
        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
        context.startActivity(intent)
        onComplete()
    } catch (e: Exception) {
        onComplete()
    }
} 