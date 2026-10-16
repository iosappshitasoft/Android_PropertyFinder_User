package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class SavedPropertiesResponse(
    @SerializedName("properties")
    val properties: List<SavedPropertyListItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: SavedPropertiesPagination? = null
)

data class SavedPropertyListItem(
    @SerializedName("area")
    val area: PropertyArea? = null,
    @SerializedName("location")
    val location: PropertyLocation? = null,
    @SerializedName("featured")
    val featured: PropertyFeatured? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("listingType")
    val listingType: SavedPropertyListingType? = null,
    @SerializedName("propertyType")
    val propertyType: SavedPropertyTypeInfo? = null,
    @SerializedName("agent")
    val agent: SavedPropertyAgent? = null,
    @SerializedName("agency")
    val agency: SavedPropertyAgency? = null,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("maidBedroom")
    val maidBedroom: Boolean? = null,
    @SerializedName("bathrooms")
    val bathrooms: Int? = null,
    @SerializedName("amenities")
    val amenities: List<SavedPropertyAmenity> = emptyList(),
    @SerializedName("price")
    val price: Int? = null,
    @SerializedName("serviceCharges")
    val serviceCharges: Int? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("virtualTour360")
    val virtualTour360: String? = null,
    @SerializedName("videoTour")
    val videoTour: String? = null,
    @SerializedName("floorPlan")
    val floorPlan: List<String> = emptyList(),
    @SerializedName("dldPermitNumber")
    val dldPermitNumber: String? = null,
    @SerializedName("dldPermitUrl")
    val dldPermitUrl: String? = null,
    @SerializedName("referenceId")
    val referenceId: String? = null,
    @SerializedName("isActive")
    val isActive: Boolean? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("furnishedStatus")
    val furnishedStatus: String? = null,
    @SerializedName("isPetFriendly")
    val isPetFriendly: Boolean? = null,
    @SerializedName("isWaterfront")
    val isWaterfront: Boolean? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean? = null,
    @SerializedName("isSuperagentListing")
    val isSuperagentListing: Boolean? = null,
    @SerializedName("views")
    val views: Int? = null,
    @SerializedName("likes")
    val likes: Int? = null,
    @SerializedName("inquiries")
    val inquiries: Int? = null,
    @SerializedName("shares")
    val shares: Int? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("metaTitle")
    val metaTitle: String? = null,
    @SerializedName("metaDescription")
    val metaDescription: String? = null,
    @SerializedName("metaKeywords")
    val metaKeywords: List<String> = emptyList(),
    @SerializedName("publishedAt")
    val publishedAt: String? = null,
    @SerializedName("lastModifiedAt")
    val lastModifiedAt: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    @SerializedName("__v")
    val version: Int? = null,
    @SerializedName("isSaved")
    val isSaved: Boolean? = null,
    @SerializedName("savedAt")
    val savedAt: String? = null
) {
    fun toPropertyListItem(): PropertyListItem {
        return PropertyListItem(
            id = id,
            title = title,
            description = description,
            listingType = listingType?.toPropertyListingType(),
            propertyType = propertyType?.toPropertyTypeInfo(),
            agent = agent?.toPropertyAgent(),
            agency = agency?.toPropertyAgency(),
            bedrooms = bedrooms,
            maidBedroom = maidBedroom,
            bathrooms = bathrooms,
            area = area,
            amenities = amenities.map { it.toPropertyAmenity() },
            price = price,
            serviceCharges = serviceCharges,
            currency = currency,
            images = images,
            virtualTour360 = virtualTour360,
            videoTour = videoTour,
            floorPlan = floorPlan,
            location = location,
            dldPermitNumber = dldPermitNumber,
            dldPermitUrl = dldPermitUrl,
            referenceId = referenceId,
            isActive = isActive,
            status = status,
            completionStatus = completionStatus,
            furnishedStatus = furnishedStatus,
            isPetFriendly = isPetFriendly,
            isWaterfront = isWaterfront,
            isFeatured = isFeatured,
            isVerified = isVerified,
            isSuperagentListing = isSuperagentListing,
            featured = featured,
            views = views,
            likes = likes,
            inquiries = inquiries,
            shares = shares,
            slug = slug,
            metaTitle = metaTitle,
            metaDescription = metaDescription,
            metaKeywords = metaKeywords,
            publishedAt = publishedAt,
            lastModifiedAt = lastModifiedAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isSaved = isSaved ?: true,
            version = version
        )
    }
}

data class SavedPropertyListingType(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("transaction")
    val transaction: String? = null,
    @SerializedName("category")
    val category: String? = null
)

data class SavedPropertyTypeInfo(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null
)

data class SavedPropertyAgent(
    @SerializedName("ratings")
    val ratings: SavedPropertyRatings? = null,
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("fullName")
    val fullName: String? = null,
    @SerializedName("agentType")
    val agentType: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null
)

data class SavedPropertyAgency(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("agencyName")
    val agencyName: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null
)

data class SavedPropertyAmenity(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null
)

data class SavedPropertyRatings(
    @SerializedName("average")
    val average: Int? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class SavedPropertiesPagination(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    @SerializedName("totalProperties")
    val totalProperties: Int? = null,
    @SerializedName("hasNextPage")
    val hasNextPage: Boolean? = null,
    @SerializedName("hasPrevPage")
    val hasPrevPage: Boolean? = null
)

private fun SavedPropertyListingType.toPropertyListingType(): PropertyListingType {
    return PropertyListingType(
        id = id,
        name = name,
        slug = slug,
        transaction = transaction,
        category = category
    )
}

private fun SavedPropertyTypeInfo.toPropertyTypeInfo(): PropertyTypeInfo {
    return PropertyTypeInfo(
        id = id,
        name = name
    )
}

private fun SavedPropertyAgent.toPropertyAgent(): PropertyAgent {
    return PropertyAgent(
        id = id,
        fullName = fullName,
        agentType = agentType,
        ratings = ratings?.toPropertyRatings(),
        profilePicture = profilePicture
    )
}

private fun SavedPropertyAgency.toPropertyAgency(): PropertyAgency {
    return PropertyAgency(
        id = id,
        agencyName = agencyName,
        profilePicture = profilePicture
    )
}

private fun SavedPropertyAmenity.toPropertyAmenity(): PropertyAmenity {
    return PropertyAmenity(
        id = id,
        name = name
    )
}

private fun SavedPropertyRatings.toPropertyRatings(): PropertyRatings {
    return PropertyRatings(
        average = average,
        totalCount = totalCount
    )
}

