package com.app.propertyfinder.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.*
import com.app.propertyfinder.ui.theme.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.dto.response.HistoricalTransactionItem
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material.icons.filled.Close
import com.app.propertyfinder.api.dto.response.PropertyType
import com.app.propertyfinder.api.dto.response.priceRange as PriceRangeOption


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TransactionOverviewScreen(
    navController: NavHostController,
    viewModel: PropertyViewModel = hiltViewModel()
) {
    val masterDataState = viewModel.masterDataState.collectAsStateWithLifecycle().value
    val historicalTransactionsState = viewModel.historicalTransactionsState.collectAsStateWithLifecycle().value

    var selectedTimePeriod by remember { mutableStateOf("YTD") }
    var isRentedView by remember { mutableStateOf(false) }

    var showPropertyTypeSheet by remember { mutableStateOf(false) }
    var showBedsSheet by remember { mutableStateOf(false) }
    var showPriceSheet by remember { mutableStateOf(false) }
    var showSearchBar by remember { mutableStateOf(false) }

    var selectedPropertyTypeId by remember { mutableStateOf<String?>(null) }
    var selectedPropertyTypeName by remember { mutableStateOf<String?>(null) }
    var selectedBeds by remember { mutableStateOf("") }
    var minPrice by remember { mutableStateOf("") }
    var maxPrice by remember { mutableStateOf("") }
    var searchLocation by remember { mutableStateOf("") }
    var filterTrigger by remember { mutableIntStateOf(0) }

    val propertyTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bedsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val priceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val pullRefreshState = rememberPullToRefreshState()

    // Re-call API when immediate filters, filterTrigger, or searchLocation change
    LaunchedEffect(isRentedView, selectedTimePeriod, filterTrigger, searchLocation, refreshTrigger) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            viewModel.getMasterData("propertytypes,pricerange") {}
        }
        val mappedTimeframe = when (selectedTimePeriod) {
            "YTD" -> "ytd"
            "1 Week" -> "1w"
            "1 Month" -> "1m"
            "3 Months" -> "3m"
            "6 Months" -> "6m"
            "1 Year" -> "1y"
            "3 Year" -> "3y"
            else -> "ytd"
        }
        val requestMap = mutableMapOf<String, Any>(
            "dealType" to (if (isRentedView) "rent" else "sale"),
            "page" to 1,
            "limit" to 25,
            "timeframe" to mappedTimeframe,
            "sortBy" to "newest"
        )
        if (selectedBeds.isNotBlank()) requestMap["bedrooms"] = if (selectedBeds == "Studio") "0" else selectedBeds
        if (!selectedPropertyTypeId.isNullOrBlank()) requestMap["propertyTypeId"] = selectedPropertyTypeId!!
        if (minPrice.isNotBlank()) requestMap["priceMin"] = minPrice.replace(",","").toDoubleOrNull() ?: 0.0
        if (maxPrice.isNotBlank()) requestMap["priceMax"] = maxPrice.replace(",","").toDoubleOrNull() ?: 0.0
        if (searchLocation.isNotBlank()) requestMap["location"] = searchLocation.trim()
        viewModel.getHistoricalTransactions(requestMap)
        isRefreshing = false
    }

    // Derived values — reused in navigation
    val mappedTimeframeForNav = when (selectedTimePeriod) {
        "YTD" -> "ytd"
        "1 Week" -> "1w"
        "1 Month" -> "1m"
        "3 Months" -> "3m"
        "6 Months" -> "6m"
        "1 Year" -> "1y"
        "3 Year" -> "3y"
        else -> "ytd"
    }
    val currentDealType = if (isRentedView) "rent" else "sale"

    val summary = historicalTransactionsState.data?.data?.summary
    val apiTransactions = historicalTransactionsState.data?.data?.transactions ?: emptyList()
    val isLoading = historicalTransactionsState.isLoading == true
    val currency = historicalTransactionsState.data?.data?.transactions?.firstOrNull()?.currency
    val changePercent = summary?.transactionCountChangePercent
    val changePercentText = changePercent?.let {
        "${if (it >= 0) "+" else ""}${it.toInt()}%"
    }
    val isPositive = (changePercent ?: 0.0) >= 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextMedium(text = "Dubai Transactions", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Close Search",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.navigationBarsPadding(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Button(
                    onClick = {
                        navController.navigate(
                            Route.TransactionList.createRoute(currentDealType, mappedTimeframeForNav)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444)
                    ),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    AppTextBold(
                        text = "See all transactions",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { innerPadding ->
        PullToRefreshBox(
            state = pullRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                refreshTrigger++
            },
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            indicator = {
                PullToRefreshDefaults.Indicator(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    color = PrimaryColor,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().background(Background)) {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.transaction_bg),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Background ),
                                startY = 350f)))

                    AppTextRegular(
                        text = "Dubai transaction data from the Land Department.",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.BottomCenter)
                            .padding(start = 15.dp, end = 15.dp)
                    )
                }
            }

            stickyHeader {
                Column(modifier = Modifier.fillMaxWidth().background(Background)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Search bar — active TextField
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(49.dp),
                        shape = RoundedCornerShape(50.dp),
                        border = BorderStroke(1.dp, if (showSearchBar) TextPrimary else Color.LightGray),
                        color = Color.White
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = null,
                                tint = if (showSearchBar) TextPrimary else Color.Gray,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchLocation,
                                onValueChange = { searchLocation = it },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                decorationBox = { inner ->
                                    if (searchLocation.isEmpty()) {
                                        AppTextLight(text = "Search location in dubai", color = Color.Gray)
                                    }
                                    inner()
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                ),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    imeAction = androidx.compose.ui.text.input.ImeAction.Search
                                ),
                                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                    onSearch = { showSearchBar = false }
                                )
                            )
                            if (searchLocation.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(18.dp).clickable {
                                        searchLocation = ""
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            FilterChipWithIcon(
                                text = if (isRentedView) "Rented" else "Sold",
                                isDark = true,
                                onClick = { isRentedView = !isRentedView }
                            )
                        }
                        item {
                            FilterChipWithIcon(
                                text = if (!selectedPropertyTypeName.isNullOrEmpty()) selectedPropertyTypeName!! else "Property type",
                                isDark = !selectedPropertyTypeName.isNullOrEmpty(),
                                onClick = { showPropertyTypeSheet = true }
                            )
                        }
                        item {
                            FilterChipWithIcon(
                                text = if (selectedBeds.isNotEmpty()) "$selectedBeds Beds" else "Beds",
                                isDark = selectedBeds.isNotEmpty(),
                                onClick = { showBedsSheet = true }
                            )
                        }
                        item {
                            val priceLabel = when {
                                minPrice.isNotEmpty() && maxPrice.isNotEmpty() -> "$minPrice - $maxPrice"
                                minPrice.isNotEmpty() -> "Min: $minPrice"
                                maxPrice.isNotEmpty() -> "Max: $maxPrice"
                                else -> "Price"
                            }
                            FilterChipWithIcon(
                                text = priceLabel,
                                isDark = minPrice.isNotEmpty() || maxPrice.isNotEmpty(),
                                onClick = { showPriceSheet = true }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(16.dp)
                ) {
                    AppTextBold(
                        text = "Transaction overview",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val periods = listOf("YTD", "1 Week", "1 Month", "3 Months", "6 Months", "1 Year", "3 Year")

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(periods) { period ->
                            val isSelected = period == selectedTimePeriod
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .border(width = 1.dp, color = if (isSelected) Color.White else Color.DarkGray, shape = RoundedCornerShape(5.dp))
                                    .clickable { selectedTimePeriod = period }
                                    .padding(horizontal = 15.dp, vertical = 8.dp)
                            ) {
                                AppTextBold(
                                    text = period,
                                    color = if (isSelected) Color.Black else Color.Gray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (isLoading) {
                        // Shimmer skeleton for stats cards
                        TransactionStatsShimmer(isRentView = isRentedView)
                    } else if (!isRentedView) {
                        // --- SOLD view (4 cards) ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatsCard(
                                title = "Avg. price",
                                value = summary?.avgDealAmountFormatted ?: "--",
                                currencyLabel = currency,
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Avg. price/sqft",
                                value = "--",
                                currencyLabel = currency,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatsCard(
                                title = "Rental Yield",
                                value = "--",
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Transactions",
                                value = summary?.transactionCountFormatted ?: "--",
                                percentage = changePercentText,
                                isPositive = isPositive,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        // --- RENTED view ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatsCard(
                                title = "Avg. Rent/year",
                                value = summary?.newRentals?.avgDealAmountFormatted ?: "--",
                                subLabel = "New rentals",
                                currencyLabel = currency,
                                modifier = Modifier.weight(1f)
                            )
                            StatsCard(
                                title = "Avg. Rent/year",
                                value = summary?.renewedRent?.avgDealAmountFormatted ?: "--",
                                subLabel = "Renewed rent",
                                currencyLabel = currency,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        StatsCard(
                            title = "Transactions",
                            value = summary?.transactionCountFormatted ?: "--",
                            percentage = changePercentText,
                            isPositive = isPositive,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    // Background gradient layers
                    Column(modifier = Modifier.matchParentSize()) {
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(
                            Brush.verticalGradient(listOf(Color.Black, Background))
                        ))
                        Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Background))
                        Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(
                            Brush.verticalGradient(listOf(Background, Color.White))
                        ))
                    }

                    if (isLoading) {
                        // Shimmer item skeletons while API loads
                        Column {
                            repeat(6) {
                                TransactionItemShimmer()
                            }
                        }
                    } else if (apiTransactions.isEmpty()) {
                        // Proper empty state UI
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.search),
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(48.dp)
                                )
                                AppTextBold(
                                    text = "No transactions found",
                                    fontSize = 16.sp,
                                    color = TextPrimary
                                )
                                AppTextRegular(
                                    text = "Try adjusting your filters or time period",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    } else {
                        // Show first 6 real transactions
                        Column {
                            apiTransactions.take(6).forEach { transaction ->
                                TransactionCard(
                                    Transaction(
                                        project = transaction.locationName.orEmpty(),
                                        location = transaction.locationSub.orEmpty(),
                                        price = "${transaction.amountFormatted.orEmpty()} ${transaction.currency.orEmpty()}",
                                        status = transaction.contractStatus.orEmpty().uppercase(),
                                        type = transaction.propertyType.orEmpty(),
                                        size = transaction.sizeSqftFormatted?.let { "$it sqft" }.orEmpty(),
                                        beds = transaction.bedroomsLabel?.let { "$it Bed" }.orEmpty(),
                                        date = transaction.dateLabel.orEmpty()
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }   // end LazyColumn
        }   // end PullToRefreshBox
    }

    if (showPropertyTypeSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPropertyTypeSheet = false },
            sheetState = propertyTypeSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            PropertyTypeBottomSheet(
                propertyTypes = masterDataState.data?.data?.propertyTypes ?: emptyList(),
                selectedTypeId = selectedPropertyTypeId,
                onTypeSelected = { id, name ->
                    selectedPropertyTypeId = id
                    selectedPropertyTypeName = name
                },
                onDismiss = {
                    showPropertyTypeSheet = false
                    filterTrigger++
                }
            )
        }
    }

    if (showBedsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBedsSheet = false },
            sheetState = bedsSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            BedsBottomSheet(
                selectedBeds = selectedBeds,
                onBedsSelected = { selectedBeds = it },
                onDismiss = {
                    showBedsSheet = false
                    filterTrigger++
                }
            )
        }
    }

    if (showPriceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPriceSheet = false },
            sheetState = priceSheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            TransactionPriceBottomSheet(
                priceRangeOptions = masterDataState.data?.data?.priceRange ?: emptyList(),
                selectedMin = minPrice,
                selectedMax = maxPrice,
                onMinSelected = { minPrice = it },
                onMaxSelected = { maxPrice = it },
                onDismiss = {
                    showPriceSheet = false
                    filterTrigger++
                }
            )
        }
    }
}

