package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class ProjectDetailResponse(
    @SerializedName("project")
    val project: ProjectDetailItem? = null,
    @SerializedName("authorizedAgencies")
    val authorizedAgencies: List<ProjectAuthorizedAgency> = emptyList(),
    @SerializedName("similarProjects")
    val similarProjects: List<ProjectSimilarItem> = emptyList(),
    @SerializedName("priceInsights")
    val priceInsights: Any? = null,
    @SerializedName("priceTrends")
    val priceTrends: Any? = null
)

data class ProjectDetailItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("projectName")
    val projectName: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("aboutProject")
    val aboutProject: String? = null,
    @SerializedName("projectType")
    val projectType: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("constructionProgress")
    val constructionProgress: Int? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,
    @SerializedName("views")
    val views: Int? = null,
    @SerializedName("inquiries")
    val inquiries: Int? = null,
    @SerializedName("likes")
    val likes: Int? = null,
    @SerializedName("shares")
    val shares: Int? = null,
    @SerializedName("publishedAt")
    val publishedAt: String? = null,
    @SerializedName("lastModifiedAt")
    val lastModifiedAt: String? = null,
    @SerializedName("launchPrice")
    val launchPrice: ProjectLaunchPrice? = null,
    @SerializedName("governmentFees")
    val governmentFees: String? = null,
    @SerializedName("hasPostHandoverPayment")
    val hasPostHandoverPayment: Boolean? = null,
    @SerializedName("postHandoverDetails")
    val postHandoverDetails: ProjectPostHandoverDetails? = null,
    @SerializedName("saleStarted")
    val saleStarted: String? = null,
    @SerializedName("keyInformation")
    val keyInformation: ProjectKeyInformation? = null,
    @SerializedName("paymentPlan")
    val paymentPlan: ProjectPaymentPlan? = null,
    @SerializedName("paymentPlansCount")
    val paymentPlansCount: Int? = null,
    @SerializedName("projectTimeline")
    val projectTimeline: List<ProjectTimelineItem> = emptyList(),
    @SerializedName("location")
    val location: ProjectLocation? = null,
    @SerializedName("imagesCount")
    val imagesCount: Int? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("masterPlan")
    val masterPlan: List<String> = emptyList(),
    @SerializedName("brochure")
    val brochure: String? = null,
    @SerializedName("virtualTour360")
    val virtualTour360: String? = null,
    @SerializedName("videoTour")
    val videoTour: String? = null,
    @SerializedName("propertyTypes")
    val propertyTypes: List<ProjectPropertyType> = emptyList(),
    @SerializedName("bedroomOptions")
    val bedroomOptions: List<Int> = emptyList(),
    @SerializedName("amenities")
    val amenities: List<ProjectAmenityItem> = emptyList(),
    @SerializedName("dldRegistration")
    val dldRegistration: ProjectDldRegistration? = null,
    @SerializedName("faqs")
    val faqs: List<ProjectFaqItem> = emptyList(),
    @SerializedName("developer")
    val developer: ProjectDeveloper? = null,
    @SerializedName("unitsFromDeveloper")
    val unitsFromDeveloper: List<ProjectUnitsFromDeveloper> = emptyList(),
    @SerializedName("shareLink")
    val shareLink: String? = null
)

data class ProjectPostHandoverDetails(
    @SerializedName("duration")
    val duration: String? = null,
    @SerializedName("percentage")
    val percentage: Double? = null
)

data class ProjectKeyInformation(
    @SerializedName("deliveryDate")
    val deliveryDate: String? = null,
    @SerializedName("deliveryQuarter")
    val deliveryQuarter: String? = null,
    @SerializedName("location")
    val location: ProjectKeyLocation? = null,
    @SerializedName("paymentPlans")
    val paymentPlans: String? = null,
    @SerializedName("propertyTypes")
    val propertyTypes: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("constructionProgress")
    val constructionProgress: Int? = null,
    @SerializedName("isDldRegistered")
    val isDldRegistered: Boolean? = null,
    @SerializedName("hasPostHandoverPayment")
    val hasPostHandoverPayment: Boolean? = null,
    @SerializedName("postHandoverDetails")
    val postHandoverDetails: ProjectPostHandoverDetails? = null,
    @SerializedName("totalUnits")
    val totalUnits: Int? = null,
    @SerializedName("availableUnits")
    val availableUnits: Int? = null
)

data class ProjectPaymentPlan(
    @SerializedName("totalPlansAvailable")
    val totalPlansAvailable: Int? = null,
    @SerializedName("plans")
    val plans: List<ProjectPaymentPlanItem> = emptyList()
)

data class ProjectPaymentPlanItem(
    @SerializedName("planName")
    val planName: String? = null,
    @SerializedName("downPayment")
    val downPayment: ProjectPaymentPart? = null,
    @SerializedName("duringConstruction")
    val duringConstruction: ProjectDuringConstruction? = null,
    @SerializedName("onHandover")
    val onHandover: ProjectPaymentPart? = null
)

data class ProjectPaymentPart(
    @SerializedName("percentage")
    val percentage: Int? = null,
    @SerializedName("amount")
    val amount: Int? = null
)

data class ProjectDuringConstruction(
    @SerializedName("percentage")
    val percentage: Int? = null,
    @SerializedName("amount")
    val amount: Int? = null,
    @SerializedName("installments")
    val installments: List<ProjectInstallment> = emptyList()
)

