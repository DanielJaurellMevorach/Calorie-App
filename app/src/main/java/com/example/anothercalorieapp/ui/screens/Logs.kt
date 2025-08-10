package com.example.anothercalorieapp.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.anothercalorieapp.ui.components.general.NavigationBar
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun LogsScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(getResponsiveSize(60.dp)))

            Text(
                text = "Logs",
                fontSize = getResponsiveFontSize(24.sp),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(getResponsiveSize(20.dp)))

            Text(
                text = "Coming Soon...",
                fontSize = getResponsiveFontSize(16.sp)
            )

            Text(
                text = "Your meal logs and statistics will be displayed here.",
                fontSize = getResponsiveFontSize(14.sp)
            )
        }

        // Navigation Bar at bottom
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            NavigationBar(navController = navController)
        }
    }
}
