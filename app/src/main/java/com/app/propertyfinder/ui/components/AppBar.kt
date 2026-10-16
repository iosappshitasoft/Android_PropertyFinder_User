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
import com.app.propertyfinder.ui.theme.TextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionAppBar(
    title: String,
    onBackClick: () -> Unit
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AppTextSemiBold(
                    text = title,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }
        },
        navigationIcon = {
            AppImage(
                resId = R.drawable.ic_bg_back_arrow,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .size(24.dp)
                    .clickable(onClick = onBackClick),
                contentDescription = stringResource(R.string.back)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        )
    )
}
