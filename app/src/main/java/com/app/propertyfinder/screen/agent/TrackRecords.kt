package com.app.propertyfinder.screen.agent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.SearchTopBar
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.PrimaryColor

fun isWithinMonths(dateString: String?, months: Int): Boolean {
    if (dateString.isNullOrBlank()) return false
    return try {
        val cleanDate = dateString.substringBefore("T")
        val parsedDate = java.time.LocalDate.parse(cleanDate)
        val cutoffDate = java.time.LocalDate.now().minusMonths(months.toLong())
        !parsedDate.isBefore(cutoffDate)
    } catch (e: Exception) {
        true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackRecords(navController: NavHostController, agentId: String) {

    val userViewModel: UserViewModel = hiltViewModel()
    val agentProfileState by userViewModel.agentProfileState.collectAsStateWithLifecycle()
    val agentProfile = agentProfileState.data?.data
    val trackRecords = agentProfile?.trackRecords.orEmpty()
    val isLoading = agentProfileState.isLoading

    LaunchedEffect(agentId) {
        if (agentId.isNotBlank()) {
            userViewModel.getAgentProfile(agentId = agentId, page = 1, limit = 100)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Last 12 Months") }

    val filteredRecords = remember(searchQuery, selectedFilter, trackRecords) {
        trackRecords.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                item.propertyName?.contains(searchQuery, ignoreCase = true) == true ||
                item.locationArea?.contains(searchQuery, ignoreCase = true) == true ||
                item.location?.contains(searchQuery, ignoreCase = true) == true ||
                item.dealType?.contains(searchQuery, ignoreCase = true) == true ||
                item.propertyType?.name?.contains(searchQuery, ignoreCase = true) == true
            }

            val matchesFilter = when (selectedFilter) {
                "Last 12 Months" -> isWithinMonths(item.dealClosedDate, 12)
                "Last 2 Years" -> isWithinMonths(item.dealClosedDate, 24)
                "Last 3 Years" -> isWithinMonths(item.dealClosedDate, 36)
                "Last 4 Years" -> isWithinMonths(item.dealClosedDate, 48)
                else -> true
            }

            matchesSearch && matchesFilter
        }
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
                    onSearchDone = {}
                )
            } else {
                // Normal Top Bar
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Track records", fontSize = 20.sp) },
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
            // Filter Chips
           /* if (!isSearchActive) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Last 12 Months", "Last 2 Years", "Last 3 Years", "Last 4 Years")) { filter ->
                        FilterChipItem(
                            text = filter,
                            isSelected = filter == selectedFilter,
                            showArrow = false,
                            onClick = { selectedFilter = filter }
                        )
                    }
                }
            }*/

            // Track Records List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (isLoading == true && trackRecords.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryColor)
                        }
                    }
                } else if (filteredRecords.isEmpty()) {
                    item {
                        AppTextRegular(
                            text = "No track records found",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    items(filteredRecords) { record ->
                        TrackRecordItem(item = record)
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TrackScreenPreview() {
    PropertyFinderTheme {
        TrackRecords(navController = rememberNavController(), agentId = "")
    }
}