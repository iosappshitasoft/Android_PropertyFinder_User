package com.app.propertyfinder.screen.agent

import android.util.Log
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.ListingType
import com.app.propertyfinder.api.dto.response.PropertyListItem
import com.app.propertyfinder.api.dto.response.sortByProperty
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.screen.HomeScreenFilterChip
import com.app.propertyfinder.screen.Property
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.BotAmenityItem
import com.app.propertyfinder.ui.components.ColorTagBox
import com.app.propertyfinder.ui.components.ColorTagBoxWithImage
import com.app.propertyfinder.ui.components.HomePropertyItem
import com.app.propertyfinder.ui.components.getTimeAgo
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Agents_Properties(navController: NavHostController, agentId: String) {
    val userViewModel: UserViewModel = hiltViewModel()
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val directoryPropertiesState by propertyViewModel.directoryPropertiesState.collectAsStateWithLifecycle()
    val savedPropertiesState by userViewModel.savedPropertiesState.collectAsStateWithLifecycle()
    val snackbarManager = rememberSnackbarManager()

    var savedPropertyIds by remember { mutableStateOf(setOf<String>()) }
    var unsavedPropertyIds by remember { mutableStateOf(setOf<String>()) }

    LaunchedEffect(Unit) {
        // Used by add/remove "saved" endpoints.
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
    }

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("listingtypes,sortbyproperty") { /* ignore */ }
        }
    }

    LaunchedEffect(TokenManager.accessToken) {
        if (TokenManager.accessToken.isNotBlank()) {
            // Fetch saved IDs once so we can reflect correct "heart" state on load.
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

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var listingTypeBottomSheet by remember { mutableStateOf(false) }
    var shortByBottomSheet by remember { mutableStateOf(false) }

    var selectedListingTypeId by remember { mutableStateOf<String?>(null) }
    var selectedSortByValue by remember { mutableStateOf<String?>(null) }

    val listingTypes = masterDataState.data?.data?.listingTypes
        .orEmpty()
        .filter { !it.name.equals("New Projects", ignoreCase = true) }
    val sortByOptions = masterDataState.data?.data?.sortByProperty.orEmpty()

    LaunchedEffect(listingTypes) {
        if (listingTypes.isNotEmpty() && selectedListingTypeId == null) {
            selectedListingTypeId = listingTypes.first()._id
        }
    }

    val selectedListingTypeName =
        listingTypes.firstOrNull { it._id == selectedListingTypeId }?.name.orEmpty()
    val selectedSortByName =
        sortByOptions.firstOrNull { it.value == selectedSortByValue }?.name.orEmpty()

    LaunchedEffect(agentId, selectedListingTypeId, selectedSortByValue) {
        val listingTypeId = selectedListingTypeId
        if (agentId.isNotBlank() && !listingTypeId.isNullOrBlank()) {
            propertyViewModel.loadDirectoryProperties(
                listingTypeId = listingTypeId,
                agentId = agentId,
                sortBy = selectedSortByValue,
                page = 1,
                limit = 20
            )
        }
    }
    val properties = directoryPropertiesState.data?.data?.properties.orEmpty()

    val filterChips = listOf("Listing type", "Sort by")

    // Don't cache with `remember` here: `properties` loads asynchronously via `propertyData`
    // and must re-render as soon as the response arrives.
    val filteredProperties =
        if (searchQuery.isBlank()) properties
        else properties.filter {
            it.title.orEmpty().contains(searchQuery, ignoreCase = true) ||
                it.location?.fullAddress.orEmpty().contains(searchQuery, ignoreCase = true)
        }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                // Search Bar View
                TopAppBar(
                    title = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                AppTextRegular(
                                    text = "Search agents...",
                                    color = Color.Gray
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            isSearchActive = false
                            searchQuery = "" // Clear search on close if desired, or keep it
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Close Search",
                                tint = Color.Black
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            } else {
                // Normal Top Bar
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Agents", fontSize = 20.sp) },
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


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                // Filter Chips
                LazyRow(
                    modifier = Modifier.fillMaxWidth().background(Background),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterChips) { chip ->
                        val chipText = when (chip) {
                            "Listing type" -> selectedListingTypeName.ifBlank { "Listing type" }
                            "Sort by" -> selectedSortByName.ifBlank { "Sort by" }
                            else -> chip
                        }
                        val isChipSelected = when (chip) {
                            "Listing type" -> !selectedListingTypeId.isNullOrBlank()
                            "Sort by" -> !selectedSortByValue.isNullOrBlank()
                            else -> false
                        }
                        HomeScreenFilterChip(
                            text = chipText,
                            isSelected = isChipSelected,
                            onClick = {
                                when (chip) {
                                    "Listing type" -> listingTypeBottomSheet = true
                                    "Sort by" -> shortByBottomSheet = true
                                }
                            }
                        )
                    }
                }


                // Property List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background),
                    contentPadding = PaddingValues(bottom = 80.dp), // Space for floating buttons
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    item {
                        // Results count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .padding(top = 10.dp, bottom = 10.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            AppTextBold(text = "${filteredProperties.size} properties are here", fontSize = 15.sp)
                        }
                    }
                    if (directoryPropertiesState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = PrimaryColor)
                            }
                        }
                    } else if (filteredProperties.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                AppTextRegular(text = "No properties found", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                    }
                    items(filteredProperties) { property ->
                        val propertyId = property.id
                        val isSaved = when {
                            propertyId != null && unsavedPropertyIds.contains(propertyId) -> false
                            propertyId != null && savedPropertyIds.contains(propertyId) -> true
                            else -> property.isSaved == true
                        }
                        AgentPropertyItem(
                            property = property,
                            onClick = { clickedId ->
                                if (clickedId.isNotBlank()) {
                                    navController.navigate("property_detail/$clickedId")
                                }
                            },
                            onSaveClick = { clickedPropertyId ->
                                val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                                if (loginType != Constants.LOGIN_USER) {
                                    snackbarManager.showError("Please signup and login to save properties")
                                } else if (clickedPropertyId.isBlank()) {
                                    // Nothing to save/remove.
                                } else {

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
                                }
                            },
                            isSaved = isSaved
                        )
                    }
                }
            }
        }
    }

    if (listingTypeBottomSheet) {
        AgentListingTypeBottomSheet(
            listingTypes = listingTypes,
            initialSelectedId = selectedListingTypeId,
            onDismiss = { listingTypeBottomSheet = false },
            onClear = {
                selectedListingTypeId = listingTypes.firstOrNull()?._id
            },
            onShowProperties = { selectedItem ->
                selectedListingTypeId = selectedItem?._id
                listingTypeBottomSheet = false
            }
        )
    }

    if (shortByBottomSheet) {
        AgentSortByBottomSheet(
            options = sortByOptions,
            selectedValue = selectedSortByValue,
            onDismiss = { shortByBottomSheet = false },
            onClear = {
                selectedSortByValue = null
                shortByBottomSheet = false
            },
            onShowProperties = { selected ->
                selectedSortByValue = selected?.value
                shortByBottomSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentListingTypeBottomSheet(
    listingTypes: List<ListingType>,
    initialSelectedId: String?,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (ListingType?) -> Unit
) {
    if (listingTypes.isEmpty()) return

    val defaultListingTypeId = listingTypes.firstOrNull()?._id

    var selectedId by remember(initialSelectedId, listingTypes) { mutableStateOf(initialSelectedId) }

    val selectedItem = listingTypes.firstOrNull { it._id == selectedId }

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
                        text = stringResource(R.string.category),
                        fontSize = 20.sp,
                        color = TextPrimary
                    )
                }
            }

            AppSpacerV(20)

            listingTypes.forEachIndexed { index, item ->
                val isSelected = selectedId == item._id

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                        .background(
                            if (isSelected) TextPrimary else Background,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clickable {
                            selectedId = item._id
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

                if (index != listingTypes.lastIndex) {
                    AppSpacerV(10)
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val canReset =
                    !defaultListingTypeId.isNullOrEmpty() && selectedId != defaultListingTypeId
                AppTextBold(
                    text = "Clear",
                    color = if (canReset) Color.Black else Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = canReset) {
                        selectedId = defaultListingTypeId
                        onClear()
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = stringResource(R.string.show_properties),
                modifier = Modifier.fillMaxWidth()
            ) {
                onShowProperties(selectedItem ?: listingTypes.firstOrNull())
            }

            AppSpacerV(16)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentSortByBottomSheet(
    options: List<sortByProperty>,
    selectedValue: String?,
    onDismiss: () -> Unit,
    onClear: () -> Unit,
    onShowProperties: (sortByProperty?) -> Unit
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
                modifier = Modifier
                    .fillMaxWidth(),
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
                        onClear()
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = stringResource(R.string.show_properties),
                cornerRadius = 99.dp,
                containerColor = PrimaryColor,
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedItem = options.firstOrNull { it.value == selectedId }
                onShowProperties(selectedItem)
            }

            AppSpacerV(16)
        }
    }
}

@Composable
fun AgentPropertyItem(
    property: PropertyListItem,
    onClick: (String) -> Unit,
    onSaveClick: (String) -> Unit = {},
    isSaved: Boolean
) {
    val propertyId = property.id.orEmpty()
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 320.dp).padding(horizontal = 16.dp, vertical = 8.dp).
        clickable {
            if (propertyId.isNotBlank()) onClick(propertyId)
        },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(modifier = Modifier) {
            val imageBaseUrl = "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/"
            val imageUrl = property.images.firstOrNull { it.isPrimary == true }?.url
                ?: property.images.firstOrNull()?.url

            Box(
                modifier = Modifier.fillMaxWidth().padding(end = 3.dp, start = 3.dp, top = 3.dp).height(210.dp)
                    .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
            ) {
                SubcomposeAsyncImage(
                    model = if (imageUrl.isNullOrBlank()) "" else imageBaseUrl + imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                )
                AppImage(
                    resId = R.drawable.damac_tag,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 3.dp, start = 3.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.align(alignment = Alignment.TopEnd).size(50.dp)
                        .padding(5.dp).background(
                            color = if (isSaved) Color(0xFFE91E63) else Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp)
                        ).clickable {
                             if (propertyId.isNotBlank()) onSaveClick(propertyId)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AppImage(
                        resId = R.drawable.heart,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        tint = if (isSaved) Color.White else null
                    )
                }


                // Tags bottom right
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 5.dp, bottom = 5.dp),
                    horizontalAlignment = Alignment.End
                ) {

                        ColorTagBoxWithImage(text = "VERIFIED", colorResource(R.color.light_green),
                            Color.White, R.drawable.verified_user
                        )
                        AppSpacerV(5)

                        ColorTagBoxWithImage(text = "SUPERAGENT", colorResource(R.color.splash_primary),
                            Color.White, R.drawable.agent
                        )
                        AppSpacerV(5)

                        ColorTagBox(text = "VILLA", Color.White, Color.Black)
                        AppSpacerV(5)

                }
                // Text bottom left
                Column(
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    AppTextLight(text = "2 days ago", color = Color.White)
                }

            }
            AppSpacerV(8)

            AppTextMedium(text = property.title.orEmpty().ifBlank { "Property" }, modifier = Modifier.padding(horizontal = 15.dp))
            AppSpacerV(4)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 15.dp)) {
                AppIcon(resId = R.drawable.location)
                AppSpacerH(4)
                AppTextLight(text = property.location?.fullAddress.orEmpty().ifBlank { "Dubai" })
            }
            AppDivider(modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp, color = colorResource(R.color.screen_bg_color))

            // 🔹 Bottom Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppTextLight(text = stringResource(R.string.launch_price),)
                    AppSpacerV(5)
                    AppTextBold(text = "${property.price ?: 0} AED")

                }
            }
            AppSpacerV(10)
                Row(modifier = Modifier.fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
                    BotAmenityItem(
                        icon = R.drawable.bad,
                        text = "${property.bedrooms ?: 0} Bed",
                        modifier = Modifier.weight(1f)
                    )
                    AppSpacerH(4)
                    BotAmenityItem(
                        icon = R.drawable.bath,
                        text = "${property.bathrooms ?: 0} Bath",
                        modifier = Modifier.weight(1f)
                    )
                    AppSpacerH(4)
                    BotAmenityItem(
                        icon = R.drawable.sqft, text = "${property.area?.sqft ?: 0} sqft",
                        modifier = Modifier.weight(1f)
                    )
                }

        }
    }
}