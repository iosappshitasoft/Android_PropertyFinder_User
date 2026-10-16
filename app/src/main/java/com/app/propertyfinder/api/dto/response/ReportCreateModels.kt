package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class ReportCreateRequest(
    @SerializedName("reportType")
    val reportType: String? = null,
    @SerializedName("reportedItemId")
    val reportedItemId: String? = null,
    @SerializedName("userType")
    val userType: String? = null,
    @SerializedName("reason")
    val reason: String? = null,
    @SerializedName("email")
    val email: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("attachments")
    val attachments: List<String>
)

data class ReportCreateResponse(
    @SerializedName("reportId")
    val reportId: String? = null
)

