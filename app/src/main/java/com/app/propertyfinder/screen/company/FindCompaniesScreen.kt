package com.app.propertyfinder.screen.company

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgencyAddress
import com.app.propertyfinder.api.dto.response.AgencySearchItem
import com.app.propertyfinder.api.dto.response.AgencyStatistics
import com.app.propertyfinder.api.dto.response.ProjectAuthorizedAgency
import com.app.propertyfinder.api.dto.response.services
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.screen.agent.FilterChipItem
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.SearchTopBar
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val AGENCY_LOGO_BASE_URL =
    "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindCompaniesScreen(
    navController: NavHostController,
    propertyViewModel: PropertyViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel()
) {

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedServiceId by remember { mutableStateOf<String?>(null) }
    var selectedServiceName by remember { mutableStateOf<String?>(null) }
    var openServiceSheet by remember { mutableStateOf(false) }
    var selectedSortOption by remember { mutableStateOf("Default") }
    var openSortSheet by remember { mutableStateOf(false) }
    val sortOptions = listOf(
        "Default",
        "Properties for Sale (High to Low)",
        "Properties for Rent (High to Low)",
        "Name (A-Z)",
        "Name (Z-A)"
    )
    val keyboardController = LocalSoftwareKeyboardController.current

    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val services = masterDataState.data?.data?.services.orEmpty()
    val authorizedAgenciesJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("authorized_agencies_json")
    val projectLocation = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("project_location").orEmpty()
    val authorizedAgenciesFromProject: List<ProjectAuthorizedAgency> = remember(authorizedAgenciesJson) {
        if (authorizedAgenciesJson.isNullOrBlank()) emptyList()
        else runCatching {
            Gson().fromJson<List<ProjectAuthorizedAgency>>(
                authorizedAgenciesJson,
                object : TypeToken<List<ProjectAuthorizedAgency>>() {}.type
            )
        }.getOrDefault(emptyList())
    }
    val isProjectAuthorizedMode = authorizedAgenciesFromProject.isNotEmpty()

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("services") { }
        }
    }

    LaunchedEffect(searchQuery, selectedServiceId) {
        if (!isProjectAuthorizedMode) {
            userViewModel.getAgencySearch(
                minRating = 0,
                page = 1,
                limit = 15,
                name = searchQuery.trim().takeIf { it.isNotBlank() },
                serviceNeeded = selectedServiceId
            )
        }
    }

    val agencySearchState by userViewModel.agencySearchState.collectAsStateWithLifecycle()
    val agencies = if (isProjectAuthorizedMode) {
        authorizedAgenciesFromProject
            .map { it.toAgencySearchItem(projectLocation) }
            .filter { agency ->
                val query = searchQuery.trim()
                query.isBlank() || agency.name.orEmpty().contains(query, ignoreCase = true)
            }
    } else {
        agencySearchState.data?.data?.items.orEmpty()
    }

    val sortedAgencies = remember(agencies, selectedSortOption) {
        when (selectedSortOption) {
            "Properties for Sale (High to Low)" -> {
                agencies.sortedByDescending { it.statistics?.totalSaleListings ?: 0 }
            }
            "Properties for Rent (High to Low)" -> {
                agencies.sortedByDescending { it.statistics?.totalRentListings ?: 0 }
            }
            "Name (A-Z)" -> {
                agencies.sortedBy { it.name.orEmpty().lowercase() }
            }
            "Name (Z-A)" -> {
                agencies.sortedByDescending { it.name.orEmpty().lowercase() }
            }
            else -> agencies
        }
    }

    if (openServiceSheet) {
        CompanyServiceNeededBottomSheet(
            options = services,
            selectedId = selectedServiceId,
            onDismiss = { openServiceSheet = false },
            onSelect = { option ->
                selectedServiceId = option?.value
                selectedServiceName = option?.name
                openServiceSheet = false
            }
        )
    }

    if (openSortSheet) {
        CompanySortByBottomSheet(
            options = sortOptions,
            selectedOption = selectedSortOption,
            onDismiss = { openSortSheet = false },
            onSelect = { option ->
                selectedSortOption = option
                openSortSheet = false
            }
        )
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                // Search Bar View
                TopAppBar(
                    title = {
                        SearchTopBar(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            isSearchActive = isSearchActive,
                            onBackClick = { isSearchActive = false },
                            onSearchDone = {
                                keyboardController?.hide()
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            } else {
                // Normal Top Bar
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Agencies", fontSize = 20.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!isSearchActive && !isProjectAuthorizedMode) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChipItem(
                            text = selectedServiceName ?: "Service needed",
                            isSelected = !selectedServiceId.isNullOrBlank(),
                            onClick = { openServiceSheet = true }
                        )
                    }
                    item {
                        FilterChipItem(
                            text = if (selectedSortOption == "Default") "Sort by" else selectedSortOption,
                            isSelected = selectedSortOption != "Default",
                            onClick = { openSortSheet = true }
                        )
                    }
                }
            }
            if (agencySearchState.isLoading && !isProjectAuthorizedMode) {
                /*Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingDialog()
                }*/
            } else {
                // Companies List
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sortedAgencies) { agency ->
                        if (isProjectAuthorizedMode) {
                            // Compact card matching AuthorisedAgenciesItem style
                            AuthorisedAgencyCard(
                                agency = agency,
                                onClick = {
                                    agency.id?.let { navController.navigate("company_detail/$it") }
                                }
                            )
                        } else {
                            CompanyCard(
                                company = agency,
                                onClick = {
                                    agency.id?.let { navController.navigate("company_detail/$it") }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun ProjectAuthorizedAgency.toAgencySearchItem(location: String = ""): AgencySearchItem {
    return AgencySearchItem(
        id = id,
        name = agencyName,
        logo = profilePicture,
        address = AgencyAddress(fullAddress = location.ifBlank { null }),
        statistics = AgencyStatistics(
            totalActiveListings = statistics?.totalActiveListings ?: 0,
            totalSaleListings = 0,
            totalRentListings = 0
        ),
        totalAgents = 0,
        totalSuperAgents = 0
    )
}

@Composable
fun CompanyCard(company: AgencySearchItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Logo, Name/Location, Arrow
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                // Logo
                val logoUrl = buildImageUrl(
                    AGENCY_LOGO_BASE_URL,
                    company.logo
                )
                AppSubComposeAsyncImage(
                    model = logoUrl,
                    modifier = Modifier.size(60.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                AppSpacerH(10)

                // Name and Location
                Column(modifier = Modifier.weight(1f)) {
                    AppTextBold(text = company.name.orEmpty(), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.location), // Assuming existing location icon
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppTextRegular(
                            text = company.address?.fullAddress
                                ?: company.address?.city
                                ?: "-",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                
                // Arrow
                Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Details",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            AppSpacerV(15)
            
            // Stats Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompanyStatChip(
                    label = "For Sale",
                    value = company.statistics?.totalSaleListings?.toString() ?: "0",
                    modifier = Modifier.weight(1f)
                )
                CompanyStatChip(
                    label = "For rent",
                    value = company.statistics?.totalRentListings?.toString() ?: "0",
                    modifier = Modifier.weight(1f)
                )
                CompanyStatChip(
                    label = "Agents",
                    value = company.totalAgents?.toString() ?: "0",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Stats Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompanyStatChip(
                    label = "Nationality",
                    value = company.address?.country ?: "-",
                    modifier = Modifier.weight(1f)
                )
                CompanyStatChip(
                    label = "Superagents",
                    value = company.totalSuperAgents?.toString() ?: "0",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun AuthorisedAgencyCard(agency: AgencySearchItem, onClick: () -> Unit) {
    val logoUrl = buildImageUrl(AGENCY_LOGO_BASE_URL, agency.logo)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                AppSubComposeAsyncImage(
                    model = logoUrl,
                    modifier = Modifier.size(45.dp),
                    contentScale = ContentScale.Crop
                )
            }
            AppSpacerH(12)

            Column(modifier = Modifier.weight(1f)) {
                AppTextMedium(
                    text = agency.name.orEmpty(),
                    fontSize = 14.sp,
                    color = Color.Black
                )
                AppSpacerV(2)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = "location",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    AppSpacerH(3)
                    AppTextMedium(
                        text = agency.address?.fullAddress
                            ?: agency.address?.city
                            ?: "-",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "right_arrow",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanyServiceNeededBottomSheet(
    options: List<services>,
    selectedId: String?,
    onDismiss: () -> Unit,
    onSelect: (services?) -> Unit
) {
    var selectedServiceId by remember(selectedId) { mutableStateOf(selectedId) }

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
                        text = "Service needed",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            AppSpacerV(20)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { option ->
                    val isSelected = option.value == selectedServiceId
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color.Black else Background,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(onClick = {
                                selectedServiceId = if (isSelected) null else option.value
                            })
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextSemiBold(
                            text = option.name.orEmpty(),
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
                val hasSelection = !selectedServiceId.isNullOrEmpty()
                AppTextBold(
                    text = "Clear",
                    color = if (hasSelection) Color.Black else Color(0xFF9E9E9E),
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedServiceId = null
                    }
                )
            }

            AppSpacerV(25)

            PrimaryRoundButton(
                text = "Show Companies",
                cornerRadius = 99.dp,
                containerColor = PrimaryColor,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val selectedService = options.firstOrNull { it.value == selectedServiceId }
                    onSelect(selectedService)
                }
            )

            AppSpacerV(16)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompanySortByBottomSheet(
    options: List<String>,
    selectedOption: String,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
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
                        color = Color.Black
                    )
                }
            }

            AppSpacerV(20)

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { option ->
                    val isSelected = option == selectedOption
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color.Black else Background,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(onClick = {
                                onSelect(option)
                            })
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextSemiBold(
                            text = option,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            AppSpacerV(25)
        }
    }
}

@Composable
fun CompanyStatChip(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF5F5F5)) // Light gray background
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextRegular(text = "$label : ", fontSize = 14.sp, color = Color.Gray)
            AppTextBold(text = value, fontSize = 14.sp, color = Color.Black)
        }
    }
}

@Preview
@Composable
fun FindCompaniesScreenPreview() {
    PropertyFinderTheme {
        FindCompaniesScreen(navController = rememberNavController())
    }
}
