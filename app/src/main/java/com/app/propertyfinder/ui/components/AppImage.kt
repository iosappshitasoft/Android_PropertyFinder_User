package com.app.propertyfinder.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R

@Composable
fun AppImage(
    resId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    tint: Color? = null
) {
    Image(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = if (tint != null) ColorFilter.tint(tint) else null
    )
}

@Composable
fun AppImage(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Fit,
    tint: Color? = null
) {
    Image(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = if (tint != null) ColorFilter.tint(tint) else null
    )
}

/**
 * Common composable for displaying icons with tint support
 * Supports both drawable resources and vector icons
 */
@Composable
fun AppIcon(
    resId: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    tint: Color = Color.Unspecified
) {
    Icon(
        painter = painterResource(id = resId),
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint
    )
}

@Composable
fun AppSubComposeAsyncImage(
    model: Any?,
    modifier: Modifier = Modifier,
    placeholder: Int = R.drawable.ic_placeholder,
    error: Int = R.drawable.ic_placeholder,
    contentScale: ContentScale = ContentScale.Crop,
    alpha: Float = 1.0f
){
    SubcomposeAsyncImage(
        model = model,
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale,
        alpha = alpha,
        loading = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = placeholder),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }
        },
        error = {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = error),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
            }
        }
    )
}
