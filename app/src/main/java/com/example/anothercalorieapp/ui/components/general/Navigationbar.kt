package com.example.anothercalorieapp.ui.components.general

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.composables.icons.lucide.ChartNoAxesColumn
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PersonStanding
import com.composables.icons.lucide.ScanText
import com.example.anothercalorieapp.HomeRoute
import com.example.anothercalorieapp.ScannerRoute
import com.example.anothercalorieapp.ProfileRoute
import com.example.anothercalorieapp.LogsRoute
import com.example.anothercalorieapp.ui.utils.getResponsiveCornerRadius
import com.example.anothercalorieapp.ui.utils.getResponsiveFontSize
import com.example.anothercalorieapp.ui.utils.getResponsiveIconSize
import com.example.anothercalorieapp.ui.utils.getResponsivePadding
import com.example.anothercalorieapp.ui.utils.getResponsiveSize

@Composable
fun NavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val containerHeight = getResponsiveSize(56.dp)
    val cornerRadius = getResponsiveCornerRadius(48.dp)
    val backgroundOpacity = 0.8f
    val iconSize = getResponsiveIconSize(20.dp)
    val labelFontSize = getResponsiveFontSize(8.sp)
    val inactivePageColor = Color(0xF5CECECE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = getResponsivePadding(16.dp),
                vertical = getResponsivePadding(16.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Top
    ) {
        // Main nav container
        Row(
            modifier = Modifier
                .height(containerHeight)
                .width(containerHeight * 3)
                .weight(1f)
                .background(Color.Black.copy(alpha = backgroundOpacity), RoundedCornerShape(cornerRadius))
                .padding(
                    horizontal = getResponsivePadding(12.dp),
                    vertical = getResponsivePadding(8.dp)
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = Lucide.House,
                label = "Home",
                iconSize = iconSize,
                fontSize = labelFontSize,
                isActive = currentRoute?.contains("HomeRoute") == true,
                inactiveColor = inactivePageColor,
                onClick = { navController.navigate(HomeRoute) }
            )
            NavItem(
                icon = Lucide.ChartNoAxesColumn,
                label = "Logs",
                iconSize = iconSize,
                fontSize = labelFontSize,
                isActive = currentRoute?.contains("LogsRoute") == true,
                inactiveColor = inactivePageColor,
                onClick = { navController.navigate(LogsRoute) }
            )
            NavItem(
                icon = Lucide.PersonStanding,
                label = "Profile",
                iconSize = iconSize,
                fontSize = labelFontSize,
                isActive = currentRoute?.contains("ProfileRoute") == true,
                inactiveColor = inactivePageColor,
                onClick = { navController.navigate(ProfileRoute) }
            )
        }

        Spacer(
            modifier = Modifier
                .defaultMinSize(minWidth = getResponsiveSize(20.dp), minHeight = containerHeight)
                .height(containerHeight)
                .weight(0.15f)
        )

        // Plus button (Scanner)
        Box(
            modifier = Modifier
                .height(containerHeight)
                .aspectRatio(1f)
                .background(Color.Black.copy(alpha = backgroundOpacity), RoundedCornerShape(cornerRadius))
                .clickable { navController.navigate(ScannerRoute) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.ScanText,
                contentDescription = "Scanner",
                tint = Color.White,
                modifier = Modifier.size(iconSize + getResponsiveSize(6.dp))
            )
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    iconSize: Dp,
    fontSize: TextUnit,
    isActive: Boolean,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(getResponsiveSize(48.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (!isActive) inactiveColor else Color.White,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = label,
            color = if (!isActive) inactiveColor else Color.White,
            fontSize = fontSize,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
