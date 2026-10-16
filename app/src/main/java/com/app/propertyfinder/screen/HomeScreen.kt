package com.app.propertyfinder.screen

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.ListingType
import com.app.propertyfinder.api.dto.response.PropertyType
import com.app.propertyfinder.api.dto.response.amenities
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.googlemap_helper.PropertyMapScreen
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppHorizontalDivider
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.screen.CreateAlertBottomSheet
import com.app.propertyfinder.ui.components.HomePropertyItem
import com.app.propertyfinder.ui.components.HomeSearchBar
import com.app.propertyfinder.ui.theme.DividerColor

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff Section 2 & 10)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkLogoNearBlack
import com.app.propertyfinder.ui.theme.MolumulkLogoRed
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState

import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.alertFrequencies
import com.app.propertyfinder.api.dto.response.sortByProperty
import com.app.propertyfinder.ui.components.AppLoader
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.app.propertyfinder.helper.DEFAULT_PRICE_RANGE
import com.app.propertyfinder.ui.components.AppTextSemiBold

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

data class Property(
    val id: String,
    val imageUrl: Int,
    val title: String,
    val location: String,
    val price: String,
    val beds: Int,
    val baths: Int,
    val area: String,
    val type: String,
    val isVerified: Boolean = true,
    val isSuperAgent: Boolean = true,
    val postedTime: String,
    val agencyLogo: String = "DAMAC"
)

