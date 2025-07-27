package com.example.caloriecounter.ui.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caloriecounter.ui.components.CustomAppBar

@Composable
fun PageLayout(
    currentPage: String,
    windowSize: WindowSizeClass,
    onNavigateToHome: () -> Unit,
    onNavigateToWeight: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToScanner: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.padding(top = 16.dp)) {
        CustomAppBar(
            title = "Calorie Counter",
            onScanClick = onNavigateToScanner
        )
        Spacer(modifier = Modifier.height(28.dp))

        when (windowSize.widthSizeClass) {
            WindowWidthSizeClass.Compact -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hello, Assad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(modifier = Modifier.height(36.dp))

                    NavBar(
                        currentPage = currentPage,
                        onNavigateToHome = onNavigateToHome,
                        onNavigateToWeight = onNavigateToWeight,
                        onNavigateToActivity = onNavigateToActivity,
                        onNavigateToRecipes = onNavigateToRecipes
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    content()
                }
            }
            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 20.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hello, Assad",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        )
                        Spacer(modifier = Modifier.height(36.dp))

                        NavBar(
                            currentPage = currentPage,
                            onNavigateToHome = onNavigateToHome,
                            onNavigateToWeight = onNavigateToWeight,
                            onNavigateToActivity = onNavigateToActivity,
                            onNavigateToRecipes = onNavigateToRecipes
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        content()
                    }
                }
            }
        }
    }
}
