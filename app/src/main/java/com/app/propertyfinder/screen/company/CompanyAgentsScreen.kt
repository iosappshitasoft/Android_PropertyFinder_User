package com.app.propertyfinder.screen.company

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgencyAgentItem
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.api.PaginationState
import com.app.propertyfinder.api.TokenManager
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.CircularProgressIndicator
import com.app.propertyfinder.screen.agent.AgentUiModel
import com.app.propertyfinder.api.dto.response.AgentSearchResponse
import com.app.propertyfinder.api.dto.response.AgentSearchItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyAgentsScreen(
    navController: NavHostController,
    companyId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val gridState = rememberLazyGridState()
    val agentsPagination by userViewModel.agentSearchPaginationState.collectAsStateWithLifecycle()

    LaunchedEffect(companyId) {
        if (companyId.isNotBlank()) {
            userViewModel.searchAgents(
                token = TokenManager.accessToken,
                request = mapOf("agencyId" to companyId)
            )
        }
    }

    LaunchedEffect(gridState, agentsPagination.items.size, agentsPagination.hasNextPage) {
        val totalItems = gridState.layoutInfo.totalItemsCount
        val lastVisible = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
        
        snapshotFlow {
            val total = gridState.layoutInfo.totalItemsCount
            val last = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            total > 0 && last >= total - 3
        }
        .distinctUntilChanged()
        .filter { it && agentsPagination.hasNextPage && !agentsPagination.isNextPageLoading && !agentsPagination.isFirstPageLoading }
        .collectLatest {
            userViewModel.searchAgentsNextPage()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = "Agents", fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                         Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.search),
                            contentDescription = "Search",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        if (agentsPagination.isFirstPageLoading && agentsPagination.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                AppLoadingDialog()
            }
        } else {
            val agents = agentsPagination.items.mapNotNull { it.toUiModel() }
            LazyVerticalGrid(
                state = gridState,
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(agents) { agent ->
                    CompanyAgentGridCard(
                        agent = agent,
                        onViewProfileClick = {
                            navController.navigate(
                                com.app.propertyfinder.navigation.Route.AgentDetail.createRoute(
                                    agent.id,
                                    agent.name
                                )
                            )
                        }
                    )
                }

                if (agentsPagination.isNextPageLoading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = PrimaryColor,
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

@Composable
fun CompanyAgentGridCard(
    agent: AgentUiModel,
    onViewProfileClick: () -> Unit
) {
    val imageUrl = agent.profilePictureUrl
    val isSuperAgent = agent.isSuperAgent
    val ratingText = agent.rating
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp) // Space for the top half of the image
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                // Top padding is handled by the Spacer to accommodate the image overlap
            ) {
                // Spacer matches half the image size + margin + the cutout depth visual
                Spacer(modifier = Modifier.height(60.dp))

                AppTextMedium(
                    text = agent.name,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                AppTextRegular(
                    text = agent.role,
                    fontSize = 11.sp,
                    color = TextSecondary, 
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Button(
                    onClick = onViewProfileClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0033AA)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .height(38.dp)
                ) {
                    AppTextMedium(text = "View profile", color = Color.White, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.height(10.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5)) // Light gray footer
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppRatingBar(rating = 4.8f, starSize = 13, maxStars = 1)
                        Spacer(modifier = Modifier.width(4.dp))
                        AppTextRegular(text = ratingText, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.size(18.dp).clip(CircleShape)
                        .background(if (isSuperAgent)PrimaryColor else colorResource(R.color.darkBlue)),
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(modifier = Modifier.fillMaxSize(),
                            resId = if (isSuperAgent) R.drawable.ic_super_agent else R.drawable.ic_agent)
                    }
                }
            }
        }

        Surface(
            shape = CircleShape,
            color = Background,
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .border(2.dp, Color.White, CircleShape)
        ) {
            AppSubComposeAsyncImage(
                model = imageUrl,
                error = R.drawable.baseline_account_circle_24,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(Background)
            )
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
        role = specialization?.title ?: agentType ?: "Agent",
        profilePictureUrl = image,
        agencyLogoUrl = agencyLogo,
        agencyName = agency?.name.orEmpty(),
        rating = "${ratings?.average ?: 0.0}/${ratings?.totalCount ?: 0}",
        isSuperAgent = agentType.equals("superagent", ignoreCase = true)
    )
}
