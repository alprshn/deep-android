package com.kami_apps.deepwork.deep_work_app.presentation.statistics_screen.components


import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import com.kami_apps.deepwork.deep_work_app.domain.usecases.WeekdayFocusData
import com.kami_apps.deepwork.deep_work_app.util.captureToBitmap
import com.kami_apps.deepwork.deep_work_app.util.saveBitmapToGallery
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun WeekdayAnalysisChartExportable(
    weekdayFocusData: List<WeekdayFocusData>,
    peakWeekday: String,
    onExported: () -> Unit
) {
    val context = LocalContext.current
    val modelProducer = remember { CartesianChartModelProducer() }

    // modeli yükle (hazır hale getir)
    LaunchedEffect(weekdayFocusData) {
        modelProducer.runTransaction {
            if (weekdayFocusData.isNotEmpty()) {
                lineSeries {
                    // Ana çizgi
                    series(
                        x = weekdayFocusData.map { it.dayOfWeek },
                        y = weekdayFocusData.map { it.totalMinutes }
                    )

                    // En yüksek noktayı göster
                    val maxData = weekdayFocusData.maxByOrNull { it.totalMinutes }
                    if (maxData != null && maxData.totalMinutes > 0) {
                        series(
                            x = listOf(maxData.dayOfWeek),
                            y = listOf(maxData.totalMinutes)
                        )
                    }
                }
            }
        }
    }

    // AndroidView ile render al
    AndroidView(
        factory = { ctx ->
            ComposeView(ctx).apply {
                setContent {
                    WeekdayAnalysisChartContent(
                        modelProducer = modelProducer,
                        peakWeekday = peakWeekday
                    )
                }

                // GÖRÜNTÜNÜ BURADA AL
                postDelayed({
                    val bitmap = this.captureToBitmap()
                    saveBitmapToGallery(context, bitmap)
                    Toast.makeText(context, "Grafik başarıyla kaydedildi.", Toast.LENGTH_SHORT).show()
                    onExported()
                }, 500) // çizilmesini bekliyoruz
            }
        }
    )
}

