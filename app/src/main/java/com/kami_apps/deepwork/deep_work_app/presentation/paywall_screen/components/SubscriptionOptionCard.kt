package com.kami_apps.deepwork.deep_work_app.presentation.paywall_screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@Composable
fun SubscriptionOptionCard(
    title: String,
    price: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    discount: String? = null,
    discountedPrice: String? = null,
    onInfoClick: () -> Unit = {},
    carColor: Color = Color(0xFF0A84FF)
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false), // Diyaloğun varsayılan genişliğini devralmamak için
            modifier = Modifier
                .fillMaxWidth(0.9f) // Diyaloğun ekran genişliğinin %90'ını kaplaması için (isteğe bağlı)
                .border( // Buraya border modifikatörünü ekliyoruz
                    BorderStroke(1.dp, Color.Gray.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(28.dp) // AlertDialog'un köşesiyle aynı olmalı
                ),
            shape = RoundedCornerShape(28.dp), // AlertDialog'un köşe yuvarlaklığı
            containerColor = Color(0xFF101012),
            textContentColor = Color.White,
            titleContentColor = Color.White, // Başlık rengini de belirttim

            title = {
                Text(
                    text = if (title == "3-Day Trial") "Weekly Subscription" else "Yearly Subscription",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = if (title == "3-Day Trial")
                        "This subscription includes a 3-day free trial period for your first purchase. After the trial ends, you will be charged weekly until you cancel."
                    else
                        "Yearly subscription with 88% discount on your first purchase. Renews automatically every year at the regular price unless canceled.",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TextButton(onClick = { showInfoDialog = false }) {
                        Text("OK", color = Color.White)
                    }
                }
            },
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                carColor.copy(alpha = 0.2f)
            } else {
                Color(0xFF565656).copy(alpha = 0.4f)
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = Brush.linearGradient(
                    colors = listOf(
                        carColor,
                        carColor.copy(alpha = 0.5f)
                    )
                )
            )
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable {
                                showInfoDialog = true
                                onInfoClick()
                            }
                    )
                }

                if (title == "Yearly Plan" && discountedPrice != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = price,
                            color = Color.Gray,
                            fontSize = 14.sp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                        Text(
                            text = discountedPrice,
                            color = Color.White,
                            fontSize = 14.sp,
                        )
                        Text(
                            text = subtext,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Text(
                        text = subtext,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (discount != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(carColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = discount,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if (title == "3-Day Trial") {
                    Text(
                        text = price,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                RadioButton(
                    selected = isSelected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = carColor,
                        unselectedColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF17152A)
@Composable
private fun SubscriptionOptionCardPreview() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        SubscriptionOptionCard(
            title = "3-Day Trial",
            price = "$9.99",
            subtext = "then $9.99 per week",
            isSelected = true,
            onClick = {}
        )

        SubscriptionOptionCard(
            title = "Yearly Plan",
            price = "$99.99",
            subtext = "per year",
            discountedPrice = "$11.99",
            discount = "SAVE 88%",
            isSelected = false,
            onClick = {}
        )
    }
}


@Preview(showBackground = true, backgroundColor = 0xFF17152A)
@Composable
private fun SubscriptionOptionCardAlertPreview() {
    val dialogBackgroundColor = Color(0xFF101012)
    val dialogStrokeColor = Color.Gray.copy(alpha = 0.2f) // Çizgi (stroke) rengi
    val dialogStrokeWidth = 1.dp            // Çizgi (stroke) kalınlığı

    AlertDialog(
        onDismissRequest = {  }, // onDismissRequest'i dışarıdan kontrol edilebilir yaptık
        properties = DialogProperties(usePlatformDefaultWidth = false), // Diyaloğun varsayılan genişliğini devralmamak için
        modifier = Modifier
            .fillMaxWidth(0.9f) // Diyaloğun ekran genişliğinin %90'ını kaplaması için (isteğe bağlı)
            .border( // Buraya border modifikatörünü ekliyoruz
                BorderStroke(dialogStrokeWidth, dialogStrokeColor),
                shape = RoundedCornerShape(28.dp) // AlertDialog'un köşesiyle aynı olmalı
            ),
        shape = RoundedCornerShape(28.dp), // AlertDialog'un köşe yuvarlaklığı
        containerColor = dialogBackgroundColor,
        textContentColor = Color.White,
        titleContentColor = Color.White, // Başlık rengini de belirttim
        text = {
            Text(
                text = "Yearly subscription with 88% discount on your first purchase. Renews automatically every year at the regular price unless canceled.",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        title = {
            Text(
                text = "Yearly Subscription",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TextButton(onClick = {  }) { // onDismiss'i çağırdık
                    Text("OK", color = Color.White)
                }
            }
        }
    )

}