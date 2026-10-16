package com.app.propertyfinder.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.PrimaryColor

@Preview
@Composable
fun AppLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    
    // Smooth Pulsing Scale Animation
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Outer Ring
            CircularProgressIndicator(
                modifier = Modifier.size(85.dp),
                color = PrimaryColor,
                strokeWidth = 3.dp
            )

            // Full White Circle to host the icon
            Surface(
                modifier = Modifier.size(64.dp).clip(CircleShape),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AppImage(
                        resId = R.mipmap.ic_launcher_foreground,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .scale(scale),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
