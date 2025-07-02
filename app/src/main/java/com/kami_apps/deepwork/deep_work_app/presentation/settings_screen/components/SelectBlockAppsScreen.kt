package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.SettingsViewModel
import com.kami_apps.deepwork.deep_work_app.util.PermissionHelper

@Composable
fun SelectBlockAppsScreen(
    navController: NavHostController? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    
    // Search state
    var searchText by remember { mutableStateOf("") }
    
    // Check permissions
    val hasAllPermissions = remember(context) {
        PermissionHelper.hasAllPermissions(context)
    }
    
    val missingPermissions = remember(context) {
        PermissionHelper.getMissingPermissions(context)
    }
    
    // Filter apps based on search text
    val filteredApps = remember(state.installedApps, searchText) {
        if (searchText.isBlank()) {
            state.installedApps
        } else {
            state.installedApps.filter { app ->
                app.appName.contains(searchText, ignoreCase = true) ||
                app.packageName.contains(searchText, ignoreCase = true)
            }
        }
    }

    // Infinite scroll logic
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = filteredApps.size
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            
            lastVisibleIndex >= totalItems - 5 && // Load when 5 items from end
            state.hasNextPage && 
            !state.isLoadingMore && 
            !state.isLoading &&
            searchText.isBlank() // Only load more when not searching
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps()
        viewModel.loadBlockedApps()
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasAllPermissions) {
            viewModel.loadNextPage()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController?.popBackStack() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                "Select Apps to Block", 
                fontSize = 20.sp, 
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Clear all button
            if (state.blockedApps.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearAllBlockedApps() }
                ) {
                    Text(
                        "Clear All",
                        color = Color(0xFF0A84FF),
                        fontSize = 14.sp
                    )
                }
            }
        }
        
        // Search bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            placeholder = {
                Text(
                    "Search apps...",
                    color = Color.Gray
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(
                        onClick = { searchText = "" }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.Gray
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Gray,
                focusedBorderColor = Color(0xFF0A84FF),
                unfocusedTextColor = Color.White,
                focusedTextColor = Color.White,
                cursorColor = Color(0xFF0A84FF)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        // Blocked apps count
        if (state.blockedApps.isNotEmpty()) {
            Text(
                "${state.blockedApps.size} apps blocked",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Permission check
        if (!hasAllPermissions) {
            PermissionRequiredCard(
                missingPermissions = missingPermissions,
                onRequestPermission = { permissionType ->
                    when (permissionType) {
                        PermissionHelper.PermissionType.USAGE_STATS -> 
                            PermissionHelper.requestUsageStatsPermission(context)
                        PermissionHelper.PermissionType.OVERLAY ->
                            PermissionHelper.requestOverlayPermission(context)
                        PermissionHelper.PermissionType.BATTERY_OPTIMIZATION ->
                            PermissionHelper.requestBatteryOptimizationDisable(context)
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        when {
            state.isLoading && state.installedApps.isEmpty() -> {
                // Initial loading
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            "Loading apps...",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
            state.error != null && state.installedApps.isEmpty() -> {
                // Error state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Error loading apps: ${state.error}",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
            else -> {
                // Apps list with pagination
                LazyColumn(
                    state = listState,
                    modifier = Modifier.padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredApps) { app ->
                        AppListItem(
                            app = app,
                            isBlocked = state.blockedApps.contains(app.packageName),
                            enabled = hasAllPermissions
                        ) {
                            if (hasAllPermissions) {
                                viewModel.toggleAppSelection(app.packageName)
                            }
                        }
                    }
                    
                    // Loading more indicator
                    if (state.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Loading more apps...",
                                    color = Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionRequiredCard(
    missingPermissions: List<PermissionHelper.PermissionType>,
    onRequestPermission: (PermissionHelper.PermissionType) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFFF9500),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    "Permissions Required",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            
            Text(
                "App blocking requires the following permissions:",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            
            missingPermissions.forEach { permission ->
                PermissionItem(
                    permission = permission,
                    onRequest = { onRequestPermission(permission) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PermissionItem(
    permission: PermissionHelper.PermissionType,
    onRequest: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                permission.displayName,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                permission.description,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
        
        Button(
            onClick = onRequest,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0A84FF)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "Grant",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun AppListItem(
    app: com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp,
    isBlocked: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable(enabled = enabled) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBlocked) Color(0xFF2C1D1D) else Color(0xFF1C1C1E)
        ),
        border = if (isBlocked) {
            BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                val iconBitmap = remember(app.icon) {
                    try {
                        app.icon.toBitmap(48, 48).asImageBitmap()
                    } catch (e: Exception) {
                        null
                    }
                }
                
                if (iconBitmap != null) {
                    Image(
                        painter = BitmapPainter(iconBitmap),
                        contentDescription = app.appName,
                        modifier = Modifier.size(40.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Apps,
                        contentDescription = app.appName,
                        tint = if (isBlocked) Color.Red else Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            
            // App Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = app.appName,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = if (isBlocked) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Status Icon
            if (isBlocked) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Not Selected",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
