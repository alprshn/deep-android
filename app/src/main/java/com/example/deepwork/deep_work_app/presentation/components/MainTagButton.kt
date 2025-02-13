package com.example.deepwork.deep_work_app.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainTagButton(heightButton: Int, textColor: Color, emoji: String, text: String) {
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .height(heightButton.dp)
            .clip(RoundedCornerShape(heightButton.dp))
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                emoji, color = textColor, modifier = Modifier.padding(end = 5.dp),
                fontSize = 10.sp
                )

            Text(text, color = textColor)
        }
    }
}


@Preview
@Composable
fun MainTagButtonPreview() {
    MainTagButton(30, Color.White, "❌", "Delete")
}