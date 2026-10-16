package com.app.propertyfinder.screen.agent

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgentProfileResponse
import com.app.propertyfinder.api.dto.response.AgentSpecialization
import com.app.propertyfinder.api.dto.response.AgentSocialLinks
import com.app.propertyfinder.api.dto.response.AgentNationality
import com.app.propertyfinder.api.dto.response.AgentTrackRecordItem
import com.app.propertyfinder.api.dto.response.AgentExpertiseArea
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.DividerColor
import com.app.propertyfinder.ui.theme.MolumulkAccentCoral
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.Constants
import coil.compose.AsyncImage
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.SnackbarManager
import com.app.propertyfinder.utils.rememberSnackbarManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentDetailScreen(navController: NavHostController, agentId: String, agentName: String) {
    val context = LocalContext.current
    val userViewModel: UserViewModel = hiltViewModel()
    val agentProfileState by userViewModel.agentProfileState.collectAsStateWithLifecycle()
    val agentProfile: AgentProfileResponse? = agentProfileState.data?.data
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()



    val cityOptions = masterDataState.data?.data?.propertyLocations
        ?.mapNotNull { it.displayName }
        .orEmpty()
        .distinct()

    LaunchedEffect(agentId) {
        if (agentId.isNotBlank()) {
            userViewModel.getAgentProfile(agentId = agentId, page = 1, limit = 20)
        }
    }

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("propertylocations") { /* ignore */ }
        }
    }

    var agentTabs by remember { mutableStateOf("Track record") }
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = if (agentName.isBlank()) "Agent detail" else agentName, fontSize = 18.sp, color = MolumulkCharcoal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareLink = "https://molumulk.tj/agentdetails/${agentProfile?.id ?: agentId}"
                        val shareText = "${agentProfile?.name.orEmpty().ifBlank { agentName }}\n$shareLink".trim()
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Share Agent")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }) {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.share), contentDescription = "Share", tint = MolumulkCharcoal)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            BottomContactBar(navController, agentProfile, snackbarHostState)
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            AgentHeaderCard(agentProfile = agentProfile, fallbackName = agentName)
            Spacer(modifier = Modifier.height(16.dp))
            AgentStatsGrid(agentProfile)
            Spacer(modifier = Modifier.height(16.dp))
            AgencyBanner(agentProfile,navController)
            Spacer(modifier = Modifier.height(20.dp))
            AgentTabs{
                agentTabs = it
            }
            Spacer(modifier = Modifier.height(16.dp))
            

            if(agentTabs.equals("Awards")){
                Awards(awards = agentProfile?.awards.orEmpty(), agentType = agentProfile?.agentType ?: "")
            }else if(agentTabs.equals("About agent")){
                About_Agent(agentProfile?.description)
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { navController.navigate(Route.View_full_details.route) },
                    colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                    modifier = Modifier.fillMaxWidth().height(MolumulkDimens.PrimaryButtonHeight)
                ) {
                    AppTextBold(text = "View full details", color = Color.White, fontSize = 14.sp)
                }
            }else{
                AppTextMedium(text = "Track Record for the past 12\nmonths", fontSize = 20.sp, color = MolumulkCharcoal)
                Spacer(modifier = Modifier.height(12.dp))
                TrackRecordList(items = agentProfile?.trackRecords.orEmpty())
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { navController.navigate(Route.Track_records.createRoute(agentId)) },
                    colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                    modifier = Modifier.fillMaxWidth().height(MolumulkDimens.PrimaryButtonHeight)
                ) {
                    AppTextBold(text = "See all", color = Color.White, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            PersonalInfoSection(agentProfile)
            
            Spacer(modifier = Modifier.height(24.dp))
            AreaOfExpertiseSection(expertiseAreas = agentProfile?.expertiseAreas.orEmpty())
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding for FAB/BottomBar
        }
    }
}

