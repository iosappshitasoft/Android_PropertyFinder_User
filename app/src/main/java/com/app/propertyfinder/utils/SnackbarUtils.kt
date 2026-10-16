package com.app.propertyfinder.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.ui.theme.ErrorColor
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.SuccessColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class SnackbarType {
    SUCCESS,
    ERROR,
    INFO
}

@Composable
fun rememberSnackbarManager(): SnackbarManager {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    return remember {
        SnackbarManager(
            snackbarHostState = snackbarHostState,
            coroutineScope = coroutineScope
        )
    }
}

class SnackbarManager(val snackbarHostState: SnackbarHostState, private val coroutineScope: CoroutineScope) {

    private data class CustomSnackbarVisuals(
        override val message: String,
        val type: SnackbarType,
        override val actionLabel: String? = null,
        override val withDismissAction: Boolean = false,
        override val duration: SnackbarDuration = SnackbarDuration.Short
    ) : SnackbarVisuals

    fun showMessage(message: String, type: SnackbarType = SnackbarType.INFO) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                CustomSnackbarVisuals(
                    message = message,
                    type = type
                )
            )
        }
    }

    fun showSuccess(message: String) {
        showMessage(message, SnackbarType.SUCCESS)
    }

    fun showError(message: String) {
        showMessage(message, SnackbarType.ERROR)
    }

    fun showInfo(message: String) {
        showMessage(message, SnackbarType.INFO)
    }

    fun clearSnackbar() {
        snackbarHostState.currentSnackbarData?.dismiss()
    }

    private fun getContainerColor(type: SnackbarType): Color {
        return when (type) {
            SnackbarType.SUCCESS -> SuccessColor
            SnackbarType.ERROR -> ErrorColor
            SnackbarType.INFO -> PrimaryColor
        }
    }

    @Composable
    fun CustomSnackbarHost(modifier: Modifier = Modifier) {
        SnackbarHost(hostState = snackbarHostState,
            modifier = modifier.fillMaxWidth().navigationBarsPadding()) { snackbarData: SnackbarData ->

            val visuals = snackbarData.visuals as? CustomSnackbarVisuals
            val type = visuals?.type ?: SnackbarType.INFO

            Snackbar(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                containerColor = getContainerColor(type),
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    when (type) {
                        SnackbarType.SUCCESS -> {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        SnackbarType.ERROR -> {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        SnackbarType.INFO -> {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = snackbarData.visuals.message,
                        color = Color.White,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}