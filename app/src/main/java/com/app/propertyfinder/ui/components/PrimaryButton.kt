package com.app.propertyfinder.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.PrimaryColor

@Composable
fun PrimaryRoundButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = PrimaryColor,
    disabledContainerColor: Color = containerColor.copy(alpha = 0.5f),
    contentColor: Color = Color.White,
    trailingIcon: ImageVector? = null,
    cornerRadius: Dp = 26.dp,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(50.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(cornerRadius),
                colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextBold(fontSize = 14.sp, text = text, color = contentColor)
            if (trailingIcon != null) {
                AppSpacerH(8)
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
fun CornerButton(
    text: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    containerColor: Color = colorResource(R.color.darkBlue),

    contentColor: Color = Color.White,
    trailingIcon: ImageVector? = null,
    cornerRadius: Dp = 10.dp,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) 0.95f else 1f, label = "scale")

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        shape = RoundedCornerShape(cornerRadius),
                colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextSemiBold(fontSize = 14.sp, text = text, color = contentColor)
            if (trailingIcon != null) {
                AppSpacerH(8)
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = contentColor
                )
            }
        }
    }
}


