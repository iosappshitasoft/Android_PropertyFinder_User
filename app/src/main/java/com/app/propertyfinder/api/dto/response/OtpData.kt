package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class SentOtpResponse(
    @SerializedName("channel")
    val channel: String? = null
)


data class VerifyOtpResponse(
    @SerializedName("user")
    val user: VerifyOtpUser? = null
)

// Separate lightweight user model for verify-otp response.
// The API returns `country` as a plain String here (not an object),
// so we cannot reuse the shared User model which has country: Country?
data class VerifyOtpUser(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("firstName")
    val firstName: String? = null,
    @SerializedName("lastName")
    val lastName: String? = null,
    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean? = null,
    @SerializedName("country")
    val country: String? = null   // API sends this as a String, not an object
)




