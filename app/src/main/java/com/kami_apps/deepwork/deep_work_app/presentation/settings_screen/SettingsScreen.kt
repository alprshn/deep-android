package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.ripple
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.outlined.AppBlocking
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MenuDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.kami_apps.deepwork.deep_work_app.data.util.darken
import com.kami_apps.deepwork.deep_work_app.data.util.lighten
import androidx.navigation.NavHostController
import com.kami_apps.deepwork.R
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.twotone.Vibration
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController? = null,
    onShowPaywall: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val restoreMessage by viewModel.restoreMessage.collectAsStateWithLifecycle()
    val isRestoring by viewModel.isRestoring.collectAsStateWithLifecycle()
    val isHapticEnabled by viewModel.isHapticEnabled.collectAsStateWithLifecycle()
    var notificationsEnabled by remember { mutableStateOf(true) }

    val isPremium by viewModel.isPremium.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Theme dropdown state
    var themeDropdownExpanded by remember { mutableStateOf(false) }
    val availableThemes = viewModel.getAvailableThemes()

    // Show restore message as snackbar
    LaunchedEffect(restoreMessage) {
        restoreMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearRestoreMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Text(
                text = "Settings",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            PremiumStatusCard(
                onClick = onShowPaywall,
                isPremium = isPremium
            )
            Card(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
            ) {

                Column {
                    // App Theme Dropdown
                    ThemeDropdownItem(
                        currentTheme = uiState.currentTheme,
                        availableThemes = availableThemes,
                        expanded = themeDropdownExpanded,
                        onExpandedChange = { themeDropdownExpanded = it },
                        onThemeSelected = { theme ->
                            viewModel.changeTheme(theme)
                            themeDropdownExpanded = false
                        }
                    )
                    SettingsBoxesItem(
                        text = "App Icon",
                        icon = Icons.Default.Apps,
                        onClickSettingsBoxesItem = {
                            navController?.navigate("AppIcon")
                        }
                    )
                    SettingsSwitchItem(
                        title = "Notification",
                        isChecked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        icon = Icons.Rounded.NotificationsNone
                    )
                    SettingsSwitchItem(
                        title = "Haptic Feedback",
                        isChecked = isHapticEnabled,
                        onCheckedChange = {
                            // Haptic feedback when toggling ON (not OFF)
                            if (!isHapticEnabled) {
                                val hapticHelper =
                                    com.kami_apps.deepwork.deep_work_app.util.helper.HapticFeedbackHelper(
                                        context
                                    )
                                hapticHelper.performButtonClick()
                            }
                            viewModel.toggleHapticFeedback()
                        },
                        icon = Icons.TwoTone.Vibration
                    )
                    SettingsBoxesItem(
                        text = "Manage Tags",
                        icon = Icons.Rounded.Sell,
                        dividerVisible = false,
                        onClickSettingsBoxesItem = {
                            navController?.navigate("ManageTags")
                        }
                    )
                }
            }


            Text(
                "FOCUS SETTINGS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp)
            )
            Card(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
            ) {
                Column {
                    SettingsBoxesItem(
                        dividerVisible = false,
                        text = "Select Apps to Block",
                        icon = Icons.Outlined.AppBlocking,
                        onClickSettingsBoxesItem = {
                            navController?.navigate("SelectBlockApps")
                        }
                    )
                }
            }
            SettingsBoxes(
                headerVisibility = false,
                headerName = "",
                isRestoring = isRestoring,
                viewModel = viewModel,
                context = context
            )

            Text(
                "MORE APPS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp)
            )
            Card(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
            ) {
                Column {
                    SettingsAppItem(dividerVisible = false)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            AppInfoSection(context = context)

        }

        // Snackbar Host
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = MaterialTheme.colorScheme.surfaceBright,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SettingsBoxes(
    headerVisibility: Boolean,
    headerName: String,
    viewModel: SettingsViewModel = hiltViewModel(),
    isRestoring: Boolean = false,
    context: Context = LocalContext.current
) {
    Column(
        modifier = Modifier
            .padding(vertical = 24.dp)
    ) {
        AnimatedVisibility(visible = headerVisibility) {
            Text(
                headerName,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .padding(bottom = 5.dp)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
        ) {
            val appLink = "https://play.google.com/store/apps/details?id=com.kami_apps.deepwork"

            Column {
                SettingsBoxesItem(
                    text = "Share App", icon = Icons.Default.Upload,
                    onClickSettingsBoxesItem = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Check out this app!")
                            putExtra(Intent.EXTRA_TEXT, appLink)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                    },
                )
                SettingsBoxesItem(
                    text = "Rate Us", icon = Icons.Rounded.StarBorder,
                    onClickSettingsBoxesItem = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(appLink)
                        )
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                    },
                )
                SettingsBoxesItem(
                    text = "Restore Purchases",
                    icon = Icons.Default.Refresh,
                    onClickSettingsBoxesItem = {
                        if (!isRestoring) {
                            viewModel.restorePurchases()
                        }
                    },
                    showProgress = isRestoring,
                    dividerVisible = false
                )
            }
        }
    }
}


@Composable
fun SettingsAppItem(
    dividerVisible: Boolean = true,
    icon: Painter = painterResource(id = R.drawable.ic_futurebaby), // örnek ikon
    title: String = "Future Baby Generate AI",
    subtitle: String = "What My Baby look Like",
    link: String = "https://play.google.com/store/apps/details?id=com.babyai.futurebaby"


) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    context.startActivity(intent)
                },
                indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                interactionSource = remember { MutableInteractionSource() }
            )) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))

            // Metinler
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )

        }
        AnimatedVisibility(visible = dividerVisible) {
            Divider(
                color = Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.padding(start = 56.dp)
            )
        }

    }
}


