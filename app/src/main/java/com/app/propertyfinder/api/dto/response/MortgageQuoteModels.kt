package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class MortgageQuoteRequest(
    @SerializedName("loanType")
    val loanType: String,
    @SerializedName("residenceStatus")
    val residenceStatus: String,
    @SerializedName("residencyStatus")
    val residencyStatus: String,
    @SerializedName("buyingProcess")
    val buyingProcess: String,
    @SerializedName("propertyPrice")
    val propertyPrice: Int,
    @SerializedName("employmentStatus")
    val employmentStatus: String,
    @SerializedName("monthlySalary")
    val monthlySalary: Int? = null,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("countryCode")
    val countryCode: String,
    @SerializedName("mobileNumber")
    val mobileNumber: String,
    @SerializedName("calculatorSnapshot")
    val calculatorSnapshot: MortgageQuoteCalculatorSnapshot
)

data class MortgageQuoteCalculatorSnapshot(
    @SerializedName("purchasePrice")
    val purchasePrice: Int,
    @SerializedName("residencyStatus")
    val residencyStatus: String,
    @SerializedName("downPayment")
    val downPayment: Int,
    @SerializedName("downPaymentPct")
    val downPaymentPct: Int,
    @SerializedName("loanAmount")
    val loanAmount: Int,
    @SerializedName("loanPeriod")
    val loanPeriod: Int,
    @SerializedName("interestRate")
    val interestRate: Int,
    @SerializedName("monthlyPayment")
    val monthlyPayment: Int,
    @SerializedName("totalInterest")
    val totalInterest: Int
) : Serializable

data class MortgageQuoteData(
    @SerializedName("quoteId")
    val quoteId: String? = null,
    @SerializedName("sentTo")
    val sentTo: String? = null
)
