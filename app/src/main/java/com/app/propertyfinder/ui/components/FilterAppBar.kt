package com.app.propertyfinder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterAppBar(
    title: String,
    onBackClick: () -> Unit,
    onResetClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            AppTextBold(
                text = title,
                fontSize = 18.sp,
                color = MolumulkCharcoal
            )
        },
        navigationIcon = {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(36.dp)
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.CenterStart
            ) {
                AppImage(
                    resId = R.drawable.back_arrow,
                    modifier = Modifier.size(16.dp),
                    contentDescription = stringResource(R.string.back),
                    tint = MolumulkCharcoal
                )
            }
        },
        actions = {
            AppTextSemiBold(
                text = stringResource(R.string.reset),
                color = MolumulkPrimaryCoralRed,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable(onClick = onResetClick)
                    .padding(end = 20.dp)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}