@Composable
fun AgentHeaderCard(agentProfile: AgentProfileResponse?, fallbackName: String) {
    Card(
        shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)), // Dark card
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    // Experience Badge (non-capsule)
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                    ) {
                        val expText = agentProfile?.experience.orEmpty().ifBlank { "0" }
                        AppTextMedium(
                            text = "+ $expText Years of experience",
                            color = Color.White, 
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    AppTextBold(
                        modifier = Modifier.padding(end = 8.dp),
                        text = agentProfile?.name.orEmpty().ifBlank { fallbackName },
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppRatingBar(rating = agentProfile?.ratings?.average?.toFloat() ?: 0f, maxStars = 5, starSize = 14)
                        Spacer(modifier = Modifier.width(4.dp))
                        AppTextRegular(
                            text = "${agentProfile?.ratings?.average ?: 0}/5",
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }
                
                // Agent Image with Badge
                Box {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(if (agentProfile?.agentType == "agent") colorResource(R.color.darkBlue) else MolumulkPrimaryCoralRed),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSubComposeAsyncImage(
                            model = "https://d1dp1oh0ra5b0z.cloudfront.net/img/agents/${agentProfile?.profilePicture}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(CircleShape).size(98.dp),
                            error = R.drawable.baseline_account_circle_24
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MolumulkPrimaryCoralRed)
                            .border(1.dp, Color.White, CircleShape)
                    ) {
                        AppImage(
                            if (agentProfile?.agentType == "agent") R.drawable.ic_agent else R.drawable.ic_super_agent,
                            modifier = Modifier.align(Alignment.Center).size(14.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            AppTextMedium(modifier = Modifier.padding(horizontal = 16.dp),
                text = "Languages known :", color = Color.White, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            val languageNames = agentProfile?.languages.orEmpty()
                .map { it.name ?: it.nativeName }
                .filterNotNull()
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (languageNames.isEmpty()) {
                    item {
                        Surface(color = Color(0xFF2C2C2C), shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)) {
                            AppTextRegular(
                                text = "No languages found",
                                color = Color.White,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                items(languageNames) { lang ->
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                    ) {
                        AppTextSemiBold(
                            text = lang,
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(12.dp))
            // Status Footer
            Surface(
                color = Color.White.copy(alpha = 0.12f),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                   verticalAlignment = Alignment.CenterVertically,
                   modifier = Modifier.padding(8.dp),
                   horizontalArrangement = Arrangement.Center
                ) {

                    AppImage(R.drawable.greencricle)
                    Spacer(modifier = Modifier.width(8.dp))
                    AppTextRegular(
                        text = "Usually responds ${agentProfile?.responseTime}",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AgentStatsGrid(agentProfile: AgentProfileResponse?) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatItem(icon = R.drawable.home, value = (agentProfile?.statistics?.totalSaleProperties ?: 0).toString(), label = "Properties for Sale", modifier = Modifier.weight(1f))
            StatItem(icon = R.drawable.key, value = (agentProfile?.statistics?.totalRentProperties ?: 0).toString(), label = "Properties for Rent", modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatItem(icon = R.drawable.frame_43__2_, value = (agentProfile?.statistics?.totalDealsClosed ?: 0).toString(), label = "Closed deals", modifier = Modifier.weight(1f))
            StatItem(icon = R.drawable.frame_43, value = "${(agentProfile?.statistics?.totalRevenue ?: 0)} AED", label = "Total value", modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItem(icon: Int, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, MolumulkBorderDivider),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            AppImage(resId = icon, modifier = Modifier.size(52.dp))
            Spacer(modifier = Modifier.height(8.dp))
            AppTextBold(text = value, fontSize = 20.sp, color = MolumulkCharcoal)
            AppTextMedium(text = label, fontSize = 12.sp, color = MolumulkMutedSlate, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AgencyBanner(agentProfile: AgentProfileResponse?, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MolumulkDimens.CardCornerRadius))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF0832AE), MolumulkPrimaryCoralRed)
                )
            )
            .padding(16.dp)
            .clickable {
                agentProfile?.agency?.id?.let { navController.navigate("company_detail/$it") }
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                    modifier = Modifier.size(56.dp)
                ) {
                    AppSubComposeAsyncImage(
                        model = "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/${agentProfile?.agency?.logo}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    AppTextBold(text = agentProfile?.agency?.name.toString(), color = Color.White, fontSize = 13.sp)
                    AppTextRegular(text = agentProfile?.agency?.description.toString(), color = Color.White, fontSize = 12.sp, lineHeight = 15.sp)
                }
            }
            AppSpacerH(10)
            AppImage(resId = R.drawable.ic_round_white_right, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun AgentTabs(
    onTabSelected: (String) -> Unit = {}
) {
    val tabs = listOf("Track record", "Awards", "About agent")
    var selectedTab by remember { mutableStateOf(tabs.first()) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        tabs.forEach { tab ->
            TabItem(
                text = tab,
                selected = tab == selectedTab,
                onClick = {
                    selectedTab = tab              // ✅ update selected tab
                    onTabSelected(tab)             // ✅ return value
                }
            )
        }
    }
}


@Composable
fun TabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        color = if (selected) MolumulkLightCoralTint else Color.White,
        border = BorderStroke(
            1.dp,
            if (selected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
        )
    ) {
        AppTextSemiBold(
            text = text,
            color = if (selected) MolumulkPrimaryCoralRed else MolumulkCharcoal,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}


@Composable
fun TrackRecordList(items: List<AgentTrackRecordItem>) {
    if (items.isEmpty()) {
        AppTextRegular(text = "No track records found", color = MolumulkMutedSlate, fontSize = 14.sp)
        return
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items.take(3).forEach { item ->
            TrackRecordItem(item = item)
        }
    }
}

@Composable
fun TrackRecordItem(item: AgentTrackRecordItem) {
    Card(
        shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MolumulkBorderDivider),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
             AppTextBold(
                 modifier = Modifier.padding(top = 12.dp, start = 12.dp, end = 12.dp),
                 text = item.propertyName.orEmpty().ifBlank { "Track record" }, 
                 fontSize = 14.sp,
                 color = MolumulkCharcoal
             )
             Row(
                 modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                 verticalAlignment = Alignment.CenterVertically
             ) {
                 Icon(imageVector = ImageVector.vectorResource(R.drawable.location), contentDescription = null, tint = MolumulkMutedSlate, modifier = Modifier.size(12.dp))
                 Spacer(modifier = Modifier.width(4.dp))
                 AppTextLight(
                     text = item.locationArea.orEmpty().ifBlank { item.location.orEmpty().ifBlank { "Dubai" } },
                     fontSize = 12.sp,
                     color = MolumulkMutedSlate
                 )
             }
             Spacer(modifier = Modifier.height(14.dp))

            // Bottom Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(color = Color(0xFFF9FAFB))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(
                    text = buildString {
                        val dealTypeFormatted = item.dealType?.replaceFirstChar { it.uppercase() } ?: "Deal"
                        append(dealTypeFormatted)
                        append("  |  ")
                        append(item.propertyType?.name ?: "Property")
                        append("  |  ")
                        append("${item.bedrooms ?: 0} Bed")
                    },
                    fontSize = 11.sp,
                    color = MolumulkCharcoal
                )

                val formattedDate = item.dealClosedDate?.substringBefore("T") ?: "-"
                AppTextLight(text = formattedDate, fontSize = 11.sp, color = MolumulkMutedSlate)
            }
        }
    }
}

@Composable
fun PersonalInfoSection(agentProfile: AgentProfileResponse?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppTextBold(text = "Personal information", fontSize = 16.sp, color = MolumulkCharcoal)
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoRow("Specialization", agentProfile?.specialization?.title.orEmpty().ifBlank { "N/A" })
        Divider(color = MolumulkBorderDivider, modifier = Modifier.padding(vertical = 12.dp))
        
        InfoRow("Experience", "${agentProfile?.experience.orEmpty().ifBlank { "0" }} Years")
        Divider(color = MolumulkBorderDivider, modifier = Modifier.padding(vertical = 12.dp))
        
        InfoRow("Broker License Number", agentProfile?.brokerLicenseNumber.orEmpty().ifBlank { "N/A" })
        Divider(color = MolumulkBorderDivider, modifier = Modifier.padding(vertical = 12.dp))

        InfoRow("Nationality", agentProfile?.nationality?.name.orEmpty().ifBlank { "N/A" })
        Divider(color = MolumulkBorderDivider, modifier = Modifier.padding(vertical = 12.dp))
        
        Column {
             AppTextRegular(text = "Links", color = MolumulkMutedSlate, fontSize = 12.sp)
             Spacer(modifier = Modifier.height(4.dp))
             Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
                 val linkedin = agentProfile?.socialLinks?.linkedin
                 if (!linkedin.isNullOrBlank()) {
                     // Open link logic
                 }
             }) {
                 AppTextBold(text = "in", color = Color(0xFF0077B5), fontSize = 14.sp)
                 Spacer(modifier = Modifier.width(4.dp))
                 AppTextBold(text = "Linkedin", color = MolumulkCharcoal, fontSize = 14.sp)
                 Spacer(modifier = Modifier.width(8.dp))
                 AppTextRegular(text = agentProfile?.socialLinks?.linkedin.orEmpty().ifBlank { "N/A" }, color = MolumulkMutedSlate, fontSize = 13.sp)
             }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column {
        AppTextRegular(text = label, color = MolumulkMutedSlate, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(4.dp))
        AppTextSemiBold(text = value, fontSize = 14.sp, color = MolumulkCharcoal)
    }
}

@Composable
fun AreaOfExpertiseSection(expertiseAreas: List<AgentExpertiseArea>) {
    if (expertiseAreas.isEmpty()) {
        return
    }

    val locations = expertiseAreas.mapNotNull { it.locationName }.distinct()
    var selectedLocation by remember(locations) { mutableStateOf(locations.firstOrNull().orEmpty()) }
    val selectedArea = expertiseAreas.firstOrNull { it.locationName == selectedLocation }

    Column {
        AppTextBold(text = "Area of Expertise", fontSize = 16.sp, color = MolumulkCharcoal)
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
            color = Color.White,
            border = BorderStroke(1.dp, MolumulkBorderDivider),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)
        ) {
            CityDropdown(
                cities = locations,
                selectedCity = selectedLocation,
                onCitySelected = { loc -> selectedLocation = loc }
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        AppTextLight(
            text = "Data based on listing prices for the past 3 months",
            color = MolumulkMutedSlate, 
            fontSize = 11.sp, 
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (selectedArea != null) {
            // Property Card
            Card(
                shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MolumulkBorderDivider),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    AppSubComposeAsyncImage(
                        model = selectedArea.listingImage.orEmpty(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(topStart = MolumulkDimens.CardCornerRadius, topEnd = MolumulkDimens.CardCornerRadius))
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        AppTextBold(text = selectedArea.locationName.orEmpty(), fontSize = 14.sp, color = MolumulkCharcoal)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MolumulkMutedSlate, modifier = Modifier.size(12.dp))
                            AppTextLight(text = " ${selectedArea.locationName.orEmpty()}", color = MolumulkMutedSlate)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Divider(color = MolumulkBorderDivider)
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        var isExpanded by remember { mutableStateOf(false) }
                        AppTextRegular(
                            text = selectedArea.description.orEmpty(),
                            maxLines = if (isExpanded) 100 else 3, 
                            lineHeight = 22.sp,
                            color = MolumulkCharcoal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        AppTextBold(
                            text = if (isExpanded) "READ LESS" else "READ MORE", 
                            fontSize = 12.sp,
                            color = MolumulkPrimaryCoralRed,
                            modifier = Modifier.clickable { isExpanded = !isExpanded }
                        )
                         
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                color = Color(0xFFF9FAFB), 
                                border = BorderStroke(1.dp, MolumulkBorderDivider), 
                                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius), 
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    AppTextRegular(text = "For sale : ", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkMutedSlate)
                                    AppTextSemiBold(text = (selectedArea.saleDeals ?: 0).toString(), modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkCharcoal)
                                }
                            }
                            Surface(
                                color = Color(0xFFF9FAFB), 
                                border = BorderStroke(1.dp, MolumulkBorderDivider), 
                                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius), 
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    AppTextRegular(text = "For rent : ", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkMutedSlate)
                                    AppTextSemiBold(text = (selectedArea.rentDeals ?: 0).toString(), modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkCharcoal)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            color = Color(0xFFF9FAFB), 
                            border = BorderStroke(1.dp, MolumulkBorderDivider), 
                            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius), 
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AppTextRegular(text = "Closed deals : ", modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkMutedSlate)
                                AppTextSemiBold(text = (selectedArea.totalDeals ?: 0).toString(), modifier = Modifier.padding(vertical = 8.dp), textAlign = TextAlign.Center, fontSize = 12.sp, color = MolumulkCharcoal)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomContactBar(navController: NavHostController, agentProfile: AgentProfileResponse?, snackbarHostState: SnackbarHostState) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Surface(
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, MolumulkBorderDivider),
        modifier = Modifier.fillMaxWidth().navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call Button (non-capsule)
            Button(
                onClick = {
                    val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                    if (loginType != Constants.LOGIN_USER) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please signup and login for access to ${context.getString(R.string.guest_user_description)}"
                            )
                        }
                        return@Button
                    } else {
                        val phoneNumber = agentProfile?.phoneNumber.toString()
                        if (phoneNumber != null) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$phoneNumber")
                            }
                            context.startActivity(intent)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkAccentCoral),
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(MolumulkDimens.PrimaryButtonHeight)
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.contact), contentDescription = "Call", tint = Color.White)
            }
            
            // View Properties Button (non-capsule)
            Button(
                onClick = {
                    navController.navigate(Route.Agents_Properties.createRoute(agentProfile?.id.orEmpty()))
                },
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                modifier = Modifier.weight(1f).height(MolumulkDimens.PrimaryButtonHeight)
            ) {
                AppTextBold(text = "View properties", color = Color.White, fontSize = 14.sp)
            }
            
            // WhatsApp Button (non-capsule)
            Button(
                onClick = {
                    val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                    if (loginType != Constants.LOGIN_USER) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Please signup and login for access to ${context.getString(R.string.guest_user_description)}"
                            )
                        }
                        return@Button
                    } else {
                        val whatsAppNumber = agentProfile?.whatsappNumber.toString()
                        val formattedNumber = whatsAppNumber.replace("+", "").replace(" ", "")
                        val uri = Uri.parse("https://wa.me/$formattedNumber")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(MolumulkDimens.PrimaryButtonHeight)
            ) {
                Icon(imageVector = ImageVector.vectorResource(R.drawable.whatapp), contentDescription = "WhatsApp", tint = Color.White)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDropdown(
    cities: List<String> = listOf("Dubai", "Abu Dhabi", "Sharjah"),
    selectedCity: String = "Dubai",
    onCitySelected: (String) -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        Surface(
            modifier = Modifier.menuAnchor().fillMaxWidth().height(MolumulkDimens.FilterChipHeight),
            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        ) {
            Row(
                modifier = Modifier.fillMaxSize().background(Color.White),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AppTextSemiBold(text = selectedCity, fontSize = 14.sp, color = MolumulkCharcoal)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MolumulkCharcoal
                )
            }
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            Modifier.background(Color.White)
        ) {
            cities.forEach { city ->
                DropdownMenuItem(
                    text = {
                        AppTextMedium(text = city, fontSize = 14.sp, color = MolumulkCharcoal)
                    },
                    onClick = {
                        onCitySelected(city)
                        expanded = false
                    }
                )
            }
        }
    }
}



@Composable
fun About_Agent(description: String?) {
    Column {
        AppTextBold(text = "About Agent", fontSize = 16.sp, color = MolumulkCharcoal)
        AppSpacerV(6)
        AppTextRegular(
            text = description.orEmpty().ifBlank { "N/A" },
            color = MolumulkMutedSlate,
            lineHeight = 22.sp
        )
    }
}


@Composable
fun Awards(awards: List<String>, agentType: String) {
    if (awards.isEmpty()) return

    Column {
        AppTextBold(text = "Awards", fontSize = 16.sp, color = MolumulkCharcoal)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(awards) { awardImage ->
                val awardUrl = "${PrefUtils.getString(Constants.TAG_URL_AWARD_IMG,"")}$awardImage"
                Box(
                    modifier = Modifier
                        .width(158.dp)
                        .height(204.dp)
                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                ) {
                    AsyncImage(
                        model = awardUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun AwardCard1(modifier: Modifier = Modifier) {
    Box(modifier = modifier.clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))) {
        Image(
            painter = painterResource(id = R.drawable.ic_awards_1),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
    }
}

@Composable
fun AwardCard2(modifier: Modifier = Modifier, agentType: String) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_award_with_agent_type),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppTextBold(text = "Outstanding", color = Color.White, fontSize = 22.sp)
                Spacer(modifier = Modifier.height(6.dp))
                // Superagent Badge
                Surface(
                    color = if (agentType == "agent") PrimaryBlue else PrimaryColor,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Image(
                            painter = painterResource(id = if (agentType == "agent") R.drawable.ic_agent else R.drawable.ic_super_agent),
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        AppTextBold(text = if (agentType == "agent") "AGENT" else "SUPERAGENT", color = Color.White, fontSize = 8.sp)
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 75.dp)) {
                AppTextBold(text = "WINNER", color = Color.White, fontSize = 13.sp)
                AppTextRegular(text = "Q3 2025 | DUBAI", color = Color.White, fontSize = 11.sp)
            }
        }
    }
}



@Preview
@Composable
fun AgentDetailPreview() {
    PropertyFinderTheme {
        AgentDetailScreen(
            navController = rememberNavController(),
            agentId = "preview-id",
            agentName = "Preview Agent"
        )
        //Awards()
    }
}
