package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class UploadProfilePictureResponse(
    @SerializedName("baseUrl")
    val baseUrl: String? = null,
    @SerializedName("uploads")
    val uploads: ProfilePictureUploads? = null,
    @SerializedName("baseUrls")
    val baseUrls: ProfilePictureBaseUrls? = null,
    @SerializedName("user")
    val user: UploadProfilePictureUser? = null
)

data class ProfilePictureUploads(
    @SerializedName("images")
    val images: List<UploadedImageItem> = emptyList(),
    @SerializedName("videos")
    val videos: List<Any> = emptyList(),
    @SerializedName("documents")
    val documents: List<Any> = emptyList()
)

data class UploadedImageItem(
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("path")
    val path: String? = null,
    @SerializedName("filename")
    val filename: String? = null,
    @SerializedName("size")
    val size: Int? = null,
    @SerializedName("mimetype")
    val mimetype: String? = null
)

data class ProfilePictureBaseUrls(
    @SerializedName("images")
    val images: String? = null
)

data class UploadProfilePictureUser(
    @SerializedName("preferences")
    val preferences: UploadPreferences? = null,
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
    @SerializedName("authProvider")
    val authProvider: String? = null,
    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean? = null,
    @SerializedName("isPhoneVerified")
    val isPhoneVerified: Boolean? = null,
    @SerializedName("recentSearches")
    val recentSearches: List<Any> = emptyList(),
    @SerializedName("isActive")
    val isActive: Boolean? = null,
    @SerializedName("isBanned")
    val isBanned: Boolean? = null,
    @SerializedName("loginAttempts")
    val loginAttempts: Int? = null,
    @SerializedName("savedProperties")
    val savedProperties: List<UserSavedPropertyItem> = emptyList(),
    @SerializedName("searchAlerts")
    val searchAlerts: List<UserSearchAlertItem> = emptyList(),
    @SerializedName("contactedProperties")
    val contactedProperties: List<UserContactedPropertyItem> = emptyList(),
    @SerializedName("searchHistory")
    val searchHistory: List<Any> = emptyList(),
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("__v")
    val version: Int? = null,
    @SerializedName("lastLogin")
    val lastLogin: String? = null,
    @SerializedName("lastActiveAt")
    val lastActiveAt: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("country")
    val country: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null
)

data class UploadPreferences(
    @SerializedName("notificationSettings")
    val notificationSettings: UploadNotificationSettings? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("language")
    val language: String? = null,
    @SerializedName("savedSearches")
    val savedSearches: Boolean? = null
)

data class UploadNotificationSettings(
    @SerializedName("email")
    val email: Boolean? = null,
    @SerializedName("sms")
    val sms: Boolean? = null,
    @SerializedName("push")
    val push: Boolean? = null
)

data class UserSavedPropertyItem(
    @SerializedName("property")
    val property: String? = null,
    @SerializedName("savedAt")
    val savedAt: String? = null,
    @SerializedName("_id")
    val id: String? = null
)

data class UserSearchAlertItem(
    @SerializedName("searchCriteria")
    val searchCriteria: UserSearchCriteria? = null,
    @SerializedName("alertName")
    val alertName: String? = null,
    @SerializedName("alertType")
    val alertType: String? = null,
    @SerializedName("frequency")
    val frequency: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean? = null,
    @SerializedName("lastSentAt")
    val lastSentAt: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("_id")
    val id: String? = null
)

data class UserSearchCriteria(
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("amenities")
    val amenities: List<Any> = emptyList()
)

data class UserContactedPropertyItem(
    @SerializedName("property")
    val property: String? = null,
    @SerializedName("agent")
    val agent: String? = null,
    @SerializedName("contactMethod")
    val contactMethod: String? = null,
    @SerializedName("contactedAt")
    val contactedAt: String? = null,
    @SerializedName("isReported")
    val isReported: Boolean? = null,
    @SerializedName("_id")
    val id: String? = null
)
