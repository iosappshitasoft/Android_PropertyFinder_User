package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class MarkNotificationReadResponse(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("isRead")
    val isRead: Boolean? = null,

    @SerializedName("readAt")
    val readAt: String? = null
)

