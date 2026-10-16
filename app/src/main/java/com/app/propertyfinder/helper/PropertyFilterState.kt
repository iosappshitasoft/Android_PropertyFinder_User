package com.app.propertyfinder.helper

const val DEFAULT_MIN_PRICE = 100000f
const val DEFAULT_MAX_PRICE = 500000000f
val DEFAULT_PRICE_RANGE: ClosedFloatingPointRange<Float> = DEFAULT_MIN_PRICE..DEFAULT_MAX_PRICE

data class PropertyFilterState(
    val selectedListingTypeId: String? = null,
    val showCommercialOnly: Boolean = false,
    /** New Projects: DLD-registered filter (sent on projects/search only). */
    val isDldRegistered: Boolean = false,
    /** New Projects: verified projects, or Rent/Buy: verified properties. */
    val isVerifiedOnly: Boolean = false,
    val locationQuery: String = "",
    val selectedPropertyTypeId: String? = null,
    val selectedPriceRange: ClosedFloatingPointRange<Float> = 100000f..500000000f,
    val selectedBeds: String? = null,
    val selectedBaths: String? = null,
    val selectedFurnishingId: String? = null,
    val minSize: String = "Any",
    val maxSize: String = "Any",
    val selectedAmenityIds: Set<String> = emptySet(),
    val postedDate: String = "Any time",
    val sortBy: String? = null,
    val selectedVirtualViewingId: String? = null,
    /** New Projects: projects/search param [deliveryDate]. */
    val selectedDeliveryDateId: String? = null
)