package com.kamiapps.deep.deep.presentation.statistics_screen.components
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt

/**
 * A modern segmented control component with smooth animations and customizable appearance.
 *
 * @param modifier Modifier for customizing the component
 * @param items List of segment labels to display (2-4 items)
 * @param selectedIndex Index of the currently selected segment
 * @param onItemSelected Callback when a segment is selected, provides the selected index
 * @param showSeparators Whether to show separators between segments
 * @param colors Color scheme for the component (defaults to modern colors)
 * @param height Height of the component (defaults to 40.dp)
 */


@Composable
fun ModernSegmentedControl(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    showSeparators: Boolean = false,
    colors: SegmentedControlColors = ModernSegmentedControlDefaults.colors(),
    height: Int = 40
) {
    require(items.size in 2..4) { "SegmentedControl must contain between 2 and 4 segments" }

    val density = LocalDensity.current
    val animationDuration = 250 // Animation duration in milliseconds

    // Store segment measurements
    val segmentPositions = remember { mutableStateMapOf<Int, Float>() }
    val segmentWidths = remember { mutableStateMapOf<Int, Float>() }

    // Track if measurements are complete
    var measurementsReady by remember { mutableStateOf(false) }

    // Track if the user has interacted with the component
    var hasUserInteracted by remember { mutableStateOf(false) }

    // Get selected item info
    val selectedPosition = segmentPositions[selectedIndex] ?: 0f
    val selectedWidth = segmentWidths[selectedIndex] ?: 0f

    // Verify when all measurements are complete
    LaunchedEffect(segmentPositions, segmentWidths) {
        if (items.indices.all { segmentPositions.containsKey(it) && segmentWidths.containsKey(it) }) {
            measurementsReady = true
        }
    }

    // Position and width - only animate after user interaction
    val animatedPosition by animateFloatAsState(
        targetValue = selectedPosition,
        animationSpec = if (hasUserInteracted) {
            tween(durationMillis = animationDuration)
        } else {
            snap() // Instant with no animation for initial render
        },
        label = "position",
    )

    val animatedWidth by animateFloatAsState(
        targetValue = selectedWidth,
        animationSpec = if (hasUserInteracted) {
            tween(durationMillis = animationDuration)
        } else {
            snap() // Instant with no animation for initial render
        },
        label = "width"
    )

    Box(
        modifier = modifier
            .clip(ModernShapes.medium)
            .background(colors.containerBackground)
            .height(height.dp)
    ) {
        // Only show selected background when measurements are ready
        if (measurementsReady) {
            Box(
                modifier = Modifier
                    .offset {
                        // Convert Dp to pixels with proper rounding for accurate positioning
                        val offsetX = with(density) {
                            animatedPosition
                                .toDp()
                                .toPx()
                        }
                        IntOffset(offsetX.roundToInt(), 0)
                    }
                    .width(with(density) { animatedWidth.toDp() })
                    .height(height.dp)
                    .padding(4.dp)
                    .background(
                        colors.selectedBackground,
                        ModernShapes.medium
                    )
                    .zIndex(1f)
            )
        }

        // Segments row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, label ->
                val isSelected = index == selectedIndex
                val weight = 1f / items.size

                // Segment text with animated color - only animate after user interaction
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) colors.selectedTextColor else colors.unselectedTextColor,
                    animationSpec = if (hasUserInteracted) {
                        tween(durationMillis = animationDuration)
                    } else {
                        snap() // Instant with no animation for initial render
                    },
                    label = "textColor"
                )

                // Segment button (no ripple effect)
                val interactionSource = remember { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .height(height.dp)
                        .onGloballyPositioned { coordinates ->
                            // Store position and width for animations
                            segmentPositions[index] = coordinates.positionInParent().x
                            segmentWidths[index] = coordinates.size.width.toFloat()
                        }
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            // Mark that user has interacted with the component
                            hasUserInteracted = true
                            onItemSelected(index)
                        }
                        .background(Color.Transparent)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 4.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        maxLines = 1,
                        style = if (isSelected) {
                            ModernTypography.segmentSelected
                        } else {
                            ModernTypography.segmentUnselected
                        },
                        textAlign = TextAlign.Center
                    )
                }

                // Only show separators when measurements are ready
                if (showSeparators && measurementsReady && index < items.size - 1) {
                    // Determine if this separator should be visible
                    val shouldBeVisible = shouldShowSeparator(items.size, selectedIndex, index)

                    // Animate opacity - only after user interaction
                    val targetAlpha = if (shouldBeVisible) 1f else 0f
                    val separatorAlpha by animateFloatAsState(
                        targetValue = targetAlpha,
                        animationSpec = if (hasUserInteracted) {
                            tween(durationMillis = animationDuration)
                        } else {
                            snap() // Instant with no animation for initial render
                        },
                        label = "separatorAlpha"
                    )

                    // Always render separator but control its visibility with alpha
                    Box(
                        modifier = Modifier
                            .height(height.dp / 2)
                            .alpha(separatorAlpha)
                            .background(colors.separatorColor)
                            .width(1.dp)
                            .zIndex(3f)
                            .align(Alignment.CenterVertically)
                    )
                }
            }
        }
    }
}

/**
 * Helper function to determine if a separator should be shown based on configuration
 */
private fun shouldShowSeparator(
    totalSegments: Int,
    selectedIndex: Int,
    currentSeparatorIndex: Int
): Boolean {
    return when (totalSegments) {
        // With 2 segments, never show separators
        2 -> false
        // With 3 segments
        3 -> when (selectedIndex) {
            0 -> currentSeparatorIndex == 1  // If first selected: separator between 2-3
            1 -> false                       // If second selected: no separators
            2 -> currentSeparatorIndex == 0  // If third selected: separator between 1-2
            else -> false
        }
        // With 4 segments
        4 -> when (selectedIndex) {
            0 -> currentSeparatorIndex in 1..2  // If first selected: separators between 2-3 and 3-4
            1 -> currentSeparatorIndex == 2     // If second selected: separator between 3-4
            2 -> currentSeparatorIndex == 0     // If third selected: separator between 1-2
            3 -> currentSeparatorIndex in 0..1  // If fourth selected: separators between 1-2 and 2-3
            else -> false
        }
        // Should never happen due to initial requirement check
        else -> false
    }
}

object ModernTypography {
    val segmentSelected = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    )
    val segmentUnselected = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.sp
    )
}

data class SegmentedControlColors(
    val containerBackground: Color,
    val selectedBackground: Color,
    val selectedTextColor: Color,
    val unselectedTextColor: Color,
    val separatorColor: Color
)


object ModernSegmentedControlDefaults {
    @Composable
    fun colors(
        containerBackground: Color = Color(0xFFF2F4F7),
        selectedBackground: Color = Color(0xFF2563EB),
        selectedTextColor: Color = Color.White,
        unselectedTextColor: Color = Color(0xFF64748B),
        separatorColor: Color = Color(0xFFCBD5E1)
    ) = SegmentedControlColors(
        containerBackground = containerBackground,
        selectedBackground = selectedBackground,
        selectedTextColor = selectedTextColor,
        unselectedTextColor = unselectedTextColor,
        separatorColor = separatorColor
    )
}

object ModernShapes {
    val small = RoundedCornerShape(6.dp)
    val medium = RoundedCornerShape(8.dp)
    val large = RoundedCornerShape(12.dp)
}
