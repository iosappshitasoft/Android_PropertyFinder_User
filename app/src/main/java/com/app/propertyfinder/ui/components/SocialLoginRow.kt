package com.app.propertyfinder.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.R

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2 & 10)
// Touch target >= 44dp, Border: MolumulkBorderDivider (#E7ECF4)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider

@Composable
fun SocialLoginRow(
    onGoogleClick: () -> Unit = {},
    onAppleClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, MolumulkBorderDivider), CircleShape)
                .clickable { onGoogleClick() }
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            AppImage(
                resId = R.drawable.ic_google,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
