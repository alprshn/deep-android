package com.kamiapps.deep.deep.presentation.timeline_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

    Dialog(onDismissRequest = {
        onTimeSelected(pickedTime)
        onDismissRequest()
    }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(8.dp))
                            .size(28.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                    )
                    WheelTimePicker(
                        startTime = initialTime,
                        onSnappedTime = { snappedTime ->
                            pickedTime = snappedTime
                        },
                        size = DpSize(164.dp, 164.dp),
                        textStyle = TextStyle(
                            fontSize = 20.sp
                        ),
                        textColor = MaterialTheme.colorScheme.onPrimary,
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            color = MaterialTheme.colorScheme.onPrimary,
                            shape = RoundedCornerShape(10.dp),
                            border = null,
                            enabled = false,

                        ),
                        rowCount = 5,
                        modifier = Modifier
                            .padding(vertical = 28.dp)
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

