package com.app.propertyfinder.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.HistoricalTransactionItem
import com.app.propertyfinder.api.dto.response.PropertyType
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.ui.components.*
import com.app.propertyfinder.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavHostController,
    initialDealType: String = "sale",
    initialTimeframe: String = "ytd",
    viewModel: PropertyViewModel = hiltViewModel()
) {
    // State collected from ViewModel
    val masterDataState = viewModel.masterDataState.collectAsStateWithLifecycle().value
    val historicalTransactionsState = viewModel.historicalTransactionsState.collectAsStateWithLifecycle().value

    var locationQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedSortFilter by remember { mutableStateOf("Newest") }

    var isRefreshing by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    val pullRefreshState = rememberPullToRefreshState()

    // Call API on load and when sort/search/refresh changes
    LaunchedEffect(selectedSortFilter, locationQuery, refreshTrigger) {
        val requestMap = mutableMapOf<String, Any>(
            "dealType" to initialDealType,
            "page" to 1,
            "limit" to 50,
            "timeframe" to initialTimeframe,
            "sortBy" to when (selectedSortFilter) {
                "Newest" -> "newest"
                "Oldest" -> "oldest"
                "High price" -> "price-high"
                "Low price" -> "price-low"
                else -> "newest"
            }
        )
        if (locationQuery.isNotBlank()) requestMap["location"] = locationQuery.trim()
        viewModel.getHistoricalTransactions(requestMap)
        isRefreshing = false
    }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopBar(
                    searchQuery = locationQuery,
                    onSearchQueryChange = { locationQuery = it },
                    isSearchActive = isSearchActive,
                    onBackClick = {
                        isSearchActive = false
                        locationQuery = ""
                    },
                    onSearchDone = {
                        isSearchActive = false
                    }
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Transactions", fontSize = 20.sp) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.back_arrow),
                                contentDescription = "Back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                             Icon(
                                painter = painterResource(id = R.drawable.search),
                                contentDescription = "Search",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Sort chips row — matches the image design
            val sortFilters = listOf("Newest", "Oldest", "High price", "Low price")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                items(sortFilters) { filter ->
                    val isSelected = filter == selectedSortFilter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) Color.Black else Color.White,
                        border = if (!isSelected) BorderStroke(1.dp, Color(0xFFE0E0E0)) else null,
                        modifier = Modifier
                            .height(36.dp)
                            .clickable { selectedSortFilter = filter }
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 16.dp)) {
                            AppTextBold(
                                text = filter,
                                fontSize = 12.sp,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }
            }

            val response = historicalTransactionsState.data?.data
            val transactionsList = response?.transactions ?: emptyList()

            // Dynamic Count Text
            val total = response?.pagination?.total ?: 0
            val totalFormatted = java.text.NumberFormat.getInstance().format(total)
            val countText = if (historicalTransactionsState.isLoading) "Loading transactions..." else "$totalFormatted transactions"
            
            AppTextBold(
                text = countText, 
                fontSize = 16.sp, 
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content Area
            if (historicalTransactionsState.isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    repeat(8) {
                        TransactionItemShimmer()
                    }
                }
            } else if (!historicalTransactionsState.errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppTextBold(text = "Something went wrong", fontSize = 16.sp)
                        AppTextRegular(text = historicalTransactionsState.errorMessage!!, color = Color.Red, fontSize = 13.sp)
                    }
                }
            } else if (transactionsList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
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
                            modifier = Modifier.size(56.dp)
                        )
                        AppTextBold(text = "No transactions found", fontSize = 16.sp)
                        AppTextRegular(
                            text = "Try adjusting your search or filters",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                    }
                }
            } else {
                PullToRefreshBox(
                    state = pullRefreshState,
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        refreshTrigger++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    indicator = {
                        PullToRefreshDefaults.Indicator(
                            state = pullRefreshState,
                            isRefreshing = isRefreshing,
                            color = PrimaryColor,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(transactionsList) { transaction ->
                            TransactionItemCard(transaction)
                        }
                    }
                }
            }
        }
    }

}


@Composable
fun TransactionItemCard(transaction: HistoricalTransactionItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 14.dp, bottom = 10.dp, top = 14.dp, end = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    AppTextBold(text = transaction.locationName.orEmpty(), fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                         Icon(
                             painter = painterResource(id = R.drawable.location),
                             contentDescription = null,
                             modifier = Modifier.size(12.dp),
                             tint = TextSecondary
                         )
                         Spacer(modifier = Modifier.width(4.dp))
                         AppTextLight(text = transaction.locationSub.orEmpty(), fontSize = 12.sp)
                    }
                }

                val status = transaction.contractStatus.orEmpty().uppercase()
                val statusColor = when (status) {
                    "READY", "NEW", "NEW RENTALS" -> Color(0xFF00A663)
                    else -> Color(0xFFEEEEEE)
                }
                val statusTextColor = if (statusColor == Color(0xFF00A663)) Color.White else Color.Black

                Surface(color = statusColor,) {
                    AppTextBold(
                        text = status,
                        fontSize = 10.sp,
                        color = statusTextColor,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }

            val priceText = "${transaction.amountFormatted.orEmpty()} ${transaction.currency.orEmpty()}"
            Row (modifier = Modifier.padding(horizontal = 14.dp),verticalAlignment = Alignment.CenterVertically){
                AppTextSemiBold(text = "Price :", fontSize = 12.sp)
                AppTextBold(text = priceText, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 14.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row (modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(Background).padding(vertical = 10.dp, horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween){
                val specs = listOfNotNull(
                    transaction.propertyType,
                    transaction.sizeSqftFormatted?.let { "$it sqft" },
                    transaction.bedroomsLabel?.let { "$it Bed" }
                ).joinToString(" | ")
                AppTextRegular(
                    text = specs,
                    fontSize = 10.sp)
                AppSpacerH(15)
                AppTextLight(text = transaction.dateLabel ?: "N/A", fontSize = 10.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyTypeSelectionBottomSheet(
    propertyTypes: List<PropertyType>,
    selectedTypeId: String?,
    onTypeSelected: (String?, String?) -> Unit,
    onDismiss: () -> Unit
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
                    Spacer(modifier = Modifier.height(15.dp))
                    AppTextBold(
                        text = "Property type",
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                propertyTypes.chunked(2).forEach { rowTypes ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowTypes.forEach { type ->
                            val isSelected = type._id == selectedTypeId
                            TransactionPropertyTypeChip(
                                text = type.name.orEmpty(),
                                isSelected = isSelected,
                                onClick = {
                                    onTypeSelected(type._id, type.name)
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowTypes.size == 1) {
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
                val hasSelection = !selectedTypeId.isNullOrEmpty()
                AppTextMedium(
                    text = "Clear",
                    color = if (hasSelection) TextPrimary else TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        onTypeSelected(null, null)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                shape = RoundedCornerShape(25.dp)
            ) {
                AppTextBold(text = "Show properties", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TransactionListScreenPreview() {
    PropertyFinderTheme {
        TransactionListScreen(navController = rememberNavController())
    }
}