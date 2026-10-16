package com.app.propertyfinder.utils

import android.content.Context
import android.provider.Settings
import android.os.Build
import java.util.UUID

object DeviceInfoUtils {
    fun getDeviceId(context: Context): String {
        val androidId = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull().orEmpty()

        return if (androidId.isNotBlank()) androidId else UUID.randomUUID().toString()
    }

    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}".trim()
    }

    fun getDeviceVersion(): String {
        // "deviceVersion" in your API request: Android version string.
        return Build.VERSION.RELEASE ?: Build.VERSION.INCREMENTAL ?: ""
    }
}

