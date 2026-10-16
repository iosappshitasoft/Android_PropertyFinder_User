package com.app.propertyfinder.screen.developer

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.DeveloperProjectByLocationItem
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.HomePropertyItem
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import com.app.propertyfinder.screen.PriceBottomSheet
import com.app.propertyfinder.screen.BedsAndBathsBottomSheet
import com.app.propertyfinder.api.dto.response.sortbyproject
import com.app.propertyfinder.ui.theme.TextPrimary

// ---------------- MODEL ----------------

data class PropertyItem(
    val title: String,
    val location: String,
    val price: String,
    val status: String,

    // ✅ New fields for Screenshot UI
    val delivery: String,
    val image: Int,
    val developerLogo: Int,

    // ✅ Optional ribbons
    val saleStarted: String? = null,
    val paymentPlan: String? = null
)


// ---------------- SCREEN ----------------

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperDetailScreen(
    navController: NavHostController,
    developerId: String? = null,
    userViewModel: UserViewModel = hiltViewModel(),
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarManager = rememberSnackbarManager()
    val developerBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_DEVELOPER_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/" }
    LaunchedEffect(developerId) {
        developerId?.takeIf { it.isNotBlank() }?.let { id ->
            userViewModel.getDeveloperProfile(id)
        }
    }

    val developerProfileState by userViewModel.developerProfileState.collectAsStateWithLifecycle()
    val developer = developerProfileState.data?.data
    val developerProjectsPagination by propertyViewModel.developerProjectsPaginationState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val sortByOptions = masterDataState.data?.data?.sortbyproject.orEmpty()

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData(
                "propertytypes,listingtypes,amenities,furnishedstatus," +
                        "alertfrequencies,virtualviewingtypes,sqftareasizes,pricerange,deliverydates,sortbyproject"
            ) {}
        }
    }

    var selectedBeds by remember { mutableStateOf<String?>(null) }
    var selectedBaths by remember { mutableStateOf<String?>(null) }

    val minPrice = 100000f
    val maxPrice = 500000000f
    val defaultPriceRange = minPrice..maxPrice
    var selectedPriceRange by remember { mutableStateOf(defaultPriceRange) }

    var selectedSortBy by remember { mutableStateOf<String?>("newest") }

    var showPriceBottomSheet by remember { mutableStateOf(false) }
    var showBedsAndBathsBottomSheet by remember { mutableStateOf(false) }
    var showSortBottomSheet by remember { mutableStateOf(false) }

    val bedsBathsChipText = when {
        selectedBeds != null && selectedBaths != null -> {
            val bedText = if (selectedBeds == "0") "Studio" else "$selectedBeds Beds"
            "$bedText, $selectedBaths Baths"
        }
        selectedBeds != null -> if (selectedBeds == "0") "Studio" else "$selectedBeds Beds"
        selectedBaths != null -> "$selectedBaths Baths"
        else -> "Beds"
    }

    val priceChipText = if (selectedPriceRange == defaultPriceRange) {
        "Price"
    } else {
        "${selectedPriceRange.start.toInt()} - ${selectedPriceRange.endInclusive.toInt()}"
    }

    val sortChipText = sortByOptions.firstOrNull { it.value == selectedSortBy }?.name ?: "Sort by"

    LaunchedEffect(developerId, selectedBeds, selectedPriceRange, selectedSortBy) {
        developerId?.takeIf { it.isNotBlank() }?.let { id ->
            propertyViewModel.loadDeveloperProjectsFirstPage(
                developerId = id,
                bedrooms = selectedBeds,
                priceMin = if (selectedPriceRange != defaultPriceRange) selectedPriceRange.start.toInt() else null,
                priceMax = if (selectedPriceRange != defaultPriceRange) selectedPriceRange.endInclusive.toInt() else null,
                sortBy = selectedSortBy
            )
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            val lastVisibleItemIndex =
                listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
            .distinctUntilChanged()
            .filter { it }
            .collectLatest {
                val p = propertyViewModel.developerProjectsPaginationState.value
                if (p.hasNextPage && !p.isNextPageLoading && !p.isFirstPageLoading) {
                    propertyViewModel.loadDeveloperProjectsNextPage()
                }
            }
    }

    val projectsCount = when {
        developerProjectsPagination.totalCount > 0 -> developerProjectsPagination.totalCount
        else -> developer?.statistics?.totalProjects ?: 0
    }

    Scaffold(
        containerColor = Color.White,
        snackbarHost = { snackbarManager.CustomSnackbarHost() },
        // ✅ TOP BAR
        topBar = {
            TopAppBar(
                title = {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        AppTextBold(text = developer?.name.orEmpty(), fontSize = 20.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                /*actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, null)
                    }
                }*/
            )
        },

    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize().background(Background)
                ) {
                    // ✅ HEADER IMAGE + FLOATING LOGO
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {

                                Image(
                                    painter = painterResource(id = R.drawable.welcome),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                // Floating Logo
                                Surface(
                                    modifier = Modifier.size(110.dp).align(Alignment.BottomCenter).offset(y = 55.dp),
                                    shape = RoundedCornerShape(18.dp),
                                    shadowElevation = 8.dp,
                                    color = Color.White
                                ) {
                                    AppSubComposeAsyncImage(
                                        model = buildImageUrl(developerBaseUrl, developer?.logo),
                                        contentScale = ContentScale.Fit
                                    )
                                }
                            }
                            AppSpacerV(70)
                        }
                    }

                    // ✅ DESCRIPTION SECTION
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = developer?.aboutUs ?: developer?.description ?: "No description available",
                                textAlign = TextAlign.Center,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Call + Email Buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {

                                // Call Button
                                Surface(
                                    modifier = Modifier.size(55.dp),
                                    shape = CircleShape,
                                    color = Color(0xFFE74C3C)
                                ) {
                                    IconButton(onClick = {
                                        val phoneNumber = developer?.phoneNumber
                                        if (!phoneNumber.isNullOrBlank()) {
                                            try {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:$phoneNumber")
                                                }
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                android.widget.Toast.makeText(context, "Cannot open dialer", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            android.widget.Toast.makeText(context, "Phone number is not available", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Call, null, tint = Color.White)
                                    }
                                }

                                // Email Button
                                Surface(
                                    modifier = Modifier.size(55.dp),
                                    shape = CircleShape,
                                    color = Color.Black
                                ) {
                                    IconButton(onClick = {
                                        val email = developer?.email
                                        if (!email.isNullOrBlank()) {
                                            try {
                                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                    data = Uri.parse("mailto:$email")
                                                }
                                                context.startActivity(Intent.createChooser(intent, "Send Email"))
                                            } catch (e: Exception) {
                                                android.widget.Toast.makeText(context, "No email client found", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        } else {
                                            android.widget.Toast.makeText(context, "Email address is not available", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }) {
                                        Icon(Icons.Default.Email, null, tint = Color.White)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }

                    // ✅ PROJECTS HEADER + FILTERS
                    item {
                        Column(modifier = Modifier.background(Background).padding(start = 16.dp, end = 16.dp, top = 15.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                AppTextBold(
                                    text = "New & Off-Plan Projects",
                                    fontSize = 15.sp
                                )

                                // Badge Pill
                                Surface(
                                    shape = RoundedCornerShape(50.dp),
                                    color = Color.White,
                                    border = BorderStroke(1.dp, Color.LightGray)
                                ) {
                                    AppTextLight(
                                        text = "$projectsCount Projects",
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Filter Chips Row
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                SmallFilterChip(
                                    text = "All",
                                    isSelected = selectedBeds == null && selectedBaths == null && selectedPriceRange == defaultPriceRange && selectedSortBy == "newest",
                                    onClick = {
                                        selectedBeds = null
                                        selectedBaths = null
                                        selectedPriceRange = defaultPriceRange
                                        selectedSortBy = "newest"
                                    }
                                )
                                SmallFilterChip(
                                    text = sortChipText,
                                    isSelected = selectedSortBy != "newest" && selectedSortBy != null,
                                    onClick = { showSortBottomSheet = true }
                                )
                                SmallFilterChip(
                                    text = bedsBathsChipText,
                                    isSelected = selectedBeds != null || selectedBaths != null,
                                    onClick = { showBedsAndBathsBottomSheet = true }
                                )
                                SmallFilterChip(
                                    text = priceChipText,
                                    isSelected = selectedPriceRange != defaultPriceRange,
                                    onClick = { showPriceBottomSheet = true }
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    // ✅ PROJECTS (projectData API by developerId)
                    if (developerProjectsPagination.isFirstPageLoading && developerProjectsPagination.items.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (developerProjectsPagination.items.isEmpty()) {
                        item {
                            Text(
                                text = developerProjectsPagination.errorMessage
                                    ?: "No projects found",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        items(
                            items = developerProjectsPagination.items,
                            key = { it.id.orEmpty() }
                        ) { property ->
                            HomePropertyItem(
                                propertyModel = property,
                                from = "new_project",
                                onClick = { clickedId ->
                                    navController.navigate(
                                        Route.DevPropertiesDetail.createRoute(
                                            propertyId = clickedId,
                                            fromScreen = "developer"
                                        )
                                    )
                                },
                                onSaveClick = { clickedPropertyId ->
                                    val loginType =
                                        PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                                    if (loginType == Constants.LOGIN_USER) {
                                        val currentlySaved = property.isSaved == true
                                        if (currentlySaved) {
                                            userViewModel.removeFromSaved(
                                                token = TokenManager.accessToken,
                                                propertyId = clickedPropertyId
                                            ) { result ->
                                                when (result) {
                                                    is ApiResult.Success -> {
                                                        snackbarManager.showSuccess(
                                                            result.body.message.toString()
                                                        )
                                                    }
                                                    is ApiResult.Error -> {
                                                        snackbarManager.showError(result.message)
                                                        Log.e(
                                                            "DEV_DETAIL_SAVE",
                                                            "Error: ${result.message}"
                                                        )
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
                                                        snackbarManager.showSuccess(
                                                            result.body.message.toString()
                                                        )
                                                    }
                                                    is ApiResult.Error -> {
                                                        snackbarManager.showError(result.message)
                                                        Log.e(
                                                            "DEV_DETAIL_SAVE",
                                                            "Error: ${result.message}"
                                                        )
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
                                isSaved = property.isSaved == true
                            )
                        }
                        if (developerProjectsPagination.isNextPageLoading) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                    item {
                        AppSpacerV(100)
                    }
                }
            }

            // Floating Buttons
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                DeveloperFloatingButtons(
                    onFilterClick = {},
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
                        selectedPriceRange = defaultPriceRange
                    },
                    onShowProperties = { selectedRange ->
                        showPriceBottomSheet = false
                        selectedPriceRange = selectedRange
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
                        selectedBeds = null
                        selectedBaths = null
                    },
                    onShowProperties = { beds, baths ->
                        showBedsAndBathsBottomSheet = false
                        selectedBeds = beds
                        selectedBaths = baths
                    }
                )
            }

            if (showSortBottomSheet) {
                DeveloperSortByBottomSheet(
                    options = sortByOptions,
                    selectedValue = selectedSortBy,
                    onDismiss = {
                        showSortBottomSheet = false
                    },
                    onSelect = { selectedItem ->
                        showSortBottomSheet = false
                        selectedSortBy = selectedItem?.value ?: "newest"
                    }
                )
            }
        }
    }
}

@Composable
private fun ProjectByLocationCard(project: DeveloperProjectByLocationItem) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = project.location ?: project.name ?: "Unknown location",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${project.totalProjects ?: project.count ?: 0} projects",
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}



@Composable
fun DeveloperFloatingButtons(
    onFilterClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onFilterClick,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(10.dp),
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

// ---------------- FILTER CHIP ----------------

@Composable
fun SmallFilterChip(
    text: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, if (isSelected) Color.Transparent else Color.LightGray),
        color = if (isSelected) Color.Black else Color.White,
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.height(33.dp).padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextSemiBold(
                text = text,
                fontSize = 12.sp,
                color = if (isSelected) Color.White else colorResource(id = R.color.textprimary)
            )
            if (text != "All") {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_down),
                    contentDescription = null,
                    modifier = Modifier.size(8.dp),
                    tint = if (isSelected) Color.White else colorResource(id = R.color.textprimary)
                )
            }
        }
    }
}

// ---------------- PROPERTY CARD ----------------

@Composable
fun PropertyCard(property: PropertyItem,navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        onClick = {navController.navigate(Route.DevPropertiesDetail.createRoute("12345id", "developer"))},
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {

        Column {

            // ================= IMAGE SECTION =================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
            ) {

                // Property Image
                Image(
                    painter = painterResource(id = property.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Developer Logo Top-Left
                Surface(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(Alignment.TopStart),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(id = property.developerLogo),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(14.dp)
                            .size(40.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Badges LazyColumn - Bottom-Right
                LazyColumn(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp)
                        .widthIn(max = 200.dp),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                )
                {
                    // OFF-PLAN Badge
                    item {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = property.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Delivery Date Badge
                    item {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.White
                        ) {
                            Text(
                                text = "DELIVERY DATE : ${property.delivery}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Optional SALE STARTED Badge
                    if (property.saleStarted != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFE74C3C)
                            ) {
                                Text(
                                    text = property.saleStarted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    // Optional Payment Plan Badge
                    if (property.paymentPlan != null) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF2ECC71)
                            ) {
                                Text(
                                    text = property.paymentPlan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 1.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }

            // ================= DETAILS SECTION =================
            Column(modifier = Modifier.padding(16.dp)) {

                // Title
                Text(
                    text = property.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Location Row
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Icon(
                        painter = painterResource(id = R.drawable.location),
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = property.location,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Divider(color = Color(0xFFEDEDED))

                Spacer(modifier = Modifier.height(14.dp))

                // Price + WhatsApp Button Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Launch price",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = property.price,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // WhatsApp Button
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = Color(0xFF5AC85A)
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                painter = painterResource(id = R.drawable.whatapp),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Whatsapp",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


// ---------------- PREVIEW ----------------

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showSystemUi = true)
@Composable
fun DeveloperDetailPreview() {
    PropertyFinderTheme {
        DeveloperDetailScreen(
            navController = rememberNavController()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeveloperSortByBottomSheet(
    options: List<sortbyproject>,
    selectedValue: String?,
    onDismiss: () -> Unit,
    onSelect: (sortbyproject?) -> Unit
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
                            .clip(RoundedCornerShape(50.dp))
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

            Button(
                onClick = {
                    val selectedItem = options.firstOrNull { it.value == selectedId }
                    onSelect(selectedItem)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(50)
            ) {
                AppTextSemiBold(text = "Show Projects", color = Color.White, fontSize = 14.sp)
            }

            AppSpacerV(16)
        }
    }
}
