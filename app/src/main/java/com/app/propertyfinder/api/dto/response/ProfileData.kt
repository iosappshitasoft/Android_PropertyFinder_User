package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class ProfileData(
    @SerializedName("user")
    val user: User? = null,
    @SerializedName("hasUnreadNotifications")
    val hasUnreadNotifications: Boolean? = null
)

data class User(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("phoneCode")
    val phoneCode: String? = null,

    @SerializedName("phoneNumberWithoutCode")
    val phoneNumberWithoutCode: String? = null,

    @SerializedName("country")
    val country: Country? = null,

    @SerializedName("preferences")
    val preferences: Preferences? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null,

    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean? = null,

    @SerializedName("isPhoneVerified")
    val isPhoneVerified: Boolean? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("isBanned")
    val isBanned: Boolean? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("searchAlerts")
    val searchAlerts: List<Any>? = null,

    @SerializedName("contactedProperties")
    val contactedProperties: List<Any>? = null
)

data class Country(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("phoneCode")
    val phoneCode: String? = null,

    @SerializedName("flag")
    val flag: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null
)

data class Preferences(
    @SerializedName("notificationSettings")
    val notificationSettings: NotificationSettings? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("language")
    val language: String? = null
)

data class NotificationSettings(
    @SerializedName("email")
    val email: Boolean? = null,

    @SerializedName("sms")
    val sms: Boolean? = null,

    @SerializedName("push")
    val push: Boolean? = null
)