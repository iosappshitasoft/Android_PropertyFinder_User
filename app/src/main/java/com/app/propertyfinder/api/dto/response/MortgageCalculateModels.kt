package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class MortgageCalculateRequest(
    @SerializedName("purchasePrice")
    val purchasePrice: Int,
    @SerializedName("residencyStatus")
    val residencyStatus: String,
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("loanPeriod")
    val loanPeriod: Int,
    @SerializedName("interestRate")
    val interestRate: Double
)

data class MortgageCalculateData(
    @SerializedName("input")
    val input: MortgageCalculateInput,
    @SerializedName("calculated")
    val calculated: MortgageCalculateCalculated,
    @SerializedName("output")
    val output: MortgageCalculateOutput,
    @SerializedName("sliderConfig")
    val sliderConfig: MortgageSliderConfig
)

data class MortgageCalculateInput(
    @SerializedName("purchasePrice")
    val purchasePrice: Int,
    @SerializedName("residencyStatus")
    val residencyStatus: String,
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("loanPeriod")
    val loanPeriod: Int,
    @SerializedName("interestRate")
    val interestRate: Double
)

data class MortgageCalculateCalculated(
    @SerializedName("downPaymentPct")
    val downPaymentPct: Double,
    @SerializedName("loanAmount")
    val loanAmount: Int,
    @SerializedName("loanAmountPct")
    val loanAmountPct: Double,
    @SerializedName("minDownPayment")
    val minDownPayment: Int,
    @SerializedName("minDownPaymentPct")
    val minDownPaymentPct: Double
)

data class MortgageCalculateOutput(
    @SerializedName("monthlyPayment")
    val monthlyPayment: Double,
    @SerializedName("totalInterest")
    val totalInterest: Double,
    @SerializedName("totalPayment")
    val totalPayment: Double,
    @SerializedName("interestPct")
    val interestPct: Double,
    @SerializedName("principalPct")
    val principalPct: Double,
    @SerializedName("principal")
    val principal: Double,
    @SerializedName("interest")
    val interest: Double,
    @SerializedName("propertiesInBudget")
    val propertiesInBudget: Int
)

data class MortgageSliderConfig(
    @SerializedName("purchasePrice")
    val purchasePrice: MortgageRangeInt,
    @SerializedName("downPayment")
    val downPayment: MortgageRangeInt,
    @SerializedName("loanAmount")
    val loanAmount: MortgageRangeInt,
    @SerializedName("loanPeriod")
    val loanPeriod: MortgageRangeInt,
    @SerializedName("interestRate")
    val interestRate: MortgageRangeDouble
)

data class MortgageRangeInt(
    @SerializedName("min")
    val min: Int,
    @SerializedName("max")
    val max: Int
)

data class MortgageRangeDouble(
    @SerializedName("min")
    val min: Double,
    @SerializedName("max")
    val max: Double
)

