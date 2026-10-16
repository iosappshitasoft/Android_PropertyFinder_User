package com.app.propertyfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary

@Composable
fun EditProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        AppTextBold(text = label, fontSize = 14.sp)
        AppSpacerV(8)

        // Custom Text Field Look
        val backgroundColor = if (enabled) Color.White else Color(0x2222221A).copy(alpha = 0.1f)
        val borderColor = if (errorMessage != null) Color.Red else if (enabled) MolumulkBorderDivider else Color.Transparent

        AppBasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = if (enabled) TextPrimary else TextSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(MolumulkDimens.SearchFieldHeight)
                .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                .background(backgroundColor)
                .border(
                    width = if (enabled || errorMessage != null) 1.dp else 0.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                )
                .padding(horizontal = 16.dp),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    innerTextField()
                }
            }
        )
        
        if (errorMessage != null) {
            AppSpacerV(4)
            AppTextRegular(text = errorMessage, color = Color.Red, fontSize = 12.sp)
        }
    }
}

@Composable
fun AppBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: androidx.compose.ui.text.TextStyle,
    keyboardType: KeyboardType = KeyboardType.Text, // 👈 ADD THIS
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = { innerTextField ->
        innerTextField()
    }
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        decorationBox = decorationBox,
        singleLine = true
    )
}

