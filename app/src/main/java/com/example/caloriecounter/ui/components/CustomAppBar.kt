package com.example.caloriecounter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Menu
import com.composables.icons.lucide.ScanText
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun CustomAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onScanClick: () -> Unit = {}
) {
    // Remember the image URL to prevent re-composition
    val imageUrl = remember { "https://images.squarespace-cdn.com/content/v1/631ba8eed2196a6795698665/3690ca61-6a9d-4c93-a2a5-83a5f2aa1648/2022-08-16-Trinet-0540-Martinez-Juan.jpg" }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoilImage(
            imageModel = { imageUrl },
            imageOptions = ImageOptions(
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            ),
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFEFEFEF).copy(0.55f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onScanClick()
                    }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.ScanText,
                    contentDescription = "Scan Icon",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFEFEFEF).copy(0.55f),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Lucide.Menu,
                    contentDescription = "Menu Icon",
                    modifier = Modifier.size(24.dp)
                )
            }

        }
    }
}