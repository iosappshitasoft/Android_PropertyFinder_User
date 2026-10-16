package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class LoginData(
    @SerializedName("user")
    val user: UserDto? = null,

    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("tokenType")
    val tokenType: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Long? = null,

    @SerializedName("tokens")
    val tokens: TokensDto? = null
) {
    fun resolvedAccessToken(): String? = accessToken ?: tokens?.accessToken
    fun resolvedRefreshToken(): String? = refreshToken ?: tokens?.refreshToken
}

data class TokensDto(
    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("tokenType")
    val tokenType: String? = null,

    @SerializedName("expiresIn")
    val expiresIn: Long? = null
)