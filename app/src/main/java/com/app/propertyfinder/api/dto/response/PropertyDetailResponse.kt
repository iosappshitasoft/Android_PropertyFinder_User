package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class PropertyDetailResponse(
    @SerializedName("property")
    val property: PropertyDetail? = null,

    @SerializedName("relatedProperties")
    val relatedProperties: List<RelatedPropertyItem> = emptyList(),

    @SerializedName("nearbyPlaces")
    val nearbyPlaces: NearbyPlaces? = null,

    @SerializedName("mortgageCalculatorDefaults")
    val mortgageCalculatorDefaults: MortgageCalculatorDefaults? = null
)

data class PropertyDetail(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("slug")
    val slug: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("listingType")
    val listingType: PropertyListingType? = null,

    @SerializedName("completionStatus")
    val completionStatus: String? = null,

    @SerializedName("furnishedStatus")
    val furnishedStatus: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("publishedAt")
    val publishedAt: String? = null,

    @SerializedName("lastModifiedAt")
    val lastModifiedAt: String? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("rentPricing")
    val rentPricing: RentPricing? = null,

    @SerializedName("serviceCharges")
    val serviceCharges: Int? = null,

    @SerializedName("bedrooms")
    val bedrooms: Int? = null,

    @SerializedName("bathrooms")
    val bathrooms: Int? = null,

    @SerializedName("maidBedroom")
    val maidBedroom: Boolean? = null,

    @SerializedName("area")
    val area: PropertyArea? = null,

    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList(),

    @SerializedName("virtualTour360")
    val virtualTour360: String? = null,

    @SerializedName("videoTour")
    val videoTour: String? = null,

    @SerializedName("floorPlan")
    val floorPlan: List<String> = emptyList(),

    @SerializedName("location")
    val location: PropertyLocation? = null,

    @SerializedName("amenities")
    val amenities: List<PropertyAmenityDetail> = emptyList(),

    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean? = null,

    @SerializedName("isSuperagentListing")
    val isSuperagentListing: Boolean? = null,

    @SerializedName("isPetFriendly")
    val isPetFriendly: Boolean? = null,

    @SerializedName("isWaterfront")
    val isWaterfront: Boolean? = null,

    @SerializedName("regulatoryInformation")
    val regulatoryInformation: RegulatoryInformation? = null,

    @SerializedName("dldPermitNumber")
    val dldPermitNumber: String? = null,

    @SerializedName("dldPermitUrl")
    val dldPermitUrl: String? = null,

    @SerializedName("referenceId")
    val referenceId: String? = null,

    @SerializedName("views")
    val views: Int? = null,

    @SerializedName("likes")
    val likes: Int? = null,

    @SerializedName("inquiries")
    val inquiries: Int? = null,

    @SerializedName("agent")
    val agent: PropertyAgentDetail? = null,

    @SerializedName("agency")
    val agency: PropertyAgencyDetail? = null,

    @SerializedName("propertyType")
    val propertyType: PropertyTypeInfo? = null,

    @SerializedName("developer")
    val developer: Any? = null,

    @SerializedName("areaPropertiesCount")
    val areaPropertiesCount: Int? = null,

    @SerializedName("agentListingsCount")
    val agentListingsCount: Int? = null,

    @SerializedName("isSaved")
    val isSaved: Boolean? = null,

    @SerializedName("shareLink")
    val shareLink: String? = null
)

