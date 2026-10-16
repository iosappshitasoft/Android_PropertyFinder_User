package com.app.propertyfinder.screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.googlemap_helper.PropertyMapScreen
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.HomePropertyItem
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.google.android.gms.maps.GoogleMap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2 & 10)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(navController: NavHostController) {

    val snackbarManager = rememberSnackbarManager()
    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
    }

    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val savedPropertiesState by userViewModel.savedPropertiesState.collectAsStateWithLifecycle()
    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val filterState by propertyViewModel.filterState.collectAsStateWithLifecycle()

    val listingTypes = masterDataState.data?.data?.listingTypes ?: emptyList()
    val propertyTypes = masterDataState.data?.data?.propertyTypes ?: emptyList()
    val amenities = masterDataState.data?.data?.amenities ?: emptyList()
    val sortByOptions = masterDataState.data?.data?.sortByProperty.orEmpty()
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("propertytypes,listingtypes,amenities,furnishedstatus," +
                    "alertfrequencies,virtualviewingtypes,sqftareasizes,pricerange,deliverydates,sortbyproperty") {}
        }
    }

    val priceRangeValues = masterDataState.data?.data?.priceRange
        ?.mapNotNull { it.value?.toFloatOrNull() }
        ?.sorted()
        .orEmpty()
    val minPrice = 100000f
    val maxPrice = 500000000f
    val defaultPriceRange = minPrice..maxPrice

    val selectedListingTypeId = filterState.selectedListingTypeId
    val selectedPropertyTypeId = filterState.selectedPropertyTypeId
    val selectedAmenityIds = filterState.selectedAmenityIds
    val selectedPriceRange = filterState.selectedPriceRange
    val selectedBeds = filterState.selectedBeds
    val selectedBaths = filterState.selectedBaths

    val selectedListingType = listingTypes.firstOrNull { it._id == selectedListingTypeId }
    val selectedPropertyTypeItem = propertyTypes.find { it._id == selectedPropertyTypeId }
    val selectedAmenityItems = amenities.filter { selectedAmenityIds.contains(it._id) }

    val categoryChipText = selectedListingType?.name.orEmpty().ifEmpty { "Buy" }
    val propertyTypeChipText = if (selectedPropertyTypeItem == null) "Property type" else selectedPropertyTypeItem.name.orEmpty()
    val priceChipText = if (priceRangeValues.isEmpty() || selectedPriceRange == defaultPriceRange) "Price" else "${selectedPriceRange.start.toInt()} - ${selectedPriceRange.endInclusive.toInt()}"
    val bedsBathsChipText = when {
        selectedBeds != null && selectedBaths != null -> {
            val bedText = if (selectedBeds == "0") "Studio" else "$selectedBeds Beds"
            "$bedText, $selectedBaths Baths"
        }
        selectedBeds != null -> if (selectedBeds == "0") "Studio" else "$selectedBeds Beds"
        selectedBaths != null -> "$selectedBaths Baths"
        else -> "Beds & Baths"
    }
    val amenitiesChipText = when {
        selectedAmenityItems.isEmpty() -> "Amenities"
        selectedAmenityItems.size == 1 -> selectedAmenityItems.first().name.orEmpty()
        else -> "${selectedAmenityItems.size} Amenities"
    }

    val filterChips = listOf("Category", "Property type", "Price", "Beds & Baths", "Amenities")

    val selectedFilterCount = listOfNotNull(
        selectedPropertyTypeId?.takeIf { it.isNotBlank() },
        selectedBeds,
        selectedBaths,
    ).size +
            (if (selectedAmenityIds.isNotEmpty()) 1 else 0) +
            (if (selectedPriceRange != defaultPriceRange) 1 else 0)

    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var showPropertyTypeBottomSheet by remember { mutableStateOf(false) }
    var showPriceBottomSheet by remember { mutableStateOf(false) }
    var showBedsAndBathsBottomSheet by remember { mutableStateOf(false) }
    var showAmenitiesBottomSheet by remember { mutableStateOf(false) }
    var showMapTypeBottomSheet by remember { mutableStateOf(false) }

    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }

    LaunchedEffect(TokenManager.accessToken, searchText, refreshTrigger) {
        if (TokenManager.accessToken.isNotBlank()) {
            userViewModel.getSavedProperties(
                token = TokenManager.accessToken,
                page = 1,
                limit = 15,
                search = if (searchText.isBlank()) null else searchText
            )
            isRefreshing = false
        }
    }
    LaunchedEffect(savedPropertiesState.data, savedPropertiesState.errorMessage) {
        when {
            savedPropertiesState.data != null -> {
                Log.d("SAVED_PROPERTIES", "Success: ${savedPropertiesState.data}")
            }

            !savedPropertiesState.errorMessage.isNullOrEmpty() -> {
                Log.e("SAVED_PROPERTIES", "Error: ${savedPropertiesState.errorMessage}")
            }
        }
    }
    var removedIds by remember { mutableStateOf(setOf<String>()) }
    var isSavedMainView by remember { mutableStateOf(true) }
    var mapType by remember { mutableStateOf(GoogleMap.MAP_TYPE_NORMAL) }
    var showRentBottomSheet by remember { mutableStateOf(false) }
    var selectedMapItem by remember {
        mutableStateOf<com.app.propertyfinder.api.dto.response.PropertyListItem?>(null)
    }
    var mapSelectedSortBy by remember { mutableStateOf<String?>(null) }
    var savedPropertyIds by remember { mutableStateOf(setOf<String>()) }
    var unsavedPropertyIds by remember { mutableStateOf(setOf<String>()) }

    val savedItems = savedPropertiesState.data?.data?.properties
        .orEmpty()
        .filter { it.id != null && !removedIds.contains(it.id) }
    val savedPropertyList = savedItems.map { it.toPropertyListItem() }
    Scaffold(
        containerColor = MolumulkOffWhite,
        bottomBar = {
            SavedBottomNavigationBar(navController)
        },
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().then(
            if (isSavedMainView) {
                Modifier.padding(paddingValues)
            } else {
                Modifier.padding(0.dp)
            }
        )) {
            if (isSavedMainView) {
                Column(modifier = Modifier.fillMaxSize().background(MolumulkOffWhite)) {
                    SavedScreenSearchBar(
                        searchText = searchText,
                        onSearchTextChange = { searchText = it },
                        selectedFilterCount = selectedFilterCount
                    )
                    val pullRefreshState = rememberPullToRefreshState()
                    PullToRefreshBox(
                        state = pullRefreshState,
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            isRefreshing = true
                            refreshTrigger++
                        },
                        modifier = Modifier.fillMaxSize(),
                        indicator = {
                            PullToRefreshDefaults.Indicator(
                                state = pullRefreshState,
                                isRefreshing = isRefreshing,
                                color = MolumulkPrimaryCoralRed,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().background(MolumulkOffWhite),
                            contentPadding = PaddingValues(bottom = 80.dp), // Space for floating buttons
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (savedItems.isEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillParentMaxSize()
                                            .padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(64.dp)
                                                .clip(CircleShape)
                                                .background(MolumulkBorderDivider.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AppImage(
                                                resId = R.drawable.heart,
                                                modifier = Modifier.size(32.dp),
                                                tint = MolumulkMutedSlate
                                            )
                                        }
                                        AppSpacerV(16)
                                        AppTextBold(
                                            text = "No saved properties",
                                            color = MolumulkCharcoal,
                                            fontSize = 18.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        AppSpacerV(8)
                                        AppTextRegular(
                                            text = "Properties you save will appear here for easy access later.",
                                            color = MolumulkMutedSlate,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                items(savedItems) { savedItem ->
                                    val property = savedItem.toPropertyListItem()

                                    HomePropertyItem(
                                        propertyModel = property,
                                        from = "home",
                                        onClick = { clickedId ->
                                            navController.navigate("property_detail/$clickedId")
                                        },
                                        onSaveClick = { clickedPropertyId ->
                                            userViewModel.removeFromSaved(
                                                token = TokenManager.accessToken,
                                                propertyId = clickedPropertyId
                                            ) { result ->
                                                when (result) {
                                                    is ApiResult.Success -> {
                                                        removedIds = removedIds + clickedPropertyId
                                                    }

                                                    is ApiResult.Error -> {
                                                        Log.e("REMOVE_SAVED", "Error: ${result.message}")
                                                    }
                                                }
                                            }
                                        },
                                        isSaved = true
                                    )
                                }
                            }
                        }
                    } // end PullToRefreshBox
                }
            }
            else {
                Box(modifier = Modifier.fillMaxSize()) {
                    PropertyMapScreen(
                        mapType = mapType,
                        items = savedPropertyList,
                        onMarkerClick = { clickedItem ->
                            selectedMapItem = clickedItem
                            showRentBottomSheet = true
                        }
                    )
                    Column(modifier = Modifier.fillMaxWidth().padding(paddingValues).align(Alignment.TopCenter).background(Color.Transparent)) {
                        SavedScreenSearchBar(
                            searchText = searchText,
                            onSearchTextChange = { searchText = it },
                            selectedFilterCount = selectedFilterCount
                        )
                    }
                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(paddingValues).padding(bottom = 20.dp)
                    ) {
                        MapViewBottomFloatingButtons(
                            onTravelTimeClick = { navController.navigate(Route.TravelTime.route) },
                            onNotificationCLick = { navController.navigate(Route.Notifications.route) },
                            onListViewCLick = { isSavedMainView = true },
                            onMaptypeCLick = { showMapTypeBottomSheet = true },
                            onPriceMapCLick = { navController.navigate(Route.PriceMap.route) }
                        )
                    }
                }
            }

            // Floating Buttons
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
                SavedScreenFloatingButtons(
                    onMapViewClick = {
                        isSavedMainView = false
                    }
                )
            }

            if (showRentBottomSheet) {
                val mapItemId = selectedMapItem?.id
                val mapItemIsSaved = if (mapItemId != null) {
                    when {
                        unsavedPropertyIds.contains(mapItemId) -> false
                        savedPropertyIds.contains(mapItemId) -> true
                        else -> true // Since we are on the SavedScreen, items are saved by default
                    }
                } else {
                    false
                }

                RentBottomSheet(
                    selectedItem = selectedMapItem,
                    sortOptions = sortByOptions,
                    selectedSortValue = mapSelectedSortBy,
                    onSortSelected = { mapSelectedSortBy = it },
                    isSaved = mapItemIsSaved,
                    onSaveClick = {
                        val clickedPropertyId = selectedMapItem?.id ?: return@RentBottomSheet
                        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                        if (loginType == Constants.LOGIN_USER) {
                            val currentlySaved = when {
                                unsavedPropertyIds.contains(clickedPropertyId) -> false
                                savedPropertyIds.contains(clickedPropertyId) -> true
                                else -> true
                            }
                            if (currentlySaved) {
                                userViewModel.removeFromSaved(
                                    token = TokenManager.accessToken,
                                    propertyId = clickedPropertyId
                                ) { result ->
                                    when (result) {
                                        is ApiResult.Success -> {
                                            snackbarManager.showSuccess(result.body.message.toString())
                                            savedPropertyIds = savedPropertyIds - clickedPropertyId
                                            unsavedPropertyIds = unsavedPropertyIds + clickedPropertyId
                                            removedIds = removedIds + clickedPropertyId
                                        }

                                        is ApiResult.Error -> {
                                            snackbarManager.showError(result.message)
                                            Log.e("REMOVE_SAVED_MAP", "Error: ${result.message}")
                                        }
                                    }
                                }
                            } else {
                                userViewModel.addToSaved(
                                    token = TokenManager.accessToken,
                                    propertyId = clickedPropertyId
                                ) { result ->
                                    when (result) {
                                        is ApiResult.Success -> {
                                            snackbarManager.showSuccess(result.body.message.toString())
                                            savedPropertyIds = savedPropertyIds + clickedPropertyId
                                            unsavedPropertyIds = unsavedPropertyIds - clickedPropertyId
                                            removedIds = removedIds - clickedPropertyId
                                        }

                                        is ApiResult.Error -> {
                                            snackbarManager.showError(result.message)
                                            Log.e("ADD_TO_SAVED_MAP", "Error: ${result.message}")
                                        }
                                    }
                                }
                            }
                        } else {
                            snackbarManager.showError(
                                "Please signup and login for access to ${context.getString(R.string.guest_user_description)}"
                            )
                        }
                    },
                    onDismiss = { showRentBottomSheet = false },
                    onPropertyClick = {
                        showRentBottomSheet = false
                        selectedMapItem?.id?.let { propertyId ->
                            navController.navigate("property_detail/$propertyId")
                        }
                    },
                    onResidenceClick = {
                        showRentBottomSheet = false
                        navController.navigate(Route.ResidenceDetails.route)
                    }
                )
            }

            if (showCategoryBottomSheet) {
                CategoryFilterBottomSheet(
                    listingTypes = listingTypes,
                    initialSelectedId = selectedListingTypeId,
                    initialShowCommercialOnly = filterState.showCommercialOnly,
                    onDismiss = { showCategoryBottomSheet = false },
                    onShowProperties = { selectedItem, showCommercialOnly ->
                        showCategoryBottomSheet = false
                        propertyViewModel.resetFilterState(defaultListingTypeId = selectedItem._id)
                        propertyViewModel.updateFilterState(
                            selectedPriceRange = defaultPriceRange,
                            showCommercialOnly = showCommercialOnly
                        )
                    }
                )
            }

            if (showPropertyTypeBottomSheet) {
                PropertyTypeBottomSheet(
                    propertyTypes = propertyTypes,
                    initialSelectedId = selectedPropertyTypeId,
                    onDismiss = { showPropertyTypeBottomSheet = false },
                    onClear = { propertyViewModel.updateFilterState(selectedPropertyTypeId = null) },
                    onShowProperties = { selectedItem ->
                        showPropertyTypeBottomSheet = false
                        propertyViewModel.updateFilterState(selectedPropertyTypeId = selectedItem?._id)
                    }
                )
            }

            if (showPriceBottomSheet) {
                PriceBottomSheet(
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    initialRange = selectedPriceRange,
                    onDismiss = { showPriceBottomSheet = false },
                    onClear = { propertyViewModel.updateFilterState(selectedPriceRange = defaultPriceRange) },
                    onShowProperties = { range ->
                        showPriceBottomSheet = false
                        propertyViewModel.updateFilterState(selectedPriceRange = range)
                    }
                )
            }

            if (showBedsAndBathsBottomSheet) {
                BedsAndBathsBottomSheet(
                    initialBeds = selectedBeds,
                    initialBaths = selectedBaths,
                    onDismiss = { showBedsAndBathsBottomSheet = false },
                    onClear = { propertyViewModel.updateFilterState(selectedBeds = null, selectedBaths = null) },
                    onShowProperties = { beds, baths ->
                        showBedsAndBathsBottomSheet = false
                        propertyViewModel.updateFilterState(selectedBeds = beds, selectedBaths = baths)
                    }
                )
            }

            if (showAmenitiesBottomSheet) {
                AmenitiesBottomSheet(
                    amenities = amenities,
                    initialSelectedIds = selectedAmenityIds,
                    onDismiss = { showAmenitiesBottomSheet = false },
                    onClear = { propertyViewModel.updateFilterState(selectedAmenityIds = emptySet<String>()) },
                    onShowProperties = { selectedAmenityItems ->
                        showAmenitiesBottomSheet = false
                        propertyViewModel.updateFilterState(selectedAmenityIds = selectedAmenityItems.mapNotNull { it._id }.toSet())
                    }
                )
            }

            if (showMapTypeBottomSheet) {
                MapTypeBottomSheet(
                    selectedType = mapType,
                    onSelected = {
                        mapType = it
                        showMapTypeBottomSheet = false
                    },
                    onDismiss = { showMapTypeBottomSheet = false }
                )
            }
        }
    }
}

