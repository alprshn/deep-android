package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PersonPin
import androidx.compose.material.icons.outlined.ScreenLockPortrait
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.PersonPin
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingPageContent
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components.OnboardingButtonSection
import androidx.compose.material3.MaterialTheme

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
            title = "Track Progress",
            description = "Monitor your focus sessions\nand see your productivity grow",
            showFloatingIcons = false
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

@Preview(
    name = "Third Page - Block Distractions",
    showBackground = true,
    backgroundColor = 0xFF000000,
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
                showFloatingIcons = false
            ),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Preview(
    name = "Fourth Page - Track Progress",
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
                title = "Track Progress",
                description = "Monitor your focus sessions\nand see your productivity grow",
                showFloatingIcons = false
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
            totalPages = 4,
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
            currentPage = 1,
            totalPages = 4,
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
            currentPage = 3,
            totalPages = 4,
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
    MaterialTheme {
        FirstPagePreviewContent()
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