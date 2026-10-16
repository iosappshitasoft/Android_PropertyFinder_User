package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class PushNotificationRegisterRequest(
    @SerializedName("token")
    val token: String,
    @SerializedName("platform")
    val platform: String = "android",
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("deviceModel")
    val deviceModel: String,
    @SerializedName("deviceVersion")
    val deviceVersion: String
)

data class PushNotificationRegisterResponse(
    @SerializedName("message")
    val message: String? = null
)

