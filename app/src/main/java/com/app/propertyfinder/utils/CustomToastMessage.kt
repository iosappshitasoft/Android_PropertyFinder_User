package com.app.propertyfinder.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.app.propertyfinder.ui.theme.ErrorColor
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.SuccessColor

object CustomToastMessage {

    fun showSuccess(context: Context, message: String) {
        show(context, message, SnackbarType.SUCCESS)
    }

    fun showError(context: Context, message: String) {
        show(context, message, SnackbarType.ERROR)
    }

    fun showInfo(context: Context, message: String) {
        show(context, message, SnackbarType.INFO)
    }

    private fun show(context: Context, message: String, type: SnackbarType) {
        val containerColor = when (type) {
            SnackbarType.SUCCESS -> SuccessColor
            SnackbarType.ERROR -> ErrorColor
            SnackbarType.INFO -> PrimaryColor
        }

        val iconVector = when (type) {
            SnackbarType.SUCCESS -> Icons.Default.CheckCircle
            SnackbarType.ERROR -> Icons.Default.Error
            SnackbarType.INFO -> Icons.Default.Info
        }

        val composeView = ComposeView(context).apply {
            setContent {
                Surface(
                    color = containerColor,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = type.name,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = message,
                            color = Color.White,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                }
            }
        }

        // Attach Lifecycle owners to avoid crashes in Compose components inside Toast
        val activity = context as? androidx.activity.ComponentActivity
        if (activity != null) {
            activity.window.decorView.let { decor ->
                decor.findViewTreeLifecycleOwner()?.let { composeView.setViewTreeLifecycleOwner(it) }
                decor.findViewTreeLifecycleOwner()?.let { 
                    val registryOwner = it as? androidx.savedstate.SavedStateRegistryOwner
                    if (registryOwner != null) {
                        composeView.setViewTreeSavedStateRegistryOwner(registryOwner)
                    }
                }
                val viewModelStoreOwner = activity as? androidx.lifecycle.ViewModelStoreOwner
                if (viewModelStoreOwner != null) {
                    composeView.setViewTreeViewModelStoreOwner(viewModelStoreOwner)
                }
            }
        }

        Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            view = composeView
            show()
        }
    }
}
