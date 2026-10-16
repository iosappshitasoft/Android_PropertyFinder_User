package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class RefreshTokenData(
    @SerializedName("user")
    val user: String? = null,

    @SerializedName("tokens")
    val tokens: Tokens? = null
)

data class Tokens(
    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null
)