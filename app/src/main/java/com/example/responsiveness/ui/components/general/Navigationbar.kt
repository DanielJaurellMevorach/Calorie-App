package com.example.responsiveness.ui.components.general

import android.annotation.SuppressLint
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.composables.icons.lucide.ChartNoAxesColumn
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.ScanText
import com.composables.icons.lucide.UserRound
import com.example.responsiveness.HomeRoute
import com.example.responsiveness.LogsRoute
import com.example.responsiveness.ProfileRoute
import com.example.responsiveness.ui.theme.DesignTokens
import kotlinx.coroutines.launch

val inactivePageColor = Color(0xF5CECECE)

@SuppressLint("UnrememberedMutableInteractionSource", "UnusedBoxWithConstraintsScope")
@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val tokens = DesignTokens.provideTokens(maxWidth, maxHeight)

        val navTokens = tokens // using dedicated nav tokens

        val backgroundOpacity = 0.9f

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = navTokens.navHorizontalPadding,
                    vertical = navTokens.navHorizontalPadding / 2
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .height(navTokens.navContainerHeight)
                    .width(navTokens.navContainerWidth(this@BoxWithConstraints.maxWidth))
                    .background(
                        Color.Black.copy(alpha = backgroundOpacity),
                        RoundedCornerShape(navTokens.navCornerRadius)
                    )
                    .padding(
                        horizontal = navTokens.navInnerPaddingH,
                        vertical = navTokens.navInnerPaddingV
                    )
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) { },
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = Lucide.House,
                    label = "Home",
                    tokens = navTokens,
                    isActive = currentRoute == HomeRoute::class.qualifiedName,
                    onClick = {
                        if (currentRoute != HomeRoute::class.qualifiedName) {
                            navController.navigate(HomeRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    }
                )
                NavItem(
                    icon = Lucide.ChartNoAxesColumn,
                    label = "Logs",
                    tokens = navTokens,
                    isActive = currentRoute == LogsRoute::class.qualifiedName,
                    onClick = {
                        if (currentRoute != LogsRoute::class.qualifiedName) {
                            navController.navigate(LogsRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    }
                )
                NavItem(
                    //icon = Lucide.PersonStanding,
                    icon = Lucide.UserRound,
                    label = "Profile",
                    tokens = navTokens,
                    isActive = currentRoute == ProfileRoute::class.qualifiedName,
                    onClick = {
                        if (currentRoute != ProfileRoute::class.qualifiedName) {
                            navController.navigate(ProfileRoute) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(navTokens.navSpacer))

            // Scanner button
            Box(
                modifier = Modifier
                    .size(navTokens.navContainerHeight)
                    .background(
                        Color.Black.copy(alpha = backgroundOpacity),
                        RoundedCornerShape(navTokens.navCornerRadius)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) {
                        if (currentRoute != "scanner") {
                            navController.navigate("scanner")
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Lucide.ScanText,
                    contentDescription = "Scan",
                    tint = Color.White,
                    modifier = Modifier.size(navTokens.navIconSize * 1.3f)
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    tokens: DesignTokens.Tokens,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val animatedColor = remember { Animatable(inactivePageColor) }
    val coroutineScope = rememberCoroutineScope()

    // Animate color when active/inactive with smoother transition
    LaunchedEffect(isActive) {
        val targetColor = if (isActive) Color.White else inactivePageColor
        animatedColor.animateTo(
            targetValue = targetColor,
            animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max) // Use Max to ensure consistent width across all items
            .clickable(
                indication = null,
                interactionSource = MutableInteractionSource()
            ) {
                // Only animate and navigate if not already active
                if (!isActive) {
                    coroutineScope.launch {
                        // Gentler bounce animation - smaller scale and longer duration
                        scale.animateTo(
                            targetValue = 1.1f, // Reduced from tokens.navIconSelectedScale
                            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing)
                        )
                        scale.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                        )
                    }
                    onClick()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(tokens.sDp(2.dp))
    ) {
        // Reserve space for the icon to prevent layout shifts
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(tokens.navIconSize)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = animatedColor.value,
                modifier = Modifier
                    .size(tokens.navIconSize)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
            )
        }

        Spacer(modifier = Modifier.height(tokens.sDp(2.dp)))

        // Fix width shift by reserving same space for both normal & bold
        Box(
            contentAlignment = Alignment.Center
        ) {
            // Invisible bold text to reserve maximum width
            Text(
                text = label,
                fontSize = tokens.navLabelFontSize,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeight = tokens.navLabelFontSize
                ),
                color = Color.Transparent,
                maxLines = 1
            )
            // Visible text with animated properties
            Text(
                text = label,
                color = animatedColor.value,
                fontSize = tokens.navLabelFontSize,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                style = TextStyle(
                    platformStyle = PlatformTextStyle(includeFontPadding = false),
                    lineHeight = tokens.navLabelFontSize
                )
            )
        }
    }
}
