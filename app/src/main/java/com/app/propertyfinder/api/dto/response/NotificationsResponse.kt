package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class NotificationsResponse(
    @SerializedName("notifications")
    val notifications: List<NotificationItem> = emptyList(),

    @SerializedName("unreadCount")
    val unreadCount: Int = 0,

    @SerializedName("pagination")
    val pagination: NotificationsPagination? = null
)

data class NotificationItem(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("notificationType")
    val notificationType: String? = null,

    @SerializedName("priority")
    val priority: String? = null,

    @SerializedName("isRead")
    val isRead: Boolean? = null,

    @SerializedName("readAt")
    val readAt: String? = null,

    @SerializedName("actionUrl")
    val actionUrl: String? = null,

    @SerializedName("actionText")
    val actionText: String? = null,

    @SerializedName("metadata")
    val metadata: NotificationMetadata? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("relatedItem")
    val relatedItem: NotificationRelatedItem? = null
)

data class NotificationRelatedItem(
    @SerializedName("itemType")
    val itemType: String? = null,

    @SerializedName("itemId")
    val itemId: String? = null
)

data class NotificationMetadata(
    @SerializedName("alertId")
    val alertId: String? = null,

    @SerializedName("frequency")
    val frequency: String? = null,

    @SerializedName("matchesCount")
    val matchesCount: Int? = null,

    @SerializedName("since")
    val since: String? = null,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("propertyId")
    val propertyId: String? = null,

    @SerializedName("propertyName")
    val propertyName: String? = null
)

data class NotificationsPagination(
    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("limit")
    val limit: Int? = null,

    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("pages")
    val pages: Int? = null
)