@Composable
fun PropertyTypeBottomSheet(
    propertyTypes: List<PropertyType>,
    selectedTypeId: String?,
    onTypeSelected: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)) {
        AppTextBold(
            text = "Property type",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Grid layout for property types from API
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            propertyTypes.chunked(2).forEach { rowTypes ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowTypes.forEach { type ->
                        TransactionPropertyTypeChip(
                            text = type.name ?: "",
                            isSelected = type._id == selectedTypeId,
                            onClick = { onTypeSelected(type._id ?: "", type.name ?: "") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowTypes.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val hasSelection = !selectedTypeId.isNullOrEmpty()
            AppTextMedium(
                text = "Clear",
                color = if (hasSelection) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasSelection) { onTypeSelected("", "") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(25.dp)
        ) {
            AppTextBold(text = "Show transactions", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TransactionPropertyTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) TextPrimary else Background,
//        border = BorderStroke(1.dp, if (isSelected) Color.Black else Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(99.dp),
        modifier = modifier
            .height(48.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AppTextRegular(
                text = text,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun BedsBottomSheet(
    selectedBeds: String,
    onBedsSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)) {
        AppTextBold(
            text = "Beds & Baths",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AppTextBold(text = "Bedrooms", fontSize = 14.sp)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        val bedOptions = listOf("Studio", "1", "2", "3", "4", "5", "6", "7", "7+")
        
        // Grid layout for beds
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            bedOptions.chunked(4).forEach { rowBeds ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowBeds.forEach { bed ->
                        TransactionBedsChip(
                            text = bed,
                            isSelected = bed == selectedBeds,
                            onClick = { onBedsSelected(bed) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // Add empty spaces if needed
                    repeat(4 - rowBeds.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val hasSelection = selectedBeds.isNotEmpty()
            AppTextMedium(
                text = "Clear",
                color = if (hasSelection) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasSelection) {
                    onBedsSelected("")
                }
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Show properties button
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
            shape = RoundedCornerShape(25.dp)
        ) {
            AppTextBold(text = "Show transactions", color = Color.White, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TransactionBedsChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isSelected) TextPrimary else Background,
//        border = BorderStroke(1.dp, if (isSelected) Color.Black else Color(0xFFE0E0E0)),
        shape = RoundedCornerShape(99.dp),
        modifier = modifier.height(48.dp).clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            AppTextRegular(
                text = text,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun TransactionPriceBottomSheet(
    priceRangeOptions: List<PriceRangeOption>,
    selectedMin: String,
    selectedMax: String,
    onMinSelected: (String) -> Unit,
    onMaxSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    // Derive range bounds from master data — fallback to 100..1000000
    val rangeMin = priceRangeOptions.firstOrNull()?.value?.toFloatOrNull() ?: 100f
    val rangeMax = priceRangeOptions.lastOrNull()?.value?.toFloatOrNull() ?: 1000000f
    val fullRange = rangeMin..rangeMax

    PriceBottomSheet(
        minPrice = selectedMin,
        maxPrice = selectedMax,
        fullRange = fullRange,
        onMinPriceChange = onMinSelected,
        onMaxPriceChange = onMaxSelected,
        onDismiss = onDismiss
    )
}

@Composable
fun PriceBottomSheet(
    minPrice: String,
    maxPrice: String,
    fullRange: ClosedFloatingPointRange<Float> = 100f..1000000f,
    onMinPriceChange: (String) -> Unit,
    onMaxPriceChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentMin = minPrice.replace(",", "").toFloatOrNull() ?: fullRange.start
    val currentMax = maxPrice.replace(",", "").toFloatOrNull() ?: fullRange.endInclusive
    var priceRange by remember { mutableStateOf(currentMin..currentMax) }

    Column(modifier = Modifier.fillMaxWidth()
        .padding(start = 20.dp, end = 20.dp)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                AppTextMedium(
                    text = stringResource(R.string.price),
                    fontSize = 20.sp,
                    color = TextPrimary
                )
            }
        }

        AppSpacerV(20)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            PriceBarChart(
                minPrice = fullRange.start,
                maxPrice = fullRange.endInclusive,
                selectedRange = priceRange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(bottom = 20.dp) // Offset bars to meet slider track center
            )

            CustomRangeSlider(
                min = fullRange.start,
                max = fullRange.endInclusive,
                start = priceRange.start,
                end = priceRange.endInclusive,
                onValueChange = { startValue, endValue ->
                    priceRange = startValue..endValue
                    onMinPriceChange(startValue.toInt().toString())
                    onMaxPriceChange(endValue.toInt().toString())
                }
            )
        }

        AppSpacerV(20)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = DividerColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppTextMedium(
                        text = stringResource(R.string.minimum),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    AppSpacerV(4)
                    AppTextSemiBold(
                        text = "${priceRange.start.toInt()}",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }

            AppTextMedium(text = "—", color = DividerColor)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(
                        width = 1.dp,
                        color = DividerColor,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppTextMedium(
                        text = stringResource(R.string.maximum),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    AppSpacerV(4)
                    AppTextSemiBold(
                        text = "${priceRange.endInclusive.toInt()}",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
            }
        }

        AppSpacerV(20)

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val hasPriceSelection = priceRange != fullRange
            AppTextMedium(
                text = stringResource(R.string.clear),
                color = if (hasPriceSelection) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.clickable(enabled = hasPriceSelection) {
                    priceRange = fullRange
                    onMinPriceChange("")
                    onMaxPriceChange("")
                }
            )
        }

        AppSpacerV(25)

        PrimaryRoundButton(
            text = "Show transactions",
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 25.dp
        ) {
            onDismiss()
        }

        AppSpacerV(16)
    }
}

@Composable
fun FilterChipWithIcon(text: String, isDark: Boolean = false, onClick: () -> Unit = {}) {
    Surface(
        color = if (isDark) Color.Black else Color.White,
        border = if (!isDark) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(33.dp).clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            AppTextSemiBold(
                text = text,
                fontSize = 12.sp,
                color = if (isDark) Color.White else TextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = null,
                modifier = Modifier.size(8.dp),
                tint = if (isDark) Color.White else TextPrimary
            )
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    value: String,
    percentage: String? = null,
    isPositive: Boolean = true,
    subLabel: String? = null,
    currencyLabel: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(115.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF383838),
                        Color(0xFF050505)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currencyLabel != null && value != "--") {
                    AppTextBold(text = "$currencyLabel ", color = Color.White, fontSize = 15.sp)
                }
                AppTextBold(text = value, color = Color.White, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            AppTextLight(text = title, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            if (percentage != null) {
                Surface(
                    color = if (!isPositive) Color(0xFFE80808) else Color(0xFF00A663),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    ) {
                        AppTextSemiBold(text = percentage, color = Color.White, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            painter = painterResource(id = if (isPositive) R.drawable.ic_filled_up_arrow else R.drawable.ic_filled_down_arrow),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }

            if (subLabel != null) {
                Surface(
                    color = Color(0xFF333333),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    AppTextRegular(
                        text = subLabel,
                        color = Color.White,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, bottom = 10.dp, top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    AppTextBold(text = transaction.project, fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(
                             painter = painterResource(id = R.drawable.location),
                             contentDescription = null,
                             modifier = Modifier.size(12.dp),
                             tint = TextSecondary
                         )
                         Spacer(modifier = Modifier.width(4.dp))
                         AppTextLight(text = transaction.location, fontSize = 12.sp)
                    }
                }

                val statusColor = when (transaction.status) {
                    "READY", "NEW", "NEW RENTALS" -> Color(0xFF00A663)
                    else -> Color(0xFFEEEEEE)
                }
                val statusTextColor = if (statusColor == Color(0xFF00A663)) Color.White else Color.Black

                Surface(color = statusColor) {
                    AppTextBold(
                        text = transaction.status,
                        fontSize = 10.sp,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                }
            }
            Row (modifier = Modifier.padding(horizontal = 14.dp),verticalAlignment = Alignment.CenterVertically){
                AppTextSemiBold(text = "Price :", fontSize = 12.sp)
                AppTextBold(text = transaction.price, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row (modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(Background).padding(vertical = 10.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween){
                AppTextRegular(
                    text = "${transaction.type} | ${transaction.size} | ${transaction.beds}",
                    fontSize = 10.sp)
                AppSpacerH(15)
                AppTextLight(text = transaction.date, fontSize = 10.sp)
            }
        }
    }
}

data class Transaction(
    val project: String,
    val location: String,
    val price: String,
    val status: String,
    val type: String,
    val size: String,
    val beds: String,
    val date: String
)

fun getMockTransactions(isRented: Boolean): List<Transaction> {
    if (isRented) {
         return listOf(
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "NEW", "Villa", "3,832sqft", "1 Bed", "30 May - 05 Jun 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "RENEWED", "Apartment", "3,832sqft", "1 Bed", "30 May - 05 Jun 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "NEW", "Villa", "3,832sqft", "1 Bed", "30 May - 05 Jun 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "NEW", "Villa", "3,832sqft", "1 Bed", "30 May - 05 Jun 2025")
        )
    } else {
        return listOf(
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "OFF-PLAN", "Apartment", "3,832sqft", "1 Bed", "30 May 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "READY", "Apartment", "3,832sqft", "1 Bed", "30 May 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "OFF-PLAN", "Apartment", "3,832sqft", "1 Bed", "30 May 2025"),
            Transaction("Omniyat Bespoke", "Dubai, Palm Jumeirah", "13,050,000 AED", "READY", "Apartment", "3,832sqft", "1 Bed", "30 May 2025")
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun TransactionOverviewScreenPreview() {
    PropertyFinderTheme {
        TransactionOverviewScreen(navController = rememberNavController())
    }
}