data class ProjectInstallment(
    @SerializedName("percentage")
    val percentage: Int? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("amount")
    val amount: Int? = null
)

data class ProjectTimelineItem(
    @SerializedName("milestone")
    val milestone: String? = null,
    @SerializedName("date")
    val date: String? = null,
    @SerializedName("status")
    val status: String? = null,
    @SerializedName("quarter")
    val quarter: String? = null
)

data class ProjectAmenityItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("icon")
    val icon: String? = null,
    @SerializedName("image")
    val image: String? = null
)

data class ProjectDldRegistration(
    @SerializedName("isDldRegistered")
    val isDldRegistered: Boolean? = null,
    @SerializedName("dldRegistrationNumber")
    val dldRegistrationNumber: String? = null,
    @SerializedName("registrationDetails")
    val registrationDetails: String? = null
)

data class ProjectFaqItem(
    @SerializedName("question")
    val question: String? = null,
    @SerializedName("answer")
    val answer: String? = null
)

data class ProjectUnitsFromDeveloper(
    @SerializedName("buildingId")
    val buildingId: String? = null,
    @SerializedName("buildingName")
    val buildingName: String? = null,
    @SerializedName("propertyTypeFilter")
    val propertyTypeFilter: List<ProjectPropertyTypeFilterItem> = emptyList(),
    @SerializedName("layouts")
    val layouts: List<ProjectUnitLayout> = emptyList()
)

data class ProjectUnitLayout(
    @SerializedName("layoutId")
    val layoutId: String? = null,
    @SerializedName("layoutName")
    val layoutName: String? = null,
    @SerializedName("propertyType")
    val propertyType: ProjectPropertyTypeFilterItem? = null,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("maidBedroom")
    val maidBedroom: Boolean? = null,
    @SerializedName("bathrooms")
    val bathrooms: Int? = null,
    @SerializedName("areaSqm")
    val areaSqm: Double? = null,
    @SerializedName("areaSqft")
    val areaSqft: Double? = null,
    @SerializedName("startingPrice")
    val startingPrice: ProjectStartingPrice? = null,
    @SerializedName("floorPlans")
    val floorPlans: List<String> = emptyList(),
    @SerializedName("totalUnits")
    val totalUnits: Int? = null,
    @SerializedName("availableUnits")
    val availableUnits: Int? = null,
    @SerializedName("contactStatus")
    val contactStatus: String? = null
)

data class ProjectAuthorizedAgency(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("agencyName")
    val agencyName: String? = null,
    @SerializedName("isVerified")
    val isVerified: Boolean? = null,
    @SerializedName("statistics")
    val statistics: ProjectAgencyStatistics? = null,
    @SerializedName("profilePicture")
    val profilePicture: String? = null
)

data class ProjectSimilarItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("projectName")
    val projectName: String? = null,
    @SerializedName("slug")
    val slug: String? = null,
    @SerializedName("developer")
    val developer: Developer? = null,
    @SerializedName("projectType")
    val projectType: String? = null,
    @SerializedName("completionStatus")
    val completionStatus: String? = null,
    @SerializedName("launchPrice")
    val launchPrice: ProjectLaunchPrice? = null,
    @SerializedName("hasPostHandoverPayment")
    val hasPostHandoverPayment: Boolean? = null,
    @SerializedName("deliveryDate")
    val deliveryDate: String? = null,
    @SerializedName("location")
    val location: ProjectLocation? = null,
    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),
    @SerializedName("propertyTypes")
    val propertyTypes: List<ProjectPropertyType> = emptyList(),
    @SerializedName("bedroomOptions")
    val bedroomOptions: List<Int> = emptyList(),
    @SerializedName("isVerified")
    val isVerified: Boolean? = null,
    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,
    @SerializedName("imagesCount")
    val imagesCount: Int? = null,
    @SerializedName("bedroomRange")
    val bedroomRange: ProjectBedroomRange? = null
)

data class ProjectKeyLocation(
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("zone")
    val zone: String? = null
)

data class ProjectPropertyTypeFilterItem(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("slug")
    val slug: String? = null
)

data class ProjectStartingPrice(
    @SerializedName("amount")
    val amount: Int? = null,
    @SerializedName("currency")
    val currency: String? = null
)

data class ProjectAgencyStatistics(
    @SerializedName("totalActiveListings")
    val totalActiveListings: Int? = null
)

/** Full developer payload on project detail (extends search-card [Developer]). */
data class ProjectDeveloper(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("logo")
    val logo: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("foundedYear")
    val foundedYear: Int? = null,
    @SerializedName("totalProjects")
    val totalProjects: Int? = null,
    @SerializedName("completedProjects")
    val completedProjects: Int? = null,
    @SerializedName("ratings")
    val ratings: ProjectDeveloperRatings? = null,
    @SerializedName("socialLinks")
    val socialLinks: Map<String, @JvmSuppressWildcards Any?>? = null,
    @SerializedName("awards")
    val awards: List<Map<String, @JvmSuppressWildcards Any?>> = emptyList()
)

data class ProjectDeveloperRatings(
    @SerializedName("average")
    val average: Double? = null,
    @SerializedName("totalCount")
    val totalCount: Int? = null
)
