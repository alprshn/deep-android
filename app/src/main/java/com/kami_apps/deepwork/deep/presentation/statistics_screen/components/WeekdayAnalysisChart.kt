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
import androidx.compose.material.icons.rounded.DateRange
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
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
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
import com.kami_apps.deepwork.deep.domain.usecases.WeekdayFocusData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.patrykandpatrick.vico.compose.cartesian.layer.point
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine

private val YDecimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

// Weekday formatter for X-axis
private val WeekdayValueFormatter = CartesianValueFormatter { _, x, _ ->
    val weekdays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val index = (x.toInt() - 1).coerceIn(0, 6)
    weekdays[index]
}

@Composable
fun WeekdayAnalysisChartContent(
    modelProducer: CartesianChartModelProducer,
    peakWeekday: String,
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
                valueFormatter = WeekdayValueFormatter,
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
fun WeekdayAnalysisChart(
    weekdayFocusData: List<WeekdayFocusData>,
    peakWeekday: String,
    modifier: Modifier = Modifier
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    
    LaunchedEffect(weekdayFocusData) {
        modelProducer.runTransaction {
            if (weekdayFocusData.isNotEmpty()) {
                lineSeries {
                    // Main line series
                    series(
                        x = weekdayFocusData.map { it.dayOfWeek },
                        y = weekdayFocusData.map { it.totalMinutes }
                    )
                    
                    // Peak point series - find max value point
                    val maxData = weekdayFocusData.maxByOrNull { it.totalMinutes }
                    if (maxData != null && maxData.totalMinutes > 0) {
                        series(
                            x = listOf(maxData.dayOfWeek),
                            y = listOf(maxData.totalMinutes)
                        )
                    }
                }
            } else {
                // Default empty data
                val emptyDays = (1..7).toList()
                val emptyData = List(7) { 0 }
                lineSeries { 
                    series(x = emptyDays, y = emptyData)
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
                    text = "Most Focused Day of The Week",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                val context = LocalContext.current
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
                            append(peakWeekday)
                        }
                        append(" every week in general")
                    }


                    ExportAsBitmap(
                        title = "Most Focused Day of The Week",
                        subtitle = subtitle,
                        content = {
                            WeekdayAnalysisChartContent(
                                modelProducer = modelProducer,
                                peakWeekday = peakWeekday,
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
                        .size(20.dp)
                        .clickable {
                            exportTrigger = true
                        },
                    imageVector = Icons.Default.IosShare,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }

            // Subtitle with peak weekday
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.DateRange,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
                Text(
                    text = "Peak Day: ",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = peakWeekday,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // Chart
            WeekdayAnalysisChartContent(modelProducer, peakWeekday, modifier)
        }
    }
}

@Composable
@Preview
private fun WeekdayAnalysisChartPreview() {
    val sampleData = listOf(
        WeekdayFocusData(1, "Mon", 120),
        WeekdayFocusData(2, "Tue", 95),
        WeekdayFocusData(3, "Wed", 150),
        WeekdayFocusData(4, "Thu", 85),
        WeekdayFocusData(5, "Fri", 110),
        WeekdayFocusData(6, "Sat", 45),
        WeekdayFocusData(7, "Sun", 75)
    )

    WeekdayPreviewBox {
        WeekdayAnalysisChart(
            weekdayFocusData = sampleData,
            peakWeekday = "Wednesday"
        )
    }
}

@Composable
private fun WeekdayPreviewBox(content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = Modifier
            .background(Color.Black)
            .padding(16.dp),
        content = content
    )
} 