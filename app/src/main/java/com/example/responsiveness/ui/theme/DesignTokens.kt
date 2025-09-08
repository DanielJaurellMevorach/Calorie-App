package com.example.responsiveness.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.min

/**
 * Central place for design baseline and base tokens.
 *
 * Usage:
 * val tokens = DesignTokens.provideTokens(availableWidth = maxWidth, availableHeight = maxHeight)
 * then use tokens.headerFont, tokens.sDp(someDp), etc.
 */
object DesignTokens {
    // Design baseline (pick the baseline you actually design on)
    // If you are designing on a small phone emulator, use 360 x 592 (or 360 x 800 recommended).
    val baselineWidth = 360.dp
    val baselineHeight = 592.dp

    // Base token values expressed for the baseline (edit here)
    private val baseHeader = 24.sp
    private val baseBody = 16.sp
    private val baseCorner = 16.dp
    private val baseBorder = 2.dp
    private val baseInnerPadding = 12.dp
    private val baseOuterInset = 4.dp
    private val baseBtnMinH = 36.dp
    private val baseBtnHPadding = 12.dp
    private val baseNutrientText = 14.sp
    private val baseCalendarText = (baseNutrientText.value - 4f).sp

    // Speedometer specific tokens
    private val baseSpeedometerStrokeWidth = 18.dp // thinner than before
    private val baseSpeedometerMainTextSize = 18.sp
    private val baseSpeedometerSubTextSize = 12.sp

    // Camera instructions specific tokens
    private val baseCameraInstructionsTitleFontSize = 20.sp // reduced
    private val baseCameraInstructionsFontSize = 16.sp // reduced
    private val baseCameraInstructionsLineHeight = 28.sp // reduced
    private val baseCameraInstructionsItemSpacing = 20.dp // reduced

    // ... inside DesignTokens object
    private val baseSearchBarHeight = 40.dp // set to a reasonable fixed height
    private val baseSearchBarFontSize = 10.sp // smaller font
    private val baseSearchBarIconSize = 20.dp // smaller icon
    private val baseSearchBarPadding = 0.dp // tighter padding

    // Toggleable button tokens
    private val baseToggleButtonHeight = 36.dp
    private val baseToggleButtonFontSize = 14.sp

    // Dedicated SettingAdjustableWheel tokens (reduced by 1/3)
    private val baseSettingWheelSize = 80.dp // was 120.dp
    private val baseSettingWheelStrokeWidth = 7.dp // was 10.dp
    private val baseSettingWheelTextSize = 16.sp // was 24.sp
    private val baseSettingWheelCardHeight = 100.dp // was 240.dp

