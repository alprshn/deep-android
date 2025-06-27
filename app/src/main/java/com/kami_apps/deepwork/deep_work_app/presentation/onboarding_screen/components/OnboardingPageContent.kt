package com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.components

import androidx.compose.animation.core.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
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
import androidx.compose.ui.unit.times
import kotlinx.coroutines.delay
import com.kami_apps.deepwork.R
import com.kami_apps.deepwork.deep_work_app.presentation.onboarding_screen.OnboardingPage
import kotlin.math.sin
import kotlin.math.cos

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier,
    onShowButtons: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(page.backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (page.showFloatingIcons) {
            // İlk sayfa için sıralı animasyonlar
            FirstPageAnimatedContent(
                page = page,
                onShowButtons = onShowButtons
            )
        } else {
            // Diğer sayfalar için basit içerik
            OtherPageContent(page = page)
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
private fun OtherPageContent(page: OnboardingPage) {
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
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 34.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Açıklama
        Text(
            text = page.description,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
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


