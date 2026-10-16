package com.app.propertyfinder.api.dto.response

import com.google.gson.annotations.SerializedName

data class MasterDataResponse(
    val propertyTypes: List<PropertyType> = emptyList(),
    val listingTypes: List<ListingType> = emptyList(),
    val amenities: List<amenities> = emptyList(),
    val alertFrequencies: List<alertFrequencies> = emptyList(),
    val helpFaqCategories: List<helpFaqCategories> = emptyList(),
    val furnishedStatus: List<furnishedStatus> = emptyList(),
    val sqftAreaSizes: List<sqftAreaSizes> = emptyList(),
    val virtualViewingTypes: List<virtualViewingTypes> = emptyList(),
    val priceRange: List<priceRange> = emptyList(),
    val sortByProperty: List<sortByProperty> = emptyList(),
    @SerializedName("sortByProject")
    val sortbyproject: List<sortbyproject> = emptyList(),
    val languages: List<languages> = emptyList(),
    val countries: List<countries> = emptyList(),
    @SerializedName("loanType")
    val loantype: List<loantype> = emptyList(),
    val deliveryDates: List<deliveryDates> = emptyList(),
    val services: List<services> = emptyList(),
    val residencyStatus: List<residencyStatus> = emptyList(),
    val buyingProcess: List<buyingProcess> = emptyList(),
    val propertyLocations: List<propertyLocations> = emptyList(),
    @SerializedName("employmentStatus")
    val employmentstatus: List<employmentstatus> = emptyList(),
    @SerializedName("notificationStatus")
    val notificationstatus: List<notificationstatus> = emptyList(),
    val supportedUrls: SupportedUrls? = null
)

data class PropertyType(
    val _id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val icon: String? = null,
    val image: String? = null,
    val description: String? = null,
    val category: String? = null,
    val isActive: Boolean? = null,
    val displayOrder: Int? = null,
    val totalListings: Int? = null,
    val metaTitle: String? = null,
    val metaDescription: String? = null
)

data class ListingType(
    val _id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val transaction: String? = null,
    val category: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null,
    val displayOrder: Int? = null
)
data class amenities(
    val _id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val category: String? = null,
    val icon: String? = null,
    val image: String? = null,
    val description: String? = null,
    val isActive: Boolean? = null,
    val displayOrder: Int? = null
)
data class alertFrequencies(
    val name: String? = null,
    val value: String? = null
)
data class helpFaqCategories(
    val name: String? = null,
    val value: String? = null
)
data class furnishedStatus(
    val name: String? = null,
    val value: String? = null
)
data class sqftAreaSizes(
    val name: String? = null,
    val value: String? = null
)
data class virtualViewingTypes(
    val name: String? = null,
    val value: String? = null
)
data class priceRange(
    val name: String? = null,
    val value: String? = null
)
data class sortByProperty(
    val name: String? = null,
    val value: String? = null
)
data class sortbyproject(
    val name: String? = null,
    val value: String? = null
)
data class languages(
    val _id: String? = null,
    val name: String? = null,
    val code: String? = null,
    val nativeName: String? = null,
    val isActive: Boolean? = null,
)
data class countries(
    val _id: String? = null,
    val name: String? = null,
    val code: String? = null,
    val phoneCode: String? = null,
    val flag: String? = null,
    val currency: Currency? = null,
    val isActive: Boolean? = null,
    val displayOrder: Int? = null,
    val mobileNumberLength: Int? = null
)

data class Currency(
    val code: String? = null,
    val symbol: String? = null
)
data class deliveryDates(
    val name: String? = null,
    val value: String? = null
)
data class loantype(
    val name: String? = null,
    val value: String? = null
)
data class services(
    val name: String? = null,
    val value: String? = null
)
data class residencyStatus(
    val name: String? = null,
    val value: String? = null
)
data class buyingProcess(
    val name: String? = null,
    val value: String? = null
)
data class propertyLocations(
    val _id: String? = null,
    val cityKey: String? = null,
    val displayName: String? = null,
    val propertyCount: Int? = null,
    val updatedAt: String? = null
)
data class employmentstatus(
    val name: String? = null,
    val value: String? = null
)
data class notificationstatus(
    val name: String? = null,
    val value: String? = null
)

data class SupportedUrls(
    val projectUrl: UrlAsset? = null,
    val propertyUrl: UrlAsset? = null,
    val agentUrl: UrlAsset? = null,
    val agencyUrl: UrlAsset? = null,
    val developerUrl: UrlAsset? = null,
    val userUrl: UrlAsset? = null,
    val amenityUrl: UrlAsset? = null,
    val awardUrl: UrlAsset? = null
)

data class UrlAsset(
    val img: String? = null,
    val vid: String? = null,
    val doc: String? = null
)