package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingButtonSection
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingPageContent
import kotlinx.coroutines.launch

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
                onNextClick = {
                    viewModel.handleAction(OnboardingActions.NextPage)
                },
                onPreviousClick = {
                    viewModel.handleAction(OnboardingActions.PreviousPage)
                },
                onSkipClick = {
                    viewModel.handleAction(OnboardingActions.SkipOnboarding)
                },
                isVisible = if (uiState.currentPage == 0) uiState.showButtons else true // İlk sayfa için animasyon kontrolü
            )
        }
    }
} 