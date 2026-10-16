package com.app.propertyfinder.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestRequireOffset(state: SwipeToDismissBoxState) {
    val offset = try { state.requireOffset() } catch (e: Exception) { 0f }
}
