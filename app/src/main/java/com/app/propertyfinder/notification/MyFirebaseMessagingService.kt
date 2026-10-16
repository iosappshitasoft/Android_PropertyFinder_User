package com.app.propertyfinder.notification

import android.util.Log
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterRequest
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.repository.ApiRepository
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.DeviceInfoUtils
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.PushNotificationUtils
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@dagger.hilt.android.AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var apiRepository: ApiRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerDeviceForPush(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_MESSAGE", "From: ${message.from}")

        message.notification?.let {
            Log.d("FCM_MESSAGE", "Notification title=${it.title} body=${it.body}")
        }

        if (message.data.isNotEmpty()) {
            Log.d("FCM_MESSAGE", "Data: ${message.data}")
        }

        // Show notification to user (especially for data-only messages).
        val inForeground =
            ProcessLifecycleOwner.get().lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

        val title =
            message.notification?.title
                ?: message.data["title"]
                ?: message.data["subject"]
                ?: ""
        val body =
            message.notification?.body
                ?: message.data["body"]
                ?: message.data["message"]
                ?: ""

        val notificationPayloadOnly = message.notification != null && message.data.isEmpty()
        if (!notificationPayloadOnly || inForeground) {
            PushNotificationUtils.showNotification(applicationContext, title, body)
        }
    }

    private fun registerDeviceForPush(fcmToken: String) {
        val isLoggedIn =
            PrefUtils.getString(Constants.TAG_LOGIN, "") == Constants.LOGIN_USER
        val bearerToken = TokenManager.accessToken.takeIf { it.isNotBlank() }
            ?: PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").orEmpty()

        if (!isLoggedIn || bearerToken.isBlank() || fcmToken.isBlank()) return

        val bearerHeader = "Bearer $bearerToken"

        val request = PushNotificationRegisterRequest(
            token = fcmToken,
            deviceId = DeviceInfoUtils.getDeviceId(applicationContext),
            deviceModel = DeviceInfoUtils.getDeviceModel(),
            deviceVersion = DeviceInfoUtils.getDeviceVersion()
        )

        CoroutineScope(Dispatchers.IO).launch {
            when (val result = apiRepository.registerPushNotification(bearerHeader, request)) {
                is ApiResult.Success -> Log.d("FCM_REGISTER", "Register push success")
                is ApiResult.Error -> Log.e(
                    "FCM_REGISTER",
                    "Register push failed: ${result.message}"
                )
            }
        }
    }


}