package com.app.propertyfinder.screen.agent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgentSearchItem
import com.app.propertyfinder.api.dto.response.services
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.screen.CountryBottomSheet
import com.app.propertyfinder.screen.LanguageBottomSheet
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppRatingBarWhite
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.SearchBarTextField
import com.app.propertyfinder.ui.components.SearchTopBar
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.TextPrimary
import androidx.compose.ui.graphics.SolidColor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

private data class AgentFilterState(
    val serviceId: String? = null,
    val serviceName: String? = null,
    val languageId: String? = null,
    val languageName: String? = null,
    val nationalityId: String? = null,
    val nationalityName: String? = null,
    val agentType: String? = null
)

data class AgentUiModel(
    val id: String,
    val name: String,
    val role: String,
    val profilePictureUrl: String?,
    val agencyLogoUrl: String?,
    val agencyName: String,
    val rating: String,
    val isSuperAgent: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentsScreen(navController: NavHostController) {
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()

    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val agentsPagination by userViewModel.agentSearchPaginationState.collectAsStateWithLifecycle()
    val gridState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var filterState by remember { mutableStateOf(AgentFilterState()) }
    var openServiceSheet by remember { mutableStateOf(false) }
    var openLanguageSheet by remember { mutableStateOf(false) }
    var openNationalitySheet by remember { mutableStateOf(false) }
    var openAgentTypeSheet by remember { mutableStateOf(false) }

    val services = masterDataState.data?.data?.services.orEmpty()
    val languages = masterDataState.data?.data?.languages.orEmpty()
    val countries = masterDataState.data?.data?.countries.orEmpty()

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("services,countries,languages") { }
        }
    }

    LaunchedEffect(searchQuery, filterState) {
        searchAgentsWithFilters(
            userViewModel = userViewModel,
            query = searchQuery,
            filters = filterState
        )
    }

    LaunchedEffect(gridState, agentsPagination.items.size, agentsPagination.hasNextPage) {
        observeGridPagination(
            gridState = gridState,
            canLoadNext = { agentsPagination.hasNextPage && !agentsPagination.isNextPageLoading && !agentsPagination.isFirstPageLoading },
            onLoadNext = { userViewModel.searchAgentsNextPage() }
        )
    }

    if (openLanguageSheet) {
        FilterOptionBottomSheet(
            title = "Select Language",
            options = languages.map { (it.nativeName ?: it.name).orEmpty() to (it._id.orEmpty()) },
            selectedValue = filterState.languageId,
            onDismiss = { openLanguageSheet = false },
            onSelect = { selectedId ->
                val picked = languages.firstOrNull { it._id == selectedId }
                filterState = filterState.copy(
                    languageId = picked?._id,
                    languageName = (picked?.nativeName ?: picked?.name)
                )
                openLanguageSheet = false
            }
        )
    }

    if (openNationalitySheet) {
        FilterOptionBottomSheet(
            title = "Select Country",
            options = countries.map { it.name.orEmpty() to it._id.orEmpty() },
            selectedValue = filterState.nationalityId,
            searchable = true,
            onDismiss = { openNationalitySheet = false },
            onSelect = { selectedId ->
                val picked = countries.firstOrNull { it._id == selectedId }
                filterState = filterState.copy(
                    nationalityId = picked?._id,
                    nationalityName = picked?.name
                )
                openNationalitySheet = false
            }
        )
    }

    if (openServiceSheet) {
        ServiceNeededBottomSheet(
            options = services,
            selectedId = filterState.serviceId,
            onDismiss = { openServiceSheet = false },
            onSelect = { option ->
                filterState = filterState.copy(
                    serviceId = option?.value,
                    serviceName = option?.name
                )
                openServiceSheet = false
            }
        )
    }

    if (openAgentTypeSheet) {
        AgentTypeBottomSheet(
            selectedType = filterState.agentType,
            onDismiss = { openAgentTypeSheet = false },
            onSelect = { selected ->
                filterState = filterState.copy(agentType = selected)
                openAgentTypeSheet = false
            }
        )
    }
    Scaffold(
        topBar = {
            if (isSearchActive) {
                TopAppBar(
                    title = {
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                            border = BorderStroke(1.dp, MolumulkBorderDivider),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp)
                                    .padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AppImage(
                                    resId = R.drawable.search,
                                    modifier = Modifier.size(18.dp),
                                    tint = MolumulkMutedSlate
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    singleLine = true,
                                    cursorBrush = SolidColor(MolumulkPrimaryCoralRed),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 14.sp,
                                        color = MolumulkCharcoal,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.W500
                                    ),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                                    modifier = Modifier.weight(1f),
                                    decorationBox = { innerTextField ->
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            if (searchQuery.isEmpty()) {
                                                AppTextRegular(text = "Search here", color = MolumulkMutedSlate, fontSize = 14.sp)
                                            }
                                            innerTextField()
                                        }
                                    }
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { isSearchActive = false }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MolumulkCharcoal
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
                )
            } else {
                CenterAlignedTopAppBar(
                    title = { AppTextBold(text = "Agents", fontSize = 18.sp, color = MolumulkCharcoal) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MolumulkCharcoal
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.search),
                                contentDescription = "Search",
                                tint = MolumulkCharcoal
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
            if (!isSearchActive) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChipItem(
                            text = filterState.serviceName ?: "Service needed",
                            isSelected = !filterState.serviceId.isNullOrBlank(),
                            onClick = { openServiceSheet = true }
                        )
                    }
                    item {
                        FilterChipItem(
                            text = filterState.languageName ?: "Language",
                            isSelected = !filterState.languageId.isNullOrBlank(),
                            onClick = { openLanguageSheet = true }
                        )
                    }
                    item {
                        FilterChipItem(
                            text = filterState.nationalityName ?: "Nationality",
                            isSelected = !filterState.nationalityId.isNullOrBlank(),
                            onClick = { openNationalitySheet = true }
                        )
                    }
                    /*item {
                        FilterChipItem(
                            text = filterState.agentType?.replaceFirstChar { it.uppercaseChar() } ?: "AgentType",
                            isSelected = !filterState.agentType.isNullOrBlank(),
                            onClick = { openAgentTypeSheet = true }
                        )
                    }*/
                }
            }

            val agents = agentsPagination.items.mapNotNull { it.toUiModel() }

            if (!agentsPagination.isFirstPageLoading && agents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppTextRegular(text = "No agents found", fontSize = 14.sp, color = Color.Gray)
                }
            } else if (agentsPagination.isFirstPageLoading && agents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MolumulkPrimaryCoralRed,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(30.dp)
                    )
                }
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(agents) { agent ->
                        AgentCard(
                            agent = agent,
                            onViewProfileClick = {
                                navController.navigate(Route.AgentDetail.createRoute(agent.id, agent.name))
                            }
                        )
                    }
                    if (agentsPagination.isNextPageLoading || agentsPagination.isFirstPageLoading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = MolumulkPrimaryCoralRed,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun observeGridPagination(
    gridState: LazyGridState,
    canLoadNext: () -> Boolean,
    onLoadNext: () -> Unit
) {
    snapshotFlow {
        val totalItems = gridState.layoutInfo.totalItemsCount
        val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        totalItems > 0 && lastVisible >= totalItems - 3
    }
        .distinctUntilChanged()
        .filter { it }
        .collectLatest {
            if (canLoadNext()) onLoadNext()
        }
}

private fun searchAgentsWithFilters(
    userViewModel: UserViewModel,
    query: String,
    filters: AgentFilterState
) {
    val request = hashMapOf<String, Any>()
    if (query.isNotBlank()) request["name"] = query.trim()
    filters.serviceId?.takeIf { it.isNotBlank() }?.let { request["serviceNeeded"] = it }
    filters.languageId?.takeIf { it.isNotBlank() }?.let { request["language"] = it }
    filters.nationalityId?.takeIf { it.isNotBlank() }?.let { request["nationality"] = it }
    filters.agentType?.takeIf { it.isNotBlank() }?.let { request["agentType"] = it }
    request["page"] = 1
    request["limit"] = 20
    userViewModel.searchAgents(TokenManager.accessToken, request)
}

@Composable
fun FilterChipItem(
    text: String,
    isSelected: Boolean = false,
    showArrow: Boolean = true,
    onClick: () -> Unit = {}
) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else Color.White
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val contentColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor,
        modifier = Modifier.height(MolumulkDimens.FilterChipHeight)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            AppTextMedium(
                text = text,
                fontSize = 13.sp,
                color = contentColor
            )
            if (showArrow) {
                Spacer(modifier = Modifier.width(4.dp))
                AppImage(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
            }
        }
    }
}

