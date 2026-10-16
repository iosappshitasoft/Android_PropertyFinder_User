package com.app.propertyfinder.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.theme.PropertyFinderTheme

// Legacy tokens (kept for reference during migration)
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TabPrimary
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2 & 11)
// Active Tab = MolumulkPrimaryCoralRed (#FF5A5F), Inactive = MolumulkMutedSlate
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate

@Composable
fun LoginTabs(
    isSignUp: Boolean,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MolumulkOffWhite, RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius))
            .border(BorderStroke(1.dp, MolumulkBorderDivider), RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius))
            .padding(3.dp)
    ) {

        TabItem(
            text = stringResource(R.string.signin),
            selected = !isSignUp,
            onClick = onSignInClick,
            modifier = Modifier.weight(1f)
        )

        TabItem(
            text = stringResource(R.string.signup),
            selected = isSignUp,
            onClick = onSignUpClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (selected) MolumulkPrimaryCoralRed else Color.Transparent,
                RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextBold(
            text = text,
            fontSize = 15.sp,
            color = if (selected) Color.White else MolumulkMutedSlate
        )
    }
}

@Preview(
    name = "LoginTabs - Sign In Selected",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun LoginTabsSignInPreview() {
    PropertyFinderTheme {
        LoginTabs(
            isSignUp = false,
            onSignInClick = {},
            onSignUpClick = {}
        )
    }
}

