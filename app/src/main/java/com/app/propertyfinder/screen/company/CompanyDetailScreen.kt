package com.app.propertyfinder.screen.company

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.api.dto.response.AgencyAgentItem
import com.app.propertyfinder.api.dto.response.AgencyAwardItem
import com.app.propertyfinder.api.dto.response.AgencyProfileResponse
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.api.viewmodel.UserViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.api.PaginationState
import com.app.propertyfinder.api.dto.response.AgentSearchItem
import com.app.propertyfinder.screen.agent.AgentUiModel
import com.app.propertyfinder.api.TokenManager

private const val AGENCY_LOGO_BASE_URL =
    "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/"

private const val AGENT_PROFILE_BASE_URL =
    "https://d1dp1oh0ra5b0z.cloudfront.net/img/agents/"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyDetailScreen(navController: NavHostController, companyId: String,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isExpandedAboutCompany by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf("About Company") }

    LaunchedEffect(companyId) {
        if (companyId.isNotBlank()) {
            userViewModel.getAgencyProfile(companyId = companyId)
            userViewModel.searchAgents(
                token = TokenManager.accessToken,
                request = mapOf("agencyId" to companyId)
            )
        }
    }

    val agencyProfileState by userViewModel.agencyProfileState.collectAsStateWithLifecycle()
    val agentsPagination by userViewModel.agentSearchPaginationState.collectAsStateWithLifecycle()
    val companyProfile: AgencyProfileResponse? = agencyProfileState.data?.data
    val companyName = companyProfile?.name.orEmpty()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = companyName, fontSize = 18.sp) },
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
                    IconButton(onClick = {
                        val shareLink = companyProfile?.shareLink
                        if (!shareLink.isNullOrBlank()) {
                            val shareText = "${companyProfile.name.orEmpty()}\n$shareLink".trim()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Share Company")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                        } else {
                            Toast.makeText(context, "Share link is not available", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.share),
                            contentDescription = "Share",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            CompanyBottomBar(
                navController = navController,
                companyId = companyId,
                phoneNumber = companyProfile?.phoneNumber,
                email = companyProfile?.email
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (agencyProfileState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AppLoadingDialog()
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize().navigationBarsPadding()) {
                item {
                    val logoUrl = buildImageUrl(
                        AGENCY_LOGO_BASE_URL,
                        companyProfile?.logo
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSubComposeAsyncImage(
                            model = logoUrl,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Black),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                item {
                    Surface(color = Color.White, modifier = Modifier.fillMaxWidth(),) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            AppTextBold(text = "Company information", fontSize = 16.sp)

                            CompanyInfoRow(
                                label = "Location",
                                value = companyProfile?.address?.fullAddress
                                    ?: companyProfile?.address?.city
                                    ?: "-"
                            )
                            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                            CompanyInfoRow(
                                label = "Nationality",
                                value = companyProfile?.address?.country ?: "-"
                            )
                            HorizontalDivider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                            CompanyInfoRow(
                                label = "Agent",
                                value = companyProfile?.totalAgents?.toString()
                                    ?: companyProfile?.statistics?.totalAgents?.toString()
                                    ?: "0"
                            )
                            Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                            CompanyInfoRow(
                                label = "Superagent",
                                value = companyProfile?.totalSuperAgents?.toString()
                                    ?: companyProfile?.statistics?.totalSuperAgents?.toString()
                                    ?: "0"
                            )
                            Divider(color = Color(0xFFF5F5F5), thickness = 1.dp)

                            // ORN Row with Info Icon
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    AppTextRegular(
                                        text = "ORN",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    AppTextBold(
                                        text = companyProfile?.orn ?: "-",
                                        fontSize = 14.sp
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                item {
                    // Map Preview
                    val dubai = com.google.android.gms.maps.model.LatLng(25.2048, 55.2708)
                    val cameraPositionState =
                        com.google.maps.android.compose.rememberCameraPositionState {
                            position =
                                com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(
                                    dubai,
                                    12f
                                )
                        }

                    Box(
                        modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(0.dp))
                    ) {
                        com.google.maps.android.compose.GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            uiSettings = com.google.maps.android.compose.MapUiSettings(
                                zoomControlsEnabled = false
                            )
                        ) {
                            com.google.maps.android.compose.MarkerComposable(
                                state = com.google.maps.android.compose.MarkerState(position = dubai),
                                title = "Dubai",
                                snippet = "Office Location"
                            ) {
                                CustomMapPin()
                            }
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(16.dp)) {
                        // Tabs
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            BlackPillTab(
                                text = "About Company",
                                isSelected = selectedTab == "About Company",
                                onClick = { selectedTab = "About Company" })
                            BlackPillTab(
                                text = "Awards",
                                isSelected = selectedTab == "Awards",
                                onClick = { selectedTab = "Awards" })
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        if (selectedTab == "About Company") {
                            AppTextBold(text = "About Company", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            AppTextRegular(
                                text = "Quisque sapien tellus, tincidunt id libero at, elementum pharetra mi. Curabitur malesuada dui sit amet erat sagittis molestie. Nulla porta cursus eleifend. Donec dui urna, dictum eu tristique in, facilisis quis sem. In faucibus pretium nulla, nec lobortis lectus porta sed. Maecenas et facilisis justo. Proin scelerisque lectus non iaculis ultrices. Vestibulum in magna sit amet dolor laoreet laoreet. Fusce posuere massa at elit sagittis congue. Nunc ullamcorper vel sem porttitor mattis. Nam sed aliquam neque. Pellentesque feugiat molestie feugiat. Quisque sagittis, dolor eu viverra facilisis, dolor ipsum\n" +
                                        "Email: ${companyProfile?.email ?: "-"}\nPhone: ${companyProfile?.phoneNumber ?: "-"}\nAddress: ${
                                    companyProfile?.address?.fullAddress
                                        ?: companyProfile?.address?.city
                                        ?: "-"
                                }",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                lineHeight = 25.sp,
                                maxLines = if (isExpandedAboutCompany) Int.MAX_VALUE else 5
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { isExpandedAboutCompany = !isExpandedAboutCompany },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0033AA)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(48.dp)
                            ) {
                                AppTextBold(
                                    text = if (isExpandedAboutCompany) "Show less" else "View full details",
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                item {
                    if (selectedTab == "Awards") {
                        Spacer(modifier = Modifier.height(8.dp))
                        Column(modifier = Modifier.fillMaxWidth().background(Background)
                            .padding(16.dp)) {
                            AppTextBold(text = "Awards", fontSize = 18.sp)
                            Spacer(modifier = Modifier.height(16.dp))

                            // Awards List
                            val awards = companyProfile?.awards.orEmpty()
                            if (awards.isEmpty()) {
                                AppTextRegular(
                                    text = "No awards found",
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    items(awards) { award ->
                                        AwardCardItem(award)
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    AppSpacerV(16)
                    AppDivider(modifier = Modifier.fillMaxWidth(), color = Color.LightGray, thickness = 1.dp)
                    AppSpacerV(8)
                }
                item {

                    Column(
                        modifier = Modifier.fillMaxWidth().background(Background)
                            .padding(vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppTextBold(text = "Our Agents", fontSize = 16.sp)
                            AppTextRegular(
                                text = "See All",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.clickable {
                                    navController.navigate(Route.CompanyAgents.createRoute(companyId))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val agents = agentsPagination.items.mapNotNull { it.toUiModel() }
                            items(agents) { agent ->
                                CompanyAgentCard(
                                    agent = agent,
                                    onViewProfileClick = {
                                        navController.navigate(
                                            Route.AgentDetail.createRoute(agent.id, agent.name)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                // Bottom spacing for FABs
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun BlackPillTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color(0xFF1F1F1F) else Color.White,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
            1.dp,
            Color(0xFFE0E0E0)
        ) else null,
        modifier = Modifier
            .height(36.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            AppTextMedium(
                text = text,
                fontSize = 13.sp,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun CompanyInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        AppTextRegular(text = label, fontSize = 12.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        AppTextBold(text = value, fontSize = 14.sp)
    }
}

@Composable
fun CompanyBottomBar(
    navController: NavHostController,
    companyId: String,
    phoneNumber: String?,
    email: String?
) {
    val context = LocalContext.current
    Surface(
        color = Color.White,
        shadowElevation = 16.dp, // Top shadow
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call Button
            IconButton(
                onClick = {
                    if (!phoneNumber.isNullOrBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phoneNumber")
                            }
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Cannot open dialer", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Phone number is not available", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444))
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.call),
                    contentDescription = "Call",
                    tint = Color.White
                )
            }

            // View Properties Button
            Button(
                onClick = { navController.navigate(Route.CompanyProperties.createRoute(companyId)) }, // Navigate to properties
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0033AA)),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                AppTextBold(text = "View properties", color = Color.White, fontSize = 16.sp)
            }

            // Email Button
            IconButton(
                onClick = {
                    if (!email.isNullOrBlank()) {
                        try {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$email")
                            }
                            context.startActivity(Intent.createChooser(intent, "Send Email"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email client found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Email address is not available", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F1F1F))
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun CompanyAgentCard(
    agent: AgentUiModel,
    onViewProfileClick: () -> Unit
) {
    Box(modifier = Modifier.width(170.dp).padding(top = 50.dp)) {
        val agentImageUrl = agent.profilePictureUrl
        val roleText = agent.role
        val ratingText = agent.rating
        val isSuperAgent = agent.isSuperAgent
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                AppTextRegular(
                    text = roleText,
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
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                        .clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
                        .background(Background).padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppRatingBar(rating = 4.8f, starSize = 13, maxStars = 1)
                        Spacer(modifier = Modifier.width(4.dp))
                        AppTextRegular(text = ratingText, fontSize = 11.sp)
                    }
                    Box(modifier = Modifier.size(15.dp).clip(CircleShape).background(
                        if (isSuperAgent)PrimaryColor else colorResource(R.color.darkBlue)
                    ),
                        contentAlignment = Alignment.Center) {
                        AppImage(modifier = Modifier.fillMaxSize(),
                            resId = if (isSuperAgent) R.drawable.ic_super_agent else R.drawable.ic_agent) }
                }
            }
        }
        Box(modifier = Modifier.size(80.dp).align(Alignment.TopCenter)
            .offset(y = (-40).dp).clip(CircleShape).background(Background)){
            AppSubComposeAsyncImage(
                model = agentImageUrl,
                error = R.drawable.baseline_account_circle_24,
                modifier = Modifier.padding(5.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AwardCardItem(award: AgencyAwardItem) {
    Card(shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        modifier = Modifier.width(320.dp).height(140.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AppImage(modifier = Modifier.fillMaxSize(),resId = R.drawable.awards_bg)
            Column(
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info, // Use Star or Trophy
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppTextBold(
                        text = "AWARDS ${award.year.orEmpty()}",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AppTextBold(
                    text = award.title.orEmpty(),
                    color = Color.White,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                AppTextRegular(
                    text = award.subtitle.orEmpty(),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CustomMapPin() {
    Image(
        painter = painterResource(id = R.drawable.locationping),
        contentDescription = "Map Pin",
        modifier = Modifier
            .size(
                width = 20.dp,
                height = 35.dp
            ) // Maintain size or adjust as needed
            .offset(y = (-37).dp) // Keep the offset to anchor at bottom center
    )
}

@Preview
@Composable
fun CompanyDetailPreview() {
    PropertyFinderTheme {
        CompanyDetailScreen(rememberNavController(), "1")
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