@Composable
fun SavedScreenSearchBar(
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    selectedFilterCount: Int = 0,
    showFilterIcon: Boolean = false,
    onFilterClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        border = androidx.compose.foundation.BorderStroke(1.dp, MolumulkBorderDivider),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(MolumulkDimens.SearchFieldHeight)
                .padding(start = 14.dp, end = 6.dp)
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppImage(
                R.drawable.search,
                modifier = Modifier.size(20.dp),
                tint = MolumulkPrimaryCoralRed
            )

            AppSpacerH(width = 8)

            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    color = MolumulkCharcoal,
                    textAlign = TextAlign.Start
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (searchText.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AppTextRegular(
                                    text = "Search city, area or building",
                                    color = MolumulkMutedSlate,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                        innerTextField()
                    }
                }
            )

            if (showFilterIcon) {
                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF5F5F5))
                            .clickable(onClick = onFilterClick),
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(R.drawable.menu_filter, modifier = Modifier.size(40.dp))
                    }

                    if (selectedFilterCount > 0) {
                        Box(
                            modifier = Modifier
                                .offset(x = 2.dp, y = (-2).dp)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(MolumulkPrimaryCoralRed)
                                .border(1.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (selectedFilterCount > 9) "9+" else selectedFilterCount.toString(),
                                color = Color.White,
                                fontSize = 8.sp,
                                maxLines = 1,
                                style = TextStyle(
                                    platformStyle = PlatformTextStyle(
                                        includeFontPadding = false
                                    ),
                                    lineHeight = 8.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedBottomNavigationBar(navController: NavHostController) {
    // NEW: Clean white native bottom bar with top hairline divider and standard 64dp height
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MolumulkBorderDivider)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MolumulkDimens.BottomNavHeight)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Item (Unselected)
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.search,
                label = stringResource(R.string.search),
                isSelected = false,
                onClick = {
                    navController.navigate(Route.Home.route) { launchSingleTop = true }
                }
            )
            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
            if (loginType == Constants.LOGIN_USER) {
                // Saved Item (Selected)
                BottomNavItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.heart,
                    label = stringResource(R.string.saved),
                    isSelected = true,
                    onClick = { }
                )
            }
            // Account Item (Unselected)
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.account,
                label = stringResource(R.string.account),
                isSelected = false,
                onClick = {
                    navController.navigate(Route.Account.route) { launchSingleTop = true }
                }
            )
        }
    }
}

@Composable
fun SavedScreenFloatingButtons(onMapViewClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onMapViewClick,
            colors = ButtonDefaults.buttonColors(containerColor = MolumulkCharcoal),
            shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppImage(
                    resId = R.drawable.maploaction,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                AppSpacerH(width = 8)
                AppTextBold(text = "Map View", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}
@Composable
fun SavedScreenMapFloatingButtons(onListViewCLick: () -> Unit = {}) {
    Row(
        modifier = Modifier.background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { onListViewCLick() },
            colors = ButtonDefaults.buttonColors(containerColor = MolumulkCharcoal),
            shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppImage(
                    resId = R.drawable.ic_list_view,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                AppSpacerH(width = 8)
                AppTextBold(text = "List view", color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

