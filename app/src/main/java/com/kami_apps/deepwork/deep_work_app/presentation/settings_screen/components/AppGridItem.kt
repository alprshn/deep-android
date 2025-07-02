package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp

@Composable
fun AppGridItem(
    app: InstalledApp,
    isBlocked: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onClick() }
            .alpha(if (enabled) 1f else 0.6f),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isBlocked) 
                Color(0xFF1C1C1E).copy(alpha = 0.8f) 
            else 
                Color(0xFF1C1C1E)
        ),
        border = if (isBlocked) 
            androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4757)) 
        else 
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Image(
                    bitmap = app.icon.toBitmap().asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                
                // Block indicator
                if (isBlocked) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                Color(0xFFFF4757),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Blocked",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = app.appName,
                color = if (isBlocked) Color(0xFFFF4757) else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isBlocked) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            if (isBlocked) {
                Text(
                    text = "BLOCKED",
                    color = Color(0xFFFF4757),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}