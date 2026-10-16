package com.app.propertyfinder.screen.developer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.DeveloperItem
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.SearchTopBar
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils

private const val FALLBACK_DEVELOPER_BASE_URL = "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDevelopersScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = hiltViewModel()
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    val developerBaseUrl = PrefUtils.getString(Constants.TAG_URL_DEVELOPER_IMG, FALLBACK_DEVELOPER_BASE_URL)
        ?.takeIf { it.isNotBlank() }
        ?: FALLBACK_DEVELOPER_BASE_URL

    val developerState by userViewModel.developerSearchState.collectAsStateWithLifecycle()
    val developers = developerState.data?.data?.items.orEmpty()

    var selectedEmirate by remember { mutableStateOf("Emirates") }
    var openEmiratesSheet by remember { mutableStateOf(false) }
    val emiratesOptions = listOf("All Emirates", "Dubai", "Abu Dhabi", "Sharjah", "Ajman", "Ras Al Khaimah", "Fujairah", "Umm Al Quwain")

    val filteredDevelopers = remember(developers, selectedEmirate) {
        if (selectedEmirate == "Emirates" || selectedEmirate == "All Emirates") {
            developers
        } else {
            developers.filter { developer ->
                val name = developer.name.orEmpty().lowercase()
                when (selectedEmirate) {
                    "Dubai" -> {
                        name.contains("emaar") || name.contains("damac") || name.contains("samana") || name.contains("sobha") || name.contains("ellington") || name.contains("danube") || name.contains("azizi")
                    }
                    "Abu Dhabi" -> {
                        name.contains("aldar")
                    }
                    "Sharjah" -> {
                        name.contains("horizon")
                    }
                    else -> true
                }
            }
        }
    }

    val totalCount = if (selectedEmirate == "Emirates" || selectedEmirate == "All Emirates") {
        developerState.data?.data?.pagination?.total ?: developers.size
    } else {
        filteredDevelopers.size
    }

    LaunchedEffect(searchText) {
        userViewModel.getDeveloperSearch(
            page = 1,
            limit = 20,
            name = searchText.trim().takeIf { it.isNotBlank() }
        )
    }

    if (openEmiratesSheet) {
        EmiratesBottomSheet(
            options = emiratesOptions,
            selectedOption = selectedEmirate,
            onDismiss = { openEmiratesSheet = false },
            onSelect = { option ->
                selectedEmirate = if (option == "All Emirates") "Emirates" else option
                openEmiratesSheet = false
            }
        )
    }
    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        SearchTopBar(
                            searchQuery = searchText,
                            onSearchQueryChange = { searchText = it },
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
                TopAppBar(
                    title = {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            AppTextBold(text = "Developers", fontSize = 20.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint =Color.Black )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.search),
                                tint = Color.Black,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))
            EmiratesDropdown(
                selectedEmirate = selectedEmirate,
                onClick = { openEmiratesSheet = true }
            )
            Spacer(modifier = Modifier.height(10.dp))

            AppTextLight(text = "$totalCount properties are available", )

            AppSpacerV(20)

            if (developerState.isLoading && developers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    items(filteredDevelopers) { developer ->
                        DeveloperCard(
                            developer = developer,
                            developerBaseUrl = developerBaseUrl,
                            onClick = {
                                developer.id?.let {
                                    navController.navigate(Route.DeveloperDetail.createRoute(it))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmiratesDropdown(selectedEmirate: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .width(300.dp)
            .height(42.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedEmirate, 
                fontSize = 14.sp, 
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                painter = painterResource(id = R.drawable.arrow_down),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color.Black
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmiratesBottomSheet(
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

                    Text(
                        text = "Select Emirate",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
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
                    val isSelected = option == selectedOption || (option == "All Emirates" && selectedOption == "Emirates")
                    Box(
                        modifier = Modifier
                            .height(45.dp)
                            .fillMaxWidth()
                            .background(
                                if (isSelected) Color.Black else Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clickable(onClick = {
                                onSelect(option)
                            })
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            AppSpacerV(25)
        }
    }
}

@Composable
fun DeveloperCard(
    developer: DeveloperItem,
    developerBaseUrl: String,
    onClick: () -> Unit
) {
    val logoUrl = buildImageUrl(developerBaseUrl, developer.logo)
    Column(
        modifier = Modifier
            .width(170.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(9.69.dp))
                .background(Color.White)
        ) {
            AppSubComposeAsyncImage(
                model = logoUrl,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(9.69.dp)),
                contentScale = ContentScale.Fit
            )
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                color = Color.White
            ) {
                AppTextLight(
                    text = "${developer.totalProjects ?: 0} Projects",
                    fontSize = 12.sp,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        AppTextBold(
            text = developer.name.orEmpty(),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
        AppSpacerV(5)
        AppTextLight(
            text = developer.foundedYear?.let { "Founded in $it" }.orEmpty(),
            textAlign = TextAlign.Center,
            lineHeight = 15.sp
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun DevelopersPreview() {
    PropertyFinderTheme {
        FindDevelopersScreen(navController = rememberNavController())
    }
}
