package com.app.propertyfinder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.PropertyFinderTheme

// Legacy tokens (kept for reference during migration)
import com.app.propertyfinder.ui.theme.PrimaryColor

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2)
// PrimaryCoralRed (#FF5A5F), Charcoal (#1F2937)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

@Composable
fun ForgotPasswordRow(
    onForgotPasswordClick: () -> Unit = {},
    rememberMeChecked: Boolean = false,
    onRememberMeChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = rememberMeChecked,
                onCheckedChange = onRememberMeChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MolumulkPrimaryCoralRed,
                    checkmarkColor = Color.White
                )
            )
            AppTextRegular("Remember me", color = MolumulkCharcoal)
        }

        AppTextBold(
            text = "Forgot password?",
            fontSize = 14.sp,
            color = MolumulkPrimaryCoralRed,
            modifier = Modifier.clickable { onForgotPasswordClick() }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordPreview() {
    PropertyFinderTheme {
        ForgotPasswordRow()
    }
}