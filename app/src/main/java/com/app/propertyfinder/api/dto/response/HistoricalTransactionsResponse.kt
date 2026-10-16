package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class HistoricalTransactionsResponse(
    @SerializedName("dealType")
    val dealType: String? = null,
    @SerializedName("timeframe")
    val timeframe: String? = null,
    @SerializedName("sortBy")
    val sortBy: String? = null,
    @SerializedName("filters")
    val filters: HistoricalFilters? = null,
    @SerializedName("summary")
    val summary: HistoricalSummary? = null,
    @SerializedName("transactions")
    val transactions: List<HistoricalTransactionItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: HistoricalPagination? = null
)

data class HistoricalFilters(
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("agency")
    val agency: String? = null,
    @SerializedName("propertyTypeId")
    val propertyTypeId: String? = null,
    @SerializedName("bedrooms")
    val bedrooms: String? = null,
    @SerializedName("priceMin")
    val priceMin: Double? = null,
    @SerializedName("priceMax")
    val priceMax: Double? = null
)

data class HistoricalSummary(
    @SerializedName("avgDealAmount")
    val avgDealAmount: Double? = null,
    @SerializedName("avgDealAmountFormatted")
    val avgDealAmountFormatted: String? = null,
    @SerializedName("transactionCount")
    val transactionCount: Int? = null,
    @SerializedName("transactionCountFormatted")
    val transactionCountFormatted: String? = null,
    @SerializedName("transactionCountChangePercent")
    val transactionCountChangePercent: Double? = null,
    @SerializedName("newRentals")
    val newRentals: HistoricalSummaryGroup? = null,
    @SerializedName("renewedRent")
    val renewedRent: HistoricalSummaryGroup? = null
)

data class HistoricalSummaryGroup(
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("avgDealAmount")
    val avgDealAmount: Double? = null,
    @SerializedName("avgDealAmountFormatted")
    val avgDealAmountFormatted: String? = null
)

data class HistoricalTransactionItem(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("dealId")
    val dealId: String? = null,
    @SerializedName("propertyId")
    val propertyId: String? = null,
    @SerializedName("locationName")
    val locationName: String? = null,
    @SerializedName("locationSub")
    val locationSub: String? = null,
    @SerializedName("amount")
    val amount: Double? = null,
    @SerializedName("amountFormatted")
    val amountFormatted: String? = null,
    @SerializedName("amountPerSqft")
    val amountPerSqft: Double? = null,
    @SerializedName("amountPerSqftFormatted")
    val amountPerSqftFormatted: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("dateFrom")
    val dateFrom: String? = null,
    @SerializedName("dateTo")
    val dateTo: String? = null,
    @SerializedName("dateLabel")
    val dateLabel: String? = null,
    @SerializedName("contractStatus")
    val contractStatus: String? = null,
    @SerializedName("propertyType")
    val propertyType: String? = null,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("bedroomsLabel")
    val bedroomsLabel: String? = null,
    @SerializedName("sizeSqft")
    val sizeSqft: Double? = null,
    @SerializedName("sizeSqftFormatted")
    val sizeSqftFormatted: String? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("zone")
    val zone: String? = null
)

data class HistoricalPagination(
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null
)
