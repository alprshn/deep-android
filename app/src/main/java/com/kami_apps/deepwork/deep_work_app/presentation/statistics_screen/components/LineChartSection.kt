package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components

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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
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
import kotlinx.coroutines.runBlocking
import java.text.DecimalFormatSymbols
import java.util.Locale

private val RangeProvider = CartesianLayerRangeProvider.fixed(maxY = 8000.0)
private val YDecimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.US))
private val StartAxisValueFormatter = CartesianValueFormatter.decimal(YDecimalFormat)
private val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

// Saat formatı için formatter
private val HourValueFormatter = CartesianValueFormatter { _, x, _ ->
    val hour = x.toInt()
    String.format("%02d:00", hour)
}

@Composable
private fun JetpackComposeElectricCarSales(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    // Güvenli şekilde peak değerini bul
    val maxPeakIndex = if (y.isNotEmpty()) {
        y.indexOf(y.maxOf { it.toDouble() })
    } else {
        0
    }

    // Index geçerli değilse varsayılan değerleri kullan
    val safeMaxPeakIndex =
        maxPeakIndex.takeIf { it >= 0 && it < x.size } ?: 12 // Varsayılan olarak 12:00
    val maxPeakXValue = x.getOrNull(safeMaxPeakIndex) ?: 12 // Güvenli erişim

    val lineColor = Color.White

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
                        // Ana çizgi - nokta yok
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
                        // Maksimum nokta için - sadece point, çizgi yok
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(Color.Transparent)),
                            pointProvider = LineCartesianLayer.PointProvider.single(
                                point = LineCartesianLayer.point(
                                    component = rememberShapeComponent(
                                        shape = CorneredShape.Pill,
                                        fill = fill(Color.White), // Dikey eksen ana çizgisi rengi
                                        ),
                                    size = 12.dp,
                                )
                            )
                        )
                    ),
                rangeProvider = RangeProvider,
            ),
            marker = customMarker,
            endAxis = VerticalAxis.rememberEnd(
                valueFormatter = StartAxisValueFormatter,
                line = rememberLineComponent(
                    fill = fill(Color.Gray.copy(alpha = 0.5f)), // Dikey eksen ana çizgisi rengi
                    thickness = 0.4.dp,
                ),
                label = rememberTextComponent(
                    color = Color.Gray, // Rengi değiştirdik
                    textSize = 12.sp,
                    margins = Insets(allDp = 4f) // YENİ: Y ekseni etiketlerine sağdan 8dp boşluk

                ),
                guideline = rememberLineComponent(
                    fill = fill(Color.Gray.copy(alpha = 0.5f)), // Dikey kılavuz çizgisi rengi
                    thickness = 0.4.dp,
                ),
            ),
            bottomAxis = HorizontalAxis.rememberBottom(
                line = rememberLineComponent(
                    fill = fill(Color.Gray.copy(alpha = 0.5f)), // Yatay eksen ana çizgisi rengi
                    thickness = 0.2.dp,
                ),
                valueFormatter = HourValueFormatter, // Saat formatı ekledik
                label = rememberTextComponent(
                    color = Color.Gray, // Rengi değiştirdik
                    textSize = 12.sp,
                    margins = Insets(allDp = 4f) // YENİ: Y ekseni etiketlerine sağdan 8dp boşluk
                ),
                guideline = rememberAxisGuidelineComponent(
                    fill = fill(Color.Gray.copy(alpha = 0.5f)), // Yatay kılavuz çizgisi rengi
                    thickness = 0.2.dp,
                )
            ),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(scrollEnabled = false),
    )
}

private val x = (0..23).toList() // 24 saat (00:00 - 23:00)
private val y = listOf<Number>(
    200,
    150,
    100,
    80,
    120,
    300,
    850,
    1200,
    2500,
    3800,
    5200,
    6500,
    7200,
    6800,
    5900,
    4200,
    3100,
    2800,
    2200,
    1800,
    1200,
    800,
    500,
    300
) // Gün içindeki saatlik aktivite süresi (dakika cinsinden)

@Composable
fun JetpackComposeElectricCarSales(modifier: Modifier = Modifier) {
    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            // Ana çizgi serisi ve maksimum nokta serisi
            lineSeries {
                series(x, y) // Ana çizgi
                
                // Maksimum nokta için ayrı series - sadece max noktada tek değer
                val maxValue = y.maxOf { it.toDouble() }
                val maxIndex = y.indexOfFirst { it.toDouble() == maxValue }
                if (maxIndex != -1) {
                    val maxXValue = x[maxIndex]
                    val maxYValue = y[maxIndex]
                    series(listOf(maxXValue), listOf(maxYValue))
                }
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
        Column( // YENİ: Başlık ve grafik için bir dikey düzen oluşturduk
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // YENİ: Grafiğin başlığı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Most Focused Period of The Day",
                    color = Color.White, // Başlık rengi beyaz
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = "Peak Time: ",
                    color = Color.White, // Başlık rengi beyaz
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "12:00",
                    color = Color.White, // Başlık rengi beyaz
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // ESKİ ROW ARTIK BURADA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Grafiği Row içine yerleştirirken `weight(1f)` ekledik
                JetpackComposeElectricCarSales(modelProducer, Modifier.weight(1f))
            }
        }

    }
}

@Composable
@Preview
private fun Preview() {
    val modelProducer = remember { CartesianChartModelProducer() }
    // Use `runBlocking` only for previews, which don't support asynchronous execution.
    runBlocking {
        modelProducer.runTransaction {
            // Learn more: https://patrykandpatrick.com/vmml6t.
            lineSeries { series(x, y) }
        }
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF101012)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),

        ) {
        Column( // YENİ: Başlık ve grafik için bir dikey düzen oluşturduk
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // YENİ: Grafiğin başlığı
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Most Focused Period of The Day",
                    color = Color.White, // Başlık rengi beyaz
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    modifier = Modifier.padding(end = 8.dp),
                    contentDescription = null,
                    tint = Color.White,
                )
                Text(
                    text = "Peak Time: ",
                    color = Color.White, // Başlık rengi beyaz
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Start,
                )
                Text(
                    text = "12:00",
                    color = Color.White, // Başlık rengi beyaz
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                )
            }

            // ESKİ ROW ARTIK BURADA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Grafiği Row içine yerleştirirken `weight(1f)` ekledik
                PreviewBox { JetpackComposeElectricCarSales(modelProducer) }
            }
        }

    }


}