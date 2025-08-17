package com.kami_apps.deepwork.deep.presentation.onboarding_screen.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.twotone.HourglassBottom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.kami_apps.deepwork.deep.presentation.onboarding_screen.OnboardingPage
import kotlin.math.sin
import kotlin.math.cos
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.platform.LocalContext
import com.kami_apps.deepwork.deep.util.PermissionHelper
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.BatteryAlert
import com.kami_apps.deepwork.R

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
    onShowButtons: () -> Unit = {},
    onNextClick: () -> Unit = {},
    onConnectScreenTime: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    onMaybeLater: () -> Unit = {},
    onRequestOverlayPermission: () -> Unit = {},
    onRequestBatteryOptimization: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(page.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        when {
            page.showFloatingIcons -> {
                // İlk sayfa için sıralı animasyonlar
                FirstPageAnimatedContent(
                    page = page,
                    onShowButtons = onShowButtons
                )
            }

            page.title == "Focus Sessions" -> {
                // İkinci sayfa için özel animasyon
                SecondPageAnimatedContent(
                    page = page,
                    onShowButtons = onShowButtons,
                    onNextClick = onNextClick
                )
            }

            page.title == "Block Distractions" -> {
                // Üçüncü sayfa için dönen emoji animasyonu
                ThirdPageAnimatedContent(
                    page = page,
                    onShowButtons = onShowButtons
                )
            }

            page.title == "Screen Time" -> {
                // Dördüncü sayfa için screen time izni
                FourthPageAnimatedContent(
                    page = page,
                    onShowButtons = onShowButtons,
                    onConnectScreenTime = onConnectScreenTime,
                    onRequestOverlayPermission = onRequestOverlayPermission,
                    onRequestBatteryOptimization = onRequestBatteryOptimization
                )
            }

            page.title == "Stay on top of your schedule" -> {
                // Beşinci sayfa için notification izni
                FifthPageAnimatedContent(
                    page = page,
                    onShowButtons = onShowButtons,
                    onRequestNotificationPermission = onRequestNotificationPermission,
                    onMaybeLater = onMaybeLater
                )
            }

            else -> {
                // Diğer sayfalar için basit içerik
                OtherPageContent(page = page)
            }
        }
    }
}

@Composable
private fun FirstPageAnimatedContent(
    page: OnboardingPage,
    onShowButtons: () -> Unit = {}
) {
    // Animasyon durumları
    var logoVisible by remember { mutableStateOf(false) }
    var iconsVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    // Alpha animasyonları - layout shifting önlemek için
    val titleAlpha by animateFloatAsState(
        targetValue = if (titleVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        label = "title_alpha"
    )

    val descriptionAlpha by animateFloatAsState(
        targetValue = if (descriptionVisible) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = LinearEasing
        ),
        label = "description_alpha"
    )

    // Logo animasyonu
    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logo_scale"
    )

    // Sıralı animasyonları başlat
    LaunchedEffect(Unit) {
        delay(500) // İlk gecikme
        logoVisible = true
        delay(1000) // Logo göründükten sonra
        iconsVisible = true
        delay(1200) // İkonlar çıktıktan sonra
        titleVisible = true
        delay(600) // Başlık göründükten sonra
        descriptionVisible = true
        delay(400) // Açıklama göründükten sonra
        buttonsVisible = true
        onShowButtons() // ViewModel'e butonları göster sinyali gönder
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo ve floating icons aynı merkezde
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(400.dp) // İkonlar için yeterli alan
            ) {
                // Floating icons (logo'dan sonra başlar)
                if (iconsVisible) {
                    AnimatedFloatingIcons(
                        icons = page.floatingIcons,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Ana logo (tam merkeze)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(190.dp)
                        .scale(logoScale)
                ) {
                    // Glow efekti için birden fazla katman
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(200.dp + (index * 20).dp)
                                .blur(20.dp + (index * 10).dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f - (index * 0.1f)),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }

                    // Ana logo
                    Image(
                        painter = painterResource(id = R.drawable.deep_inline_icon),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(110.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))


            DynamicShimmeringText(
                modifier = Modifier.alpha(titleAlpha),
                text = page.title, baseColor = Color.Gray, textStyle = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp,
                )
            )




            Spacer(modifier = Modifier.height(22.dp))


            // Açıklama (animasyonlu)
            Text(
                text = page.description,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.alpha(descriptionAlpha)
            )
        }
    }
}

