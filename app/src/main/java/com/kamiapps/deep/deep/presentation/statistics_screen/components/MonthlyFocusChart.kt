package com.kamiapps.deep.deep.presentation.statistics_screen.components

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
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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
import com.kamiapps.deep.deep.domain.usecases.MonthlyFocusData

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
private fun MonthlyFocusChartContent(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = fill(lineColor),
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
        scrollState = rememberVicoScrollState(scrollEnabled = true),
    )
}

@Composable
fun MonthlyFocusChart(
    monthlyFocusData: List<MonthlyFocusData>,
    totalFocusTime: String,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val exportTrigger = remember { mutableStateOf(false) }

    LaunchedEffect(monthlyFocusData) {
        modelProducer.runTransaction {
            if (monthlyFocusData.isNotEmpty()) {
                columnSeries { 
                    series(monthlyFocusData.map { it.totalMinutes }) 
                }
                extras { 
                    it[BottomAxisLabelKey] = monthlyFocusData.map { data ->
                        data.dayOfMonth.toString()
                    }
                }
            } else {
                // Default empty data for a month (31 days max)
                val emptyData = List(31) { 0 }
                val emptyLabels = List(31) { day -> (day + 1).toString() }
                columnSeries { series(emptyData) }
                extras { it[BottomAxisLabelKey] = emptyLabels }
            }
        }
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)),
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
                    text = "Monthly Focus Distribution",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )

                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp)
                        .clickable {
                            exportTrigger.value = true
                        },
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Subtitle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Focused Time: ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = totalFocusTime,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // Chart
            MonthlyFocusChartContent(modelProducer, modifier)
        }
    }

    if (exportTrigger.value) {
        ExportAsBitmap(
            title = "Monthly Focus Analysis",
            subtitle = AnnotatedString("Total Focus Time: $totalFocusTime"),
            content = {
                MonthlyFocusChartContent(
                    modelProducer = modelProducer,
                    modifier = Modifier,
                    lineColor = Color.Black
                )
            },
            onExported = { exportTrigger.value = false }
        )
    }
}

@Composable
@Preview
private fun MonthlyFocusChartPreview() {
    val sampleData = listOf(
        MonthlyFocusData(1, 60, java.time.LocalDate.now()),
        MonthlyFocusData(2, 45, java.time.LocalDate.now().plusDays(1)),
        MonthlyFocusData(3, 90, java.time.LocalDate.now().plusDays(2)),
        MonthlyFocusData(4, 30, java.time.LocalDate.now().plusDays(3)),
        MonthlyFocusData(5, 120, java.time.LocalDate.now().plusDays(4))
    )
    
    MonthlyPreviewBox { 
        MonthlyFocusChart(
            monthlyFocusData = sampleData,
            totalFocusTime = "25h 30m"
        )
    }
}

@Composable
private fun MonthlyPreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp), 
        content = content
    )
} 