package com.app.propertyfinder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.PropertyFinderTheme

// =========================================================================
// SOCIAL / ACTION BUTTON (Updated for molumulk 2026 Design System)
// Standard height: 52dp, Corner Radius: 12-16dp (MolumulkDimens.PrimaryCtaRadius)
// =========================================================================
@Composable
fun SocialButton(
    icon: Int,
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    contentColor: Color = MolumulkCharcoal,
    borderColor: Color = MolumulkBorderDivider,
    shape: Shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
    buttonHeight: Dp = MolumulkDimens.PrimaryButtonHeight,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(buttonHeight),
        shape = shape,
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 0.dp
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        )
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        AppTextSemiBold(text = text, fontSize = 15.sp, color = contentColor)
    }
}

@Preview(
    name = "Social Button Preview",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun SocialButtonPreview() {
    PropertyFinderTheme {
        SocialButton(
            icon = R.drawable.ic_google,
            text = "Continue with Google",
            modifier = Modifier.fillMaxWidth(),
            onClick = {}
        )
    }
}

