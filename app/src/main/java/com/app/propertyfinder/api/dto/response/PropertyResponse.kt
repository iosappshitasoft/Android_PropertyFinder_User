package com.app.propertyfinder.api.dto.response
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type


data class PropertyResponse(
    @SerializedName("properties")
    val properties: List<PropertyListItem> = emptyList(),

    @SerializedName("pagination")
    val pagination: PropertyPagination? = null,

    @SerializedName("appliedFilters")
    val appliedFilters: PropertyAppliedFilters? = null
)

data class PropertyListItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("listingType")
    val listingType: PropertyListingType? = null,

    @SerializedName("propertyType")
    val propertyType: PropertyTypeInfo? = null,

    @SerializedName("developer")
    val developer: Developer? = null,

    @SerializedName("agent")
    val agent: PropertyAgent? = null,

    @SerializedName("agency")
    val agency: PropertyAgency? = null,

    @SerializedName("bedrooms")
    val bedrooms: Int? = null,

    @SerializedName("maidBedroom")
    val maidBedroom: Boolean? = null,

    @SerializedName("bathrooms")
    val bathrooms: Int? = null,

    @SerializedName("area")
    val area: PropertyArea? = null,

    @SerializedName("amenities")
    val amenities: List<PropertyAmenity> = emptyList(),

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("serviceCharges")
    val serviceCharges: Int? = null,

    @SerializedName("maintenanceFees")
    val maintenanceFees: Int? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("rentPricing")
    val rentPricing: PropertyRentPricing? = null,

    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),

    @SerializedName("virtualTour360")
    val virtualTour360: String? = null,

    @SerializedName("videoTour")
    val videoTour: String? = null,

    @SerializedName("floorPlan")
    val floorPlan: List<String> = emptyList(),

    @SerializedName("location")
    val location: PropertyLocation? = null,

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

    @SerializedName("deliveryQuarter")
    val deliveryQuarter: String? = null,

    @SerializedName("saleStarted")
    val saleStarted: String? = null,

    @SerializedName("paymentPlansCount")
    val paymentPlansCount: Int? = null,

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

    @SerializedName("featured")
    val featured: PropertyFeatured? = null,

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

    @SerializedName("isSaved")
    val isSaved: Boolean? = null,

    @SerializedName("shareLink")
    val shareLink: String? = null,

    @SerializedName("imagesCount")
    val imagesCount: Int? = null,

    @SerializedName("lastModifiedBy")
    val lastModifiedBy: String? = null,

    @SerializedName("lastContactedAt")
    val lastContactedAt: String? = null,

    @SerializedName("__v")
    val version: Int? = null
)

data class Developer(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("logo")
    val logo: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
)
data class PropertyListingType(
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

data class PropertyTypeInfo(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null
)

data class PropertyAgent(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("agentType")
    val agentType: String? = null,

    @SerializedName("ratings")
    val ratings: PropertyRatings? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null
)

data class PropertyAgency(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("agencyName")
    val agencyName: String? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null
)

data class PropertyRatings(
    @SerializedName("average")
    @JsonAdapter(FlexibleIntAdapter::class)
    val average: Int? = null,

    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class PropertyArea(
    @SerializedName("sqm")
    @JsonAdapter(FlexibleIntAdapter::class)
    val sqm: Int? = null,

    @SerializedName("sqft")
    @JsonAdapter(FlexibleIntAdapter::class)
    val sqft: Int? = null
)

data class PropertyAmenity(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null
)

data class PropertyImageItem(
    @SerializedName("url")
    val url: String? = null,

    @SerializedName("isPrimary")
    val isPrimary: Boolean? = null,

    @SerializedName("order")
    val order: Int? = null,

    @SerializedName("caption")
    val caption: String? = null,

    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("uploadedAt")
    val uploadedAt: String? = null
)

data class PropertyLocation(
    @SerializedName("fullAddress")
    val fullAddress: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("zone")
    val zone: String? = null,

    @SerializedName("building")
    val building: String? = null,

    @SerializedName("googlePlaceId")
    val googlePlaceId: String? = null,

    @SerializedName("coordinates")
    val coordinates: PropertyCoordinates? = null
)

data class PropertyCoordinates(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("coordinates")
    val coordinates: List<Double> = emptyList()
)

data class PropertyFeatured(
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,

    @SerializedName("priority")
    val priority: Int? = null
)

data class PropertyRentPricing(
    @SerializedName("yearly")
    val yearly: Int? = null,

    @SerializedName("monthly")
    val monthly: Int? = null
)

data class PropertyPagination(
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

data class PropertyAppliedPriceRange(
    @SerializedName("min")
    val min: Int? = null,

    @SerializedName("max")
    val max: Int? = null
)

data class PropertyAppliedFilters(
    @SerializedName("listingType")
    val listingType: String? = null,

    @SerializedName("priceRange")
    val priceRange: PropertyAppliedPriceRange? = null,

    @SerializedName("sortBy")
    val sortBy: String? = null
)

/**
 * Handles APIs that sometimes send integer fields as decimal/string values
 * (e.g. `269.1` for area) so deserialization does not fail.
 */
class FlexibleIntAdapter : JsonDeserializer<Int?> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Int? {
        if (json == null || json.isJsonNull) return null
        val primitive = json.asJsonPrimitive

        return when {
            primitive.isNumber -> primitive.asDouble.toInt()
            primitive.isString -> primitive.asString.trim().toDoubleOrNull()?.toInt()
            else -> null
        }
    }
}