//@Preview
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController = rememberNavController(), viewModel: PropertyViewModel) {
    val topAppBarState = rememberTopAppBarState()
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarManager = rememberSnackbarManager()
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, view)
            .isAppearanceLightStatusBars = true
    }
    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
    }
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val propertyPaginationState by viewModel.propertyPaginationState.collectAsStateWithLifecycle()
    val filterState by viewModel.filterState.collectAsStateWithLifecycle()

    val userViewModel: UserViewModel = hiltViewModel()
    val addToSavedState by userViewModel.addToSavedState.collectAsStateWithLifecycle()
    val createAlertState by userViewModel.createAlertState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    var listingTypes by remember { mutableStateOf<List<ListingType>>(emptyList()) }
    var propertyTypes by remember { mutableStateOf<List<PropertyType>>(emptyList()) }
    var amenities by remember { mutableStateOf<List<amenities>>(emptyList()) }
    var alertfrequencies by remember { mutableStateOf<List<alertFrequencies>>(emptyList()) }
    var sortByOptions by remember { mutableStateOf<List<sortByProperty>>(emptyList()) }
    var selectedAlertFrequencyName by rememberSaveable { mutableStateOf("") }

    var savedPropertyIds by remember { mutableStateOf(setOf<String>()) }
    var unsavedPropertyIds by remember { mutableStateOf(setOf<String>()) }
    val accessToken = remember { PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "") }
    val hasUnreadNotifications = PrefUtils.getBoolean(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, false)
    LaunchedEffect(Unit) {
        val refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "")
    }
    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            viewModel.getMasterData("propertytypes,listingtypes,amenities,furnishedstatus," +
                    "alertfrequencies,virtualviewingtypes,sqftareasizes,pricerange,deliverydates,sortbyproperty") { response ->
                when (response) {
                    is ApiResult.Success -> {
                        Log.d("MASTER_DATA", "Success: ${response.body}")
                    }

                    is ApiResult.Error -> {
                        Log.e("MASTER_DATA", "Error: ${response.message}")
                    }
                }
            }
        }
    }

    listingTypes = masterDataState.data?.data?.listingTypes ?: emptyList()
    propertyTypes = masterDataState.data?.data?.propertyTypes ?: emptyList()
    amenities = masterDataState.data?.data?.amenities ?: emptyList()
    alertfrequencies = masterDataState.data?.data?.alertFrequencies ?: emptyList()
    sortByOptions = masterDataState.data?.data?.sortByProperty ?: emptyList()

    val priceRangeValues = masterDataState.data?.data?.priceRange
        ?.mapNotNull { it.value?.toFloatOrNull() }
        ?.sorted()
        .orEmpty()
    val minPrice = 100000f
    val maxPrice = 500000000f
    val defaultPriceRange = minPrice..maxPrice
    LaunchedEffect(minPrice, maxPrice) {
        // Always sync VM bounds with master-data (or fallbacks) so search requests match the filter UI.
        viewModel.setPriceBounds(minPrice, maxPrice)
        if (priceRangeValues.isNotEmpty() &&
            filterState.selectedPriceRange == DEFAULT_PRICE_RANGE &&
            defaultPriceRange != DEFAULT_PRICE_RANGE
        ) {
            viewModel.updateFilterState(selectedPriceRange = defaultPriceRange)
        }
    }

    val selectedListingTypeId = filterState.selectedListingTypeId
    val selectedPropertyTypeId = filterState.selectedPropertyTypeId
    val selectedAmenityIds = filterState.selectedAmenityIds
    val selectedPriceRange = filterState.selectedPriceRange
    val selectedBeds = filterState.selectedBeds
    val selectedBaths = filterState.selectedBaths

    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val pullRefreshState = rememberPullToRefreshState()

    LaunchedEffect(listingTypes) {
        if (listingTypes.isNotEmpty()) {
            viewModel.ensureDefaultListingType(listingTypes.first()._id)
        }
    }

    LaunchedEffect(
        listingTypes,
        selectedListingTypeId,
        selectedPropertyTypeId,
        selectedAmenityIds,
        selectedPriceRange.start,
        selectedPriceRange.endInclusive,
        filterState.locationQuery,
        selectedBeds,
        selectedBaths,
        filterState.selectedFurnishingId,
        filterState.minSize,
        filterState.maxSize,
        filterState.postedDate,
        filterState.sortBy,
        filterState.selectedVirtualViewingId,
        filterState.selectedDeliveryDateId,
        filterState.showCommercialOnly,
        filterState.isDldRegistered,
        filterState.isVerifiedOnly,
        refreshTrigger
    ) {
        // Avoid calling the search API before master data is restored after returning to this screen;
        // otherwise listing type id cannot be resolved to a name and New Projects is requested as properties (empty list).
        if (listingTypes.isEmpty()) return@LaunchedEffect
        // Always reload when filters change. Do not skip when items already loaded —
        // that was blocking API calls after the user changed any filter.
        selectedListingTypeId?.let { listingTypeId ->
            val selectedListingTypeName = listingTypes.firstOrNull { it._id == listingTypeId }?.name.orEmpty()
            viewModel.loadFirstPropertyPage(
                listingTypeId = listingTypeId,
                isNewProjects = selectedListingTypeName.equals("New Projects", ignoreCase = true),
                limit = 15,
                amenityIds = selectedAmenityIds.takeIf { it.isNotEmpty() }?.toList()
            ) { response ->
                when (response) {
                    is ApiResult.Success -> {
                        Log.v("PROPERTY_DATA", response.body.toString())
                    }

                    is ApiResult.Error -> {
                        Log.e("PROPERTY_DATA", response.message)
                    }
                }
            }
        }
        isRefreshing = false
    }

    LaunchedEffect(listState, propertyPaginationState.items.size, propertyPaginationState.hasNextPage) {
        snapshotFlow {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 2
        }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                if (propertyPaginationState.hasNextPage && !propertyPaginationState.isNextPageLoading) {
                    viewModel.loadNextPropertyPage { response ->
                        when (response) {
                            is ApiResult.Success -> {
                                Log.v("NEXT_PAGE", response.body.toString())
                            }

                            is ApiResult.Error -> {
                                Log.e("NEXT_PAGE", response.message)
                            }
                        }
                    }
                }
            }
    }

    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var showPropertyTypeBottomSheet by remember { mutableStateOf(false) }
    var showPriceBottomSheet by remember { mutableStateOf(false) }
    var showBedsAndBathsBottomSheet by remember { mutableStateOf(false) }
    var showAmenitiesBottomSheet by remember { mutableStateOf(false) }

    val selectedListingType = listingTypes.firstOrNull { it._id == selectedListingTypeId }
    val selectedPropertyTypeItem = propertyTypes.find { it._id == selectedPropertyTypeId }
    val selectedAmenityItems = amenities.filter { selectedAmenityIds.contains(it._id) }

    val categoryChipText = selectedListingType?.name.orEmpty().ifEmpty { "Buy" }

    val propertyTypeChipText = when {
        selectedPropertyTypeItem == null -> "Property type"
        else -> selectedPropertyTypeItem.name.orEmpty()
    }

    val priceChipText = if (priceRangeValues.isEmpty() || selectedPriceRange == defaultPriceRange) {
        "Price"
    } else {
        "${selectedPriceRange.start.toInt()} - ${selectedPriceRange.endInclusive.toInt()}"
    }

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

    val filterChips = listOf(
        "Category",
        "Property type",
        "Price",
        "Beds & Baths",
        "Amenities"
    )

    var showCreateAlertDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Rent") }

    var showMapTypeBottomSheet by remember { mutableStateOf(false) }
    var showNewProjectBottomSheet by remember { mutableStateOf(false) }
    var showRentBottomSheet by remember { mutableStateOf(false) }
    var selectedMapItem by remember { mutableStateOf<com.app.propertyfinder.api.dto.response.PropertyListItem?>(null) }

    // Persist view mode across navigation so returning from other screens
    // keeps the user in Map View when it was active.
    var isHomeMainView by rememberSaveable { mutableStateOf(true) }
    var homeSearchText by rememberSaveable { mutableStateOf("") }
    var mapType by rememberSaveable { mutableStateOf(GoogleMap.MAP_TYPE_NORMAL) }

    LaunchedEffect(selectedListingType?.name) {
        selectedCategory = selectedListingType?.name ?: "Rent"
    }

    val isNewProjectsCategory =
        selectedListingType?.name.equals("New Projects", ignoreCase = true)

    val selectedFilterCount = listOfNotNull(
        filterState.locationQuery.takeIf { it.isNotBlank() },
        selectedPropertyTypeId?.takeIf { it.isNotBlank() },
        selectedBeds,
        selectedBaths,
        if (!isNewProjectsCategory) filterState.selectedFurnishingId?.takeIf { it.isNotBlank() } else null,
        filterState.sortBy?.takeIf { it.isNotBlank() },
        if (!isNewProjectsCategory) filterState.selectedVirtualViewingId?.takeIf { it.isNotBlank() } else null,
        if (isNewProjectsCategory) filterState.selectedDeliveryDateId?.takeIf { it.isNotBlank() } else null
    ).size +
            (if (selectedAmenityIds.isNotEmpty()) 1 else 0) +
            (if (selectedPriceRange != defaultPriceRange) 1 else 0) +
            (if (filterState.minSize != "Any" || filterState.maxSize != "Any") 1 else 0) +
            (if (filterState.postedDate != "Any time") 1 else 0) +
            (if (filterState.showCommercialOnly) 1 else 0) +
            (if (filterState.isDldRegistered) 1 else 0) +
            (if (filterState.isVerifiedOnly) 1 else 0)

    if (createAlertState.isLoading){
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center) {
//            AppLoader()
            AppLoadingDialog()
        }
    }
    Scaffold(
        modifier = Modifier.nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
        topBar = {
            if (isHomeMainView) {
                TopAppBar(
                    title = {
                        AppTextBold(text = "Explore", fontSize = 26.sp, color = MolumulkCharcoal)
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Route.Notifications.route) }) {
                            AppImage(
                                if (hasUnreadNotifications) R.drawable.notification_icon else R.drawable.notifictionsetting,
                                tint = MolumulkCharcoal,
                                modifier = Modifier.size(23.dp)
                            )
                        }
                        AppSpacerH(width = 8)
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, MolumulkBorderDivider, RoundedCornerShape(20.dp))
                                .background(MolumulkOffWhite)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppTextSemiBold(fontSize = 12.sp, text = "EN", color = MolumulkCharcoal)
                                AppSpacerH(width = 4)
                                AppImage(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = MolumulkCharcoal,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White),
                    scrollBehavior = topAppBarScrollBehavior
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        snackbarHost = {
            snackbarManager.CustomSnackbarHost(modifier = Modifier.padding(bottom = 70.dp))
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().then(
            if (isHomeMainView) {
                Modifier.padding(paddingValues)
            } else {
                Modifier.padding(0.dp)
            }
        )) {
            if (isHomeMainView) {
                Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
                    HomeScreenSearchBar(
                        searchText = filterState.locationQuery,
                        onSearchTextChange = { viewModel.updateFilterState(locationQuery = it) },
                        selectedFilterCount = selectedFilterCount,
                        onFilterClick = { navController.navigate(Route.Filter.route) }
                    )
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(filterChips) { chip ->
                            val chipText = when (chip) {
                                "Category" -> categoryChipText
                                "Property type" -> propertyTypeChipText
                                "Price" -> priceChipText
                                "Beds & Baths" -> bedsBathsChipText
                                "Amenities" -> amenitiesChipText
                                else -> chip
                            }

                            val isSelected = when (chip) {
                                "Category" -> selectedListingTypeId != null
                                "Property type" -> selectedPropertyTypeId != null
                                "Price" -> selectedPriceRange != defaultPriceRange
                                "Beds & Baths" -> selectedBeds != null || selectedBaths != null
                                "Amenities" -> selectedAmenityIds.isNotEmpty()
                                else -> false
                            }

                            HomeScreenFilterChip(
                                text = chipText,
                                isSelected = isSelected,
                                onClick = {
                                    when (chip) {
                                        "Category" -> showCategoryBottomSheet = true
                                        "Property type" -> showPropertyTypeBottomSheet = true
                                        "Price" -> showPriceBottomSheet = true
                                        "Beds & Baths" -> showBedsAndBathsBottomSheet = true
                                        "Amenities" -> showAmenitiesBottomSheet = true
                                    }
                                }
                            )
                        }
                    }
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
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(top = 5.dp).background(MolumulkOffWhite),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val totalCount = if (propertyPaginationState.totalCount > 0) {
                                    propertyPaginationState.totalCount
                                } else {
                                    propertyPaginationState.items.size
                                }
                                val isNewProjects = selectedListingType?.name.equals("New Projects", ignoreCase = true)
                                val unit = if (isNewProjects) "projects" else "properties"

                                AppTextBold(
                                    text = "$totalCount $unit",
                                    fontSize = 17.sp,
                                    color = MolumulkCharcoal
                                )
                                if (!isNewProjects) {
                                    AppSpacerH(width = 10)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(MolumulkLightCoralTint)
                                            .padding(horizontal = 10.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        AppTextRegular(
                                            text = "$totalCount ${unit}new",
                                            color = MolumulkPrimaryCoralRed,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                            }
                        }

                        val propertyList = propertyPaginationState.items

                        items(propertyList) { property ->
                            val isNewProjectsCategory = selectedListingType?.name.equals("New Projects", ignoreCase = true)
                            val propertyId = property.id
                            val isSaved = when {
                                propertyId != null && unsavedPropertyIds.contains(propertyId) -> false
                                propertyId != null && savedPropertyIds.contains(propertyId) -> true
                                else -> property.isSaved == true
                            }

                            HomePropertyItem(
                                property,
                                if (isNewProjectsCategory) "new_project" else "home",
                                onClick = { clickedId ->
                                    if (isNewProjectsCategory) {
                                        navController.navigate(
                                            Route.DevPropertiesDetail.createRoute(
                                                propertyId = clickedId,
                                                fromScreen = "home"
                                            )
                                        )
                                    } else {
                                        navController.navigate("property_detail/$clickedId")
                                    }
                                },
                                onSaveClick = { clickedPropertyId ->
                                    val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                                    if (loginType == Constants.LOGIN_USER) {
                                        val currentlySaved = when {
                                            unsavedPropertyIds.contains(clickedPropertyId) -> false
                                            savedPropertyIds.contains(clickedPropertyId) -> true
                                            else -> property.isSaved == true
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
                                                    }

                                                    is ApiResult.Error -> {
                                                        snackbarManager.showError(result.message)
                                                        Log.e("REMOVE_SAVED", "Error: ${result.message}")
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
                                                    }
                                                    is ApiResult.Error -> {
                                                        snackbarManager.showError(result.message)
                                                        Log.e("ADD_TO_SAVED", "Error: ${result.message}")
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        snackbarManager.showError("Please signup and login for access to ${context.getString(R.string.guest_user_description)}")
                                    }
                                },
                                isSaved = isSaved
                            )
                        }

                        if (propertyPaginationState.isNextPageLoading) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = MolumulkPrimaryCoralRed)
                                }
                            }
                        }
                    }
                    } // end PullToRefreshBox
                }

                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)) {
                    HomeScreenFloatingButtons(
                        "home",
                        onCreateAlertClick = {
                            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                            if (loginType == Constants.LOGIN_USER) {
                                showCreateAlertDialog = true
                            } else{
                                snackbarManager.showInfo("Sign-up or log-in for create alert")
                            }
                        },
                        onMapViewClick = {
                            isHomeMainView = false
                        }
                    )
                }

                if (propertyPaginationState.isFirstPageLoading && propertyPaginationState.items.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MolumulkPrimaryCoralRed)
                    }
                }
            }
            else {
                Box(modifier = Modifier.fillMaxSize()) {
                    PropertyMapScreen(
                        mapType = mapType,
                        items = propertyPaginationState.items,
                        onMarkerClick = { clickedItem ->
                            selectedMapItem = clickedItem
                            if (selectedCategory.equals("New Projects", ignoreCase = true)) {
                                showNewProjectBottomSheet = true
                            } else {
                                showRentBottomSheet = true
                            }
                        }
                    )

                    Column(modifier = Modifier.fillMaxWidth().padding(paddingValues).align(Alignment.TopCenter).background(Color.Transparent)) {
                        HomeScreenSearchBar(
                            searchText = filterState.locationQuery,
                            onSearchTextChange = { viewModel.updateFilterState(locationQuery = it) },
                            selectedFilterCount = selectedFilterCount,
                            onFilterClick = { navController.navigate(Route.Filter.route) }
                        )
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(filterChips) { chip ->
                                val chipText = when (chip) {
                                    "Category" -> categoryChipText
                                    "Property type" -> propertyTypeChipText
                                    "Price" -> priceChipText
                                    "Beds & Baths" -> bedsBathsChipText
                                    "Amenities" -> amenitiesChipText
                                    else -> chip
                                }

                                val isSelected = when (chip) {
                                    "Category" -> selectedListingTypeId != null
                                    "Property type" -> selectedPropertyTypeId != null
                                    "Price" -> selectedPriceRange != defaultPriceRange
                                    "Beds & Baths" -> selectedBeds != null || selectedBaths != null
                                    "Amenities" -> selectedAmenityIds.isNotEmpty()
                                    else -> false
                                }

                                HomeScreenFilterChip(
                                    text = chipText,
                                    isSelected = isSelected,
                                    onClick = {
                                        when (chip) {
                                            "Category" -> showCategoryBottomSheet = true
                                            "Property type" -> showPropertyTypeBottomSheet = true
                                            "Price" -> showPriceBottomSheet = true
                                            "Beds & Baths" -> showBedsAndBathsBottomSheet = true
                                            "Amenities" -> showAmenitiesBottomSheet = true
                                        }
                                    }
                                )
                            }
                        }
                        if (propertyPaginationState.items.isEmpty() && !propertyPaginationState.isFirstPageLoading) {
                            val isNewProjectsCategory = selectedCategory.equals("New Projects", ignoreCase = true)
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Column(modifier = Modifier.padding(top = 8.dp)
                                        .wrapContentWidth()
                                        .background(color = MolumulkCharcoal.copy(alpha = 0.85f), shape = RoundedCornerShape(99.dp))
                                        .border(width = 1.dp, color = MolumulkBorderDivider, shape = RoundedCornerShape(99.dp))
                                        .padding(horizontal = 16.dp, vertical = 7.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AppTextMedium(
                                        text = if (isNewProjectsCategory) "No Projects found here" else "No Properties found here",
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(paddingValues).padding(bottom = 20.dp)
                    ) {
                        MapViewBottomFloatingButtons(
                            onTravelTimeClick = { navController.navigate(Route.TravelTime.route) },
                            onNotificationCLick = { navController.navigate(Route.Notifications.route) },
                            onListViewCLick = { isHomeMainView = true },
                            onMaptypeCLick = { showMapTypeBottomSheet = true },
                            onPriceMapCLick = { navController.navigate(Route.PriceMap.route) }
                        )
                    }
                }
            }
        }

        if (showCategoryBottomSheet) {
            CategoryFilterBottomSheet(
                listingTypes = listingTypes,
                initialSelectedId = selectedListingTypeId,
                initialShowCommercialOnly = filterState.showCommercialOnly,
                onDismiss = {
                    showCategoryBottomSheet = false
                },
                onShowProperties = { selectedItem, showCommercialOnly ->
                    showCategoryBottomSheet = false
                    viewModel.resetFilterState(defaultListingTypeId = selectedItem._id)
                    viewModel.updateFilterState(
                        selectedPriceRange = defaultPriceRange,
                        showCommercialOnly = showCommercialOnly
                    )

                    Log.d("CATEGORY_FILTER", "Selected id = ${selectedItem._id}")
                    Log.d("CATEGORY_FILTER", "Selected name = ${selectedItem.name}")
                }
            )
        }

        if (showPropertyTypeBottomSheet) {
            PropertyTypeBottomSheet(
                propertyTypes = propertyTypes,
                initialSelectedId = selectedPropertyTypeId,
                onDismiss = {
                    showPropertyTypeBottomSheet = false
                },
                onClear = {
                    viewModel.updateFilterState(
                        selectedPropertyTypeId = null
                    )
                },
                onShowProperties = { selectedItem ->
                    showPropertyTypeBottomSheet = false
                    viewModel.updateFilterState(
                        selectedPropertyTypeId = selectedItem?._id
                    )

                    Log.d("PROPERTY_TYPE_FILTER", "Selected = ${selectedItem?._id}")
                }
            )
        }

        if (showPriceBottomSheet) {
            PriceBottomSheet(
                minPrice = minPrice,
                maxPrice = maxPrice,
                initialRange = selectedPriceRange,
                onDismiss = {
                    showPriceBottomSheet = false
                },
                onClear = {
                    viewModel.updateFilterState(
                        selectedPriceRange = defaultPriceRange
                    )
                },
                onShowProperties = { selectedRange ->
                    showPriceBottomSheet = false
                    viewModel.updateFilterState(
                        selectedPriceRange = selectedRange
                    )

                    Log.d("PRICE_FILTER", "Min = ${selectedRange.start.toInt()}")
                    Log.d("PRICE_FILTER", "Max = ${selectedRange.endInclusive.toInt()}")
                }
            )
        }

        if (showBedsAndBathsBottomSheet) {
            BedsAndBathsBottomSheet(
                initialBeds = selectedBeds,
                initialBaths = selectedBaths,
                onDismiss = {
                    showBedsAndBathsBottomSheet = false
                },
                onClear = {
                    viewModel.updateFilterState(
                        selectedBeds = null,
                        selectedBaths = null
                    )
                },
                onShowProperties = { beds, baths ->
                    showBedsAndBathsBottomSheet = false
                    viewModel.updateFilterState(
                        selectedBeds = beds,
                        selectedBaths = baths
                    )

                    Log.d("BEDS_BATHS_FILTER", "Beds = $beds")
                    Log.d("BEDS_BATHS_FILTER", "Baths = $baths")
                }
            )
        }

        if (showAmenitiesBottomSheet) {
            AmenitiesBottomSheet(
                amenities = amenities,
                initialSelectedIds = selectedAmenityIds,
                onDismiss = {
                    showAmenitiesBottomSheet = false
                },
                onClear = {
                    viewModel.updateFilterState(
                        selectedAmenityIds = emptySet()
                    )
                },
                onShowProperties = { selectedItems ->
                    showAmenitiesBottomSheet = false
                    viewModel.updateFilterState(
                        selectedAmenityIds = selectedItems.mapNotNull { it._id }.toSet()
                    )

                    Log.d("AMENITIES_FILTER", "Selected = ${selectedItems.map { it._id }}")
                }
            )
        }

        var selectedAlertFrequencyValue by rememberSaveable { mutableStateOf("") }

        CreateAlertBottomSheet(
            showBottomSheet = showCreateAlertDialog,
            alertfrequencies = alertfrequencies,
            selectedFrequencyValue = selectedAlertFrequencyValue,
            onDismiss = { showCreateAlertDialog = false },
            snackbarManager = snackbarManager,
            onCreateAlert = { alertName, frequencyValue ->

                selectedAlertFrequencyValue = frequencyValue // changed
                val selectedListingTypeIdForAlert = selectedListingTypeId

                val request = buildMap<String, Any> {
                    put("alertName", alertName)
                    put("frequency", frequencyValue)

                    // alertType — listing type id (only if selected)
                    selectedListingTypeIdForAlert?.let { put("alertType", it) }

                    val criteria = mutableMapOf<String, Any>()

                    // location — only if user typed a search query
                    val locationQuery = filterState.locationQuery.trim()
                    if (locationQuery.isNotBlank()) {
                        criteria["location"] = locationQuery
                    }

                    // property type (only if selected)
                    selectedPropertyTypeId?.let { criteria["propertyType"] = it }

                    // bedrooms (only if selected)
                    selectedBeds?.let { criteria["bedrooms"] = it }

                    // bathrooms (only if selected)
                    selectedBaths?.let { criteria["bathrooms"] = it }

                    // price range (only if user changed from default)
                    val effectiveRange = selectedPriceRange
                    if (effectiveRange != defaultPriceRange) {
                        criteria["priceRange"] = mapOf(
                            "min" to effectiveRange.start.toInt(),
                            "max" to effectiveRange.endInclusive.toInt()
                        )
                    }

                    // amenities (only if any selected)
                    if (selectedAmenityIds.isNotEmpty()) {
                        criteria["amenities"] = selectedAmenityIds.toList()
                    }

                    // furnished status (only if selected in filter)
                    filterState.selectedFurnishingId?.takeIf { it.isNotBlank() }
                        ?.let { criteria["furnished"] = it }

                    // Default values for unimplemented filters
//                    criteria["petFriendly"] = false
//                    criteria["waterfront"] = false
                    criteria["completionStatus"] = "all"
                    if (criteria.isNotEmpty()) {
                        put("searchCriteria", criteria)
                    }
                }

                userViewModel.createUserAlert(
                    token = TokenManager.accessToken,
                    request = request
                ) { result ->
                    when (result) {
                        is com.app.propertyfinder.api.network.ApiResult.Success -> {
                            snackbarManager.showSuccess(result.body.message.toString())
                        }

                        is com.app.propertyfinder.api.network.ApiResult.Error -> {
                            snackbarManager.showError(result.message)
                        }
                    }
                }

                showCreateAlertDialog = false
            }
        )

        if (showMapTypeBottomSheet) {
            MapTypeBottomSheet(
                selectedType = mapType,
                onDismiss = { showMapTypeBottomSheet = false },
                onSelected = {
                    mapType = it
                    showMapTypeBottomSheet = false
                }
            )
        }

        if (showNewProjectBottomSheet) {
            NewProjectBottomSheet(
                selectedItem = selectedMapItem,
                onPropertyClick = {
                    showNewProjectBottomSheet = false
                    selectedMapItem?.id?.let { clickedId ->
                        navController.navigate(
                            Route.DevPropertiesDetail.createRoute(
                                propertyId = clickedId,
                                fromScreen = "home"
                            )
                        )
                    }
                },
                onDismiss = { showNewProjectBottomSheet = false }
            )
        }

        if (showRentBottomSheet) {
            val mapItemId = selectedMapItem?.id
            val mapItemIsSaved = if (mapItemId != null) {
                when {
                    unsavedPropertyIds.contains(mapItemId) -> false
                    savedPropertyIds.contains(mapItemId) -> true
                    else -> selectedMapItem?.isSaved == true
                }
            } else {
                false
            }

            RentBottomSheet(
                selectedItem = selectedMapItem,
                sortOptions = sortByOptions,
                selectedSortValue = filterState.sortBy,
                onSortSelected = { sortValue ->
                    viewModel.updateFilterState(sortBy = sortValue)
                },
                isSaved = mapItemIsSaved,
                onSaveClick = {
                    val clickedPropertyId = selectedMapItem?.id ?: return@RentBottomSheet
                    val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                    if (loginType == Constants.LOGIN_USER) {
                        val currentlySaved = when {
                            unsavedPropertyIds.contains(clickedPropertyId) -> false
                            savedPropertyIds.contains(clickedPropertyId) -> true
                            else -> selectedMapItem?.isSaved == true
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
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(result.message)
                                        Log.e("ADD_TO_SAVED_MAP", "Error: ${result.message}")
                                    }
                                }
                            }
                        }
                    } else {
                        snackbarManager.showError("Please signup and login for access to ${context.getString(R.string.guest_user_description)}")
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
    }
}
@Composable
fun HomeScreenSearchBar(
    searchText: String = "",
    onSearchTextChange: (String) -> Unit = {},
    selectedFilterCount: Int = 0,
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
                                modifier = Modifier.fillMaxWidth()) {
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

            Box(
                modifier = Modifier.wrapContentSize(),
                contentAlignment = Alignment.TopEnd
            )
            {
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
@Composable
fun HomeScreenFilterChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else Color.Transparent
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
            )
            .clickable(onClick = onClick)
            .height(34.dp)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
            AppTextSemiBold(
                text = text,
                color = textColor,
                fontSize = 12.sp
            )
            AppSpacerH(4)
            AppImage(
                resId = R.drawable.arrow_down,
                modifier = Modifier.size(8.dp).padding(top = 1.dp),
                tint = textColor
            )
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
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
            // Search Item (Selected)
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.search,
                label = stringResource(R.string.search),
                isSelected = true,
                onClick = {
                    navController.navigate(Route.Home.route) {
                        launchSingleTop = true
                    }
                }
            )

            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
            if (loginType == Constants.LOGIN_USER) {
                // Saved Item (Unselected)
                BottomNavItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.heart,
                    label = stringResource(R.string.saved),
                    isSelected = false,
                    onClick = {
                        navController.navigate(Route.Saved.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // Account Item (Unselected)
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.account,
                label = stringResource(R.string.account),
                isSelected = false,
                onClick = {
                    navController.navigate(Route.Account.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun BottomNavItem(
    modifier: Modifier,
    iconRes: Int? = null,
    iconVector: ImageVector? = null,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // NEW: Active Tab uses MolumulkPrimaryCoralRed (#FF5A5F), Inactive uses MolumulkMutedSlate (#667085)
    val contentColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkMutedSlate

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        } else if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
        }

        AppSpacerV(height = 4)

        AppTextMedium(
            text = label,
            color = contentColor,
            fontSize = 11.sp
        )
    }
}

@Composable
fun HomeScreenFloatingButtons(screen : String,
    onCreateAlertClick: () -> Unit = {},
    onMapViewClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent),
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
        if (screen == "home") {
            Button(
                onClick = onCreateAlertClick,
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppImage(
                        resId = R.drawable.create,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )

                    AppSpacerH(width = 8)
                    AppTextBold(text = "Create alert", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun MapViewBottomFloatingButtons(
    onTravelTimeClick: () -> Unit = {},
    onNotificationCLick: () -> Unit = {},
    onListViewCLick: () -> Unit = {},
    onMaptypeCLick: () -> Unit = {},
    onPriceMapCLick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .clickable { onTravelTimeClick() },
            contentAlignment = Alignment.Center
        ) {
            AppImage(R.drawable.ic_travel_time, modifier = Modifier.size(20.dp))
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .clickable { onNotificationCLick() },
            contentAlignment = Alignment.Center
        ) {
            AppImage(R.drawable.notifictionsetting, modifier = Modifier.size(20.dp))
        }
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
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .clickable { onMaptypeCLick() },
            contentAlignment = Alignment.Center
        ) {
            AppImage(R.drawable.ic_map_type, modifier = Modifier.size(20.dp))
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEEEEE))
                .clickable { onPriceMapCLick() },
            contentAlignment = Alignment.Center
        ) {
            AppImage(R.drawable.ic_price_map, modifier = Modifier.size(20.dp))
        }
    }
}