private fun AgentSearchItem.toUiModel(): AgentUiModel? {
    val agentId = id ?: return null
    val image = profilePicture?.takeIf { it.isNotBlank() }?.let {
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/agents/$it"
    }
    val agencyLogo = agency?.logo?.takeIf { it.isNotBlank() }?.let {
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/$it"
    }
    return AgentUiModel(
        id = agentId,
        name = name.orEmpty().ifBlank { "Unknown Agent" },
        role = position ?: agentType ?: "Agent",
        profilePictureUrl = image,
        agencyLogoUrl = agencyLogo,
        agencyName = agency?.name.orEmpty(),
        rating = "${ratings?.average ?: 0.0}/${ratings?.totalCount}",
        isSuperAgent = agentType.equals("superagent", ignoreCase = true)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceNeededBottomSheet(
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
                            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                            .background(
                                if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite,
                                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                            )
                            .border(
                                1.dp,
                                if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider,
                                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                            )
                            .clickable(onClick = {
                                selectedServiceId = if (isSelected) null else option.value
                            })
                            .padding(horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppTextSemiBold(
                            text = option.name.orEmpty(),
                            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
                            fontSize = 13.sp
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
                    color = if (hasSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedServiceId = null
                    }
                )
            }

            AppSpacerV(25)

            Button(
                onClick = {
                    val selectedService = options.firstOrNull { it.value == selectedServiceId }
                    onSelect(selectedService)
                },
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight)
            ) {
                AppTextBold(text = "Show Agents", color = Color.White, fontSize = 16.sp)
            }

            AppSpacerV(16)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentTypeBottomSheet(
    selectedType: String?,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit
) {
    val options = listOf("agent", "superagent")
    var selectedAgentType by remember(selectedType) { mutableStateOf(selectedType) }

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
                        text = "Agent Type",
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            AppSpacerV(20)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(options) { option ->
                    val isSelected = selectedAgentType.equals(option, ignoreCase = true)
                    Surface(
                        onClick = {
                            selectedAgentType = if (isSelected) null else option
                        },
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                        color = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        AppTextBold(
                            text = option.replaceFirstChar { it.uppercaseChar() },
                            fontSize = 13.sp,
                            color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val hasSelection = !selectedAgentType.isNullOrEmpty()
                AppTextBold(
                    text = "Clear",
                    color = if (hasSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedAgentType = null
                    }
                )
            }

            AppSpacerV(25)

            Button(
                onClick = { onSelect(selectedAgentType) },
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight)
            ) {
                AppTextBold(text = "Show Agents", color = Color.White, fontSize = 16.sp)
            }

            AppSpacerV(16)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterOptionBottomSheet(
    title: String,
    options: List<Pair<String, String>>,
    selectedValue: String?,
    searchable: Boolean = false,
    onDismiss: () -> Unit,
    onSelect: (String?) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedOptionId by remember(selectedValue) { mutableStateOf(selectedValue) }
    val filtered = remember(options, query, searchable) {
        if (!searchable || query.isBlank()) options
        else options.filter { it.first.contains(query, ignoreCase = true) }
    }

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
                        text = title,
                        fontSize = 20.sp,
                        color = Color.Black
                    )
                }
            }

            AppSpacerV(20)

            if (searchable) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                    border = BorderStroke(1.dp, MolumulkBorderDivider),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppImage(
                            resId = R.drawable.search,
                            modifier = Modifier.size(18.dp),
                            tint = MolumulkMutedSlate
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = query,
                            onValueChange = { query = it },
                            singleLine = true,
                            cursorBrush = SolidColor(MolumulkPrimaryCoralRed),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 14.sp,
                                color = MolumulkCharcoal
                            ),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (query.isEmpty()) {
                                        AppTextRegular(text = "Search country", color = MolumulkMutedSlate, fontSize = 14.sp)
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                AppSpacerV(10)
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filtered) { item ->
                    val isSelected = selectedOptionId == item.second
                    Surface(
                        onClick = {
                            selectedOptionId = if (isSelected) null else item.second
                        },
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                        color = if (isSelected) MolumulkLightCoralTint else MolumulkOffWhite,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AppTextBold(
                                text = item.first,
                                fontSize = 13.sp,
                                color = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            AppSpacerV(20)

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                val hasSelection = !selectedOptionId.isNullOrEmpty()
                AppTextBold(
                    text = "Clear",
                    color = if (hasSelection) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable(enabled = hasSelection) {
                        selectedOptionId = null
                    }
                )
            }

            AppSpacerV(25)

            Button(
                onClick = { onSelect(selectedOptionId) },
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight)
            ) {
                AppTextBold(text = "Show Agents", color = Color.White, fontSize = 16.sp)
            }
            AppSpacerV(16)
        }
    }
}

