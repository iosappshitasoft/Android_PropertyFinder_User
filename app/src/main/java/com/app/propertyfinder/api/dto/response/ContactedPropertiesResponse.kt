package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Body of [com.app.propertyfinder.api.network.ApiEnvelope] for `users/contacted-properties`.
 * Matches API `data` object: `properties` + `pagination`.
 */
data class ContactedPropertiesResponse(
    @SerializedName("properties")
    val properties: List<ContactedPropertyItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: ContactedPropertiesPagination? = null
)

/**
 * Single contacted record: either a resale/lease [property] (+ optional [agent]) or a new [project].
 */
data class ContactedPropertyItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("project")
    val project: ContactedProjectSummary? = null,
    @SerializedName("property")
    val property: ContactedPropertyDetails? = null,
    @SerializedName("agent")
    val agent: ContactAgent? = null,
    @SerializedName("contactMethod")
    val contactMethod: String? = null,
    @SerializedName("contactedAt")
    val contactedAt: String? = null,
    @SerializedName("isReported")
    val isReported: Boolean? = null
)

/** Nested `project` object from contacted-properties list (subset of full project document). */
data class ContactedProjectSummary(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("projectName")
    val projectName: String? = null,
    @SerializedName("projectType")
    val projectType: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("launchPrice")
    val launchPrice: ProjectLaunchPrice? = null,
    @SerializedName("location")
    val location: ContactedProjectLocation? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("publishStatus")
    val publishStatus: String? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null
)

data class ContactedProjectLocation(
    @SerializedName("coordinates")
    val coordinates: PropertyCoordinates? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("zone")
    val zone: String? = null,
    @SerializedName("googlePlaceId")
    val googlePlaceId: String? = null
)

data class ContactedPropertyDetails(
    @SerializedName("location")
    val location: PropertyLocation? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("listingType")
    val listingType: PropertyListingType? = null,
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("furnishedStatus")
    val furnishedStatus: String? = null,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("bathrooms")
    val bathrooms: Int? = null,
    @SerializedName("area")
    val area: PropertyArea? = null
)

data class ContactAgent(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("whatsappNumber")
    val whatsappNumber: String? = null,
    @SerializedName("agency")
    val agency: String? = null
)

data class ContactedPropertiesPagination(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null
)

