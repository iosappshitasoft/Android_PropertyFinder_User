package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import com.app.propertyfinder.ui.components.AppSwitch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.TextSecondary

import com.app.propertyfinder.utils.PrefUtils
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.app.propertyfinder.ui.components.AppDivider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State for system permission
    var areSystemNotificationsEnabled by remember { mutableStateOf(areNotificationsEnabled(context)) }

    // Refresh permission state on resume
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                areSystemNotificationsEnabled = areNotificationsEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    var prefPushGeneral by remember { mutableStateOf(PrefUtils.getBoolean("PUSH_NOTIF_GENERAL", true)) }
    var emailNotifications by remember { mutableStateOf(PrefUtils.getBoolean("EMAIL_NOTIF_GENERAL", true)) }
    var alertEmail by remember { mutableStateOf(PrefUtils.getBoolean("EMAIL_NOTIF_ALERTS", true)) }
    var prefPushAlerts by remember { mutableStateOf(PrefUtils.getBoolean("PUSH_NOTIF_ALERTS", true)) }
    
    // Effective UI State: Toggle is ON only if Pref is TRUE AND Permission is GRANTED
    val pushNotificationsUi = prefPushGeneral && areSystemNotificationsEnabled
    val alertPushUi = prefPushAlerts && areSystemNotificationsEnabled

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = "Set Notification", fontSize = 18.sp, color = MolumulkCharcoal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            
            // General Notifications
            Surface(
                shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
                color = White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    NotificationSwitchItem(
                        title = "Push notifications", 
                        checked = pushNotificationsUi, 
                        onCheckedChange = { isChecked ->
                            prefPushGeneral = isChecked
                            PrefUtils.save("PUSH_NOTIF_GENERAL", isChecked)
                            if (isChecked) openNotificationSettings(context)
                        }
                    )
                    AppSpacerV(16)
                    AppDivider(color = MolumulkBorderDivider)
                    AppSpacerV(16)
                    NotificationSwitchItem(
                        title = "Email notification", 
                        checked = emailNotifications, 
                        onCheckedChange = { 
                            emailNotifications = it 
                            PrefUtils.save("EMAIL_NOTIF_GENERAL", it)
                        }
                    )
                }
            }

            AppSpacerV(24)

            // Saved Alerts
            AppTextRegular(text = "Saved Alerts", color = MolumulkMutedSlate, fontSize = 12.sp)
            AppSpacerV(8)

            Surface(
                shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
                color = White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 16.dp)) {
                    NotificationSwitchItem(
                        title = "Email", 
                        checked = alertEmail, 
                        onCheckedChange = { 
                            alertEmail = it
                            PrefUtils.save("EMAIL_NOTIF_ALERTS", it)
                        }
                    )
                    AppSpacerV(16)
                    AppDivider(color = MolumulkBorderDivider)
                    AppSpacerV(16)
                    NotificationSwitchItem(
                        title = "Push notifications", 
                        checked = alertPushUi,
                        onCheckedChange = { isChecked ->
                            prefPushAlerts = isChecked
                            PrefUtils.save("PUSH_NOTIF_ALERTS", isChecked)
                            if (isChecked) openNotificationSettings(context)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationSwitchItem(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextMedium(text = title, fontSize = 14.sp, color = MolumulkCharcoal)
        AppSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

fun areNotificationsEnabled(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun openNotificationSettings(context: Context) {
    try {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
         // Fallback to general settings
         val intent = Intent(Settings.ACTION_SETTINGS)
         context.startActivity(intent)
    }
}
