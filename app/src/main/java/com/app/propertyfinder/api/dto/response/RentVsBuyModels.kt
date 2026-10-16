package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class RentVsBuyCalculateRequest(
    @SerializedName("purchasePrice")
    val purchasePrice: Int,
    @SerializedName("residencyStatus")
    val residencyStatus: String? = null,
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("loanPeriod")
    val loanPeriod: Int? = null,
    @SerializedName("mortgageLoanPeriod")
    val mortgageLoanPeriod: Int,
    @SerializedName("interestRate")
    val interestRate: Double? = null,
    @SerializedName("monthlyRent")
    val monthlyRent: Int? = null,
    @SerializedName("annualRent")
    val annualRent: Int,
    @SerializedName("comparisonYears")
    val comparisonYears: Int? = null,
    @SerializedName("rentIncreaseAnnualPct")
    val rentIncreaseAnnualPct: Int? = null,
    @SerializedName("advanced")
    val advanced: RentVsBuyAdvancedRequest
)

data class RentVsBuyAdvancedRequest(
    @SerializedName("fixedMortgagePeriod")
    val fixedMortgagePeriod: Int,
    @SerializedName("fixedInterestRate")
    val fixedInterestRate: Double,
    @SerializedName("reversionRate")
    val reversionRate: Double,
    @SerializedName("annualRentGrowthRate")
    val annualRentGrowthRate: Int,
    @SerializedName("annualHomePriceGrowthRate")
    val annualHomePriceGrowthRate: Int,
    @SerializedName("maintenancePctOfPricePerYear")
    val maintenancePctOfPricePerYear: Int? = null,
    @SerializedName("annualHomeInsurance")
    val annualHomeInsurance: Int? = null,
    @SerializedName("annualServiceCharges")
    val annualServiceCharges: Int? = null,
    @SerializedName("annualRenterInsurance")
    val annualRenterInsurance: Int? = null,
    @SerializedName("includePurchaseFees")
    val includePurchaseFees: Boolean? = null,
    @SerializedName("homeAppreciationPctPerYear")
    val homeAppreciationPctPerYear: Int? = null,
    @SerializedName("sellingCostPctOfSale")
    val sellingCostPctOfSale: Int? = null,
    @SerializedName("mortgageProtectionMonthly")
    val mortgageProtectionMonthly: Int? = null,
    @SerializedName("rentAgentFeeAed")
    val rentAgentFeeAed: Int? = null,
    @SerializedName("rentGovernmentFeeAed")
    val rentGovernmentFeeAed: Int? = null
)

data class RentVsBuyCalculateData(
    @SerializedName("totals")
    val totals: RentVsBuyTotals? = null,
    @SerializedName("yearlyComparison")
    val yearlyComparison: List<RentVsBuyYearlyComparisonItem>? = null,
    @SerializedName("paymentBreakdown")
    val paymentBreakdown: RentVsBuyPaymentBreakdown? = null,
    @SerializedName("sliderConfig")
    val sliderConfig: RentVsBuySliderConfig? = null
)

data class RentVsBuyPaymentBreakdownResponseData(
    @SerializedName("paymentBreakdown")
    val paymentBreakdown: RentVsBuyPaymentBreakdown? = null
)

data class RentVsBuyTotals(
    @SerializedName("differenceRentMinusBuy")
    val differenceRentMinusBuy: Double? = null,
    @SerializedName("differenceRentMinusNetBuy")
    val differenceRentMinusNetBuy: Double? = null
)

data class RentVsBuyYearlyComparisonItem(
    @SerializedName("year")
    val year: Int,
    @SerializedName("cumulativeRent")
    val cumulativeRent: Double,
    @SerializedName("cumulativeBuy")
    val cumulativeBuy: Double,
    @SerializedName("rentMonthlyTotal")
    val rentMonthlyTotal: Double? = null,
    @SerializedName("buyMonthlyTotal")
    val buyMonthlyTotal: Double? = null,
    @SerializedName("buyMonthlyBreakdown")
    val buyMonthlyBreakdown: RentVsBuyMonthlyBreakdownValues? = null
)

data class RentVsBuyPaymentBreakdown(
    @SerializedName("monthly")
    val monthly: RentVsBuyMonthlyPaymentBreakdown? = null,
    @SerializedName("overHorizon")
    val overHorizon: RentVsBuyOverHorizon? = null
)

