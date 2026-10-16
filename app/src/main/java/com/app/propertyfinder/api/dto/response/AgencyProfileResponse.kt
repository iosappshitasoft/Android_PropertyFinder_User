package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class AgencyProfileResponse(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("logo")
    val logo: String? = null,

    @SerializedName("orn")
    val orn: String? = null,

    @SerializedName("address")
    val address: AgencyAddress? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("statistics")
    val statistics: AgencyProfileStatistics? = null,

    @SerializedName("ratings")
    val ratings: AgencyRatings? = null,

    @SerializedName("nationality")
    val nationality: AgencyNationality? = null,

    @SerializedName("totalAgents")
    val totalAgents: Int? = null,

    @SerializedName("totalSuperAgents")
    val totalSuperAgents: Int? = null,

    @SerializedName("awards")
    val awards: List<AgencyAwardItem> = emptyList(),

    @SerializedName("listingsByLocation")
    val listingsByLocation: List<Any> = emptyList(),

    @SerializedName("agents")
    val agents: AgencyAgentsSection? = null,

    @SerializedName("properties")
    val properties: AgencyPropertiesSection? = null,

    @SerializedName("shareLink")
    val shareLink: String? = null
)

data class AgencyProfileStatistics(
    @SerializedName("totalActiveListings")
    val totalActiveListings: Int? = null,

    @SerializedName("totalInactiveListings")
    val totalInactiveListings: Int? = null,

    @SerializedName("totalAgents")
    val totalAgents: Int? = null,

    @SerializedName("totalSuperAgents")
    val totalSuperAgents: Int? = null,

    @SerializedName("totalRevenueSales")
    val totalRevenueSales: Int? = null,

    @SerializedName("totalRevenueRent")
    val totalRevenueRent: Int? = null,

    @SerializedName("thisMonthRevenueSales")
    val thisMonthRevenueSales: Int? = null,

    @SerializedName("thisMonthRevenueRent")
    val thisMonthRevenueRent: Int? = null,

    @SerializedName("totalLeads")
    val totalLeads: Int? = null,

    @SerializedName("thisMonthLeads")
    val thisMonthLeads: Int? = null,

    @SerializedName("totalDeals")
    val totalDeals: Int? = null,

    @SerializedName("thisMonthDeals")
    val thisMonthDeals: Int? = null,

    @SerializedName("totalSaleListings")
    val totalSaleListings: Int? = null,

    @SerializedName("totalRentListings")
    val totalRentListings: Int? = null
)

data class AgencyAwardItem(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("subtitle")
    val subtitle: String? = null,

    @SerializedName("year")
    val year: String? = null
)

data class AgencyAgentsSection(
    @SerializedName("items")
    val items: List<AgencyAgentItem> = emptyList(),

    @SerializedName("pagination")
    val pagination: AgencyPagination? = null
)

data class AgencyAgentItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null,

    @SerializedName("agentType")
    val agentType: String? = null,

    @SerializedName("specialization")
    val specialization: String? = null,

    @SerializedName("ratings")
    val ratings: AgencyAgentRatings? = null,

    @SerializedName("nationality")
    val nationality: AgencyNationality? = null,

    @SerializedName("languages")
    val languages: List<AgencyLanguageItem> = emptyList(),

    @SerializedName("statistics")
    val statistics: AgencyAgentStatistics? = null,

    @SerializedName("agency")
    val agency: AgencyAgentAgency? = null
)

data class AgencyAgentRatings(
    @SerializedName("breakdown")
    val breakdown: AgencyRatingsBreakdown? = null,

    @SerializedName("average")
    val average: Double? = null,

    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class AgencyAgentStatistics(
    @SerializedName("totalSaleProperties")
    val totalSaleProperties: Int? = null,

    @SerializedName("totalRentProperties")
    val totalRentProperties: Int? = null
)

data class AgencyLanguageItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("nativeName")
    val nativeName: String? = null
)

data class AgencyAgentAgency(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("logo")
    val logo: String? = null
)

data class AgencyPropertiesSection(
    @SerializedName("items")
    val items: List<AgencyPropertyItem> = emptyList(),

    @SerializedName("pagination")
    val pagination: AgencyPagination? = null
)

data class AgencyPropertyItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("images")
    val images: List<AgencyPropertyImageItem> = emptyList(),

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("bedrooms")
    val bedrooms: Int? = null,

    @SerializedName("bathrooms")
    val bathrooms: Int? = null,

    @SerializedName("area")
    val area: AgencyPropertyArea? = null,

    @SerializedName("location")
    val location: AgencyPropertyLocation? = null,

    @SerializedName("listingType")
    val listingType: AgencyListingType? = null,

    @SerializedName("propertyType")
    val propertyType: AgencyPropertyType? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("publishedAt")
    val publishedAt: String? = null,

    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,

    @SerializedName("featuredPriority")
    val featuredPriority: Int? = null
)

data class AgencyPropertyImageItem(
    @SerializedName("url")
    val url: String? = null,

    @SerializedName("isPrimary")
    val isPrimary: Boolean? = null,

    @SerializedName("order")
    val order: Int? = null,

    @SerializedName("caption")
    val caption: String? = null,

    @SerializedName("uploadedAt")
    val uploadedAt: String? = null,

    @SerializedName("_id")
    val id: String? = null
)

data class AgencyPropertyArea(
    @SerializedName("sqm")
    val sqm: Int? = null,

    @SerializedName("sqft")
    val sqft: Int? = null
)

data class AgencyPropertyLocation(
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
    val coordinates: AgencyCoordinates? = null
)

data class AgencyCoordinates(
    @SerializedName("type")
    val type: String? = null,

    @SerializedName("coordinates")
    val coordinates: List<Double> = emptyList()
)

data class AgencyListingType(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("slug")
    val slug: String? = null
)

data class AgencyPropertyType(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("slug")
    val slug: String? = null
)

