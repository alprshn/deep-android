package com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen

import android.app.Activity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.twotone.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep_work_app.presentation.components.ShimmerEffectOnImage
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.DynamicShimmeringText
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.ShimmeringText
import com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen.components.FeatureItem
import com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen.components.GradientButton
import com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen.components.SubscriptionOptionCard
import kotlinx.coroutines.launch

@Composable
fun PaywallScreen(
    onDismiss: () -> Unit,
    onSubscribe: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    // Alpha animasyonları - layout shifting önlemek için
    val titleAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        label = "title_alpha"
    )
    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    // Handle errors with snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // If user is already premium, navigate back
    LaunchedEffect(uiState.isPremium) {
        if (uiState.isPremium) {
            onSubscribe()
        }
    }

    // Handle errors with snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.handleAction(PaywallActions.DismissError)
        }
    }

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bg_stars),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header Section with Background Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // Close Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .padding(8.dp)
                            .align(alignment = Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {

                    ShimmerEffectOnImage(
                        painter = painterResource(id = R.drawable.deep_inline_icon),
                        modifier = Modifier.size((screenHeight * 0.07).dp)
                    )


                    DynamicShimmeringText(
                        modifier = Modifier.alpha(titleAlpha),
                        text = "Deep Premium", baseColor = Color.Gray, textStyle = TextStyle(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 34.sp,
                        )
                    )

                    Text(
                        text = "Enhance yoyr focus and achieve deeper\nproductivity",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )

                    // Features Section
                    FeatureItem(
                        title = "Advanced Analytics",
                        description = "Track your focuş trends over time",
                        icon = Icons.Default.BarChart
                    )

                    FeatureItem(
                        title = "Unlimited Sessions",
                        description = "Create as many focus sessions as\nyou need",
                        icon = Icons.TwoTone.HourglassBottom
                    )

                    FeatureItem(
                        title = "App Blocking",
                        description = "Block distracting apps during focus",
                        icon = Icons.Default.Shield
                    )

                    FeatureItem(
                        title = "Custom Themes",
                        description = "Personalize with unique app icons",
                        icon = Icons.Default.Palette
                    )
                }

                // Subscription Options Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Free Trial Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF565656).copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Enable Free Trial",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Not sure yet? Try first",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = uiState.isFreeTrial,
                                onCheckedChange = {
                                    viewModel.handleAction(PaywallActions.ToggleFreeTrial)
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF0A84FF),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                    }

                    // Weekly subscription option
                    SubscriptionOptionCard(
                        title = "3-Day Trial",
                        price = uiState.weeklyPackage?.product?.price?.formatted ?: "₺17,99",
                        subtext = "then ${uiState.weeklyPackage?.product?.price?.formatted ?: "₺17,99"} per week",
                        isSelected = uiState.isFreeTrial,
                        onClick = {
                            viewModel.handleAction(PaywallActions.SelectPlan(PlanType.WEEKLY))
                        },
                        onInfoClick = {
                            viewModel.handleAction(PaywallActions.ShowInfoDialog)
                        }
                    )

                    // Yearly subscription option
                    val yearlyPrice = uiState.yearlyPackage?.product?.price?.formatted ?: "000"
                    val discountedYearlyPrice = viewModel.calculateDiscountedPrice(yearlyPrice)

                    SubscriptionOptionCard(
                        title = "Yearly Plan",
                        price = discountedYearlyPrice,
                        subtext = "per year",
                        discountedPrice = yearlyPrice,
                        discount = "SAVE 88%",
                        isSelected = !uiState.isFreeTrial,
                        onClick = {
                            viewModel.handleAction(PaywallActions.SelectPlan(PlanType.YEARLY))
                        },
                        onInfoClick = {
                            viewModel.handleAction(PaywallActions.ShowInfoDialog)
                        }
                    )

                    // Purchase Button
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp),
                            color = Color(0xFFbe5bf4)
                        )
                    } else {
                        GradientButton(
                            text = if (uiState.isFreeTrial) "Start Free Trial" else "Continue",
                            enabled = !uiState.isLoading,
                            showArrow = !uiState.isFreeTrial,
                            onClick = {
                                activity?.let { act ->
                                    val packageToPurchase = viewModel.getCurrentPackage()

                                    if (packageToPurchase == null) {
                                        if (uiState.configError) {
                                            // Test mode
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("✅ Test purchase completed! (Debug mode)")
                                                kotlinx.coroutines.delay(1500)
                                                onSubscribe()
                                            }
                                        } else {
                                            coroutineScope.launch {
                                                snackbarHostState.showSnackbar("Package not available. Please try again later.")
                                            }
                                        }
                                        return@GradientButton
                                    }

                                    // Start actual purchase
                                    viewModel.purchasePackage(act, packageToPurchase)
                                } ?: run {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Cannot process purchase at this time")
                                    }
                                }
                            }
                        )

                        // Footer Links
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextButton(
                                onClick = {
                                    viewModel.handleAction(PaywallActions.RestorePurchases)
                                }
                            ) {
                                Text(
                                    text = "Restore",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.handleAction(PaywallActions.OpenPrivacyPolicy)
                                }
                            ) {
                                Text(
                                    text = "Privacy",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            TextButton(
                                onClick = {
                                    viewModel.handleAction(PaywallActions.OpenTermsOfService)
                                }
                            ) {
                                Text(
                                    text = "Terms",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaywallScreenPreview() {
    val context = LocalContext.current
    val activity = context as? Activity
    val coroutineScope = rememberCoroutineScope()

    // Handle errors with snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // If user is already premium, navigate back


    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Background Image
            Image(
                painter = painterResource(id = R.drawable.bg_stars),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Background with gradient


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header Section with Background Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {

                    // Close Button
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .padding(8.dp)
                            .align(alignment = Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.deep_inline_icon),
                        contentDescription = null,
                        modifier = Modifier.size((screenHeight * 0.07).dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = "Deep Work Pro",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Maximize your focus and productivity with\nadvanced deep work features",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center
                    )

                    // Features Section
                    FeatureItem(
                        title = "Unlimited Focus Sessions",
                        description = "Track as many deep work sessions\nas you want",
                        icon = Icons.Default.Timer
                    )

                    FeatureItem(
                        title = "Advanced App Blocking",
                        description = "Block distracting apps during\nfocus sessions",
                        icon = Icons.Default.Block
                    )

                    FeatureItem(
                        title = "Detailed Analytics",
                        description = "Get insights into your productivity\npatterns and progress",
                        icon = Icons.Default.Analytics
                    )

                    FeatureItem(
                        title = "All Tag Options",
                        description = "Organize your sessions with\nunlimited custom tags",
                        icon = Icons.Default.Book
                    )
                }

                // Subscription Options Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Free Trial Toggle
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF565656).copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Enable Free Trial",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "Not sure yet? Try first",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                            Switch(
                                checked = true,
                                onCheckedChange = {
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFFbe5bf4),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.Gray
                                )
                            )
                        }
                    }

                    // Weekly subscription option
                    SubscriptionOptionCard(
                        title = "3-Day Trial",
                        price = "₺17,99",
                        subtext = "then ₺17,99 per week",
                        isSelected = true,
                        onClick = {
                        },
                        onInfoClick = {
                        }
                    )

                    // Yearly subscription option

                    SubscriptionOptionCard(
                        title = "Yearly Plan",
                        price = "₺17,99",
                        subtext = "per year",
                        discountedPrice = "₺17,99",
                        discount = "SAVE 88%",
                        isSelected = false,
                        onClick = {
                        },
                        onInfoClick = {
                        }
                    )


                    GradientButton(
                        text = "Start Free Trial",
                        enabled = true,
                        showArrow = true,
                        onClick = {
                            activity?.let { act ->
                                // Start actual purchase
                            } ?: run {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Cannot process purchase at this time")
                                }
                            }
                        }
                    )

                    // Footer Links
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextButton(
                            onClick = {
                            }
                        ) {
                            Text(
                                text = "Restore",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        TextButton(
                            onClick = {
                            }
                        ) {
                            Text(
                                text = "Privacy",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        TextButton(
                            onClick = {
                            }
                        ) {
                            Text(
                                text = "Terms",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

            }
        }
    }
}
