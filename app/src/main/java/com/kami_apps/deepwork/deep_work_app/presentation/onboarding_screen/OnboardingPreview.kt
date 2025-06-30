package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material.icons.outlined.ScreenLockPortrait
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingPageContent
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingButtonSection

// Preview parametresi için provider
class OnboardingPageProvider : PreviewParameterProvider<OnboardingPage> {
    override val values = sequenceOf(
        // İlk sayfa - Floating icons ile
        OnboardingPage(
            title = "Ultra Focus with Deep",
            description = "Plan your day, block distractions\nand build better focus",
            showFloatingIcons = true,
            floatingIcons = listOf(
                Icons.Default.HourglassEmpty,
                Icons.Default.Schedule,
                Icons.Outlined.PersonPin,
                Icons.Default.BarChart,
                Icons.Outlined.ScreenLockPortrait,
                Icons.Rounded.CalendarMonth
            )
        ),
        // İkinci sayfa
        OnboardingPage(
            title = "Focus Sessions",
            description = "Create customizable focus sessions\nwith timers and breaks",
            showFloatingIcons = false
        ),
        // Üçüncü sayfa
        OnboardingPage(
            title = "Block Distractions",
            description = "Block apps and websites\nthat distract you during focus time",
            showFloatingIcons = false
        ),
        // Dördüncü sayfa
        OnboardingPage(
            title = "Screen Time",
            description = "This lets us block distracting apps.\nYour data is private and never\nleaves your phone.",
            showFloatingIcons = false,
            backgroundColor = Color.Black
        ),
        // Beşinci sayfa
        OnboardingPage(
            title = "Stay on top of your schedule",
            description = "We'll keep notifications minimal. Deep is\nmade to help you focus, not\ndistract you.",
            showFloatingIcons = false,
            backgroundColor = Color.Black
        )
    )
}

