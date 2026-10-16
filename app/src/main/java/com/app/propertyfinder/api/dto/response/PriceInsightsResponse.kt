package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class PriceInsightsResponse(
    @SerializedName("propertyId")
    val propertyId: String? = null,
    @SerializedName("sold")
    val sold: PriceInsightsGroup? = null,
    @SerializedName("rent")
    val rent: PriceInsightsGroup? = null,
    @SerializedName("trends")
    val trends: PriceInsightsTrends? = null
)

data class PriceInsightsGroup(
    @SerializedName("transactions")
    val transactions: List<PriceInsightsTransaction> = emptyList(),
    @SerializedName("totalCount")
    val totalCount: Int? = null
)

data class PriceInsightsTransaction(
    @SerializedName("propertyId")
    val propertyId: String? = null,
    @SerializedName("dealId")
    val dealId: String? = null,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("subtitle")
    val subtitle: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("price")
    val price: Double? = null,
    @SerializedName("priceFormatted")
    val priceFormatted: String? = null,
    @SerializedName("areaSqft")
    val areaSqft: Double? = null,
    @SerializedName("areaFormatted")
    val areaFormatted: String? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("source")
    val source: String? = null
)

data class PriceInsightsTrends(
    @SerializedName("dealType")
    val dealType: String? = null,
    @SerializedName("summaryTitle")
    val summaryTitle: String? = null,
    @SerializedName("averagePrice")
    val averagePrice: Double? = null,
    @SerializedName("averagePriceFormatted")
    val averagePriceFormatted: String? = null,
    @SerializedName("averagePricePerSqft")
    val averagePricePerSqft: Double? = null,
    @SerializedName("currency")
    val currency: String? = null,
    @SerializedName("priceCompareNote")
    val priceCompareNote: String? = null,
    @SerializedName("priceDifferencePercent")
    val priceDifferencePercent: Int? = null,
    @SerializedName("period")
    val period: String? = null,
    @SerializedName("periodOptions")
    val periodOptions: List<String> = emptyList(),
    @SerializedName("chart")
    val chart: PriceInsightsChart? = null
)

data class PriceInsightsChart(
    @SerializedName("categories")
    val categories: List<String> = emptyList(),
    @SerializedName("series")
    val series: List<PriceInsightsChartSeries> = emptyList(),
    @SerializedName("yAxisMax")
    val yAxisMax: Int? = null
)

data class PriceInsightsChartSeries(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("data")
    val data: List<Double> = emptyList()
)
