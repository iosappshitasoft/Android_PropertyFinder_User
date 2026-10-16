package com.app.propertyfinder.screen.company

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgencyPropertyItem
import com.app.propertyfinder.api.dto.response.ListingType
import com.app.propertyfinder.api.dto.response.sortByProperty
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.screen.HomeScreenFilterChip
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.app.propertyfinder.ui.components.CompanyPropertyItem
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.material3.TopAppBar
import com.app.propertyfinder.ui.components.SearchTopBar

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyPropertiesScreen(
    navController: NavHostController,
    companyId: String,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {
    var showListingTypeSheet by remember { mutableStateOf(false) }
    var showSortBySheet by remember { mutableStateOf(false) }
    var selectedListingTypeId by remember { mutableStateOf<String?>(null) }
    var selectedSortBy by remember { mutableStateOf<String?>(null) }
    var selectedListingTypeName by remember { mutableStateOf<String?>(null) }
    var selectedSortByName by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val snackbarManager = rememberSnackbarManager()

    var savedPropertyIds by remember { mutableStateOf(setOf<String>()) }
    var unsavedPropertyIds by remember { mutableStateOf(setOf<String>()) }

    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val listingTypes = masterDataState.data?.data?.listingTypes
        .orEmpty()
        .filter { !it.name.equals("New Projects", ignoreCase = true) }
    val sortByList = masterDataState.data?.data?.sortByProperty.orEmpty()

    val directoryPropertiesState by propertyViewModel.directoryPropertiesState.collectAsStateWithLifecycle()
    val savedPropertiesState by userViewModel.savedPropertiesState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
    }

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("listingtypes,sortbyproperty") { }
        }
    }

    LaunchedEffect(listingTypes) {
        if (listingTypes.isNotEmpty() && selectedListingTypeId == null) {
            selectedListingTypeId = listingTypes.first()._id
            selectedListingTypeName = listingTypes.first().name
        }
    }

    LaunchedEffect(TokenManager.accessToken) {
        if (TokenManager.accessToken.isNotBlank()) {
            userViewModel.getSavedProperties(
                token = TokenManager.accessToken,
                page = 1,
                limit = 50
            )
        }
    }

    LaunchedEffect(savedPropertiesState.data) {
        val savedIds = savedPropertiesState.data?.data?.properties
            ?.mapNotNull { it.id }
            .orEmpty()
            .toSet()
        if (savedIds.isNotEmpty() || savedPropertiesState.data != null) {
            savedPropertyIds = savedIds
            unsavedPropertyIds = emptySet()
        }
    }

    LaunchedEffect(companyId, selectedListingTypeId, selectedSortBy, searchQuery) {
        val listingTypeId = selectedListingTypeId
        if (companyId.isNotBlank() && !listingTypeId.isNullOrBlank()) {
            propertyViewModel.loadDirectoryProperties(
                listingTypeId = listingTypeId,
                agencyId = companyId,
                sortBy = selectedSortBy,
                keyword = searchQuery.trim().takeIf { it.isNotBlank() },
                page = 1,
                limit = 20
            )
        }
    }

    val properties = directoryPropertiesState.data?.data?.properties.orEmpty()
    val totalProperties =
        directoryPropertiesState.data?.data?.pagination?.totalProperties ?: properties.size

    if (showListingTypeSheet) {
        CompanyListingTypeBottomSheet(
            listingTypes = listingTypes,
            selectedId = selectedListingTypeId,
            onDismiss = { showListingTypeSheet = false },
            onSelect = { selectedItem ->
                selectedListingTypeId = selectedItem?._id
                selectedListingTypeName = selectedItem?.name
                showListingTypeSheet = false
            },
            onClear = {
                val first = listingTypes.firstOrNull()
                selectedListingTypeId = first?._id
                selectedListingTypeName = first?.name
                showListingTypeSheet = false
            }
        )
    }
    if (showSortBySheet) {
        CompanySortByBottomSheet(
            options = sortByList,
            selectedValue = selectedSortBy,
            onDismiss = { showSortBySheet = false },
            onSelect = { option ->
                selectedSortBy = option?.value
                selectedSortByName = option?.name
                showSortBySheet = false
            }
        )
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { searchQuery = it },
                    isSearchActive = isSearchActive,
                    onBackClick = {
                        isSearchActive = false
                        searchQuery = ""
                    },
                    onSearchDone = {
                        keyboardController?.hide()
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Properties", fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.search),
                                contentDescription = "Search",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
                )
            }
        },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        HomeScreenFilterChip(
                            text = selectedListingTypeName ?: "Property type",
                            isSelected = !selectedListingTypeId.isNullOrBlank(),
                            onClick = { showListingTypeSheet = true }
                        )
                    }
                    item {
                        HomeScreenFilterChip(
                            text = selectedSortByName ?: "Sort by",
                            isSelected = !selectedSortBy.isNullOrBlank(),
                            onClick = { showSortBySheet = true }
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextBold(text = "$totalProperties Properties", fontSize = 15.sp)
                    AppSpacerH(width = 8)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextRegular(text = "0 new", color = TextSecondary, fontSize = 10.sp)
                    }
                }

            if (directoryPropertiesState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingDialog()
                }
            }

            if (!directoryPropertiesState.isLoading && properties.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppTextRegular(
                        text = "No properties found",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(properties) { property ->
                        val propertyId = property.id
                        val isSaved = when {
                            propertyId != null && unsavedPropertyIds.contains(propertyId) -> false
                            propertyId != null && savedPropertyIds.contains(propertyId) -> true
                            else -> property.isSaved == true
                        }
                        CompanyPropertyItem(property = property, onClick = {
                            val id = property.id
                            if (id != null) {
                                navController.navigate("property_detail/$id")
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
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    snackbarManager.showError("Please signup and login to save properties")
                                }
                            },
                            isSaved = isSaved
                        )
                    }
                }
            }
            }
        }
    }
}

