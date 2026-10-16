package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class DeleteNotificationsResponse(
    @SerializedName("deletedCount")
    val deletedCount: Int? = null
)

