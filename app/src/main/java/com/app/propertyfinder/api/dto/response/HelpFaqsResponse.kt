package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class HelpFaqsResponse(
    @SerializedName("activeCategory")
    val activeCategory: String? = null,
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("faqs")
    val faqs: List<HelpFaqItem> = emptyList()
)

data class HelpFaqItem(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("categoryKey")
    val categoryKey: String? = null,
    @SerializedName("question")
    val question: String? = null,
    @SerializedName("answer")
    val answer: String? = null,
    @SerializedName("order")
    val order: Int? = null
)

