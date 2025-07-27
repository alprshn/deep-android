package com.kami_apps.deepwork.deep_work_app.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import java.time.LocalTime

@Composable
fun WheelTimePickerDialog(
    initialTime: LocalTime = LocalTime.now(),
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    var pickedTime by remember { mutableStateOf(initialTime) }

    AlertDialog(
        onDismissRequest = {
            onTimeSelected(pickedTime) // Kullanıcı dışarıya tıklayınca seçilen zamanı bildir
            onDismissRequest()
        },
        confirmButton = {}, // Buton yok
        containerColor = Color(0xFF2C2C2E),
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(6.dp),)
                        .size(28.dp)
                        .background(Color.Gray)

                )
                WheelTimePicker(
                    startTime = initialTime,
                    onSnappedTime = { snappedTime ->
                        pickedTime = snappedTime // sadece güncelle
                    },
                    textStyle = MaterialTheme.typography.titleMedium,
                    selectorProperties = WheelPickerDefaults.selectorProperties(
                        color = Color.Gray,
                        shape = RoundedCornerShape(10.dp),
                        border = null,
                        enabled = false
                    )
                )
            }
        }
    )
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