data class RentVsBuyMonthlyPaymentBreakdown(
    @SerializedName("rentPayment")
    val rentPayment: Double? = null,
    @SerializedName("mortgagePrincipal")
    val mortgagePrincipal: Double? = null,
    @SerializedName("mortgageInterest")
    val mortgageInterest: Double? = null,
    @SerializedName("mortgageTotalPI")
    val mortgageTotalPI: Double? = null,
    @SerializedName("maintenance")
    val maintenance: Double? = null,
    @SerializedName("homeInsurance")
    val homeInsurance: Double? = null,
    @SerializedName("serviceCharges")
    val serviceCharges: Double? = null,
    @SerializedName("mortgageProtection")
    val mortgageProtection: Double? = null,
    @SerializedName("ownerOccupierTotal")
    val ownerOccupierTotal: Double? = null
)

data class RentVsBuyMonthlyBreakdownValues(
    @SerializedName("maintenance")
    val maintenance: Double? = null,
    @SerializedName("homeInsurance")
    val homeInsurance: Double? = null,
    @SerializedName("serviceCharges")
    val serviceCharges: Double? = null,
    @SerializedName("mortgageProtection")
    val mortgageProtection: Double? = null
)

data class RentVsBuyOverHorizon(
    @SerializedName("initial")
    val initial: RentVsBuyInitial? = null,
    @SerializedName("recurring")
    val recurring: RentVsBuyRecurring? = null,
    @SerializedName("summary")
    val summary: RentVsBuySummary? = null
)

data class RentVsBuyInitial(
    @SerializedName("rent")
    val rent: RentVsBuyInitialRent? = null,
    @SerializedName("buy")
    val buy: RentVsBuyInitialBuy? = null
)

data class RentVsBuyInitialRent(
    @SerializedName("agentFees")
    val agentFees: Double? = null,
    @SerializedName("governmentFees")
    val governmentFees: Double? = null,
    @SerializedName("total")
    val total: Double? = null
)

data class RentVsBuyInitialBuy(
    @SerializedName("downPayment")
    val downPayment: Double? = null,
    @SerializedName("agentFees")
    val agentFees: Double? = null,
    @SerializedName("bankFees")
    val bankFees: Double? = null,
    @SerializedName("governmentFees")
    val governmentFees: Double? = null,
    @SerializedName("total")
    val total: Double? = null
)

data class RentVsBuyRecurring(
    @SerializedName("rent")
    val rent: RentVsBuyRecurringRent? = null,
    @SerializedName("buy")
    val buy: RentVsBuyRecurringBuy? = null
)

data class RentVsBuyRecurringRent(
    @SerializedName("rentPayments")
    val rentPayments: Double? = null,
    @SerializedName("renterInsurance")
    val renterInsurance: Double? = null,
    @SerializedName("total")
    val total: Double? = null
)

data class RentVsBuyRecurringBuy(
    @SerializedName("principal")
    val principal: Double? = null,
    @SerializedName("interest")
    val interest: Double? = null,
    @SerializedName("insurancesTotal")
    val insurancesTotal: Double? = null,
    @SerializedName("communityServiceCosts")
    val communityServiceCosts: Double? = null,
    @SerializedName("maintenanceCosts")
    val maintenanceCosts: Double? = null,
    @SerializedName("total")
    val total: Double? = null
)

data class RentVsBuySummary(
    @SerializedName("netCostRent")
    val netCostRent: Double? = null,
    @SerializedName("netCostBuyAfterSale")
    val netCostBuyAfterSale: Double? = null
)

data class RentVsBuySliderConfig(
    @SerializedName("purchasePrice")
    val purchasePrice: RentVsBuyRangeInt? = null,
    @SerializedName("downPayment")
    val downPayment: RentVsBuyRangeInt? = null,
    @SerializedName("loanPeriod")
    val loanPeriod: RentVsBuyRangeInt? = null,
    @SerializedName("interestRate")
    val interestRate: RentVsBuyRangeDouble? = null,
    @SerializedName("monthlyRent")
    val monthlyRent: RentVsBuyRangeInt? = null,
    @SerializedName("comparisonYears")
    val comparisonYears: RentVsBuyRangeInt? = null,
    @SerializedName("rentIncreaseAnnualPct")
    val rentIncreaseAnnualPct: RentVsBuyRangeInt? = null
)

data class RentVsBuyRangeInt(
    @SerializedName("min")
    val min: Int,
    @SerializedName("max")
    val max: Int
)

data class RentVsBuyRangeDouble(
    @SerializedName("min")
    val min: Double,
    @SerializedName("max")
    val max: Double
)