    // Returned token set
    data class Tokens(
        val scale: Float,
        val headerFont: TextUnit,
        val bodyFont: TextUnit,
        val corner: Dp,
        val border: Dp,
        val innerPadding: Dp,
        val outerInset: Dp,
        val btnMinHeight: Dp,
        val btnHPadding: Dp,
        // Nutrient card specific tokens
        val cardHeight: Dp,
        val iconSize: Dp,
        val nutrientTextSize: TextUnit,
        // Calendar specific tokens
        val calendarTextSize: TextUnit,
        // Speedometer specific tokens
        val speedometerStrokeWidth: Dp,
        val speedometerMainTextSize: TextUnit,
        val speedometerSubTextSize: TextUnit,
        // Camera sheet specific tokens
        val cameraSheetMinHeight: Dp,
        val cameraSheetInitialHeight: Dp,
        val cameraSheetMaxHeight: Dp,
        val cameraControlButtonSize: Dp,
        val cameraCaptureButtonSize: Dp,
        val cameraIconSize: Dp,
        val cameraCaptureIconSize: Dp,
        // Camera instructions specific tokens
        val cameraInstructionsTitleFontSize: TextUnit,
        val cameraInstructionsFontSize: TextUnit,
        val cameraInstructionsLineHeight: TextUnit,
        val cameraInstructionsItemSpacing: Dp,
        // Shimmer effect specific tokens
        val shimmerCardHeight: Dp,
        val shimmerIconSize: Dp,
        val shimmerCardCorner: Dp,
        val shimmerCardPadding: Dp,
        val shimmerTextHeightLarge: Dp,
        val shimmerTextHeightMedium: Dp,
        val shimmerTextHeightSmall: Dp,
        val shimmerTextWidthLarge: Dp,
        val shimmerTextWidthMedium: Dp,
        val shimmerTextWidthSmall: Dp,
        val shimmerShareButtonSize: Dp,
        val shimmerShareButtonCorner: Dp,
        val shimmerQuantityControlHeight: Dp,
        val shimmerQuantityControlCorner: Dp,
        val shimmerIngredientCardHeight: Dp,
        val shimmerIngredientIconSize: Dp,
        val shimmerIngredientIconCorner: Dp,
        val shimmerIngredientTextPadding: Dp,
        val shimmerIngredientTextWidth: Dp,
        val shimmerIngredientTextHeight: Dp,
        val shimmerSpacerWidth: Dp,
        val shimmerSpacerHeight: Dp,
        val shimmerProgressBarHeight: Dp,
        val shimmerProgressBarCorner: Dp,
        // navigation
        val navContainerHeight: Dp,
        val navCornerRadius: Dp,
        val navIconSize: Dp,
        val navLabelFontSize: TextUnit,
        val navHorizontalPadding: Dp,
        val navInnerPaddingH: Dp,
        val navInnerPaddingV: Dp,
        val navSpacer: Dp,
        val navIconSelectedScale: Float,
        val navContainerWidth: (Dp) -> Dp,
        // Search Bar specific tokens
        val searchBarHeight: Dp,
        val searchBarFontSize: TextUnit,
        val searchBarIconSize: Dp,
        val searchBarPadding: Dp,
        // Toggleable button tokens
        val toggleButtonHeight: Dp,
        val toggleButtonFontSize: TextUnit,
        // Dedicated SettingAdjustableWheel tokens
        val settingWheelSize: Dp,
        val settingWheelStrokeWidth: Dp,
        val settingWheelTextSize: TextUnit,
        val settingWheelCardHeight: Dp,
        // helpers (convenience)
        val sDp: (Dp) -> Dp,
        val sSp: (TextUnit) -> TextUnit
    )

