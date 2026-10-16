package com.app.propertyfinder.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R

// Legacy tokens (kept for reference during migration)
import com.app.propertyfinder.ui.theme.TextSecondary

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2)
// PrimaryCoralRed (#FF5A5F), MutedSlate (#667085)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

@Composable
fun TermsText() {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppTextRegular(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.molumulk_terms_footer),
            color = MolumulkMutedSlate,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextBold(
                text = stringResource(R.string.terms_use),
                fontSize = 13.sp,
                color = MolumulkPrimaryCoralRed
            )
            AppSpacerH(3)
            AppTextRegular(
                text = "and",
                color = MolumulkMutedSlate,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )
            AppSpacerH(3)
            AppTextBold(
                text = stringResource(R.string.privacy_policy),
                fontSize = 13.sp,
                color = MolumulkPrimaryCoralRed
            )
        }
    }
}
