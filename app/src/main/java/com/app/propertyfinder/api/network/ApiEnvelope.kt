package com.app.propertyfinder.api.network

import com.google.gson.annotations.SerializedName

data class ApiEnvelope<T>(
    @SerializedName(value = "success", alternate = ["status"])
    val success: Boolean? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("data")
    val data: T? = null
)