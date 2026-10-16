package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class AgencySearchResponse(
    @SerializedName("items")
    val items: List<AgencySearchItem> = emptyList(),

    @SerializedName("pagination")
    val pagination: AgencyPagination? = null
)

data class AgencySearchItem(
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

    @SerializedName("statistics")
    val statistics: AgencyStatistics? = null,

    @SerializedName("totalAgents")
    val totalAgents: Int? = null,

    @SerializedName("totalSuperAgents")
    val totalSuperAgents: Int? = null,

    @SerializedName("ratings")
    val ratings: AgencyRatings? = null,

    @SerializedName("nationality")
    val nationality: AgencyNationality? = null
)

data class AgencyPagination(
    @SerializedName("total")
    val total: Int? = null,

    @SerializedName("totalPages")
    val totalPages: Int? = null,

    @SerializedName("page")
    val page: Int? = null,

    @SerializedName("limit")
    val limit: Int? = null
)

data class AgencyAddress(
    @SerializedName("street")
    val street: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("state")
    val state: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("zipCode")
    val zipCode: String? = null,

    @SerializedName("fullAddress")
    val fullAddress: String? = null
)

data class AgencyStatistics(
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

data class AgencyRatings(
    @SerializedName("average")
    val average: Double? = null,

    @SerializedName("totalCount")
    val totalCount: Int? = null,

    // Used by some nested responses (breakdown objects).
    @SerializedName("breakdown")
    val breakdown: AgencyRatingsBreakdown? = null
)

data class AgencyRatingsBreakdown(
    @SerializedName("fiveStar")
    val fiveStar: Int? = null,

    @SerializedName("fourStar")
    val fourStar: Int? = null,

    @SerializedName("threeStar")
    val threeStar: Int? = null,

    @SerializedName("twoStar")
    val twoStar: Int? = null,

    @SerializedName("oneStar")
    val oneStar: Int? = null
)

data class AgencyNationality(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("code")
    val code: String? = null,

    @SerializedName("flag")
    val flag: String? = null
)

