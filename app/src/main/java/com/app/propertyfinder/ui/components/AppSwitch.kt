package com.app.propertyfinder.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

@Composable
fun AppSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkedColor: Color = MolumulkPrimaryCoralRed
) {
    val trackWidth = 45.dp
    val trackHeight = 25.dp
    val thumbSize = 21.dp
    val gap = (trackHeight - thumbSize) / 2

    val animateOffset by animateDpAsState(
        targetValue = if (checked) trackWidth - thumbSize - gap else gap,
        label = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .size(trackWidth, trackHeight)
            .clip(CircleShape)
            .background(if (checked) checkedColor else Color(0xFFE0E0E0))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onCheckedChange(!checked) }
            .padding(horizontal = gap),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = animateOffset - gap)
                .size(thumbSize)
                .background(Color.White, CircleShape)
        )
    }
}
