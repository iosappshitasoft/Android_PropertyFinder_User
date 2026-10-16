package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class AgentSearchResponse(
    @SerializedName("items")
    val items: List<AgentSearchItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: AgentPagination? = null
)

data class AgentSearchItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("agentType")
    val agentType: String? = null,
    @SerializedName("position")
    val position: String? = null,
    @SerializedName("ratings")
    val ratings: AgentRatings? = null,
    @SerializedName("nationality")
    val nationality: AgentNationality? = null,
    @SerializedName("languages")
    val languages: List<AgentLanguage> = emptyList(),
    @SerializedName("responseTime")
    val responseTime: String? = null,
    @SerializedName("statistics")
    val statistics: AgentStatistics? = null,
    @SerializedName("agency")
    val agency: AgentAgency? = null,
    @SerializedName("specialization")
    val specialization: AgentSpecialization? = null
)

data class AgentProfileResponse(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null,
    @SerializedName("agentType")
    val agentType: String? = null,
    @SerializedName("position")
    val position: String? = null,
    @SerializedName("specialization")
    val specialization: AgentSpecialization? = null,
    @SerializedName("experience")
    val experience: String? = null,
    @SerializedName("brokerLicenseNumber")
    val brokerLicenseNumber: String? = null,
    @SerializedName("nationality")
    val nationality: AgentNationality? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("socialLinks")
    val socialLinks: AgentSocialLinks? = null,
    @SerializedName("ratings")
    val ratings: AgentRatings? = null,
    @SerializedName("responseTime")
    val responseTime: String? = null,
    @SerializedName("languages")
    val languages: List<AgentLanguage> = emptyList(),
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("whatsappNumber")
    val whatsappNumber: String? = null,
    @SerializedName("statistics")
    val statistics: AgentProfileStatistics? = null,
    @SerializedName("awards")
    val awards: List<String> = emptyList(),
    @SerializedName("trackRecords")
    val trackRecords: List<AgentTrackRecordItem> = emptyList(),
    @SerializedName("expertiseAreas")
    val expertiseAreas: List<AgentExpertiseArea> = emptyList(),
    @SerializedName("agency")
    val agency: AgentAgency? = null,
    @SerializedName("properties")
    val properties: AgentProfileProperties? = null
)

data class AgentSpecialization(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("description")
    val description: String? = null
)

data class AgentSocialLinks(
    @SerializedName("linkedin")
    val linkedin: String? = null,
    @SerializedName("facebook")
    val facebook: String? = null,
    @SerializedName("twitter")
    val twitter: String? = null,
    @SerializedName("instagram")
    val instagram: String? = null
)

data class AgentPagination(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null
)

data class AgentRatings(
    @SerializedName("average")
    val average: Double? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class AgentNationality(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("flag")
    val flag: String? = null
)

data class AgentLanguage(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("nativeName")
    val nativeName: String? = null
)

data class AgentStatistics(
    @SerializedName("totalSaleProperties")
    val totalSaleProperties: Int? = null,
    @SerializedName("totalRentProperties")
    val totalRentProperties: Int? = null
)

data class AgentProfileStatistics(
    @SerializedName("totalRevenueSales")
    val totalRevenueSales: Long? = null,
    @SerializedName("totalRevenueRent")
    val totalRevenueRent: Long? = null,
    @SerializedName("activeListings")
    val activeListings: Int? = null,
    @SerializedName("totalListings")
    val totalListings: Int? = null,
    @SerializedName("totalRentProperties")
    val totalRentProperties: Int? = null,
    @SerializedName("totalSaleProperties")
    val totalSaleProperties: Int? = null,
    @SerializedName("totalInquiries")
    val totalInquiries: Int? = null,
    @SerializedName("newInquiries")
    val newInquiries: Int? = null,
    @SerializedName("dealsClosedSales")
    val dealsClosedSales: Int? = null,
    @SerializedName("dealsClosedRent")
    val dealsClosedRent: Int? = null,
    @SerializedName("totalDeals")
    val totalDeals: Int? = null,
    @SerializedName("totalDealsClosed")
    val totalDealsClosed: Int? = null,
    @SerializedName("totalRevenue")
    val totalRevenue: Long? = null
)

data class AgentAgency(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("logo")
    val logo: String? = null,
    @SerializedName("description")
    val description: String? = null
)

data class AgentTrackRecordPropertyType(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null
)

data class AgentTrackRecordItem(
    @SerializedName("dealClosedDate")
    val dealClosedDate: String? = null,
    @SerializedName("dealType")
    val dealType: String? = null,
    @SerializedName("dealAmount")
    val dealAmount: Double? = null,
    @SerializedName("dealCategory")
    val dealCategory: String? = null,
    @SerializedName("propertyName")
    val propertyName: String? = null,
    @SerializedName("locationArea")
    val locationArea: String? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("propertyType")
    val propertyType: AgentTrackRecordPropertyType? = null
)

data class AgentExpertiseArea(
    @SerializedName("locationName")
    val locationName: String? = null,
    @SerializedName("saleDeals")
    val saleDeals: Int? = null,
    @SerializedName("rentDeals")
    val rentDeals: Int? = null,
    @SerializedName("totalDeals")
    val totalDeals: Int? = null,
    @SerializedName("totalRevenue")
    val totalRevenue: Long? = null,
    @SerializedName("listingImage")
    val listingImage: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("averageRating")
    val averageRating: Double? = null,
    @SerializedName("ratingsCount")
    val ratingsCount: Int? = null
)

data class AgentProfileProperties(
    @SerializedName("items")
    val items: List<PropertyListItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: AgentPagination? = null
)