@Composable
private fun SecondPageAnimatedContent(
    page: OnboardingPage,
    onShowButtons: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    // Animasyon durumları
    var filledDotsCount by remember { mutableStateOf(0) }
    var showBlur by remember { mutableStateOf(false) }
    var showSocialMedia by remember { mutableStateOf(false) }
    var socialMediaTitleVisible by remember { mutableStateOf(false) }
    var socialMediaDescVisible by remember { mutableStateOf(false) }
    var isClickable by remember { mutableStateOf(false) }

    // Blur animasyonu
    val blurAmount by animateFloatAsState(
        targetValue = if (showBlur) 10f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "blur_amount"
    )

    // Social media içerik alpha
    val socialMediaAlpha by animateFloatAsState(
        targetValue = if (showSocialMedia) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = FastOutSlowInEasing
        ),
        label = "social_media_alpha"
    )

    // Animasyon sırası
    LaunchedEffect(Unit) {
        // Noktaları tek tek doldur (22 tane)
        for (i in 1..22) {
            filledDotsCount = i
            delay(250) // Her nokta için 300ms bekleme
        }

        delay(1000) // Tüm noktalar dolduktan sonra bekle
        showBlur = true
        delay(300) // Blur animasyonu için bekle
        showSocialMedia = true
        socialMediaTitleVisible = true
        socialMediaDescVisible = true
        delay(500)
        isClickable = true // Tıklanabilir hale getir
        onShowButtons()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .let { modifier ->
                if (isClickable) {
                    modifier.clickable { onNextClick() }
                } else {
                    modifier
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // İlk içerik (70 yıllık hayat)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurAmount.dp)
        ) {
            FirstLifeContent(
                filledDotsCount = filledDotsCount
            )
        }

        // Social media içeriği
        Box(
            modifier = Modifier
                .fillMaxSize()
                .alpha(socialMediaAlpha)
        ) {
            SocialMediaContent(
                titleVisible = socialMediaTitleVisible,
                descriptionVisible = socialMediaDescVisible
            )
        }

        // Tıklanabilir olduğunda gösterge
        if (isClickable) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            ) {
                DynamicShimmeringText(
                    modifier = Modifier.alpha(socialMediaAlpha),
                    text = "Tap anywhere to continue",
                    baseColor = Color.Gray,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                    )
                )

            }
        }
    }
}

@Composable
private fun FirstLifeContent(
    filledDotsCount: Int = 0
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

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

        LifeDotsGrid(filledDotsCount = filledDotsCount)

    }
}

