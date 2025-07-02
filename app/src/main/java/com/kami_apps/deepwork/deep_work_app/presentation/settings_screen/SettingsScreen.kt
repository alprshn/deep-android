package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.outlined.AppBlocking
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Sell
import androidx.compose.material.icons.rounded.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.kami_apps.deepwork.deep_work_app.data.util.darken
import com.kami_apps.deepwork.deep_work_app.data.util.lighten
import androidx.navigation.NavHostController


@Composable
fun SettingsScreen(navController: NavHostController? = null) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            PremiumStatusCard({})
            Card(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
            ) {
                Column {
                    SettingsBoxesItem(text = "App Theme", icon = Icons.Default.Contrast)
                    SettingsBoxesItem(
                        text = "App Icon", 
                        icon = Icons.Default.Apps,
                        onClickSettingsBoxesItem = {
                            navController?.navigate("AppIcon")
                        }
                    )
                    SettingsSwitchItem("Notification", true, {}, Icons.Rounded.NotificationsNone)
                    SettingsSwitchItem("Haptic Feedback", false, {}, Icons.Default.Vibration)
                    SettingsBoxesItem(
                        text = "Manage Tags",
                        icon = Icons.Rounded.Sell,
                        dividerVisible = false
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
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
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
            SettingsBoxes(headerVisibility = false, headerName = "")

            SettingsBoxes(true,"MORE APPS")
        }
    }
}

@Composable
fun SettingsBoxes(headerVisibility: Boolean, headerName: String) {
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
                modifier = Modifier.padding(start = 15.dp).padding(bottom = 5.dp)
            )
        }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1E))
        ) {
            Column {
                SettingsBoxesItem(text = "Share App", icon = Icons.Default.Upload)
                SettingsBoxesItem(text = "Rate Us", icon = Icons.Rounded.StarBorder)
                SettingsBoxesItem(
                    dividerVisible = false,
                    text = "Restore Purchases",
                    icon = Icons.Outlined.Replay
                )
            }
        }
    }
}

@Composable
fun SettingsBoxesItem(
    dividerVisible: Boolean = true,
    icon: ImageVector = Icons.Default.Bolt,
    text: String = "Deneme",
    onClickSettingsBoxesItem: () -> Unit = {}
) {

    Column(modifier = Modifier.fillMaxWidth().clickable(
        onClick = onClickSettingsBoxesItem,
        indication = ripple(color = Color.White.copy(alpha = 0.1f)),
        interactionSource = remember { MutableInteractionSource() }
    )) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 15.dp),
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal
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
    SettingsScreen()
}


@Composable
fun PremiumStatusCardButton(
    onClick: () -> Unit,
    baseColor: Color = Color(0xFF0A84FF), // Varsayılan mavi ton
    imageVector: ImageVector = Icons.Filled.Bolt,
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
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Upgrade", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun PremiumStatusCard(
    onClick: () -> Unit,
    baseColor: Color = Color(0xFF0A84FF), // Varsayılan mavi ton
) {

    val gradientColors = listOf(
        baseColor.copy(alpha = 1f).lighten(0.3f),  // %30 daha açık ton
        baseColor.copy(alpha = 1f),                // Orijinal renk
        baseColor.copy(alpha = 1f).darken(0.3f)   // %30 daha koyu ton
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                indication = ripple(color = Color.White.copy(alpha = 0.1f)),
                interactionSource = remember { MutableInteractionSource() }
            ),
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
                Text(text = "Upgrade to", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.linearGradient(colors = gradientColors),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        "PRO",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "Switch to Pro plan to get access to unlimited tags, sessions stats, timeline view an more",
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .padding(bottom = 16.dp)
            )
            PremiumStatusCardButton(onClick)

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
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = Color.White,
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