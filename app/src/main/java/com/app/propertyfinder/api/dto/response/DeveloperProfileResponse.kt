package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class DeveloperProfileResponse(
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
    @SerializedName("aboutUs")
    val aboutUs: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("statistics")
    val statistics: DeveloperStatistics? = null,
    @SerializedName("awards")
    val awards: List<DeveloperAwardItem> = emptyList(),
    @SerializedName("projectsByLocation")
    val projectsByLocation: List<DeveloperProjectByLocationItem> = emptyList()
)

data class DeveloperStatistics(
    @SerializedName("totalProjects")
    val totalProjects: Int? = null,
    @SerializedName("completedProjects")
    val completedProjects: Int? = null,
    @SerializedName("ongoingProjects")
    val ongoingProjects: Int? = null,
    @SerializedName("offPlanProjects")
    val offPlanProjects: Int? = null
)

data class DeveloperAwardItem(
    @SerializedName("title")
    val title: String? = null
)

data class DeveloperProjectByLocationItem(
    @SerializedName("location")
    val location: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("totalProjects")
    val totalProjects: Int? = null,
    @SerializedName("count")
    val count: Int? = null
)
