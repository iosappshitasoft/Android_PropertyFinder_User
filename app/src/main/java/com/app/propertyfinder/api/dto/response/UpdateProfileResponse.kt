package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("firstName")
    val firstName: String,
    @SerializedName("lastName")
    val lastName: String,
    @SerializedName("phoneNumber")
    val phoneNumber: String,
    @SerializedName("countryId")
    val countryId: String,
    @SerializedName("phoneCode")
    val phoneCode: String,
    @SerializedName("phoneNumberWithoutCode")
    val phoneNumberWithoutCode: String
)

data class UpdateProfileResponse(
    @SerializedName("user")
    val user: User? = null
)
