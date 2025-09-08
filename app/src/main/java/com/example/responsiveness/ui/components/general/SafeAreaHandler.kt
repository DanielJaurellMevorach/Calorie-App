package com.example.responsiveness.ui.components.general

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Provides dynamic padding values that account for system bars and floating navigation
 */
@Composable
fun rememberSafeContentPadding(
    includeStatusBar: Boolean = true,
    includeNavigationBar: Boolean = false,
    additionalBottomPadding: Dp = 0.dp
): PaddingValues {
    val statusBarPadding = if (includeStatusBar) {
        WindowInsets.statusBars.asPaddingValues()
    } else {
        PaddingValues(0.dp)
    }

    val navigationBarPadding = if (includeNavigationBar) {
        WindowInsets.navigationBars.asPaddingValues()
    } else {
        PaddingValues(0.dp)
    }

    return PaddingValues(
        start = maxOf(
            statusBarPadding.calculateStartPadding(LayoutDirection.Ltr),
            navigationBarPadding.calculateStartPadding(LayoutDirection.Ltr)
        ),
        top = statusBarPadding.calculateTopPadding(),
        end = maxOf(
            statusBarPadding.calculateEndPadding(LayoutDirection.Ltr),
            navigationBarPadding.calculateEndPadding(LayoutDirection.Ltr)
        ),
        bottom = navigationBarPadding.calculateBottomPadding() + additionalBottomPadding
    )
}

/**
 * Provides content padding for LazyColumn/LazyRow that accounts for floating navigation
 */
@Composable
fun rememberScrollableContentPadding(
    horizontal: Dp = 0.dp,
    top: Dp = 0.dp,
    floatingNavBarHeight: Dp = 80.dp,
    additionalBottomPadding: Dp = 16.dp
): PaddingValues {
    return PaddingValues(
        start = horizontal,
        top = top,
        end = horizontal,
        bottom = floatingNavBarHeight + additionalBottomPadding
    )
}