    /**
     * Call from a Composable with the available width/height (BoxWithConstraints.maxWidth/maxHeight).
     * This will compute scale and produce scaled tokens.
     */
    @Composable
    fun provideTokens(availableWidth: Dp, availableHeight: Dp): Tokens {
        val conf = LocalConfiguration.current
        val fontScale = conf.fontScale
        val density = LocalDensity.current

        // compute a uniform scale factor preserving aspect ratio
        val scaleW = availableWidth / baselineWidth
        val scaleH = availableHeight / baselineHeight
        val scale = min(scaleW, scaleH).coerceIn(0.5f, 3.0f)

        // helpers to scale Dp/Sp
        val sDp: (Dp) -> Dp = { it * scale }
        val sSp: (TextUnit) -> TextUnit = { it * scale * fontScale }

        val cameraSheetMaxHeight = availableHeight * 0.95f

        return Tokens(
            scale = scale,
            headerFont = sSp(baseHeader),
            bodyFont = sSp(baseBody),
            corner = sDp(baseCorner),
            border = sDp(baseBorder),
            innerPadding = sDp(baseInnerPadding),
            outerInset = sDp(baseOuterInset),
            btnMinHeight = sDp(baseBtnMinH),
            btnHPadding = sDp(baseBtnHPadding),
            cardHeight = sDp(100.dp),
            iconSize = sDp(32.dp),
            nutrientTextSize = sSp(baseNutrientText),
            calendarTextSize = sSp(baseCalendarText),
            speedometerStrokeWidth = sDp(baseSpeedometerStrokeWidth),
            speedometerMainTextSize = sSp(baseSpeedometerMainTextSize),
            speedometerSubTextSize = sSp(baseSpeedometerSubTextSize),
            // Camera sheet specific tokens
            cameraSheetMinHeight = sDp(90.dp),
            cameraSheetInitialHeight = sDp(120.dp),
            cameraSheetMaxHeight = cameraSheetMaxHeight,
            cameraControlButtonSize = sDp(48.dp),
            cameraCaptureButtonSize = sDp(60.dp),
            cameraIconSize = sDp(20.dp),
            cameraCaptureIconSize = sDp(28.dp),
            // Camera instructions specific tokens
            cameraInstructionsTitleFontSize = sSp(baseCameraInstructionsTitleFontSize),
            cameraInstructionsFontSize = sSp(baseCameraInstructionsFontSize),
            cameraInstructionsLineHeight = sSp(baseCameraInstructionsLineHeight),
            cameraInstructionsItemSpacing = sDp(baseCameraInstructionsItemSpacing),
            // Shimmer effect specific tokens
            shimmerCardHeight = sDp(100.dp),
            shimmerIconSize = sDp(24.dp),
            shimmerCardCorner = sDp(12.dp),
            shimmerCardPadding = sDp(8.dp),
            shimmerTextHeightLarge = sDp(24.dp),
            shimmerTextHeightMedium = sDp(18.dp),
            shimmerTextHeightSmall = sDp(15.dp),
            shimmerTextWidthLarge = sDp(120.dp),
            shimmerTextWidthMedium = sDp(60.dp),
            shimmerTextWidthSmall = sDp(40.dp),
            shimmerShareButtonSize = sDp(52.dp),
            shimmerShareButtonCorner = sDp(26.dp),
            shimmerQuantityControlHeight = sDp(52.dp),
            shimmerQuantityControlCorner = sDp(28.dp),
            shimmerIngredientCardHeight = sDp(72.dp),
            shimmerIngredientIconSize = sDp(24.dp),
            shimmerIngredientIconCorner = sDp(8.dp),
            shimmerIngredientTextPadding = sDp(10.dp),
            shimmerIngredientTextWidth = sDp(120.dp),
            shimmerIngredientTextHeight = sDp(19.dp),
            shimmerSpacerWidth = sDp(8.dp),
            shimmerSpacerHeight = sDp(8.dp),
            shimmerProgressBarHeight = sDp(20.dp),
            shimmerProgressBarCorner = sDp(6.dp),

            // Navigation specific tokens
            navContainerHeight = sDp(56.dp),
            navCornerRadius = sDp(48.dp),
            navIconSize = sDp(20.dp),
            navLabelFontSize = sSp(8.sp),
            navHorizontalPadding = sDp(16.dp),
            navInnerPaddingH = sDp(1.dp),
            navInnerPaddingV = sDp(8.dp),
            navSpacer = sDp(16.dp),
            navIconSelectedScale = 1.15f,
            navContainerWidth = { availableWidth -> (availableWidth * 0.65f).coerceIn(sDp(220.dp), sDp(320.dp)) },
            // Search Bar specific tokens
            searchBarHeight = sDp(baseSearchBarHeight),
            searchBarFontSize = sSp(baseSearchBarFontSize),
            searchBarIconSize = sDp(baseSearchBarIconSize),
            searchBarPadding = sDp(baseSearchBarPadding),
            // Toggleable button tokens
            toggleButtonHeight = sDp(baseToggleButtonHeight),
            toggleButtonFontSize = sSp(baseToggleButtonFontSize),
            // Dedicated SettingAdjustableWheel tokens
            settingWheelSize = sDp(baseSettingWheelSize),
            settingWheelStrokeWidth = sDp(baseSettingWheelStrokeWidth),
            settingWheelTextSize = sSp(baseSettingWheelTextSize),
            settingWheelCardHeight = sDp(baseSettingWheelCardHeight),
            sDp = sDp,
            sSp = sSp
        )
    }
}
