package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class MortgageUpfrontCostsRequest(
    @SerializedName("purchasePrice")
    val purchasePrice: Int,
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("loanAmount")
    val loanAmount: Int,
    @SerializedName("loanPeriod")
    val loanPeriod: Int,
    @SerializedName("interestRate")
    val interestRate: Double,
    @SerializedName("residencyStatus")
    val residencyStatus: String
)

data class MortgageUpfrontCostsData(
    @SerializedName("paymentBreakdown")
    val paymentBreakdown: MortgageUpfrontCostsPaymentBreakdown,
    @SerializedName("calculationsBasedOn")
    val calculationsBasedOn: MortgageUpfrontCostsCalculationsBasedOn,
    @SerializedName("totalAmountRequiredUpfront")
    val totalAmountRequiredUpfront: Int
)

data class MortgageUpfrontCostsPaymentBreakdown(
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("totalPurchaseCosts")
    val totalPurchaseCosts: Int,
    @SerializedName("landDepartmentFee")
    val landDepartmentFee: Int,
    @SerializedName("registrationTrusteeFee")
    val registrationTrusteeFee: Int,
    @SerializedName("mortgageRegistrationFee")
    val mortgageRegistrationFee: Int,
    @SerializedName("realEstateAgencyFee")
    val realEstateAgencyFee: Int,
    @SerializedName("mortgageArrangementFee")
    val mortgageArrangementFee: Int,
    @SerializedName("adminFee")
    val adminFee: Int
)

data class MortgageUpfrontCostsCalculationsBasedOn(
    @SerializedName("monthlyPayment")
    val monthlyPayment: Int,
    @SerializedName("interestRate")
    val interestRate: Double,
    @SerializedName("loanAmount")
    val loanAmount: Int,
    @SerializedName("loanDuration")
    val loanDuration: String? = null,
    @SerializedName("residencyStatus")
    val residencyStatus: String? = null
)

