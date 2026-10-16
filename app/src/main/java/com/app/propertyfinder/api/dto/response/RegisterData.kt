package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class RegisterData(
    @SerializedName("user")
    val user: UserDto? = null
)

data class UserDto(
    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("authProvider")
    val authProvider: String? = null,

    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean? = null,

    @SerializedName("isPhoneVerified")
    val isPhoneVerified: Boolean? = null,

    @SerializedName("recentSearches")
    val recentSearches: List<Any>? = null,

    @SerializedName("preferences")
    val preferences: PreferencesDto? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("isBanned")
    val isBanned: Boolean? = null,

    @SerializedName("loginAttempts")
    val loginAttempts: Int? = null,

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("savedProperties")
    val savedProperties: List<Any>? = null,

    @SerializedName("searchAlerts")
    val searchAlerts: List<Any>? = null,

    @SerializedName("contactedProperties")
    val contactedProperties: List<Any>? = null,

    @SerializedName("searchHistory")
    val searchHistory: List<Any>? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("__v")
    val version: Int? = null
)

data class PreferencesDto(
    @SerializedName("notificationSettings")
    val notificationSettings: NotificationSettingsDto? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("language")
    val language: String? = null,

    @SerializedName("savedSearches")
    val savedSearches: Boolean? = null
)

data class NotificationSettingsDto(
    @SerializedName("email")
    val email: Boolean? = null,

    @SerializedName("sms")
    val sms: Boolean? = null,

    @SerializedName("push")
    val push: Boolean? = null
)