package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import com.kami_apps.deepwork.deep_work_app.util.helper.HapticFeedbackHelper
import java.time.LocalTime

@Composable
fun WheelTimePickerDialog(
    initialTime: LocalTime = LocalTime.now(),
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var pickedTime by remember { mutableStateOf(initialTime) }

    Dialog(onDismissRequest = {
        onTimeSelected(pickedTime)
        onDismissRequest()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.4f)) // Arka plan
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2E)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(6.dp))
                            .size(28.dp)
                            .background(Color.Gray)
                    )
                    WheelTimePicker(
                        startTime = initialTime,
                        onSnappedTime = { snappedTime ->
                            pickedTime = snappedTime
                        },
                        textStyle = MaterialTheme.typography.titleMedium,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            color = Color.Gray,
                            shape = RoundedCornerShape(10.dp),
                            border = null,
                            enabled = false
                        ),
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }

            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun WheelTimePickerDialogPreview() {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        WheelTimePickerDialog(
            initialTime = LocalTime.of(14, 30),
            onDismissRequest = { showDialog = false },
            onTimeSelected = { selectedTime ->
                println("Seçilen Saat: $selectedTime") // test çıktısı
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WheelTimePickerPreview() {
    WheelTimePicker(
        startTime = LocalTime.of(10, 0),
        onSnappedTime = { println("Snapped: $it") }
    )
}

