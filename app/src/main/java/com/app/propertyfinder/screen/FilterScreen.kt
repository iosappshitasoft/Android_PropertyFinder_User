package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import com.app.propertyfinder.ui.components.AppSwitch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.helper.PropertyFilterState
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.FilterAppBar
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

private const val FILTER_FALLBACK_MIN_PRICE = 100000f
private const val FILTER_FALLBACK_MAX_PRICE = 500000000f
private const val AMENITY_ICON_BASE_URL = "https://d1dp1oh0ra5b0z.cloudfront.net/img/amenities/"

data class UiFilterOption(
    val id: String,
    val name: String,
    val iconUrl: String? = null
)

@Composable
private fun FilterScreenSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextMedium(
            text = label,
            color = MolumulkCharcoal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        AppSwitch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FilterPropertyTypeChipPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterPropertyTypeChip(text = "Apartment", isSelected = true, onClick = {})
        FilterPropertyTypeChip(text = "Villa", isSelected = false, onClick = {})
    }
}

@Composable
fun FilterScreen(
    navController: NavHostController,
    viewModel: PropertyViewModel
) {
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val masterData = masterDataState.data?.data

    val listingTypes = remember(masterData) {
        val originalList = masterData?.listingTypes
            ?.mapNotNull { item ->
                val id = item._id.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name)
            }
            ?: emptyList()

        val hasCommercialBuy = originalList.any {
            it.name.equals("commercial buy", ignoreCase = true) ||
            it.name.equals("commercial_buy", ignoreCase = true)
        }
        val hasCommercialRent = originalList.any {
            it.name.equals("commercial rent", ignoreCase = true) ||
            it.name.equals("commercial_rent", ignoreCase = true)
        }
        val commercialItem = originalList.firstOrNull {
            it.name.equals("commercial", ignoreCase = true)
        }

        if (!hasCommercialBuy && !hasCommercialRent && commercialItem != null) {
            originalList.flatMap { item ->
                if (item.id == commercialItem.id) {
                    listOf(
                        UiFilterOption(id = item.id, name = "Commercial Buy"),
                        UiFilterOption(id = item.id + "_rent", name = "Commercial Rent")
                    )
                } else {
                    listOf(item)
                }
            }
        } else {
            originalList
        }
    }

    val propertyTypes = remember(masterData) {
        masterData?.propertyTypes
            ?.mapNotNull { item ->
                val id = item._id.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name)
            }
            ?: emptyList()
    }

    val furnishedStatuses = remember(masterData) {
        masterData?.furnishedStatus
            ?.mapNotNull { item ->
                val id = item.value.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name)
            }
            ?: emptyList()
    }

    val amenities = remember(masterData) {
        masterData?.amenities
            ?.mapNotNull { item ->
                val id = item._id.orEmpty()
                val name = item.name.orEmpty()
                val iconFile = item.image.orEmpty()

                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name, iconUrl = AMENITY_ICON_BASE_URL + iconFile)
            }
            ?: emptyList()
    }

    val virtualViewingOptions = remember(masterData) {
        masterData?.virtualViewingTypes
            ?.mapNotNull { item ->
                val id = item.value.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name)
            }
            ?: emptyList()
    }

    val deliveryDateOptions = remember(masterData) {
        masterData?.deliveryDates
            ?.mapNotNull { item ->
                val id = item.value.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null
                else UiFilterOption(id = id, name = name)
            }
            ?: emptyList()
    }
    val sortByOptions = remember(masterData) {
        masterData?.sortByProperty
            ?.mapNotNull { item ->
                val id = item.value.orEmpty()
                val name = item.name.orEmpty()
                if (id.isBlank() || name.isBlank()) null else UiFilterOption(id = id, name = name)
            }.orEmpty()
    }
    val priceRangeValues = remember(masterData) {
        masterData?.priceRange
            ?.mapNotNull { it.value?.toFloatOrNull() }
            ?.sorted()
            .orEmpty()
    }
    val minPrice = 100000f
    val maxPrice = 500000000f

    LaunchedEffect(minPrice, maxPrice) {
        viewModel.setPriceBounds(minPrice, maxPrice)
    }

    val sizeOptions = remember(masterData) {
        val apiSizes = masterData?.sqftAreaSizes
            ?.mapNotNull { it.value?.trim() }
            ?.filter { it.isNotBlank() }
            ?.distinct()
            ?: emptyList()

        listOf("Any") + apiSizes
    }

    FilterScreenContend(
        navController = navController,
        viewModel = viewModel,
        listingTypes = listingTypes,
        propertyTypes = propertyTypes,
        furnishedStatuses = furnishedStatuses,
        sortByOptions = sortByOptions,
        minPrice = minPrice,
        maxPrice = maxPrice,
        sizeOptions = sizeOptions,
        amenities = amenities,
        virtualViewingOptions = virtualViewingOptions,
        deliveryDateOptions = deliveryDateOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterScreenContend(
    navController: NavHostController,
    viewModel: PropertyViewModel,
    listingTypes: List<UiFilterOption>,
    propertyTypes: List<UiFilterOption>,
    furnishedStatuses: List<UiFilterOption>,
    sortByOptions: List<UiFilterOption>,
    minPrice: Float,
    maxPrice: Float,
    sizeOptions: List<String>,
    amenities: List<UiFilterOption>,
    virtualViewingOptions: List<UiFilterOption>,
    deliveryDateOptions: List<UiFilterOption>
) {
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    // 40dp horizontal padding + 2 gaps of 8dp each = 56dp
    val chipWidth = (screenWidth - 56.dp) / 3
    val defaultPriceRange = minPrice..maxPrice

    val defaultListingTypeId = listingTypes.firstOrNull()?.id
    val postedDateOptions = sortByOptions

    var selectedListingTypeId by remember(filterState, defaultListingTypeId) {
        mutableStateOf(filterState.selectedListingTypeId ?: defaultListingTypeId)
    }
    var showCommercialOnly by remember(filterState) {
        mutableStateOf(filterState.showCommercialOnly)
    }
    var isDldRegistered by remember(filterState) {
        mutableStateOf(filterState.isDldRegistered)
    }
    var isVerifiedOnly by remember(filterState) {
        mutableStateOf(filterState.isVerifiedOnly)
    }
    var expandedPropertyTypes by remember { mutableStateOf(false) }
    var selectedPropertyTypeId by remember(filterState) {
        mutableStateOf(filterState.selectedPropertyTypeId)
    }
    var pricePeriod by remember { mutableStateOf("Monthly") }
    var priceRange by remember(filterState, minPrice, maxPrice) {
        mutableStateOf(
            if (filterState.selectedPriceRange.start < minPrice ||
                filterState.selectedPriceRange.endInclusive > maxPrice
            ) defaultPriceRange else filterState.selectedPriceRange
        )
    }
    var selectedBedrooms by remember(filterState) {
        mutableStateOf(filterState.selectedBeds)
    }
    var selectedBathrooms by remember(filterState) {
        mutableStateOf(filterState.selectedBaths)
    }
    var selectedFurnishingId by remember(filterState) {
        mutableStateOf(filterState.selectedFurnishingId)
    }
    var minSize by remember(filterState) {
        mutableStateOf(filterState.minSize)
    }
    var maxSize by remember(filterState) {
        mutableStateOf(filterState.maxSize)
    }
    var selectedAmenityIds by remember(filterState) {
        mutableStateOf(filterState.selectedAmenityIds)
    }
    var sortByValue by remember(filterState) {
        mutableStateOf(filterState.sortBy)
    }
    var postedDate by remember(filterState, postedDateOptions) {
        mutableStateOf(
            postedDateOptions.firstOrNull { it.id == filterState.sortBy }?.name
                ?: filterState.postedDate
        )
    }
    var selectedVirtualViewingId by remember(filterState) {
        mutableStateOf(filterState.selectedVirtualViewingId)
    }
    var selectedDeliveryDateId by remember(filterState) {
        mutableStateOf(filterState.selectedDeliveryDateId)
    }

    LaunchedEffect(defaultListingTypeId) {
        if (selectedListingTypeId == null && defaultListingTypeId != null) {
            selectedListingTypeId = defaultListingTypeId
        }
    }

    LaunchedEffect(selectedListingTypeId) {
        val name = listingTypes.firstOrNull { it.id == selectedListingTypeId }?.name
        val isNew = name.equals("New Projects", ignoreCase = true)
        if (!isNew) {
            isDldRegistered = false
            selectedDeliveryDateId = null
        } else {
            selectedFurnishingId = null
            selectedVirtualViewingId = null
        }
    }

    fun resetLocalState() {
        selectedListingTypeId = defaultListingTypeId
        showCommercialOnly = false
        isDldRegistered = false
        isVerifiedOnly = false
        expandedPropertyTypes = false
        selectedPropertyTypeId = null
        pricePeriod = "Monthly"
        priceRange = defaultPriceRange
        selectedBedrooms = null
        selectedBathrooms = null
        selectedFurnishingId = null
        minSize = "Any"
        maxSize = "Any"
        selectedAmenityIds = emptySet()
        postedDate = "Any time"
        sortByValue = null
        selectedVirtualViewingId = null
        selectedDeliveryDateId = null
    }

    Scaffold(
        modifier = Modifier.background(MolumulkOffWhite),
        containerColor = MolumulkOffWhite,
        topBar = {
            FilterAppBar(
                title = stringResource(R.string.filter),
                onBackClick = { navController.popBackStack() },
                onResetClick = { resetLocalState() }
            )
        }
    ) { paddingValues ->
        val selectedCategoryName = listingTypes.firstOrNull { it.id == selectedListingTypeId }?.name.orEmpty()
        val isNewProjectsCategory = selectedCategoryName.equals("New Projects", ignoreCase = true)

        LazyColumn(modifier = Modifier.fillMaxSize().background(MolumulkOffWhite).padding(paddingValues),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AppSpacerV(10)
                    Column(modifier = Modifier.fillMaxWidth().padding()) {
                        LazyRow(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(listingTypes) { category ->
                                val formattedCategoryName = when {
                                    category.name.equals("commercial buy", ignoreCase = true) || category.name.equals("commercial_buy", ignoreCase = true) -> "Commercial Buy"
                                    category.name.equals("commercial rent", ignoreCase = true) || category.name.equals("commercial_rent", ignoreCase = true) -> "Commercial Rent"
                                    category.name.equals("new projects", ignoreCase = true) || category.name.equals("new_projects", ignoreCase = true) -> "New Projects"
                                    category.name.equals("buy", ignoreCase = true) -> "Buy"
                                    category.name.equals("rent", ignoreCase = true) -> "Rent"
                                    category.name.equals("commercial", ignoreCase = true) -> "Commercial Buy"
                                    else -> category.name
                                }
                                val isSelected = selectedListingTypeId == category.id
                                FilterChip(
                                    modifier = Modifier.defaultMinSize(minWidth = chipWidth),
                                    text = formattedCategoryName,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedListingTypeId = category.id
                                    }
                                )
                            }
                        }
                    }
                    AppSpacerV(20)
                    AppDivider(color = MolumulkBorderDivider)
                    AppSpacerV(12)
                    Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)){
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            AppTextMedium(
                                text = when (listingTypes.firstOrNull { it.id == selectedListingTypeId }?.name) {
                                    "Rent" -> stringResource(R.string.view_commercial_properties_rent_buy)
                                    "Buy" -> stringResource(R.string.view_commercial_properties_rent_buy)
                                    "New Projects" -> stringResource(R.string.view_commercial_properties_projects)
                                    else -> stringResource(R.string.view_commercial_properties_rent_buy)
                                },
                                color = MolumulkCharcoal,
                                fontSize = 14.sp
                            )

                            AppSwitch(
                                checked = showCommercialOnly,
                                onCheckedChange = { showCommercialOnly = it }
                            )
                        }
                        AppSpacerV(20)

                        if (isNewProjectsCategory) {
                            FilterScreenSwitchRow(
                                label = stringResource(R.string.dld_registered_projects),
                                checked = isDldRegistered,
                                onCheckedChange = { isDldRegistered = it }
                            )
                            AppSpacerV(20)
                            FilterScreenSwitchRow(
                                label = stringResource(R.string.view_verified_projects_only),
                                checked = isVerifiedOnly,
                                onCheckedChange = { isVerifiedOnly = it }
                            )
                        } else {
                            FilterScreenSwitchRow(
                                label = stringResource(R.string.view_verified_projects_only),
                                checked = isVerifiedOnly,
                                onCheckedChange = { isVerifiedOnly = it }
                            )
                        }

                        AppSpacerV(20)
                    }
                    AppDivider(color = MolumulkBorderDivider)

                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    AppSpacerV(20)
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextBold(
                            text = stringResource(R.string.property_type),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )
                    }

                    AppSpacerV(15)

                    val displayTypes = if (expandedPropertyTypes) propertyTypes else propertyTypes.take(6)

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 4
                    ) {
                        displayTypes.forEach { type ->
                            FilterPropertyTypeChip(
                                text = type.name,
                                isSelected = selectedPropertyTypeId == type.id,
                                onClick = {
                                    selectedPropertyTypeId =
                                        if (selectedPropertyTypeId == type.id) null else type.id
                                }
                            )
                        }
                    }

                    AppSpacerV(24)

                    if (propertyTypes.size > 6) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            AppTextSemiBold(
                                text = if (expandedPropertyTypes) "show less" else stringResource(R.string.see_all_properties),
                                color = MolumulkPrimaryCoralRed,
                                fontSize = 14.sp,
                                modifier = Modifier.clickable {
                                    expandedPropertyTypes = !expandedPropertyTypes
                                }
                            )
                        }
                        AppSpacerV(24)
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextBold(
                            text = stringResource(R.string.price_period),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        PricePeriodDropdown(
                            selectedPeriod = pricePeriod,
                            onPeriodChange = { pricePeriod = it }
                        )
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                    AppSpacerV(15)

                    AppTextBold(
                        text = stringResource(R.string.price_range),
                        fontSize = 16.sp,
                        color = MolumulkCharcoal
                    )

                    AppSpacerV(20)

                    PriceRangeFilterContent(
                        minPrice = minPrice,
                        maxPrice = maxPrice,
                        currentPriceRange = priceRange,
                        onPriceRangeChange = { priceRange = it }
                    )

                    AppSpacerV(20)
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    AppTextBold(
                        text = stringResource(R.string.bedrooms),
                        fontSize = 16.sp,
                        color = MolumulkCharcoal
                    )

                    AppSpacerV(12)

                    val bedOptions = listOf("Studio", "1", "2", "3", "4", "5", "6", "7", "7+")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bedOptions.forEach { label ->
                            val value = if (label == "Studio") "0" else label
                            val isSelected = selectedBedrooms == value
                            FilterBedsBathsChip(
                                text = label,
                                isSelected = isSelected,
                                onClick = {
                                    selectedBedrooms = if (isSelected) null else value
                                },
                                modifier = if (label == "Studio") {
                                    Modifier.height(40.dp).widthIn(min = 72.dp)
                                } else {
                                    Modifier.size(width = 42.dp, height = 40.dp)
                                }
                            )
                        }
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                    AppTextBold(
                        text = stringResource(R.string.bathrooms),
                        fontSize = 16.sp,
                        color = MolumulkCharcoal
                    )

                    AppSpacerV(12)

                    val bathOptions = listOf("1", "2", "3", "4", "5", "6", "7", "7+")
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        bathOptions.forEach { label ->
                            val isSelected = selectedBathrooms == label
                            FilterBedsBathsChip(
                                text = label,
                                isSelected = isSelected,
                                onClick = {
                                    selectedBathrooms = if (isSelected) null else label
                                },
                                modifier = Modifier.size(width = 42.dp, height = 40.dp)
                            )
                        }
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                if (isNewProjectsCategory) {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        AppTextBold(
                            text = stringResource(R.string.delivery_date),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )
                        AppSpacerV(12)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(deliveryDateOptions) { option ->
                                FilterPropertyTypeChip(
                                    text = option.name,
                                    isSelected = selectedDeliveryDateId == option.id,
                                    onClick = {
                                        selectedDeliveryDateId =
                                            if (selectedDeliveryDateId == option.id) null else option.id
                                    }
                                )
                            }
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                        AppTextBold(
                            text = stringResource(R.string.furnishing_status),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )
                        AppSpacerV(12)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(furnishedStatuses) { status ->
                                FilterPropertyTypeChip(
                                    text = status.name,
                                    isSelected = selectedFurnishingId == status.id,
                                    onClick = {
                                        selectedFurnishingId =
                                            if (selectedFurnishingId == status.id) null else status.id
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                    AppTextBold(
                        text = stringResource(R.string.property_size),
                        fontSize = 16.sp,
                        color = MolumulkCharcoal
                    )

                    AppSpacerV(12)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        SizeDropdown(
                            placeholder = "Any",
                            selectedSize = minSize,
                            onSizeChange = { minSize = it },
                            options = sizeOptions,
                            modifier = Modifier.weight(1f)
                        )

                        SizeDropdown(
                            placeholder = "Any",
                            selectedSize = maxSize,
                            onSizeChange = { maxSize = it },
                            options = sizeOptions,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                var expandedAmenities by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                    AppTextBold(
                        text = stringResource(R.string.amenities),
                        fontSize = 16.sp,
                        color = MolumulkCharcoal
                    )

                    AppSpacerV(12)

                    val visibleAmenities = if (expandedAmenities) amenities else amenities.take(5)

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        visibleAmenities.forEach { amenity ->
                            FilterAmenityItem(
                                name = amenity.name,
                                iconUrl = amenity.iconUrl,
                                isSelected = selectedAmenityIds.contains(amenity.id),
                                onClick = {
                                    selectedAmenityIds =
                                        if (selectedAmenityIds.contains(amenity.id)) {
                                            selectedAmenityIds - amenity.id
                                        } else {
                                            selectedAmenityIds + amenity.id
                                        }
                                }
                            )
                        }
                    }

                    if (amenities.size > 5) {
                        AppSpacerV(12)

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            AppTextSemiBold(
                                text = if (expandedAmenities) "show less" else stringResource(R.string.see_all_amenities),
                                color = MolumulkPrimaryCoralRed,
                                fontSize = 13.sp,
                                modifier = Modifier.clickable {
                                    expandedAmenities = !expandedAmenities
                                }
                            )
                        }
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextBold(
                            text = stringResource(R.string.posted_date),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        PostedDateDropdown(
                            selectedDate = postedDate,
                            onDateChange = { selectedName ->
                                postedDate = selectedName
                                sortByValue = postedDateOptions.firstOrNull { it.name == selectedName }?.id
                            },
                            options = postedDateOptions.map { it.name }
                        )
                    }
                }
            }

            item { AppDivider(color = MolumulkBorderDivider) }

            if (!isNewProjectsCategory) {
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(20.dp)
                    ) {
                        AppTextBold(
                            text = stringResource(R.string.virtual_viewing),
                            fontSize = 16.sp,
                            color = MolumulkCharcoal
                        )

                        AppSpacerV(12)

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(virtualViewingOptions) { option ->
                                FilterPropertyTypeChip(
                                    text = option.name,
                                    isSelected = selectedVirtualViewingId == option.id,
                                    onClick = {
                                        selectedVirtualViewingId =
                                            if (selectedVirtualViewingId == option.id) null else option.id
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp)
                        .padding(top = 15.dp, bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    PrimaryRoundButton(
                        text = stringResource(R.string.show_properties),
                        modifier = Modifier.fillMaxWidth().height(MolumulkDimens.PrimaryButtonHeight),
                        containerColor = MolumulkPrimaryCoralRed,
                        cornerRadius = MolumulkDimens.PrimaryCtaRadius
                    ) {
                        viewModel.applyFullFilterState(
                            PropertyFilterState(
                                selectedListingTypeId = (selectedListingTypeId ?: defaultListingTypeId)?.removeSuffix("_rent"),
                                showCommercialOnly = showCommercialOnly,
                                isDldRegistered = if (isNewProjectsCategory) isDldRegistered else false,
                                isVerifiedOnly = isVerifiedOnly,
                                selectedPropertyTypeId = selectedPropertyTypeId,
                                selectedPriceRange = priceRange,
                                selectedBeds = selectedBedrooms,
                                selectedBaths = selectedBathrooms,
                                selectedFurnishingId = if (!isNewProjectsCategory) selectedFurnishingId else null,
                                minSize = minSize,
                                maxSize = maxSize,
                                selectedAmenityIds = selectedAmenityIds,
                                postedDate = postedDate,
                                sortBy = sortByValue,
                                selectedVirtualViewingId = if (!isNewProjectsCategory) selectedVirtualViewingId else null,
                                selectedDeliveryDateId = if (isNewProjectsCategory) selectedDeliveryDateId else null
                            )
                        )
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(if (isSelected) MolumulkLightCoralTint else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextSemiBold(
            text = text,
            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun FilterPropertyTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(if (isSelected) MolumulkLightCoralTint else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextSemiBold(
            text = text,
            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
            fontSize = 12.sp,
            maxLines = 1
        )
    }
}

@Composable
fun FilterBedsBathsChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(
                if (isSelected) MolumulkLightCoralTint else Color.White,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        AppTextSemiBold(
            text = text,
            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
            fontSize = 12.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PricePeriodDropdown(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedPeriod,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.W500,
                color = MolumulkCharcoal
            ),
            modifier = Modifier
                .widthIn(min = 120.dp)
                .defaultMinSize(minHeight = 49.dp)
                .menuAnchor(),
            trailingIcon = {
                AppImage(
                    resId = R.drawable.arrow_down,
                    modifier = Modifier.size(width = 9.dp, height = 5.dp),
                    tint = MolumulkCharcoal
                )
            },
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MolumulkPrimaryCoralRed,
                unfocusedBorderColor = MolumulkBorderDivider,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = Color.White,
                surfaceTint = Color.Transparent
            )
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                listOf(
                    stringResource(R.string.monthly),
                    stringResource(R.string.yearly)
                ).forEach { period ->
                    DropdownMenuItem(
                        text = {
                            AppTextMedium(
                                text = period,
                                color = MolumulkCharcoal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.background(Color.White),
                        onClick = {
                            onPeriodChange(period)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SizeDropdown(
    placeholder: String,
    selectedSize: String,
    onSizeChange: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedSize.ifBlank { "Any" },
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 49.dp)
                .menuAnchor(),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.W500,
                color = MolumulkCharcoal
            ),
            placeholder = {
                AppTextMedium(
                    text = placeholder,
                    color = MolumulkMutedSlate,
                    fontSize = 12.sp
                )
            },
            trailingIcon = {
                AppImage(
                    resId = R.drawable.arrow_down,
                    modifier = Modifier.size(width = 9.dp, height = 5.dp),
                    tint = MolumulkCharcoal
                )
            },
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MolumulkPrimaryCoralRed,
                unfocusedBorderColor = MolumulkBorderDivider,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = Color.White,
                surfaceTint = Color.Transparent
            )
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { size ->
                    DropdownMenuItem(
                        text = {
                            AppTextMedium(
                                text = size,
                                color = MolumulkCharcoal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.background(Color.White),
                        onClick = {
                            onSizeChange(size)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostedDateDropdown(
    selectedDate: String,
    onDateChange: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 12.sp,
                lineHeight = 12.sp,
                fontWeight = FontWeight.W500,
                color = MolumulkCharcoal
            ),
            modifier = Modifier
                .widthIn(min = 120.dp)
                .defaultMinSize(minHeight = 49.dp)
                .menuAnchor(),
            trailingIcon = {
                AppImage(
                    resId = R.drawable.arrow_down,
                    modifier = Modifier.size(width = 9.dp, height = 5.dp),
                    tint = MolumulkCharcoal
                )
            },
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MolumulkPrimaryCoralRed,
                unfocusedBorderColor = MolumulkBorderDivider,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                surface = Color.White,
                surfaceTint = Color.Transparent
            )
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { date ->
                    DropdownMenuItem(
                        text = {
                            AppTextMedium(
                                text = date,
                                color = MolumulkCharcoal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.background(Color.White),
                        onClick = {
                            onDateChange(date)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeFilterContent(
    minPrice: Float = FILTER_FALLBACK_MIN_PRICE,
    maxPrice: Float = FILTER_FALLBACK_MAX_PRICE,
    currentPriceRange: ClosedFloatingPointRange<Float> = FILTER_FALLBACK_MIN_PRICE..FILTER_FALLBACK_MAX_PRICE,
    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit
) {
    var priceRange by remember(currentPriceRange.start, currentPriceRange.endInclusive) {
        mutableStateOf(currentPriceRange)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            PriceBarChart(
                minPrice = minPrice,
                maxPrice = maxPrice,
                selectedRange = priceRange,
                activeColor = Color(0xFFFCBBBD),
                inactiveColor = Color(0xFFE7ECF4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(bottom = 20.dp) // Offset bars to meet slider track center
            )
            CustomRangeSlider(
                min = minPrice,
                max = maxPrice,
                start = priceRange.start,
                end = priceRange.endInclusive,
                onValueChange = { startValue, endValue ->
                    priceRange = startValue..endValue
                    onPriceRangeChange(startValue..endValue)
                }
            )
        }

        AppSpacerV(15)
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).height(74.dp)
                    .border(width = 1.dp, color = MolumulkBorderDivider, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)).background(Color.White)
                    .padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                    AppTextMedium(text = stringResource(R.string.minimum), color = MolumulkMutedSlate, fontSize = 12.sp)
                    AppSpacerV(4)
                    AppTextBold(text = "%,d".format(priceRange.start.toInt()), color = MolumulkCharcoal, fontSize = 15.sp)
                }
            }

            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(1.dp)
                    .background(MolumulkBorderDivider)
            )

            Box(modifier = Modifier.weight(1f).height(74.dp)
                .border(width = 1.dp, color = MolumulkBorderDivider, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)).background(Color.White)
                .padding(horizontal = 16.dp)) {
                Column(modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
                    AppTextMedium(text = stringResource(R.string.maximum), color = MolumulkMutedSlate, fontSize = 12.sp)
                    AppSpacerV(4)
                    AppTextBold(text = "%,d".format(priceRange.endInclusive.toInt()), color = MolumulkCharcoal, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun FilterAmenityItem(
    name: String,
    iconUrl: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) MolumulkLightCoralTint else Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val iconTint = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) Color.White else MolumulkOffWhite),
            contentAlignment = Alignment.Center
        ) {
            SubcomposeAsyncImage(
                model = iconUrl,
                contentDescription = name,
                modifier = Modifier.size(18.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(iconTint),
                loading = {
                    AppImage(
                        resId = R.drawable.ic_placeholder,
                        contentDescription = name,
                        modifier = Modifier.size(18.dp),
                        tint = iconTint
                    )
                },
                error = {
                    AppImage(
                        resId = R.drawable.search,
                        contentDescription = name,
                        modifier = Modifier.size(18.dp),
                        tint = iconTint
                    )
                }
            )
            if (iconUrl.isNullOrBlank()) {
                AppImage(
                    resId = R.drawable.search,
                    contentDescription = name,
                    modifier = Modifier.size(18.dp),
                    tint = iconTint
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))
        AppTextSemiBold(
            text = name,
            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )
        if (isSelected) {
            AppImage(
                resId = R.drawable.tick,
                contentDescription = "Selected",
                modifier = Modifier.size(18.dp),
                tint = MolumulkPrimaryCoralRed
            )
        }
    }
}

