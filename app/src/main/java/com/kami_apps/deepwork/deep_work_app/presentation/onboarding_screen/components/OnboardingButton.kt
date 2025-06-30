package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.OnboardingPage

@Composable
fun OnboardingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    backgroundColor: Color = Color.White,
    textColor: Color = Color.Black,
    enabled: Boolean = true
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor,
                disabledContainerColor = backgroundColor.copy(alpha = 0.6f),
                disabledContentColor = textColor.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(35.dp),
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 2.dp,
                disabledElevation = 0.dp
            )
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun OnboardingButtonSection(
    currentPage: Int,
    totalPages: Int,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true, // İlk sayfa için animasyon kontrolü
    currentPageData: OnboardingPage? = null
) {
    // 2. sayfa (Focus Sessions), 4. sayfa (Screen Time) ve 5. sayfa (Stay on top of your schedule) için buton gösterme
    val shouldShowButtons = currentPage != 1 && 
                           currentPageData?.title != "Screen Time" && 
                           currentPageData?.title != "Stay on top of your schedule"
    
    // Beyaz arka plan kontrolü
    val isWhiteBackground = currentPageData?.backgroundColor == Color.White
    val skipTextColor = if (isWhiteBackground) Color.Black.copy(alpha = 0.7f) else Color.White.copy(alpha = 0.7f)
    
    AnimatedVisibility(
        visible = isVisible && shouldShowButtons,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = 300
            )
        ) + fadeIn(
            animationSpec = tween(
                durationMillis = 600,
                delayMillis = 300
            )
        )
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        // Skip button (sadece ilk sayfalarda göster)
        AnimatedVisibility(
            visible = currentPage < totalPages - 1,
            modifier = Modifier.weight(1f)
        ) {
            TextButton(
                onClick = onSkipClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Skip",
                    color = skipTextColor,
                    fontSize = 14.sp
                )
            }
        }
        
            // Ana buton
            OnboardingButton(
                text = when (currentPage) {
                    totalPages - 1 -> "Get Started"
                    else -> "Continue"
                },
                onClick = onNextClick,
                modifier = Modifier.weight(2f),
                backgroundColor = if (isWhiteBackground) Color.Black else Color.White,
                textColor = if (isWhiteBackground) Color.White else Color.Black
            )
        }
    }
} 