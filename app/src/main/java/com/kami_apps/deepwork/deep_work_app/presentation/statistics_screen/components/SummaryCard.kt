package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kami_apps.deepwork.R

data class FocusStatistics(
    val totalFocusTime: String = "0",
    val totalSessions: Int = 0,
    val averageDuration: String = "0"
)

@Composable
fun SummaryCardsSection(
    statistics: FocusStatistics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Large Total Focus Card
        TotalFocusCard(
            focusTime = statistics.totalFocusTime,
            modifier = Modifier.fillMaxWidth()
        )

        // Two smaller cards in a row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MetricCard(
                icon = "#",
                title = "Total Sessions",
                value = statistics.totalSessions.toString(),
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                icon = "⏱",
                title = "Average Duration",
                value = statistics.averageDuration,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TotalFocusCard(
    focusTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .animateContentSize(),
            contentAlignment = Alignment.Center,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Decorative elements
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        // painterResource, otomatik olarak BitmapDrawable veya VectorDrawable'ı tanır.
                        painter = painterResource(id = R.drawable.wreath_left), // Örnek olarak 'ic_wreath_left.xml' oluşturulduğunu varsayalım
                        contentDescription = null, // Varsa daha açıklayıcı bir metin ekleyin
                        modifier = Modifier.size(60.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary) // 🔥 burası rengi belirliyor
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Focus",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = focusTime,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                    Image(
                        // painterResource, otomatik olarak BitmapDrawable veya VectorDrawable'ı tanır.
                        painter = painterResource(id = R.drawable.wreath_right), // Örnek olarak 'ic_wreath_left.xml' oluşturulduğunu varsayalım
                        contentDescription = null, // Varsa daha açıklayıcı bir metin ekleyin
                        modifier = Modifier.size(60.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary) // 🔥 burası rengi belirliyor
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: String,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = icon,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )


            Column(
                modifier = Modifier.weight(1f).padding(start = 10.dp),
            ) {
                // Title
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Left,
                )

                // Value
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Left
                )
            }

        }
    }
}


@Composable
@Preview
fun SummaryCardsSectionPreview() {
    val sampleStatistics = FocusStatistics(
        totalFocusTime = "178h 20m",
        totalSessions = 66,
        averageDuration = "2h 42m"
    )

    SummaryCardsSection(
        statistics = sampleStatistics,
        modifier = Modifier.padding(bottom = 16.dp)
    )


}