data class PropertyAmenityDetail(
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

data class PropertyAgentDetail(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("fullName")
    val fullName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null,

    @SerializedName("agentType")
    val agentType: String? = null,

    @SerializedName("brokerLicenseNumber")
    val brokerLicenseNumber: String? = null,

    @SerializedName("ratings")
    val ratings: PropertyRatings? = null,

    @SerializedName("responseTime")
    val responseTime: String? = null,

    @SerializedName("closedDeals")
    val closedDeals: Int? = null,

    @SerializedName("statistics")
    val statistics: PropertyAgentStatistics? = null,

    @SerializedName("languages")
    val languages: List<PropertyLanguage> = emptyList(),

    @SerializedName("agency")
    val agency: String? = null
)

data class PropertyAgentStatistics(
    @SerializedName("totalListings")
    val totalListings: Int? = null,

    @SerializedName("activeListings")
    val activeListings: Int? = null
)

data class PropertyAgencyDetail(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("agencyName")
    val agencyName: String? = null,

    @SerializedName("profilePicture")
    val profilePicture: String? = null,

    @SerializedName("phoneNumber")
    val phoneNumber: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("brokerLicense")
    val brokerLicense: String? = null,

    @SerializedName("brokerLicenseNumber")
    val brokerLicenseNumber: String? = null,

    @SerializedName("statistics")
    val statistics: PropertyAgencyStatistics? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean? = null
)

data class PropertyAgencyStatistics(
    @SerializedName("totalActiveListings")
    val totalActiveListings: Int? = null
)

data class PropertyLanguage(
    @SerializedName("_id")
    val id: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("nativeName")
    val nativeName: String? = null
)

data class RentPricing(
    @SerializedName("monthly")
    val monthly: Int? = null,
    @SerializedName("yearly")
    val yearly: Int? = null
)

data class RegulatoryInformation(
    @SerializedName("referenceId")
    val referenceId: String? = null,
    @SerializedName("agencyName")
    val agencyName: String? = null,
    @SerializedName("brokerLicenseNumber")
    val brokerLicenseNumber: String? = null,
    @SerializedName("zoneName")
    val zoneName: String? = null,
    @SerializedName("dldPermitNumber")
    val dldPermitNumber: String? = null,
    @SerializedName("dldPermitUrl")
    val dldPermitUrl: String? = null
)

data class RelatedPropertyItem(
    @SerializedName("_id")
    val id: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("propertyType")
    val propertyType: PropertyTypeInfo? = null,

    @SerializedName("bedrooms")
    val bedrooms: Int? = null,

    @SerializedName("bathrooms")
    val bathrooms: Int? = null,

    @SerializedName("area")
    val area: PropertyArea? = null,

    @SerializedName("price")
    val price: Int? = null,

    @SerializedName("currency")
    val currency: String? = null,

    @SerializedName("location")
    val location: RelatedPropertyLocation? = null,

    @SerializedName("completionStatus")
    val completionStatus: String? = null,

    @SerializedName("furnishedStatus")
    val furnishedStatus: String? = null,

    @SerializedName("isFeatured")
    val isFeatured: Boolean? = null,

    @SerializedName("isVerified")
    val isVerified: Boolean? = null,

    @SerializedName("isSuperagentListing")
    val isSuperagentListing: Boolean? = null,

    @SerializedName("views")
    val views: Int? = null,

    @SerializedName("slug")
    val slug: String? = null,

    @SerializedName("publishedAt")
    val publishedAt: String? = null,

    @SerializedName("images")
    val images: List<PropertyImageItem> = emptyList()
)

data class RelatedPropertyLocation(
    @SerializedName("fullAddress")
    val fullAddress: String? = null,

    @SerializedName("city")
    val city: String? = null,

    @SerializedName("zone")
    val zone: String? = null
)

data class NearbyPlaces(
    @SerializedName("restaurants")
    val restaurants: List<NearbyPlaceItem> = emptyList(),

    @SerializedName("schools")
    val schools: List<NearbyPlaceItem> = emptyList(),

    @SerializedName("hospitals")
    val hospitals: List<NearbyPlaceItem> = emptyList(),

    @SerializedName("transport")
    val transport: List<NearbyPlaceItem> = emptyList(),

    @SerializedName("shoppingMalls")
    val shoppingMalls: List<NearbyPlaceItem> = emptyList(),

    @SerializedName("hotels")
    val hotels: List<NearbyPlaceItem> = emptyList()
)

data class NearbyPlaceItem(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("vicinity")
    val vicinity: String? = null,

    @SerializedName("distance")
    val distance: String? = null
)

data class MortgageCalculatorDefaults(
    @SerializedName("purchasePrice")
    val purchasePrice: Int? = null,

    @SerializedName("residencyStatus")
    val residencyStatus: String? = null,

    @SerializedName("downPayment")
    val downPayment: Int? = null,

    @SerializedName("downPaymentPct")
    val downPaymentPct: Int? = null,

    @SerializedName("loanAmount")
    val loanAmount: Int? = null,

    @SerializedName("loanAmountPct")
    val loanAmountPct: Int? = null,

    @SerializedName("loanPeriod")
    val loanPeriod: Int? = null,

    @SerializedName("interestRate")
    val interestRate: Double? = null
)