public const val PROPERTY_IMAGE_BASE_URL =
    "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/"

public fun buildImageUrl(baseUrl: String, path: String?): String? {
    if (path.isNullOrBlank()) return null
    return if (path.startsWith("http://") || path.startsWith("https://")) path else baseUrl + path
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanySortByBottomSheet(
    options: List<sortByProperty>,
    selectedValue: String?,
    onDismiss: () -> Unit,
    onSelect: (sortByProperty?) -> Unit
) {
    var selectedId by remember(selectedValue) { mutableStateOf(selectedValue) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppImage(
                        resId = R.drawable.bottomsheetdivider,
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )

                    AppSpacerV(15)

                    AppTextMedium(
                        text = "Sort by",
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }
            }

            AppSpacerV(20)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { item ->
                    val isSelected = selectedId == item.value
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .background(
                                if (isSelected) TextPrimary else Background,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable { selectedId = if (isSelected) null else item.value }
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextSemiBold(
                            text = item.name.orEmpty(),
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val hasSelection = !selectedId.isNullOrEmpty()
                AppTextBold(
                    text = "Clear",
                    color = if (hasSelection) Color.Black else Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedId = null
                        onSelect(null)
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = "Show Properties",
                cornerRadius = 99.dp,
                containerColor = PrimaryColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedItem = options.firstOrNull { it.value == selectedId }
                onSelect(selectedItem)
            }

            AppSpacerV(16)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyListingTypeBottomSheet(
    listingTypes: List<ListingType>,
    selectedId: String?,
    onDismiss: () -> Unit,
    onSelect: (ListingType?) -> Unit,
    onClear: () -> Unit
) {
    val defaultListingTypeId = listingTypes.firstOrNull()?._id
    var internalSelectedId by remember(selectedId) { mutableStateOf(selectedId) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppImage(
                        resId = R.drawable.bottomsheetdivider,
                        contentDescription = null,
                        contentScale = ContentScale.None
                    )

                    AppSpacerV(15)

                    AppTextMedium(
                        text = "Listing type",
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }
            }

            AppSpacerV(20)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listingTypes) { item ->
                    val isSelected = internalSelectedId == item._id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .background(
                                if (isSelected) TextPrimary else Background,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable {
                                internalSelectedId = item._id
                            }
                            .padding(horizontal = 15.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextSemiBold(
                            text = item.name.orEmpty(),
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val canReset =
                    !defaultListingTypeId.isNullOrEmpty() &&
                        internalSelectedId != defaultListingTypeId
                AppTextBold(
                    text = "Clear",
                    color = if (canReset) Color.Black else Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = canReset) {
                        internalSelectedId = defaultListingTypeId
                        onClear()
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = "Show Properties",
                cornerRadius = 99.dp,
                containerColor = PrimaryColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedItem =
                    listingTypes.firstOrNull { it._id == internalSelectedId }
                        ?: listingTypes.firstOrNull()
                onSelect(selectedItem)
            }

            AppSpacerV(16)
        }
    }
}

@Composable
private fun AgencyPropertyCard(property: AgencyPropertyItem, onClick: () -> Unit) {
    val primaryImagePath = property.images.firstOrNull { it.isPrimary == true }?.url
        ?: property.images.firstOrNull()?.url
    val imageUrl = buildImageUrl(PROPERTY_IMAGE_BASE_URL, primaryImagePath)

    val title = property.title.orEmpty()
    val locationText = property.location?.fullAddress
        ?: property.location?.city
        ?: "-"
    val priceText = buildString {
        append(property.price?.toString() ?: "0")
        if (!property.currency.isNullOrBlank()) append(" ${property.currency}")
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    AppSubComposeAsyncImage(
                        model = imageUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_placeholder),
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                AppTextBold(text = title, fontSize = 14.sp, modifier = Modifier.fillMaxWidth())
                AppTextMedium(text = locationText, fontSize = 12.sp, color = TextSecondary)
                AppTextBold(text = priceText, fontSize = 13.sp, color = Color.Black)
            }
        }
    }
}
