package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class AlertsResponse(
    @SerializedName("items")
    val items: List<AlertItem> = emptyList(),

    @SerializedName("pagination")
    val pagination: AlertsPagination? = null
)

data class AlertItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("alertName")
    val alertName: String? = null,

    @SerializedName("alertType")
    val alertType: String? = null,

    @SerializedName("searchCriteria")
    val searchCriteria: AlertSearchCriteria? = null,

    @SerializedName("frequency")
    val frequency: String? = null,

    @SerializedName("isActive")
    val isActive: Boolean? = null,

    @SerializedName("lastSentAt")
    val lastSentAt: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("newMatchesCount")
    val newMatchesCount: Int? = null
)

data class AlertSearchCriteria(
    @SerializedName("location")
    val location: String? = null,

    @SerializedName("propertyType")
    val propertyType: String? = null,

    @SerializedName("bedrooms")
    val bedrooms: Int? = null,

    @SerializedName("bathrooms")
    val bathrooms: Int? = null,

    @SerializedName("priceRange")
    val priceRange: AlertPriceRange? = null,

    @SerializedName("amenities")
    val amenities: List<String> = emptyList(),

    @SerializedName("furnished")
    val furnished: String? = null,

    @SerializedName("completionStatus")
    val completionStatus: String? = null,

    @SerializedName("petFriendly")
    val petFriendly: Boolean? = null,

    @SerializedName("waterfront")
    val waterfront: Boolean? = null
)

data class AlertPriceRange(
    @SerializedName("min")
    val min: Int? = null,

    @SerializedName("max")
    val max: Int? = null
)

data class AlertsPagination(
    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("totalPages")
    val totalPages: Int? = null,

    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("limit")
    val limit: Int? = null
)

