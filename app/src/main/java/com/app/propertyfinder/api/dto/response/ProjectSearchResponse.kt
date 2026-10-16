package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class ProjectSearchResponse(
    @SerializedName("projects")
    val projects: List<ProjectListItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: ProjectPagination? = null
)

data class ProjectListItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("projectName")
    val projectName: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("propertyTypes")
    val propertyTypes: List<ProjectPropertyType> = emptyList(),
    @SerializedName("bedroomRange")
    val bedroomRange: ProjectBedroomRange? = null,
    @SerializedName("launchPrice")
    val launchPrice: ProjectLaunchPrice? = null,
    @SerializedName("deliveryDate")
    val deliveryDate: String? = null,
    @SerializedName("deliveryQuarter")
    val deliveryQuarter: String? = null,
    @SerializedName("publishedAt")
    val publishedAt: String? = null,
    @SerializedName("saleStarted")
    val saleStarted: String? = null,
    @SerializedName("paymentPlansCount")
    val paymentPlansCount: Int? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,
    @SerializedName("location")
    val location: ProjectLocation? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("developer")
    val developer: Developer? = null,
    @SerializedName("shareLink")
    val shareLink: String? = null
)

data class ProjectPropertyType(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null
)

data class ProjectBedroomRange(
    @SerializedName("min")
    val min: Int? = null,
    @SerializedName("max")
    val max: Int? = null
)

data class ProjectLaunchPrice(
    @SerializedName("startingFrom")
    val startingFrom: Int? = null,
    @SerializedName("currency")
    val currency: String? = null
)

data class ProjectLocation(
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("zone")
    val zone: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("coordinates")
    val coordinates: ProjectCoordinates? = null
)

data class ProjectCoordinates(
    @SerializedName("type")
    val type: String? = null,
    @SerializedName("coordinates")
    val coordinates: List<Double> = emptyList()
)

data class ProjectPagination(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    @SerializedName("totalProjects")
    val totalProjects: Int? = null,
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean? = null,
    @SerializedName("hasPrevPage")
    val hasPrevPage: Boolean? = null
)

fun ProjectListItem.toPropertyListItem(): PropertyListItem {
    return PropertyListItem(
        id = id,
        title = projectName,
        propertyType = PropertyTypeInfo(name = propertyTypes.firstOrNull()?.name),
        bedrooms = bedroomRange?.max,
        bathrooms = null,
        price = launchPrice?.startingFrom,
        currency = launchPrice?.currency,
        images = images,
        developer = developer,
        location = PropertyLocation(
            city = location?.city,
            zone = location?.zone,
            fullAddress = location?.address,
            coordinates = PropertyCoordinates(
                type = location?.coordinates?.type,
                coordinates = location?.coordinates?.coordinates ?: emptyList()
            )
        ),
        completionStatus = completionStatus,
        deliveryQuarter = deliveryQuarter,
        saleStarted = saleStarted,
        paymentPlansCount = paymentPlansCount,
        isFeatured = isFeatured,
        isVerified = isVerified,
        publishedAt = publishedAt ?: saleStarted,
        shareLink = shareLink
    )
}