@Composable
fun AgentCard(agent: AgentUiModel, onViewProfileClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter) {
        // 1. White Card Body
        // Pushed down so the top of the card aligns roughly with the center of the images
        Card(
            shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(top = 50.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Spacer pushes content down to clear the bottom half of the images
                Spacer(modifier = Modifier.height(60.dp))

                // Name & Role
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    AppTextBold(
                        text = agent.name, 
                        fontSize = 13.sp,
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp,
                        color = MolumulkCharcoal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    AppTextLight(
                        text = agent.role,
                        fontSize = 10.sp,
                        maxLines = 1, 
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = MolumulkMutedSlate
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // View Profile Button
                Button(
                    onClick = onViewProfileClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp).fillMaxWidth().padding(horizontal = 15.dp)
                ) {
                    AppTextMedium(fontSize = 12.sp, text = "View profile", color = Color.White)
                }

                Spacer(modifier = Modifier.height(25.dp))

                // Footer (Rating & Badge)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
                        .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                        .background(Background)
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    // Rating
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppRatingBarWhite(rating = 0f, maxStars = 1, starSize = 13)
                        Spacer(modifier = Modifier.width(8.dp))
                        AppTextMedium(text = agent.rating, fontSize = 11.sp, color = MolumulkCharcoal)
                    }
                    AppImage(resId = if (agent.isSuperAgent) R.drawable.ic_super_agent else R.drawable.ic_agent,
                        modifier = Modifier.size(18.dp))
                }
            }
        }

        // 2. Floating Images Header
        // Careful positioning to match the "Agent Left, Agency Right (Larger & On Top)" look
        Box(contentAlignment = Alignment.Center, modifier = Modifier.offset(y = 0.dp)) {
            // Container size to hold both
            Box(modifier = Modifier.width(125.dp).height(110.dp)) {
                // Agent Image (Left, Smaller, Behind)
                AppSubComposeAsyncImage(
                    model = agent.profilePictureUrl,
                    error = R.drawable.baseline_account_circle_24,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(Background)
                        .align(Alignment.CenterStart)
                        .border(7.dp, Color(0xFFF5F5F5), CircleShape)
                )

                // Agency Logo (Right, Larger, On Top)
                Surface(
                    shape = CircleShape,
                    shadowElevation = 0.dp,
                    modifier = Modifier
                        .size(70.dp)
                        .align(Alignment.CenterEnd)
                        .border(7.dp, Color(0xFFF5F5F5), CircleShape) // Thick border cuts into Agent image
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AppSubComposeAsyncImage(
                            model = agent.agencyLogoUrl,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .align(Alignment.CenterStart)
                        )
                    }
                }
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AgentScreenPreview() {
    PropertyFinderTheme {
        AgentsScreen(navController = rememberNavController())
    }
}


