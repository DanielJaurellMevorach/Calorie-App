package com.example.anothercalorieapp.ui.components.general

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.composables.icons.lucide.ChartNoAxesColumn
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.PersonStanding
import com.composables.icons.lucide.ScanText

val activePage = "Home"
val inactivePageColor = Color(0xF5CECECE)

@Composable
fun NavigationBar() {
    val containerHeight = 56.dp // Reduced height
    val cornerRadius = 48.dp // Slightly reduced for proportion
    val backgroundOpacity = 0.8f
    val iconSize = 20.dp // Slightly smaller icons
    val labelFontSize = 8.sp // Slightly larger for readability

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp), // Remove .background(Color.Transparent)
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
                .padding(horizontal = 12.dp, vertical = 8.dp), // Less padding
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(Lucide.House, "Home", iconSize, labelFontSize)
            NavItem(Lucide.ChartNoAxesColumn, "Logs", iconSize, labelFontSize)
            NavItem(Lucide.PersonStanding, "Profile", iconSize, labelFontSize)
        }

        Spacer(
            modifier = Modifier
                .defaultMinSize(minWidth = 20.dp, minHeight = containerHeight)
                .height(containerHeight)
                .weight(0.15f)
        )

        // Plus button
        Box(
            modifier = Modifier
                .height(containerHeight)
                .aspectRatio(1f)
                .background(Color.Black.copy(alpha = backgroundOpacity), RoundedCornerShape(cornerRadius)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Lucide.ScanText,
                contentDescription = "Add",
                tint = Color.White,
                modifier = Modifier.size(iconSize + 6.dp)
            )
        }
    }
}

@Composable
fun NavItem(icon: ImageVector, label: String, iconSize: Dp, fontSize: TextUnit) {
    Column(
        modifier = Modifier.width(48.dp), // Fixed width for centering
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(0.dp) // No space between icon and label
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (label != activePage) inactivePageColor else Color.White,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = label,
            color = if (label != activePage) inactivePageColor else Color.White,
            fontSize = fontSize,
            fontWeight = if (label == activePage) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