@Preview
@Composable
fun SettingsScreenPreview() {
}


@Preview
@Composable
fun SettingsScreen_2Preview() {
    AppInfoSection()
}


@Composable
fun PremiumStatusCardButton(
    onClick: () -> Unit,
    isPremium: Boolean = false,
    baseColor: Color = MaterialTheme.colorScheme.primary, // Varsayılan mavi ton
    imageVector: ImageVector = if (isPremium) Icons.Filled.Favorite else Icons.Filled.Bolt,
) {

    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(60.dp)
            .fillMaxWidth()
            .shadow(8.dp, shape = RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(colors = gradientColors),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(
                onClick = onClick,
                indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = if (isPremium) "Premium User" else "Upgrade",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPremium) "Thank You" else "Upgrade",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun PremiumStatusCard(
    onClick: () -> Unit,
    isPremium: Boolean = false,
    baseColor: Color = MaterialTheme.colorScheme.tertiary, // Varsayılan mavi ton
) {

    val gradientColorsBlue = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isPremium) "You are" else "Upgrade to",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(colors = gradientColorsBlue),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "PRO",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            Text(
                text = if (isPremium) {
                    "Thanks for supporting us! You have access to all premium features including unlimited tags, detailed statistics, and timeline view."
                } else {
                    "Switch to Pro plan to get access to unlimited tags, sessions stats, timeline view an more"
                },
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(bottom = 16.dp)
            )
            PremiumStatusCardButton(
                onClick = onClick,
                isPremium = isPremium
            )

        }
    }
}


@Composable
fun SettingsSwitchItem(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: ImageVector
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onCheckedChange(!isChecked) },
                    indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )


            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF0A84FF),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.Gray
                ),
            )
        }

        Divider(
            color = Color.Gray.copy(alpha = 0.2f),
            modifier = Modifier.padding(start = 56.dp)
        )
    }
}


@Composable
fun AppInfoSection(
    context: Context = LocalContext.current,
    privacyPolicyUrl: String = "https://www.kamiapps.com/page/privacy-policy",
    termsUrl: String = "https://www.kamiapps.com/page/terms-conditions"
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val appVersion = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "Unknown"
        } catch (e: PackageManager.NameNotFoundException) {
            "Unknown"
        }

        val iconBitmap = remember(R.mipmap.ic_launcher) {
            try {
                ContextCompat.getDrawable(context, R.mipmap.ic_launcher)?.toBitmap()?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        iconBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Current App Icon",
                modifier = Modifier
                    .size(70.dp)
                    .padding(top = 16.dp)
            )
        }

        Text(
            text = appVersion,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Privacy policy",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.padding(end = 8.dp).clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                    context.startActivity(intent)
                }
            )


            Text(
                text = "Terms of service",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.clickable {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsUrl))
                    context.startActivity(intent)
                }
            )
        }
    }
}

@Composable
fun SettingsBoxesItem(
    text: String,
    icon: ImageVector,
    dividerVisible: Boolean = true,
    showProgress: Boolean = false,
    onClickSettingsBoxesItem: () -> Unit = { }
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = onClickSettingsBoxesItem,
                    indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            if (showProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
                    strokeWidth = 2.dp
                )
            }
        }
        if (dividerVisible) {
            Divider(
                color = Color.Gray.copy(alpha = 0.2f),
                modifier = Modifier.padding(start = 56.dp)
            )
        }
    }
}

@Composable
private fun ThemeDropdownItem(
    currentTheme: String,
    availableThemes: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onThemeSelected: (String) -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { onExpandedChange(true) },
                    indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Contrast,
                contentDescription = "App Theme",
                tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "App Theme",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )

            // Current theme + dropdown icon
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentTheme,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = "Select Theme",
                    tint = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                availableThemes.forEachIndexed { index, theme ->
                    val isSelected = theme == currentTheme
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = theme,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal
                                )
                                if (isSelected) {
                                    Text(
                                        text = "✓",
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        onClick = {
                            onThemeSelected(theme)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    if (index < availableThemes.size - 1) {
                        HorizontalDivider(
                            color = Color.Gray.copy(alpha = 0.2f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
        // Divider after the item
        Divider(
            color = Color.Gray.copy(alpha = 0.2f),
            modifier = Modifier.padding(start = 56.dp)
        )
    }
}

