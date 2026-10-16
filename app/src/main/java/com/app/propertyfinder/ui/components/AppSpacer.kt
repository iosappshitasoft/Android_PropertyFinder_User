package com.app.propertyfinder.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppSpacerV(height: Int) {
    Spacer(modifier = Modifier.height(height.dp))
}

@Composable
fun AppSpacerH(width: Int) {
    Spacer(modifier = Modifier.width(width.dp))
}

@Composable
fun ColumnScope.AppSpacerWeight(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}

@Composable
fun RowScope.AppSpacerWeight(weight: Float = 1f) {
    Spacer(modifier = Modifier.weight(weight))
}
