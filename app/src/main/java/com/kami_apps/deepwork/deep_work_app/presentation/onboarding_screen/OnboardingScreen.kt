package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingButtonSection
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingPageContent
import com.kami_apps.deepwork.deep_work_app.util.rememberScreenTimePermissionLauncher
import kotlinx.coroutines.launch
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

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
    
    // Screen Time Permission Launcher
    val permissionLauncher = rememberScreenTimePermissionLauncher { granted ->
        if (granted) {
            // Permission verildiyse sonraki sayfaya geç
            viewModel.handleAction(OnboardingActions.NextPage)
        }
        // Permission verilmezse kullanıcı manuel olarak settings'den verebilir
    }

    // Notification Permission Launcher
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            // Permission verildiyse onboarding'i tamamla
            viewModel.handleAction(OnboardingActions.CompleteOnboarding)
        } else {
            // Permission verilmezse de onboarding'i tamamla
            viewModel.handleAction(OnboardingActions.CompleteOnboarding)
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
            .background(Color.Black)
    ) {
        // Sayfa içerikleri
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
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
                        permissionLauncher.requestUsageAccessPermission()
                    },
                    onRequestNotificationPermission = {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            // Android 13 altında notification permission otomatik verilir
                            viewModel.handleAction(OnboardingActions.CompleteOnboarding)
                        }
                    },
                    onMaybeLater = {
                        viewModel.handleAction(OnboardingActions.MaybeLater)
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