@Composable
private fun LifeDotsGrid(filledDotsCount: Int = 0) {
    val totalYears = 70
    val dotsPerRow = 10
    val totalRows = totalYears / dotsPerRow

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until totalRows) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until dotsPerRow) {
                    val yearIndex = row * dotsPerRow + col
                    val isFilled = yearIndex < filledDotsCount

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

@Composable
private fun SocialMediaContent(
    titleVisible: Boolean,
    descriptionVisible: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Ana social media ikonları (yukarı kayma animasyonu)
        AnimatedVisibility(
            visible = titleVisible,
            enter = slideInVertically(
                initialOffsetY = { -it }, // Yukarıdan gelsin
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 2000,
                    delayMillis = 0
                )
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_instagram),
                    contentDescription = "Instagram",
                    modifier = Modifier.size(40.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_tiktok),
                    contentDescription = "TikTok",
                    modifier = Modifier.size(40.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_youtube),
                    contentDescription = "YouTube",
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Başlık (animasyonsuz)
        Text(
            text = "Social Media Takes\n30% of Your Life",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            modifier = Modifier.alpha(if (titleVisible) 1f else 0f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Açıklama (animasyonsuz)
        Text(
            text = "That's 21 years of your life",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(if (descriptionVisible) 1f else 0f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Alt ikincil social media ikonları (aşağı kayma animasyonu)
        AnimatedVisibility(
            visible = descriptionVisible,
            enter = slideInVertically(
                initialOffsetY = { it }, // Aşağıdan gelsin
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 2000,
                    delayMillis = 0 // Alt ikonlar için 300ms gecikme
                )
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(40.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_twitch),
                    contentDescription = "Twitch",
                    modifier = Modifier.size(40.dp)
                )

                Image(
                    painter = painterResource(id = R.drawable.ic_reddit),
                    contentDescription = "Reddit",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@Composable
private fun OtherPageContent(page: OnboardingPage) {
    val isWhiteBackground = page.backgroundColor == Color.White
    val textColor = if (isWhiteBackground) Color.Black else Color.White
    val descriptionColor =
        if (isWhiteBackground) Color.Black.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.8f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Diğer sayfalardaki icon
        page.iconResource?.let { iconRes ->
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(60.dp))
        }

        // Başlık
        Text(
            text = page.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Açıklama
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = descriptionColor,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Composable
private fun AnimatedFloatingIcons(
    icons: List<ImageVector>,
    modifier: Modifier = Modifier
) {
    if (icons.isEmpty()) return

    // İconların dışarıya çıkma animasyonu için state
    var iconsExpanded by remember { mutableStateOf(false) }

    // Dönme için ayrı state
    var shouldRotate by remember { mutableStateOf(false) }

    // Yarıçap animasyonu - içeriden dışarıya çıkma efekti (daha yavaş ve smooth)
    val radiusProgress by animateFloatAsState(
        targetValue = if (iconsExpanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500, // Daha uzun süre
            easing = FastOutSlowInEasing // Smooth easing
        ),
        label = "radius_expansion"
    )

    // İkon ölçek animasyonu
    val iconScale by animateFloatAsState(
        targetValue = if (iconsExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "icon_scale"
    )

    // Sürekli dönme animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_animation")
    val rotationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 15000, // Daha yavaş dönme
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "continuous_rotation"
    )

    // Dönme başladığında smooth geçiş için rotationOffset
    val rotationOffset by animateFloatAsState(
        targetValue = if (shouldRotate) rotationValue else 0f,
        animationSpec = tween(
            durationMillis = 500, // Yumuşak geçiş
            easing = LinearEasing
        ),
        label = "rotation_offset"
    )

    // İconların çıkmasını başlat
    LaunchedEffect(Unit) {
        delay(200) // Kısa gecikme
        iconsExpanded = true
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        icons.forEachIndexed { index, iconVector ->
            // Her icon için sabit açı pozisyonu
            val baseAngle = (index * (360f / icons.size))

            // Çıkma animasyonu tamamlandıktan sonra dönme başlar
            if (radiusProgress >= 1f && !shouldRotate) {
                shouldRotate = true
            }

            // İconların pozisyonu: sabit açı + yumuşak dönme offset'i
            val currentAngle = (baseAngle + rotationOffset) * (Math.PI / 180)

            // Yarıçap - içeriden dışarıya çıkma
            val maxRadius = 150.dp
            val currentRadius = maxRadius * radiusProgress

            val offsetX = (cos(currentAngle) * currentRadius.value).dp
            val offsetY = (sin(currentAngle) * currentRadius.value).dp

            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .offset(offsetX, offsetY)
                    .alpha(radiusProgress)
            )
        }
    }
}

// Shimmer Effect için utility fonksiyonlar
fun Color.brighter(): Color {
    val blendRatio = 0.8f // Adjust to control brightness
    return Color(
        red = (this.red * (1 - blendRatio) + 1f * blendRatio).coerceIn(0f, 1f),
        green = (this.green * (1 - blendRatio) + 1f * blendRatio).coerceIn(0f, 1f),
        blue = (this.blue * (1 - blendRatio) + 1f * blendRatio).coerceIn(0f, 1f),
        alpha = this.alpha
    )
}


@Composable
fun DynamicShimmeringText(
    text: String,
    baseColor: Color = Color.Gray,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current
) {
    // Basit alpha tabanlı shimmer effect
    val infiniteTransition = rememberInfiniteTransition(label = "AlphaShimmer")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ShimmerAlpha"
    )
    val shimmerColor = baseColor.brighter()
    ShimmeringText(
        text = text,
        shimmerColor = shimmerColor,
        modifier = modifier.alpha(alpha),
        textStyle = textStyle
    )
}


@Composable
fun ShimmeringText(
    text: String,
    shimmerColor: Color,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = LocalTextStyle.current,
    animationSpec: DurationBasedAnimationSpec<Float> = tween(1000, 500, LinearEasing)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShimmeringTextTransition")

    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animationSpec),
        label = "ShimmerProgress"
    )

    val brush = remember(shimmerProgress) {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                val initialXOffset = -size.width
                val totalSweepDistance = size.width * 2
                val currentPosition = initialXOffset + totalSweepDistance * shimmerProgress

                return LinearGradientShader(
                    colors = listOf(Color.Gray, shimmerColor, Color.Gray),
                    from = Offset(currentPosition, 0f),
                    to = Offset(currentPosition + size.width, 0f)
                )
            }
        }
    }

    Text(
        text = text,
        modifier = modifier,
        style = textStyle.copy(brush = brush)
    )
}

@Composable
private fun ThirdPageAnimatedContent(
    page: OnboardingPage,
    onShowButtons: () -> Unit = {}
) {
    // Animasyon durumları
    var textVisible by remember { mutableStateOf(false) }
    var emojisVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    // Text animasyonu
    val textScale by animateFloatAsState(
        targetValue = if (textVisible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "text_scale"
    )

    // Emoji'ler listesi
    val emojis = listOf(
        "🚲", "📚", "🎻", "👑", "🏄",
        "🎨", "⛵", "🗺️", "🏃", "❤️"
    )

    // Sıralı animasyonları başlat
    LaunchedEffect(Unit) {
        delay(500) // İlk gecikme
        textVisible = true
        delay(800) // Text göründükten sonra
        emojisVisible = true
        delay(1500) // Emoji'ler çıktıktan sonra
        buttonsVisible = true
        onShowButtons() // ViewModel'e butonları göster sinyali gönder
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Merkezi text ve floating emojis aynı merkezde
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(400.dp) // Emoji'ler için yeterli alan
            ) {
                // Floating emojis (text'ten sonra başlar)
                if (emojisVisible) {
                    AnimatedFloatingEmojis(
                        emojis = emojis,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Merkezi text
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(250.dp)
                        .scale(textScale)
                ) {
                    Text(
                        text = "Don't waste\nyour time\nlive life",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        lineHeight = 38.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimatedFloatingEmojis(
    emojis: List<String>,
    modifier: Modifier = Modifier
) {
    if (emojis.isEmpty()) return

    // İkonların dışarıya çıkma animasyonu için state
    var emojisExpanded by remember { mutableStateOf(false) }

    // Dönme için ayrı state
    var shouldRotate by remember { mutableStateOf(false) }

    // Yarıçap animasyonu - içeriden dışarıya çıkma efekti (daha yavaş ve smooth)
    val radiusProgress by animateFloatAsState(
        targetValue = if (emojisExpanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = 1500, // Daha uzun süre
            easing = FastOutSlowInEasing // Smooth easing
        ),
        label = "radius_expansion"
    )

    // İkon ölçek animasyonu
    val iconScale by animateFloatAsState(
        targetValue = if (emojisExpanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "icon_scale"
    )

    // Sürekli dönme animasyonu
    val infiniteTransition = rememberInfiniteTransition(label = "rotation_animation")
    val rotationValue by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 15000, // Daha yavaş dönme
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "continuous_rotation"
    )

    // Dönme başladığında smooth geçiş için rotationOffset
    val rotationOffset by animateFloatAsState(
        targetValue = if (shouldRotate) rotationValue else 0f,
        animationSpec = tween(
            durationMillis = 500, // Yumuşak geçiş
            easing = LinearEasing
        ),
        label = "rotation_offset"
    )

    // İkonların çıkmasını başlat
    LaunchedEffect(Unit) {
        delay(200) // Kısa gecikme
        emojisExpanded = true
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        emojis.forEachIndexed { index, emoji ->
            // Her emoji için sabit açı pozisyonu
            val baseAngle = (index * (360f / emojis.size))

            // Çıkma animasyonu tamamlandıktan sonra dönme başlar
            if (radiusProgress >= 1f && !shouldRotate) {
                shouldRotate = true
            }

            // Emojilerin pozisyonu: sabit açı + yumuşak dönme offset'i
            val currentAngle = (baseAngle + rotationOffset) * (Math.PI / 180)

            // Yarıçap - içeriden dışarıya çıkma
            val maxRadius = 150.dp
            val currentRadius = maxRadius * radiusProgress

            val offsetX = (cos(currentAngle) * currentRadius.value).dp
            val offsetY = (sin(currentAngle) * currentRadius.value).dp

            Text(
                text = emoji,
                fontSize = 32.sp,
                modifier = Modifier
                    .offset(offsetX, offsetY)
                    .alpha(radiusProgress)
            )
        }
    }
}

@Composable
private fun FourthPageAnimatedContent(
    page: OnboardingPage,
    onShowButtons: () -> Unit = {},
    onConnectScreenTime: () -> Unit = {},
    onRequestOverlayPermission: () -> Unit = {},
    onRequestBatteryOptimization: () -> Unit = {}
) {
    // Animasyon durumları
    var appIconsVisible by remember { mutableStateOf(false) }
    var hourglassVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    // App ikonları (placeholder - gerçek ikonlar sonra eklenecek)
    val appIcons = listOf(
        "ic_instagram", "ic_facebook", "ic_tiktok", "ic_youtube",
        "ic_twitter", "ic_reddit", "ic_twitch", "ic_amazon",
        "ic_disney", "ic_linkedin", "ic_pinterest", "ic_netflix"
    )

    // Sıralı animasyonları başlat
    LaunchedEffect(Unit) {
        delay(300)
        appIconsVisible = true
        delay(800)
        hourglassVisible = true
        delay(500)
        titleVisible = true
        delay(400)
        descriptionVisible = true
        delay(600)
        buttonVisible = true
        onShowButtons()
    }

    // YENİ: izin durumları + hata vurgusu
    val context = LocalContext.current
    var isOverlayPermissionGranted by remember { mutableStateOf(PermissionHelper.hasOverlayPermission(context)) }
    var isBatteryOptimizationDisabled by remember { mutableStateOf(PermissionHelper.isBatteryOptimizationDisabled(context)) }
    var highlightErrors by remember { mutableStateOf(false) }

    // İzinleri periyodik kontrol et (kullanıcı ayarlardan dönünce güncellensin)
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            isOverlayPermissionGranted = PermissionHelper.hasOverlayPermission(context)
            isBatteryOptimizationDisabled = PermissionHelper.isBatteryOptimizationDisabled(context)
            if (isOverlayPermissionGranted && isBatteryOptimizationDisabled) {
                highlightErrors = false // hepsi tamamlanınca kırmızıyı kaldır
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App ikonları grid
        AnimatedVisibility(
            visible = appIconsVisible,
            enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            AppIconsGrid(appIcons = appIcons)
        }

        Spacer(modifier = Modifier.height(60.dp))

        // Hourglass ikonu
        AnimatedVisibility(
            visible = hourglassVisible,
            enter = fadeIn(animationSpec = tween(600)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Icon(
                imageVector = Icons.TwoTone.HourglassBottom,
                contentDescription = null,
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(80.dp)
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Screen Time başlığı
        AnimatedVisibility(
            visible = titleVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600)
            )
        ) {
            Text(
                text = "Screen Time",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Açıklama
        AnimatedVisibility(
            visible = descriptionVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600)
            )
        ) {
            Text(
                text = page.description,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Permissions Checklist
        AnimatedVisibility(
            visible = descriptionVisible, // Same visibility as description
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(800)
            )
        ) {
            PermissionsChecklistSection(
                isOverlayPermissionGranted = isOverlayPermissionGranted,
                isBatteryOptimizationDisabled = isBatteryOptimizationDisabled,
                highlightErrors = highlightErrors,
                onRequestOverlayPermission = onRequestOverlayPermission,
                onRequestBatteryOptimization = onRequestBatteryOptimization
            )

        }

        Spacer(modifier = Modifier.weight(1f))

        // Connect Screen Time butonu
        AnimatedVisibility(
            visible = buttonVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Button(
                onClick = {
                    val allGranted = isOverlayPermissionGranted && isBatteryOptimizationDisabled
                    if (allGranted) {
                        onConnectScreenTime()   // usage access launcher'ı burada çağrılacak
                    } else {
                        highlightErrors = true  // eksik izin(ler)i kırmızı göster
                    }                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(35.dp)
            ) {
                Text(
                    text = "Connect screen time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = Icons.Default.ArrowForwardIos,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AppIconsGrid(appIcons: List<String>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 3 satır, her satırda 4 ikon
        for (row in 0 until 3) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (col in 0 until 4) {
                    val iconIndex = row * 4 + col
                    if (iconIndex < appIcons.size) {
                        AppIconItem(iconName = appIcons[iconIndex])
                    }
                }
            }
        }
    }
}

@Composable
private fun AppIconItem(iconName: String) {
    // Icon map'i - mevcut ikonları buraya ekleyeceğiz
    val iconMap = mapOf(
        "ic_instagram" to R.drawable.ic_instagram,
        "ic_facebook" to R.drawable.ic_facebook,
        "ic_tiktok" to R.drawable.ic_tiktok,
        "ic_youtube" to R.drawable.ic_youtube,
        "ic_reddit" to R.drawable.ic_reddit,
        "ic_twitch" to R.drawable.ic_twitch,
        "ic_twitter" to R.drawable.ic_twitter,
        "ic_amazon" to R.drawable.ic_amazon,
        "ic_disney" to R.drawable.ic_disney,
        "ic_linkedin" to R.drawable.ic_linkedin,
        "ic_pinterest" to R.drawable.ic_pinterest,
        "ic_netflix" to R.drawable.ic_netflix,
        // Diğer ikonlar kullanıcı tarafından eklenecek
    )


    Box(
        modifier = Modifier
            .size(64.dp)
            .background(
                color = Color(0xFF2D2D2D),
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        val iconResource = iconMap[iconName]
        if (iconResource != null) {
            Image(
                painter = painterResource(id = iconResource),
                contentDescription = iconName,
                modifier = Modifier.size(38.dp)
            )
        } else {
            // Icon bulunamazsa placeholder göster
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color.Gray, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iconName.take(2).uppercase(),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FifthPageAnimatedContent(
    page: OnboardingPage,
    onShowButtons: () -> Unit = {},
    onRequestNotificationPermission: () -> Unit = {},
    onMaybeLater: () -> Unit = {}
) {
    // Animasyon durumları
    var phoneImageVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var descriptionVisible by remember { mutableStateOf(false) }
    var buttonsVisible by remember { mutableStateOf(false) }

    // Sıralı animasyonları başlat
    LaunchedEffect(Unit) {
        delay(300)
        phoneImageVisible = true
        delay(800)
        titleVisible = true
        delay(400)
        descriptionVisible = true
        delay(400)
        buttonsVisible = true
        onShowButtons()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp)
            .padding(top = 60.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phone mockup - Kullanıcı tarafından verilecek image
        AnimatedVisibility(
            visible = phoneImageVisible,
            enter = fadeIn(animationSpec = tween(800)) + scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Phone image with gradient overlay
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
                
                // Subtle siyah gradient - sadece alt kısım
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.2f)
                                ),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))

        // Title
        AnimatedVisibility(
            visible = titleVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600)
            )
        ) {
            Text(
                text = page.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,

                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        AnimatedVisibility(
            visible = descriptionVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it / 3 },
                animationSpec = tween(600)
            )
        ) {
            Text(
                text = page.description,
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Allow Notifications Button
        AnimatedVisibility(
            visible = buttonsVisible,
            enter = fadeIn(animationSpec = tween(600)) + slideInVertically(
                initialOffsetY = { it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        onRequestNotificationPermission()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(35.dp)
                ) {
                    Text(
                        text = "Allow Notifications",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForwardIos,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .size(16.dp)
                    )
                }

                // Maybe later button
                Text(
                    text = "Maybe later",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable {
                        onMaybeLater()
                    }
                )
            }
        }
    }
}



@Composable
private fun PermissionsChecklistSection(
    isOverlayPermissionGranted: Boolean,
    isBatteryOptimizationDisabled: Boolean,
    highlightErrors: Boolean,
    onRequestOverlayPermission: () -> Unit,
    onRequestBatteryOptimization: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PermissionChecklistItem(
            title = "Display Over Other Apps",
            icon = Icons.Default.Security,
            isGranted = isOverlayPermissionGranted,
            highlightError = highlightErrors,
            onClick = {
                if (!isOverlayPermissionGranted) {
                    onRequestOverlayPermission()
                }
            }
        )
        PermissionChecklistItem(
            title = "Battery Optimization",
            icon = Icons.Default.BatteryAlert,
            isGranted = isBatteryOptimizationDisabled,
            highlightError = highlightErrors,
            onClick = {
                if (!isBatteryOptimizationDisabled) {
                    onRequestBatteryOptimization()
                }
            }
        )
    }
}

@Composable
private fun PermissionChecklistItem(
    title: String,
    icon: ImageVector,
    isGranted: Boolean,
    onClick: () -> Unit,
    highlightError: Boolean = false // YENİ
) {
    // Arka plan ve border renklerini duruma göre belirle
    val bgColor = when {
        isGranted -> Color(0xFF1E3A2E)
        highlightError -> Color(0xFF5A1A1A) // kırmızı ton
        else -> Color(0xFF2C2C2E)
    }
    val borderColor = if (!isGranted && highlightError) Color(0xFFFF3B30) else Color.Transparent
    val badgeBg = when {
        isGranted -> Color(0xFF30D158)
        highlightError -> Color(0xFFFF453A)
        else -> Color(0xFF48484A)
    }
    val statusTextColor = when {
        isGranted -> Color(0xFF30D158)
        highlightError -> Color(0xFFFF3B30)
        else -> Color(0xFF0A84FF)
    }
    val statusText = when {
        isGranted -> "✓"
        highlightError -> "Required"
        else -> "Tap"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { if (!isGranted) onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sol rozet
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(badgeBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isGranted) Icons.Default.CheckCircle else icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Başlık
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )

            // Sağ durum metni
            Text(
                text = statusText,
                fontSize = if (isGranted) 16.sp else 12.sp,
                color = statusTextColor,
                fontWeight = FontWeight.Bold.takeIf { isGranted } ?: FontWeight.Medium
            )
        }
    }
}

