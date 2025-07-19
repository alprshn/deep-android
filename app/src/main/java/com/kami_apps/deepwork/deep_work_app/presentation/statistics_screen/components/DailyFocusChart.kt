package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding

import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import androidx.compose.foundation.layout.height
import com.patrykandpatrick.vico.compose.cartesian.cartesianLayerPadding

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.text.DecimalFormat
import com.kami_apps.deepwork.deep_work_app.domain.usecases.DailyFocusData

private const val Y_DIVISOR = 1

private val BottomAxisLabelKey = ExtraStore.Key<List<String>>()

private val YDecimalFormat = DecimalFormat("#")

private val StartAxisValueFormatter = CartesianValueFormatter { _, value, _ ->
    "${YDecimalFormat.format(value / Y_DIVISOR)} min"
}

private val BottomAxisValueFormatter = CartesianValueFormatter { context, x, _ ->
    context.model.extraStore[BottomAxisLabelKey][x.toInt()]
}

private val MarkerValueFormatter =
    DefaultCartesianMarker.ValueFormatter { _, targets ->
        val column = (targets[0] as ColumnCartesianLayerMarkerTarget).columns[0]
        SpannableStringBuilder()
            .append(
                "${YDecimalFormat.format(column.entry.y / Y_DIVISOR)} min",
                ForegroundColorSpan(column.color),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE,
            )
    }

@Composable
private fun DailyFocusChartContent(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = fill(Color.White),
                            thickness = 40.dp,
                            shape = CorneredShape.rounded(
                                topLeftPercent = 10,
                                topRightPercent = 10
                            ),
                        )
                    )
                ),
                endAxis = VerticalAxis.rememberEnd(
                    valueFormatter = StartAxisValueFormatter,
                    line = rememberLineComponent(
                        fill = fill(Color.Gray.copy(alpha = 0.5f)),
                        thickness = 0.4.dp,
                    ),
                    label = rememberTextComponent(
                        color = Color.Gray,
                        textSize = 12.sp,
                        margins = Insets(allDp = 4f)
                    ),
                    guideline = rememberLineComponent(
                        fill = fill(Color.Gray.copy(alpha = 0.5f)),
                        thickness = 0.4.dp,
                    ),
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    itemPlacer = remember { HorizontalAxis.ItemPlacer.segmented() },
                    valueFormatter = BottomAxisValueFormatter,
                    line = rememberLineComponent(
                        fill = fill(Color.Gray.copy(alpha = 0.5f)),
                        thickness = 0.2.dp,
                    ),
                    label = rememberTextComponent(
                        color = Color.Gray,
                        textSize = 12.sp,
                        margins = Insets(allDp = 4f)
                    ),
                    guideline = rememberAxisGuidelineComponent(
                        fill = fill(Color.Gray.copy(alpha = 0.5f)),
                        thickness = 0.2.dp,
                    )
                ),
                layerPadding = { cartesianLayerPadding(scalableStart = 8.dp, scalableEnd = 8.dp) },
            ),
        modelProducer = modelProducer,
        modifier = modifier.height(220.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
fun DailyFocusChart(
    dailyFocusData: List<DailyFocusData>,
    totalFocusTime: String,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(dailyFocusData) {
        modelProducer.runTransaction {
            if (dailyFocusData.isNotEmpty()) {
                columnSeries { 
                    series(dailyFocusData.map { it.totalMinutes }) 
                }
                extras { 
                    it[BottomAxisLabelKey] = dailyFocusData.map { data ->
                        data.dayName
                    }
                }
            } else {
                // Default empty data for a week
                val emptyData = List(7) { 0 }
                val emptyLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                columnSeries { series(emptyData) }
                extras { it[BottomAxisLabelKey] = emptyLabels }
            }
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101012)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Focused Time Distribution",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp),
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            // Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Focused Time: ",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = totalFocusTime,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // Chart
            DailyFocusChartContent(modelProducer, modifier)
        }
    }
}

@Composable
@Preview
private fun DailyFocusChartPreview() {
    val sampleData = listOf(
        DailyFocusData(1, "Mon", 120, java.time.LocalDate.now()),
        DailyFocusData(2, "Tue", 85, java.time.LocalDate.now().plusDays(1)),
        DailyFocusData(3, "Wed", 150, java.time.LocalDate.now().plusDays(2)),
        DailyFocusData(4, "Thu", 95, java.time.LocalDate.now().plusDays(3)),
        DailyFocusData(5, "Fri", 180, java.time.LocalDate.now().plusDays(4)),
        DailyFocusData(6, "Sat", 45, java.time.LocalDate.now().plusDays(5)),
        DailyFocusData(7, "Sun", 75, java.time.LocalDate.now().plusDays(6))
    )
    
    DailyPreviewBox { 
        DailyFocusChart(
            dailyFocusData = sampleData,
            totalFocusTime = "12h 30m"
        )
    }
}

@Composable
private fun DailyPreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp), 
        content = content
    )
} 