// İlk sayfa özel preview (floating icons ile)
@Preview(
    name = "First Page - Floating Icons",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun FirstPagePreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Ultra Focus with Deep",
                description = "Plan your day, block distractions\nand build better focus",
                showFloatingIcons = true,
                floatingIcons = listOf(
                    Icons.Default.HourglassEmpty,
                    Icons.Default.Schedule,
                    Icons.Outlined.PersonPin,
                    Icons.Default.BarChart,
                    Icons.Outlined.ScreenLockPortrait,
                    Icons.Rounded.CalendarMonth
                )
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    name = "Second Page - Focus Sessions",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun SecondPagePreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Focus Sessions",
                description = "Create customizable focus sessions\nwith timers and breaks",
                showFloatingIcons = false
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// İkinci sayfa animasyonu için özel preview
@Preview(
    name = "Second Page - Life Animation",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun SecondPageAnimationPreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Focus Sessions", // Bu trigger olarak kullanılıyor
                description = "Create customizable focus sessions\nwith timers and breaks",
                showFloatingIcons = false
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// İlk hayat görünümü için static preview
@Preview(
    name = "Life Dots - Static View",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun LifeDotsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "70-Year Human Life",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Each dot represents 1 year of life",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 70 yıl için statik noktalar
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0 until 7) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        for (col in 0 until 10) {
                            val yearIndex = row * 10 + col
                            val isFilled = yearIndex < 22

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .background(
                                        color = if (isFilled) Color.White else Color.Black,
                                        shape = CircleShape
                                    )
                                    .border(
                                        shape = CircleShape,
                                        width = 1.dp,
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

// Social media görünümü için static preview
@Preview(
    name = "Social Media - Static View",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun SocialMediaPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Ana social media ikonları
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Instagram
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF833AB4),
                                    Color(0xFFE1306C),
                                    Color(0xFFFCAF45)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // TikTok
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.Black,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tiktok),
                        contentDescription = "TikTok",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // YouTube
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color(0xFFFF0000),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_youtube),
                        contentDescription = "YouTube",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Social Media Takes\n30% of Your Life",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "That's 21 years of your life",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Alt social media ikonları
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFF1877F2),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFF9146FF),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_twitch),
                        contentDescription = "Twitch",
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = Color(0xFFFF4500),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_reddit),
                        contentDescription = "Reddit",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Third Page - Block Distractions",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ThirdPagePreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Block Distractions",
                description = "Block apps and websites\nthat distract you during focus time",
                showFloatingIcons = false,
                backgroundColor = Color.White
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Üçüncü sayfa emoji animasyonu için özel preview
@Preview(
    name = "Third Page - Emoji Animation",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun ThirdPageEmojiAnimationPreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Block Distractions", // Bu trigger olarak kullanılıyor
                description = "Block apps and websites\nthat distract you during focus time",
                showFloatingIcons = false,
                backgroundColor = Color.White
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    name = "Fourth Page - Screen Time",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun FourthPagePreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Screen Time",
                description = "This lets us block distracting apps.\nYour data is private and never\nleaves your phone.",
                showFloatingIcons = false,
                backgroundColor = Color.Black
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Dördüncü sayfa screen time animasyonu için özel preview
@Preview(
    name = "Fourth Page - App Icons Animation",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun FourthPageAppIconsAnimationPreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Screen Time", // Bu trigger olarak kullanılıyor
                description = "This lets us block distracting apps.\nYour data is private and never\nleaves your phone.",
                showFloatingIcons = false,
                backgroundColor = Color.Black
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Page indicator kaldırıldı

// Buton section preview'ları
@Preview(name = "Button Section - First Page")
@Composable
fun ButtonSectionFirstPagePreview() {
    MaterialTheme {
        OnboardingButtonSection(
            currentPage = 0,
            totalPages = 5,
            onNextClick = { },
            onPreviousClick = { },
            onSkipClick = { }
        )
    }
}

@Preview(name = "Button Section - Middle Page")
@Composable
fun ButtonSectionMiddlePagePreview() {
    MaterialTheme {
        OnboardingButtonSection(
            currentPage = 2,
            totalPages = 5,
            onNextClick = { },
            onPreviousClick = { },
            onSkipClick = { }
        )
    }
}

@Preview(name = "Button Section - Last Page")
@Composable
fun ButtonSectionLastPagePreview() {
    MaterialTheme {
        OnboardingButtonSection(
            currentPage = 4,
            totalPages = 5,
            onNextClick = { },
            onPreviousClick = { },
            onSkipClick = { }
        )
    }
}

// Farklı cihaz boyutları için preview'lar
@Preview(
    name = "Phone Portrait",
    device = "spec:width=360dp,height=640dp,dpi=480",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun OnboardingScreenPhonePreview() {

    Box(
        modifier = Modifier.size(300.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.mck_phone),
            contentDescription = null,
            modifier = Modifier.size(300.dp)
        )

        // Gradient overlay on top of image
        Box(
            modifier = Modifier
                .size(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 1f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )
    }
}

@Preview(
    name = "Tablet Landscape",
    device = "spec:width=1280dp,height=800dp,dpi=240",
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun OnboardingScreenTabletPreview() {
    MaterialTheme {
        FirstPagePreviewContent()
    }
}

@Composable
private fun FirstPagePreviewContent() {
    OnboardingPageContent(
        page = OnboardingPage(
            title = "Ultra Focus with Deep",
            description = "Plan your day, block distractions\nand build better focus",
            showFloatingIcons = true,
            floatingIcons = listOf(
                Icons.Default.HourglassEmpty,
                Icons.Default.Schedule,
                Icons.Outlined.PersonPin,
                Icons.Default.BarChart,
                Icons.Outlined.ScreenLockPortrait,
                Icons.Rounded.CalendarMonth
            )
        ),
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(
    name = "Fifth Page - Notification Permission",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun FifthPagePreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Stay on top of your schedule",
                description = "We'll keep notifications minimal. Deep is\nmade to help you focus, not\ndistract you.",
                showFloatingIcons = false,
                backgroundColor = Color.Black
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Beşinci sayfa notification permission animasyonu için özel preview
@Preview(
    name = "Fifth Page - Notification Animation",
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun FifthPageNotificationAnimationPreview() {
    MaterialTheme {
        OnboardingPageContent(
            page = OnboardingPage(
                title = "Stay on top of your schedule", // Bu trigger olarak kullanılıyor
                description = "We'll keep notifications minimal. Deep is\nmade to help you focus, not\ndistract you.",
                showFloatingIcons = false,
                backgroundColor = Color.Black
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
} 