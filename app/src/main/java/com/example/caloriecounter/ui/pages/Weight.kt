package com.example.caloriecounter.ui.pages

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.caloriecounter.ui.components.navigation.PageLayout

@Composable
fun Weight(
    modifier: Modifier = Modifier,
    windowSize: WindowSizeClass,
    onNavigateToHome: () -> Unit = {},
    onNavigateToWeight: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onNavigateToRecipes: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {}
) {
    PageLayout(
        currentPage = "Weight",
        windowSize = windowSize,
        onNavigateToHome = onNavigateToHome,
        onNavigateToWeight = onNavigateToWeight,
        onNavigateToActivity = onNavigateToActivity,
        onNavigateToRecipes = onNavigateToRecipes,
        onNavigateToScanner = onNavigateToScanner,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Weight page content goes here.")
    }
}
