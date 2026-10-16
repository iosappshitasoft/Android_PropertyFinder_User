package com.app.propertyfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.ui.theme.DividerColor

@Composable
fun AppDivider(
    modifier: Modifier = Modifier,
    color: Color = DividerColor,
    thickness: Dp = 1.dp,
    horizontalPadding: Dp = 0.dp
) {
    Divider(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        color = color,
        thickness = thickness
    )
}

@Composable
fun AppVerticalDivider(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE0E0E0),
    width: Dp = 1.dp,
    height: Dp = 24.dp
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .background(color)
    )
}

