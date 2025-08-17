package com.kami_apps.deepwork.deep.presentation.statistics_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberEnd
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.core.common.shape.Shape
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import com.kami_apps.deepwork.deep.domain.usecases.HourlyFocusData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

private val YDecimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

// Hour formatter for X-axis
private val HourValueFormatter = CartesianValueFormatter { _, x, _ ->
    val hour = x.toInt()
    String.format("%02d:00", hour)
}

@Composable
private fun WeeklyPeakChartContent(
    modelProducer: CartesianChartModelProducer,
    peakHour: String,
    modifier: Modifier = Modifier,
    lineColor: Color = MaterialTheme.colorScheme.onPrimary // varsayılan beyaz
) {

    val customMarker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(
            color = Color.White,
            textSize = 12.sp,
            background = rememberLineComponent(
                fill = fill(Color(0xFF6200EE)),
                shape = Shape.Rectangle,
                thickness = 0.dp
            ),
        ),
        valueFormatter = MarkerValueFormatter
    )

    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        // Main line - no points
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(lineColor.copy(alpha = 0.6f), Color.Transparent)
                                        )
                                    )
                                ),
                            pointConnector = LineCartesianLayer.PointConnector.Sharp,
                        ),
                        // Peak point - only point, no line
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(Color.Transparent)),
                            pointProvider = LineCartesianLayer.PointProvider.single(
                                point = LineCartesianLayer.point(
                                    component = rememberShapeComponent(
                                        shape = CorneredShape.Pill,
                                        fill = fill(lineColor),
                                    ),
                                    size = 12.dp,
                                )
                            )
                        )
                    ),
            ),
            marker = customMarker,
            endAxis = VerticalAxis.rememberEnd(
                valueFormatter = CartesianValueFormatter { _, value, _ ->
                    "${value.toInt()} min"
                },
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
                line = rememberLineComponent(
                    fill = fill(Color.Gray.copy(alpha = 0.5f)),
                    thickness = 0.2.dp,
                ),
                valueFormatter = HourValueFormatter,
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
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

@Composable
fun WeeklyPeakChart(
    hourlyFocusData: List<HourlyFocusData>,
    peakHour: String,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(hourlyFocusData) {
        modelProducer.runTransaction {
            if (hourlyFocusData.isNotEmpty()) {
                lineSeries {
                    // Main line series
                    series(
                        x = hourlyFocusData.map { it.hour },
                        y = hourlyFocusData.map { it.totalMinutes }
                    )
                    
                    // Peak point series - find max value point
                    val maxData = hourlyFocusData.maxByOrNull { it.totalMinutes }
                    if (maxData != null && maxData.totalMinutes > 0) {
                        series(
                            x = listOf(maxData.hour),
                            y = listOf(maxData.totalMinutes)
                        )
                    }
                }
            } else {
                // Default empty data
                val emptyHours = (0..23).toList()
                val emptyData = List(24) { 0 }
                lineSeries { 
                    series(x = emptyHours, y = emptyData)
                }
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
                    text = "Most Focused Period of The Day",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )

                var exportTrigger by remember { mutableStateOf(false) }

                if (exportTrigger) {
                    val subtitle = buildAnnotatedString {
                        append("Most Focused on ")
                        withStyle(
                            style = SpanStyle(
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(peakHour)
                        }
                        append(" every day in general")
                    }


                    ExportAsBitmap(
                        title = "Most Focused Period of The Day",
                        subtitle = subtitle,
                        content = {
                            WeeklyPeakChartContent(
                                modelProducer = modelProducer,
                                 peakHour = peakHour,
                                modifier = Modifier,
                                lineColor = Color.Black // sadece export’ta siyah çizgi
                            )
                        },
                        onExported = { exportTrigger = false }
                    )
                }

                Icon(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(20.dp).clickable {
                            exportTrigger = true
                        },
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Subtitle with peak time
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = "Peak Time: ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = peakHour,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // Chart
            WeeklyPeakChartContent(modelProducer, peakHour, modifier)
        }
    }
}

@Composable
@Preview
private fun WeeklyPeakChartPreview() {
    val sampleData = listOf(
        HourlyFocusData(6, 30),
        HourlyFocusData(7, 45),
        HourlyFocusData(8, 60),
        HourlyFocusData(9, 90),
        HourlyFocusData(10, 120),
        HourlyFocusData(11, 75),
        HourlyFocusData(12, 45),
        HourlyFocusData(13, 30),
        HourlyFocusData(14, 80),
        HourlyFocusData(15, 100),
        HourlyFocusData(16, 95),
        HourlyFocusData(17, 60),
        HourlyFocusData(18, 40),
        HourlyFocusData(19, 20),
        HourlyFocusData(20, 15)
    )

    WeeklyPreviewBox {
        WeeklyPeakChart(
            hourlyFocusData = sampleData,
            peakHour = "10:00"
        )
    }
}

@Composable
private fun WeeklyPreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
        content = content
    )
} 