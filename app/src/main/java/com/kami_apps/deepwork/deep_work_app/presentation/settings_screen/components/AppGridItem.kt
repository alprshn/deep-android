package com.kami_apps.deepwork.deep_work_app.presentation.settings_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.compose.material.Checkbox
import androidx.wear.compose.material.Text
import com.kami_apps.deepwork.deep_work_app.domain.data.InstalledApp

@Composable
fun AppGridItem(
    app: InstalledApp,
    onCheckedChange: (Boolean) -> Unit
) {
    Column (
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1D1A1F))
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Row (verticalAlignment = Alignment.CenterVertically) {
            Image(
                bitmap = app.icon.toBitmap().asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = app.appName,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Checkbox(
                checked = app.isSelected,
                onCheckedChange = onCheckedChange
            )
        }
    }
}