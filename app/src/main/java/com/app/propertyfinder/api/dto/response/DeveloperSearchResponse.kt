package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class DeveloperSearchResponse(
    @SerializedName("items")
    val items: List<DeveloperItem> = emptyList(),
    @SerializedName("pagination")
    val pagination: DeveloperPagination? = null
)

data class DeveloperItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("logo")
    val logo: String? = null,
    @SerializedName("foundedYear")
    val foundedYear: Int? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("totalProjects")
    val totalProjects: Int? = null,
    @SerializedName("completedProjects")
    val completedProjects: Int? = null
)

data class DeveloperPagination(
    @SerializedName("total")
    val total: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("limit")
    val limit: Int? = null
)
