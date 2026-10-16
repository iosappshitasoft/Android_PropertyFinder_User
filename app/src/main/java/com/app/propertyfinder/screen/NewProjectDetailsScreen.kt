package com.app.propertyfinder.screen

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.ProjectDetailItem
import com.app.propertyfinder.api.dto.response.ProjectTimelineItem
import com.app.propertyfinder.api.dto.response.ProjectAuthorizedAgency
import com.app.propertyfinder.api.dto.response.PropertyImageItem
import com.app.propertyfinder.api.dto.response.ProjectPaymentPlanItem
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.googlemap_helper.bitmapDescriptorFromVector
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.CornerButton
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.HtmlText
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.theme.DividerColor
import com.app.propertyfinder.ui.theme.OTPVerifiedBtn
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.openUrlInChromeOrDefault
import com.app.propertyfinder.utils.resolveCdnOrAbsoluteMediaUrl
import com.app.propertyfinder.utils.CustomToastMessage
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import com.google.gson.Gson
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class DummyProperty(
    val id: String,
    val imageUrl: Int,
    val title: String,
    val location: String,
    val projectDetails: String,
    val price: String,
)

data class AmenitiesItem(
    val icon: Int = R.drawable.ic_map_detail,
    val title: String,
    val iconUrl: String? = null
)

data class DummyAgency(
    val id: String? = null,
    val imageUrl: Int = R.drawable.agent,
    val imageUrlRemote: String? = null,
    val title: String,
    val location: String,
)
data class FaqItem(
    val title: String,
    val description: String
)
val faqList = listOf(
    FaqItem(
        title = "Integer porta pharetra scelerisque. Vivamus faucibus mi non augue",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Ut in eleifend lorem. Pellentesque pellentesque era",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Duis suscipit mollis a vitae molestie.",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Integer porta pharetra scelerisque. Vivamus faucibus mi non augue",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Integer porta pharetra scelerisque. Vivamus faucibus mi non augue",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Ut in eleifend lorem. Pellentesque pellentesque era",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Duis suscipit mollis a vitae molestie.",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    ),
    FaqItem(
        title = "Integer porta pharetra scelerisque. Vivamus faucibus mi non augue",
        description = "Morbi quis tristique arcu. Donec augue ante, fringilla id nulla eu, blandit venenatis eros. Nullam vel nulla vitae massa tristique tempor ac ut velit. Nam auctor elit nisl."
    )
)

data class SimilarProjects(
    val id: String? = null,
    val imageUrl: String? = null,
    val developerLogoUrl: String? = null,
    val title: String,
    val location: String,
    val price: String,
    val off_plan: String,
    val delivery_date: String,
    val sale_started: String,
    val payment_plan_available: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NewProjectDetailsScreenPreview() {

    NewProjectDetailsScreen(
        navController = androidx.navigation.compose.rememberNavController(),
        propertyId = "1",
        fromScreen = "home"
    )
}

@Composable
fun NewProjectDetailsScreen(
    navController: NavController,
    propertyId: String?,
    fromScreen: String,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val projectDetailsState by propertyViewModel.projectDetailsState.collectAsStateWithLifecycle()

    LaunchedEffect(propertyId) {
        Log.d("PropertyDetail", "Received propertyId: $propertyId")
        propertyId?.takeIf { it.isNotBlank() }?.let {
            propertyViewModel.getProjectDetails(it)
        }
    }

    val systemBarsHeight = with(LocalDensity.current) {
        WindowInsets.systemBars.getTop(this).toDp()
    }
    val project = projectDetailsState.data?.data?.project
    val projectDetailsPayload = projectDetailsState.data?.data
    val projectImageBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROJECT_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/" }
    val projectDocBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROJECT_DOC,
        "https://d1dp1oh0ra5b0z.cloudfront.net/doc/project/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/doc/project/" }
    val projectVideoBaseUrl = PrefUtils.getString(Constants.TAG_URL_PROJECT_VID, "").orEmpty()
        .ifBlank { PrefUtils.getString(Constants.TAG_URL_PROPERTY_VID, "").orEmpty() }
        .ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/vid/property/" }
    val agencyImageBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_AGENCY_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/" }
    val projectImages = project?.images.orEmpty()
    val titleText = project?.projectName ?: "Project"
    val locationText = listOfNotNull(project?.location?.city, project?.location?.zone).joinToString(", ")
        .ifBlank { project?.location?.address.orEmpty() }
    val priceText = project?.launchPrice?.startingFrom?.let { "${it} ${project.launchPrice.currency ?: "AED"}" } ?: "Coming soon"
    val descriptionText = project?.aboutProject ?: project?.description ?: ""
    val dummyProperty = DummyProperty(
        id = propertyId.toString(),
        imageUrl = R.drawable.welcome,
        title = titleText,
        location = locationText,
        projectDetails = descriptionText,
        price = priceText
    )
    val availableAmenities = remember(project?.amenities) {
        project?.amenities.orEmpty().map {
            AmenitiesItem(
                title = it.name.orEmpty(),
                iconUrl = buildImageUrl(PrefUtils.getString(Constants.TAG_URL_AMENITY_IMG, "").orEmpty(), it.image)
            )
        }
    }
    val faqItems = remember(project?.faqs) {
        project?.faqs.orEmpty().map {
            FaqItem(
                title = it.question.orEmpty(),
                description = it.answer.orEmpty()
            )
        }
    }
    val dummyAgency = remember(projectDetailsPayload?.authorizedAgencies, project?.location, agencyImageBaseUrl) {
        projectDetailsPayload?.authorizedAgencies.orEmpty().map {
            DummyAgency(
                id = it.id,
                imageUrlRemote = buildImageUrl(agencyImageBaseUrl, it.profilePicture),
                title = it.agencyName.orEmpty(),
                location = listOfNotNull(project?.location?.city, project?.location?.zone).joinToString(", ")
            )
        }
    }
    val similarProjectsList = remember(projectDetailsPayload?.similarProjects, projectImageBaseUrl) {
        projectDetailsPayload?.similarProjects.orEmpty().map { item ->
            SimilarProjects(
                id = item.id,
                imageUrl = buildImageUrl(projectImageBaseUrl, item.images.firstOrNull { it.isPrimary == true }?.url ?: item.images.firstOrNull()?.url),
                developerLogoUrl = buildImageUrl(agencyImageBaseUrl, item.developer?.logo),
                title = item.projectName.orEmpty(),
                location = listOfNotNull(item.location?.city, item.location?.zone, item.location?.address).joinToString(", "),
                price = item.launchPrice?.startingFrom?.let { "${it} ${item.launchPrice.currency ?: "AED"}" } ?: "",
                off_plan = item.projectType?.uppercase(Locale.getDefault()).orEmpty(),
                delivery_date = formatDateToDisplay(item.deliveryDate),
                sale_started = "",
                payment_plan_available = ""
            )
        }
    }
    val coordinates = project?.location?.coordinates?.coordinates.orEmpty()
    val mapLng = coordinates.getOrNull(0) ?: 55.2718
    val mapLat = coordinates.getOrNull(1) ?: 25.2058

    val heroSectionHeight = 225.dp + systemBarsHeight
    val thumbRowHeight = 100.dp
    val videoSectionHeight = 225.dp
    val sheetOverlap = 180.dp
    val scrollTopPadding = heroSectionHeight + thumbRowHeight + videoSectionHeight - sheetOverlap
    val galleryUrls = remember(projectImages, projectImageBaseUrl) {
        projectImages.sortedBy { it.order ?: Int.MAX_VALUE }
            .mapNotNull { buildImageUrl(projectImageBaseUrl, it.url) }
    }
    var selectedImageIndex by remember(propertyId) { mutableStateOf(0) }
    LaunchedEffect(propertyId, projectImages, galleryUrls) {
        val sorted = projectImages.sortedBy { it.order ?: Int.MAX_VALUE }
        val primaryIdx = sorted.indexOfFirst { it.isPrimary == true }
        val idx = when {
            primaryIdx >= 0 -> primaryIdx
            else -> 0
        }
        val maxIdx = (galleryUrls.size - 1).coerceAtLeast(0)
        selectedImageIndex = if (galleryUrls.isEmpty()) 0 else idx.coerceIn(0, maxIdx)
    }
    val fullVideoTourUrl = remember(project?.videoTour, projectVideoBaseUrl) {
        resolveCdnOrAbsoluteMediaUrl(project?.videoTour, projectVideoBaseUrl)
    }
    val virtualTourTrimmed = project?.virtualTour360?.trim().orEmpty()
    val context = LocalContext.current
    val navigateToMediaFullView = remember(
        navController,
        galleryUrls,
        selectedImageIndex,
        titleText,
        fullVideoTourUrl
    ) {
        { openVideoFirst: Boolean ->
            navController.currentBackStackEntry?.savedStateHandle?.apply {
                set("images", galleryUrls)
                set("selectedImageIndex", selectedImageIndex)
                set("imageTitle", titleText)
                set("videoTourFullUrl", fullVideoTourUrl)
                set("openVideoFirst", openVideoFirst)
            }
            navController.navigate(Route.ImageFullView.route)
        }
    }
    val heroImageUrl = galleryUrls.getOrNull(selectedImageIndex) ?: galleryUrls.firstOrNull()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(colorResource(R.color.screen_bg_color))) {
        Box(modifier = Modifier.fillMaxSize().weight(1f)) {
            Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(heroSectionHeight)) {
                    AppSubComposeAsyncImage(
                        model = heroImageUrl,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().height(thumbRowHeight).padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(3) { index ->
                        val url = galleryUrls.getOrNull(index)
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(4.dp))) {
                            AppSubComposeAsyncImage(
                                model = url,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(videoSectionHeight)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable(enabled = fullVideoTourUrl != null) {
                            navigateToMediaFullView(true)
                        }
                ) {
                    val videoThumbnailUrl = galleryUrls.getOrNull(3) ?: heroImageUrl
                    if (videoThumbnailUrl != null) {
                        AppSubComposeAsyncImage(
                            model = videoThumbnailUrl,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.7f
                        )
                    }
                    AppImage(
                        resId = R.drawable.ic_video_play,
                        modifier = Modifier
                            .size(50.dp)
                            .align(Alignment.Center)
                            .offset(y = (-10).dp)
                    )
                }
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(scrollTopPadding)
                        .clickable(enabled = galleryUrls.isNotEmpty() || fullVideoTourUrl != null) {
                            navigateToMediaFullView(false)
                        })
                }
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .align(Alignment.BottomCenter)
                                .background(
                                    Brush.verticalGradient(
                                        colorStops = arrayOf(
                                            0.0f to Color.Transparent,
                                            0.4f to Color.Transparent,
                                            0.7f to Background.copy(alpha = 0.6f),
                                            1f to Background
                                        )
                                    )
                                )
                        )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val developerUrl = project?.developer?.logo
                    val developerImageBaseUrl = PrefUtils.getString(
                        Constants.TAG_URL_DEVELOPER_IMG,
                        "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/"
                    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/" }
                    AppSubComposeAsyncImage(
                        model = buildImageUrl(developerImageBaseUrl, developerUrl),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(start = 15.dp)
                            .width(55.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )

                    AppImage(
                        resId = R.drawable.ic_report,
                        contentDescription = "Report",
                        modifier = Modifier
                            .width(110.dp)
                            .clickable {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reportType", "project")
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reportedItemId", dummyProperty.id)
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("userType", "user")
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("reason", "incorrect-info")
                                navController.navigate(Route.ReportProblem.route)
                            },
                        contentScale = ContentScale.FillBounds
                    )
                }
                    }
                }
                item {
                    PropertyDetailsSection(
                        dummyProperty = dummyProperty,
                        navController = navController,
                        lat = mapLat,
                        lng = mapLng,
                        isVerified = project?.isVerified == true,
                        amenitiesCount = availableAmenities.size,
                        shareLink = project?.shareLink.takeIf { !it.isNullOrBlank() }
                            ?: "https://molumulk.tj/projectdrilldown/${project?.id ?: ""}",
                        developerPhoneNumber = project?.developer?.phoneNumber.orEmpty()
                    )
                }
                item {
                    project?.let { safeProject ->
                        ProjectDetailsSection(safeProject, navController)
                    }
                }
                item {
                    project?.let { safeProject ->
                        KeyInformationSection(safeProject)
                    }
                }

                item {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 17.dp)) {
                        AppTextBold(text = stringResource(R.string.amenities), fontSize = 16.sp, color = TextPrimary)
                    }
                }
                items(count = minOf(availableAmenities.size, 4)) { index ->
                    val item = availableAmenities[index]
                    Row( verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Background)
                            .padding(horizontal = 20.dp, vertical = 10.dp)) {
                        Box(modifier = Modifier
                            .size(37.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!item.iconUrl.isNullOrBlank()) {
                                AppSubComposeAsyncImage(
                                    model = item.iconUrl,
                                    modifier = Modifier.size(20.dp),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                AppImage(
                                    resId = item.icon,
                                    contentDescription = item.title, tint = Color.Gray,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }

                        AppTextMedium(text = item.title, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 10.dp))
                    }
                }
                item {
                    Column (modifier = Modifier
                        .fillMaxWidth()
                        .background(Background)
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)) {
                        AppSpacerV(10)
                        PrimaryRoundButton(
                            text = if (availableAmenities.size > 4) {
                                "Show all amenities (${availableAmenities.size - 4})"
                            } else {
                                "No more amenities"
                            },
                            onClick = {
                                val navItems = availableAmenities.map {
                                    AllAmenitiesNavItem(title = it.title, iconUrl = it.iconUrl)
                                }
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("amenities_json", Gson().toJson(navItems))
                                navController.navigate(Route.AllAmenities.route)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = colorResource(R.color.darkBlue),
                            contentColor = Color.White,
                            cornerRadius = 10.dp
                        )
                    }
                }

                item {
                    val brochureUrl = remember(project?.brochure, projectDocBaseUrl) {
                        resolveCdnOrAbsoluteMediaUrl(project?.brochure, projectDocBaseUrl)
                    }
                    View360_BrochureSection(
                        virtualTour360 = virtualTourTrimmed.takeIf { it.isNotEmpty() },
                        brochureUrl = brochureUrl,
                        context = context
                    )
                }

                item {
                    PaymentPlansSection(
                        navController = navController,
                        plans = project?.paymentPlan?.plans.orEmpty()
                    )
                }

                item {
                    ProjectTimelineScreen(project?.projectTimeline.orEmpty())
                }

                item {
                    About_Master_PropertyLoc_View(
                        navController = navController,
                        projectDetailItem = project,
                        lat = mapLat,
                        lng = mapLng,
                        developerPhoneNumber = project?.developer?.phoneNumber.orEmpty()
                    )
                }
                items(count = minOf(dummyAgency.size, 3)) { index ->
                    val item = dummyAgency[index]
                    AuthorisedAgenciesItem(
                        agencyList = item,
                        position = index,   // 👈 all except first
                        onClick = {
                            item.id?.takeIf { it.isNotBlank() }?.let { agencyId ->
                                navController.navigate("company_detail/$agencyId")
                            }
                        }
                    )
                }

                item {
                    Column {
                        Faq_View(
                            navController = navController,
                            faqItems = faqItems,
                            authorizedAgencies = projectDetailsPayload?.authorizedAgencies.orEmpty(),
                            projectLocation = locationText
                        )
                    }
                }

                items(count = similarProjectsList.size) { index ->
                    val item = similarProjectsList[index]
                    SimilarProjectsItem(
                        similarProjectList = item,
                        position = index,
                        onClick = {
                            item.id?.takeIf { it.isNotBlank() }?.let { clickedProjectId ->
                                navController.navigate(Route.DevPropertiesDetail.createRoute(clickedProjectId, fromScreen))
                            }
                        }
                    )
                }

                item {
                    Column {
                        Report_View(navController, project?.id)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier
                    .size(35.dp)
                    .clickable { navController.popBackStack() }) {
                    AppImage(
                        resId = R.drawable.ic_bg_back_arrow,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(modifier = Modifier
                    .size(35.dp)
                    .clickable {
                        val sLink = project?.shareLink.takeIf { !it.isNullOrBlank() }
                            ?: "https://molumulk.tj/projectdrilldown/${project?.id ?: ""}"
                        val shareText = "${project?.projectName ?: ""}\n$sLink".trim()

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Share")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }) {
                    AppImage(
                        resId = R.drawable.ic_bg_share,
                        contentDescription = "Share",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Surface(modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(), color = Color.Black, shadowElevation = 8.dp) {
            Column (){
                /*Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = colorResource(R.color.darkBlue)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppTextRegular(text = "* Prices and availability may change. Contact us for updated info. * Prices and availability may change. Contact us for updated info.",
                        color = Color.White, fontSize = 12.sp, maxLines = 1,
                        overflow = TextOverflow.Clip,
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .weight(1f)
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                velocity = 25.dp
                            ))

                    AppImage(
                        resId = R.drawable.ic_cancel, // Replace with WhatsApp icon
                        contentDescription = "cancel",
                        modifier = Modifier
                            .padding(end = 20.dp, start = 10.dp, top = 5.dp, bottom = 5.dp)
                            .size(13.dp), tint = Color.White)
                }*/
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AppImage(
                        resId = R.drawable.ic_mail_btn,
                        contentDescription = "mail",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                val developerEmail = project?.developer?.email.orEmpty().trim()
                                if (developerEmail.isEmpty()) {
                                    CustomToastMessage.showError(context, "Developer email is not available")
                                } else {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:$developerEmail")
                                        putExtra(Intent.EXTRA_SUBJECT, "Inquiry about ${project?.projectName.orEmpty()}")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Hello,\n\nI am interested in the project \"${project?.projectName.orEmpty()}\". Please provide more details.\n\nThank you."
                                        )
                                    }
                                    try {
                                        context.startActivity(emailIntent)
                                    } catch (e: Exception) {
                                        CustomToastMessage.showError(context, "No email app found on this device")
                                    }
                                }
                            }
                    )

                    // Price in center
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        AppTextRegular(text = "Launch price", color = TextSecondary, fontSize = 12.sp)

                        AppSpacerV(5)
                        AppTextBold(
                            text = dummyProperty.price,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }

                    AppImage(
                        resId = R.drawable.ic_whatsapp_btn,
                        contentDescription = "whatsapp",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                val rawPhone = project?.developer?.phoneNumber.orEmpty().trim()
                                when {
                                    rawPhone.isEmpty() -> {
                                        CustomToastMessage.showError(context, "Developer phone number is not available")
                                    }
                                    else -> {
                                        val normalizedPhone = rawPhone.removePrefix("+")
                                        val waUri = Uri.parse("https://wa.me/$normalizedPhone")
                                        val waIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                            setPackage("com.whatsapp")
                                        }
                                        val waBusinessIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                            setPackage("com.whatsapp.w4b")
                                        }
                                        try {
                                            context.startActivity(waIntent)
                                        } catch (e1: Exception) {
                                            try {
                                                context.startActivity(waBusinessIntent)
                                            } catch (e2: Exception) {
                                                CustomToastMessage.showError(context, "WhatsApp is not installed on this device")
                                            }
                                        }
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun TopViewImagesSection(
    navController: NavController,
    dummyProperty: DummyProperty,
    systemBarsHeight: Dp,
    projectImages: List<PropertyImageItem>,
    projectImageBaseUrl: String
) {
    val sortedImages = remember(projectImages) { projectImages.sortedBy { it.order ?: Int.MAX_VALUE } }
    val primaryImage = sortedImages.firstOrNull { it.isPrimary == true } ?: sortedImages.firstOrNull()
    val primaryImageUrl = buildImageUrl(projectImageBaseUrl, primaryImage?.url)
    val context = LocalContext.current

    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(225.dp + systemBarsHeight)) {
            AppSubComposeAsyncImage(
                model = primaryImageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Background.copy(alpha = 0.65f)
                            )
                        )
                    )
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = systemBarsHeight + 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        navController.popBackStack()
                    }
                ) {
                    AppImage(
                        resId = R.drawable.ic_bg_back_arrow,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        // Wait, TopViewImagesSection has dummyProperty.
                        val sLinkConstructed =
                            "https://molumulk.tj/projectdrilldown/${dummyProperty.id}"
                        val shareText = "${dummyProperty.title}\n$sLinkConstructed".trim()

                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Share")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                    }) {
                    AppImage(
                        resId = R.drawable.ic_bg_share, contentDescription = "Share",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            sortedImages.take(3).forEach { imageItem ->
                Box(
                    modifier = Modifier
                        .height(70.dp + systemBarsHeight)
                        .weight(1f)
                        .clickable {
                            /* Handle image selection */
                        }) {
                    AppSubComposeAsyncImage(
                        model = buildImageUrl(projectImageBaseUrl, imageItem.url),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .clickable {
                /* Handle video play */
            }) {
            AppSubComposeAsyncImage(
                model = primaryImageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_video_play),
                contentDescription = "Play video",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                tint = Color.White
            )

            Row(modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppImage(
                    resId = R.drawable.ic_tag,
                    contentDescription = "Tag",
                    modifier = Modifier
                        .width(90.dp)
                        .height(70.dp),
                    contentScale = ContentScale.FillBounds
                )

                AppImage(
                    resId = R.drawable.ic_report,
                    contentDescription = "Report",
                    modifier = Modifier
                        .width(110.dp)
                        .height(60.dp)
                        .clickable {
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("reportType", "project")
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("reportedItemId", dummyProperty.id)
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("userType", "user")
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("reason", "incorrect-info")
                            navController.navigate(Route.ReportProblem.route)
                        },
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    }
}

@Composable
fun PropertyDetailsSection(
    dummyProperty: DummyProperty,
    navController: NavController,
    lat: Double,
    lng: Double,
    isVerified: Boolean,
    amenitiesCount: Int,
    shareLink: String? = null,
    developerPhoneNumber: String = ""
) {
    Column (modifier = Modifier.background(Background)) {
        AppSpacerV(10)
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)) {
            AppTextBold(text = dummyProperty.title, fontSize = 16.sp, color = TextPrimary)
            AppSpacerV(5)
            Row (verticalAlignment = Alignment.CenterVertically){
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = TextSecondary
                )

                AppSpacerH(5)
                AppTextRegular(text = dummyProperty.location, color = TextSecondary, fontSize = 14.sp)
            }
            AppSpacerV(10)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            if (isVerified) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .background(OTPVerifiedBtn)
                ) {
                    AppImage(
                        resId = R.drawable.ic_sharp_verified_user,
                        contentDescription = "verified",
                        modifier = Modifier.size(12.dp)
                    )
                    AppTextMedium(
                        text = "VERIFIED",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(DividerColor)) {
                AppTextMedium(
                    text = "${amenitiesCount}+ Amenities",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .background(Color.Black)
                    .clickable {
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("project_location_lat", lat)
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("project_location_lng", lng)
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("developer_phone_number", developerPhoneNumber)
                        navController.navigate(Route.PropertyLocation.route)
                    }) {
                AppImage(
                    resId = R.drawable.ic_map_detail,
                    contentDescription = "map",
                    modifier = Modifier.size(13.dp)
                )
                AppTextMedium(
                    text = "View on map",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ProjectDetailsSection(projectDetailItem: ProjectDetailItem, navController: NavController) {
    fun getAreaRangeString(projectDetailItem: ProjectDetailItem): String? {
        val layouts = projectDetailItem.unitsFromDeveloper.orEmpty().flatMap { it.layouts.orEmpty() }
        val areaSqftList = layouts.mapNotNull { it.areaSqft }
        val areaSqmList = layouts.mapNotNull { it.areaSqm }
        
        fun formatDouble(d: Double): String {
            return if (d % 1.0 == 0.0) d.toInt().toString() else d.toString()
        }
        
        val sqftStr = if (areaSqftList.isNotEmpty()) {
            val minSqft = areaSqftList.minOrNull()
            val maxSqft = areaSqftList.maxOrNull()
            if (minSqft != null && maxSqft != null) {
                if (minSqft == maxSqft) {
                    "${formatDouble(minSqft)} sqft"
                } else {
                    "${formatDouble(minSqft)} - ${formatDouble(maxSqft)} sqft"
                }
            } else null
        } else null

        val sqmStr = if (areaSqmList.isNotEmpty()) {
            val minSqm = areaSqmList.minOrNull()
            val maxSqm = areaSqmList.maxOrNull()
            if (minSqm != null && maxSqm != null) {
                if (minSqm == maxSqm) {
                    "${formatDouble(minSqm)} sqm"
                } else {
                    "${formatDouble(minSqm)} - ${formatDouble(maxSqm)} sqm"
                }
            } else null
        } else null

        return if (!sqftStr.isNullOrBlank() && !sqmStr.isNullOrBlank()) {
            "$sqftStr / $sqmStr"
        } else {
            sqftStr ?: sqmStr
        }
    }

    val projectFeatures = listOfNotNull(
        projectDetailItem.keyInformation?.propertyTypes?.takeIf { it.isNotBlank() }?.let { "It's a $it" },
        getAreaRangeString(projectDetailItem),
        projectDetailItem.projectType?.takeIf { it.isNotBlank() }?.let { type ->
            type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }
    )

    Column (modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .padding(horizontal = 20.dp, vertical = 10.dp)){
        AppTextSemiBold(text = stringResource(R.string.project_details),fontSize = 18.sp)
        AppSpacerV(10)
        
        val description = projectDetailItem.description?.takeIf { it.isNotBlank() }
            ?: projectDetailItem.aboutProject?.takeIf { it.isNotBlank() }
            ?: ""
        if (description.isNotBlank()) {
            HtmlText(
                html = description,
                colorResId = R.color.textlight,
                fontSizeSp = 14f,
                modifier = Modifier.fillMaxWidth()
            )
            AppSpacerV(10)
        }

        projectFeatures.forEach {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 5.dp)) {
                AppTextMedium(text = "•", fontSize = 15.sp, modifier = Modifier.padding(end = 6.dp))
                AppTextMedium(text = it, fontSize = 15.sp, modifier = Modifier.padding(end = 6.dp))
            }
        }
        AppSpacerV(20)
        PrimaryRoundButton(
            text = stringResource(R.string.view_full_details),
            onClick = {
                navController.navigate(Route.ProjectDetails.createRoute(projectDetailItem.id.orEmpty()))
            },
            modifier = Modifier.fillMaxWidth(),
            containerColor = colorResource(R.color.darkBlue),
            contentColor = Color.White,
            cornerRadius = 10.dp
        )
        AppSpacerV(20)
    }
}

@Composable
fun KeyInformationSection(projectDetailItem: ProjectDetailItem) {
    val keyInfo = projectDetailItem.keyInformation
    val locationValue = listOfNotNull(
        projectDetailItem.location?.city,
        projectDetailItem.location?.zone
    ).joinToString(", ").ifBlank { projectDetailItem.location?.address.orEmpty() }
    val paymentPlanCount = projectDetailItem.paymentPlan?.totalPlansAvailable ?: projectDetailItem.paymentPlansCount ?: 0
    val propertyTypesText = projectDetailItem.propertyTypes.mapNotNull { it.name }.joinToString(", ")
    Column(modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)) {
        Column (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp)){
            AppTextSemiBold(text = stringResource(R.string.key_information))
            KeyInfoItem("Delivery date", formatDateToDisplay(keyInfo?.deliveryDate),true)
            KeyInfoItem("Location", locationValue,true)
            KeyInfoItem("Sale started at", formatDateToDisplay(projectDetailItem.saleStarted),true)
            KeyInfoItem("Payment plan", "$paymentPlanCount payment plans available",true)
            KeyInfoItem("Number of buildings", "1",true)
            KeyInfoItem("Property types", propertyTypesText.ifBlank { keyInfo?.propertyTypes.orEmpty() },true)
            KeyInfoItem("Government fee", "${projectDetailItem.governmentFees.toString()}%",true)
//            KeyInfoItem("Ownership type", "N/A",false)
        }
    }
}

@Composable
fun View360_BrochureSection(
    virtualTour360: String?,
    brochureUrl: String?,
    context: Context
) {
    val hasTour = !virtualTour360.isNullOrBlank()
    val hasBrochure = !brochureUrl.isNullOrBlank()
    val onOpenTour = {
        openUrlInChromeOrDefault(context, virtualTour360, "Project360")
    }
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 25.dp)){
        // 360 degree view
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.Black)
            .clickable(enabled = hasTour, onClick = onOpenTour),
            contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .background(Color.Black)) {
                AppImage(resId = R.drawable.threesixty, modifier = Modifier.size(54.dp, 40.dp))
                AppSpacerV(16)
                AppTextMedium("Experience Project\nin 360 view", color = Color.White, fontSize = 20.sp,
                    textAlign = TextAlign.Center, maxLines = 2)
                AppSpacerV(20)
                PrimaryRoundButton(
                    text = "View now",
                    onClick = onOpenTour,
                    enabled = hasTour,
                    containerColor = colorResource(R.color.darkBlue),
                    contentColor = Color.White,
                    cornerRadius = 10.dp
                )
            }
        }
        // green project brochure view
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(R.color.light_green)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 45.dp)) {
                AppTextMedium(stringResource(R.string.see_the_project), color = Color.White, fontSize = 20.sp,
                    textAlign = TextAlign.Center, maxLines = 2)
                AppSpacerV(15)

                Column(modifier = Modifier
                    .padding(horizontal = 25.dp)
                    .clickable(enabled = hasBrochure) {
                        try {
                            val request = DownloadManager.Request(Uri.parse(brochureUrl)).apply {
                                val fileName = brochureUrl?.substringAfterLast("/", "project_brochure.pdf") ?: "project_brochure.pdf"
                                setTitle(fileName)
                                setDescription("Downloading project brochure...")
                                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                setAllowedOverMetered(true)
                                setAllowedOverRoaming(true)
                            }
                            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            downloadManager.enqueue(request)
                            CustomToastMessage.showSuccess(context, "Download started...")
                        } catch (e: Exception) {
                            CustomToastMessage.showError(context, "Download failed: ${e.message}")
                        }
                    }) {
                    Row (modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center){
                        AppIcon(resId = R.drawable.download_icon,
                            modifier = Modifier.size(18.dp))
                        AppTextMedium(text = stringResource(R.string.download_brochure),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 14.sp, color = colorResource(R.color.light_green))
                    }
                }
            }
        }
    }

}

@Composable
fun PaymentPlansSection(
    navController: NavController,
    plans: List<ProjectPaymentPlanItem>
) {
    var selectedPlanIndex by remember(plans) { mutableStateOf(0) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val selectedPlan = plans.getOrNull(selectedPlanIndex)

    val paymentList = remember(selectedPlan) {
        if (selectedPlan == null) {
            emptyList()
        } else {
            val list = mutableListOf<PaymentItem>()
            selectedPlan.downPayment?.let { down ->
                list.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_top,
                        percentage = "${down.percentage ?: 0}%",
                        title = "Down payment",
                        subtitle = "At sales launch"
                    )
                )
            }
            selectedPlan.duringConstruction?.installments.orEmpty().forEach { inst ->
                list.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_center,
                        percentage = "${inst.percentage ?: 0}%",
                        title = "During Construction",
                        subtitle = formatPaymentDate(inst.date)
                    )
                )
            }
            selectedPlan.onHandover?.let { handover ->
                list.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_bottom,
                        percentage = "${handover.percentage ?: 0}%",
                        title = "On Handover",
                        subtitle = "On Handover"
                    )
                )
            }
            list
        }
    }

    Column (modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextSemiBold(stringResource(R.string.payment_plans))

            if (plans.isNotEmpty()) {
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.White)
                            .clickable { isDropdownExpanded = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        AppTextMedium(
                            text = selectedPlan?.planName.orEmpty(),
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        AppSpacerH(4)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        plans.forEachIndexed { index, plan ->
                            DropdownMenuItem(
                                text = { AppTextMedium(text = plan.planName.orEmpty(), fontSize = 13.sp) },
                                onClick = {
                                    selectedPlanIndex = index
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        AppSpacerV(15)
        if (paymentList.isEmpty()) {
            AppTextRegular(
                text = "No payment plans available",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            Column(modifier = Modifier) {
                paymentList.forEachIndexed { index, item ->
                    val alphaValue = (1f - (index * 0.10f)).coerceIn(0.2f, 1f)
                    val iconRes = when {
                        index == 0 -> R.drawable.ic_payment_top
                        index == paymentList.lastIndex -> R.drawable.ic_payment_bottom
                        else -> R.drawable.ic_payment_center
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (-20 * index).dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(85.dp), contentAlignment = Alignment.Center) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(alphaValue)
                                    .let { if (index == paymentList.lastIndex) it.offset(y = (-5).dp) else it },
                                tint = Color.Unspecified
                            )
                            AppTextMedium(text = item.percentage, color = Color.White, fontSize = 14.sp)
                        }
                        AppSpacerH(4)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(75.dp)
                                .padding(bottom = 13.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(Color.White),
                            verticalArrangement = Arrangement.Center
                        ) {
                            AppTextSemiBold(text = item.title, fontSize = 14.sp, modifier = Modifier.padding(start = 15.dp))
                            AppSpacerV(4)
                            AppTextLight(text = item.subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 15.dp))
                        }
                    }
                }
            }
        }
        PrimaryRoundButton(
            text = stringResource(R.string.see_full_details),
            onClick = {
                val json = Gson().toJson(plans)
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("payment_plans_json", json)
                navController.navigate(Route.PaymentPlan.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = if (paymentList.isEmpty()) 0.dp else (-65).dp),
            containerColor = colorResource(R.color.darkBlue),
            contentColor = Color.White,
            cornerRadius = 10.dp
        )
    }
}

@Composable
fun KeyInfoItem(title: String, value: String, isDivider: Boolean?) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp)) {
        AppTextLight(text = title, fontSize = 12.sp, color = colorResource(R.color.textlight))
        AppSpacerV(4)
        AppTextMedium(text = value, fontSize = 14.sp, color = colorResource(R.color.textprimary))
    }
    if (isDivider == true){
        AppDivider(color = colorResource(R.color.slider_unselected), thickness = 0.5.dp)
    }
}
@Composable
fun AuthorisedAgenciesItem(agencyList: DummyAgency, position: Int, onClick: () -> Unit = {}) {
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
            Row (modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(8.dp))
                , verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                if (!agencyList.imageUrlRemote.isNullOrBlank()) {
                    AppSubComposeAsyncImage(
                        model = agencyList.imageUrlRemote,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AppImage(
                        resId = agencyList.imageUrl,
                        contentDescription = "logo",
                        modifier = Modifier.size(25.dp),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            AppSpacerH(12)

            // 🔹 Title + Location
            Column(modifier = Modifier.weight(1f)) {
                AppTextMedium(
                    text = agencyList.title, fontSize = 14.sp, color = Color.Black
                )
                AppSpacerV(2)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(resId = R.drawable.location,
                        contentDescription = "location",
                        modifier = Modifier.size(14.dp),
                        tint = Color.Gray)
                    AppSpacerH(3)
                    AppTextLight(text = agencyList.location, fontSize = 12.sp,
                        color = colorResource(R.color.textlight))
                }
            }
            AppIcon(resId = R.drawable.ic_arrow_right,
                contentDescription = "right_arrow",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SimilarProjectsItem(
    similarProjectList: SimilarProjects,
    position: Int,
    onClick: () -> Unit = {}
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White   // ✅ whole item white
        )
    ) {

        Column {
            // 🔹 IMAGE WITH OVERLAYS
            Box {
                AppSubComposeAsyncImage(
                    model = similarProjectList.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(3.dp)
                        .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp)),
                    contentScale = ContentScale.Crop
                )
                // EMAAR logo (top-left)

                if (!similarProjectList.developerLogoUrl.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .width(60.dp)
                            .height(45.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSubComposeAsyncImage(
                            model = similarProjectList.developerLogoUrl,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 7.dp, start = 7.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )
                    }
                } else {
                    AppImage(modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 7.dp, start = 7.dp),
                        resId = R.drawable.damac_tag)
                }

                // Labels (top-right)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    TagBox(text = similarProjectList.off_plan, Color.White, Color.Black)
                    AppSpacerV(3)
                    if(similarProjectList.delivery_date.isNotEmpty()) {
                        TagBox(
                            "DELIVERY DATE : ${similarProjectList.delivery_date}",
                            Color.White,
                            Color.Black
                        )
                    }
                    if (similarProjectList.sale_started.isNotEmpty()){
                        AppSpacerV(3)
                        TagBox("SALE STARTED : ${similarProjectList.sale_started}", Color.Red, Color.White)
                    }
                    if (!similarProjectList.payment_plan_available.isEmpty()){
                        AppSpacerV(3)
                        TagBox(similarProjectList.payment_plan_available, colorResource(R.color.light_green), Color.White)
                    }
                }
            }

            // 🔹 DETAILS SECTION
            Column(modifier = Modifier.padding(horizontal = 15.dp, vertical = 18.dp)) {

                AppTextMedium(text = similarProjectList.title,
                    fontSize = 16.sp, color = Color.Black)
                AppSpacerV(3)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(resId = R.drawable.location,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp))
                    AppSpacerH(4)
                    AppTextLight(text = similarProjectList.location,
                        fontSize = 13.sp, color = Color.Gray)
                }

                AppSpacerV(12)

                AppDivider(color = Color.LightGray, thickness = 1.dp)
                AppSpacerV(10)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column (modifier = Modifier.weight(1f)){
                        AppTextLight(text = "Launch price", fontSize = 12.sp, color = Color.Gray)
                        if (!similarProjectList.price.isEmpty()){
                            AppTextBold(text = similarProjectList.price, fontSize = 16.sp,
                                color = Color.Black)
                        } else {
                            AppTextBold(text = stringResource(R.string.coming_soon), fontSize = 16.sp,
                                color = Color.Black)
                        }
                    }

                 /*   Button(
                        onClick = {

                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.light_green)
                        )
                    ) {
                        AppIcon(resId = R.drawable.whatapp,
                            contentDescription = "whatsapp",
                            modifier = Modifier.size(18.dp))
                       AppSpacerH(5)

                        AppTextMedium(text = "Whatsapp", color = Color.White, fontSize = 14.sp)
                    }*/
                }
            }
        }
    }
}
@Composable
fun ProjectTimelineScreen(timelineItems: List<ProjectTimelineItem>) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .padding(horizontal = 20.dp)
        .offset(y = (-20).dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E0E0E)),
        shape = RoundedCornerShape(16.dp)) {

        Column(modifier = Modifier) {
            AppTextBold(text = "Project Timeline",color = Color.White, fontSize = 18.sp,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp)
                    .fillMaxWidth(), textAlign = TextAlign.Center)
            AppSpacerV(15)
            AppDivider(color = colorResource(R.color.textlight), thickness = 1.dp)
            AppSpacerV(15)

            timelineItems.forEachIndexed { index, item ->
                TimelineItem(
                    title = item.milestone.orEmpty(),
                    date = formatDateToDisplay(item.date),
                    isCompleted = item.status.equals("completed", true),
                    isLast = index == timelineItems.lastIndex,
                    position = index
                )
            }
        }
    }
}

@Composable
fun TimelineItem(title: String, date: String, isCompleted: Boolean, isLast: Boolean, position: Int) {
    Row {
        // Left timeline part
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 20.dp, top = if (position==0) 10.dp else 0.dp)) {
            // Circle
            Box(modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (isCompleted) Color(0xFF00C853)
                    else Color(0xFF3A3A3A)
                ),
                contentAlignment = Alignment.Center
            ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
            }

            // Vertical line
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(38.dp)
                        .background(
                            if (isCompleted) Color(0xFF00C853)  // Green
                            else Color(0xFF2A2A2A)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Text section
        Column(modifier = Modifier.padding(top = 2.dp)) {
            AppTextMedium(text = title,
                color = if (isCompleted) Color.White else Color(0xFFB0B0B0),
                fontSize = 14.sp,
            )
            AppSpacerV(2)
            AppTextLight(text = date, color = Color(0xFF8A8A8A), fontSize = 12.sp)
            AppSpacerV(8)
        }
    }
}

@Composable
fun About_Master_PropertyLoc_View(
    navController: NavController,
    projectDetailItem: ProjectDetailItem?,
    lat: Double,
    lng: Double,
    developerPhoneNumber: String
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 5.dp, horizontal = 20.dp)
    ) {
        AppTextSemiBold(stringResource(R.string.about_the_project))

        HtmlText(
            html = projectDetailItem?.description?.takeIf { it.isNotBlank() } ?: projectDetailItem?.aboutProject.orEmpty(),
            colorResId = R.color.textlight,
            fontSizeSp = 12f,
            maxLines = 8,
            lineSpacingExtra = 4f,
            modifier = Modifier.padding(top = 6.dp).fillMaxWidth()
        )

        PrimaryRoundButton(
            text = stringResource(R.string.see_full_description),
            onClick = {
                navController.navigate(Route.AboutProject.createRoute(projectDetailItem?.id.orEmpty()))
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            containerColor = colorResource(R.color.darkBlue),
            contentColor = Color.White,
            cornerRadius = 10.dp
        )
        AppSpacerV(15)

    }
    Box(modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .height(250.dp)
        .padding(horizontal = 20.dp)
        .clip(RoundedCornerShape(10.dp))){
        AppImage(modifier = Modifier.fillMaxSize(),
            resId = R.drawable.unit_bg,
            contentScale = ContentScale.FillWidth)
        Column (modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            AppImage(resId = R.drawable.ic_unit, Modifier.size(40.dp))
            AppSpacerV(10)
            AppTextBold(text = "Available units from\ndevelopers", color = Color.White,
                textAlign = TextAlign.Center)
            AppSpacerV(10)
            CornerButton(text = "See all available units",
                modifier = Modifier.padding(horizontal = 30.dp),
                containerColor = colorResource(R.color.textprimary),
                onClick = {
                    val unitsJson = Gson().toJson(projectDetailItem?.unitsFromDeveloper.orEmpty())
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("units_from_developer_json", unitsJson)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("project_id_for_inquiry", projectDetailItem?.id.orEmpty())
                    navController.navigate(Route.DeveloperUnit.route)
                })
        }
    }

    Row(modifier = Modifier
        .height(10.dp)
        .fillMaxWidth()
        .background(Background)){}
    AppDivider(color = Color.LightGray, thickness = 1.dp)
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(Background)
        .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        AppTextSemiBold(stringResource(R.string.masterplan))

        val projectImageBaseUrl = PrefUtils.getString(
            Constants.TAG_URL_PROJECT_IMG,
            "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/"
        ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/" }

        val masterPlanPath = projectDetailItem?.masterPlan?.firstOrNull()
        val floorPlanUrl = if (!masterPlanPath.isNullOrBlank()) {
            buildImageUrl(projectImageBaseUrl, masterPlanPath)
        } else {
            null
        }

        if (!floorPlanUrl.isNullOrBlank()) {
            AppSubComposeAsyncImage(
                model = floorPlanUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(top = 15.dp)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = R.drawable.welcome,
                error = R.drawable.welcome,
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(top = 15.dp)
                    .background(Color.White, shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AppImage(
                        resId = R.drawable.ic_placeholder,
                        modifier = Modifier.size(50.dp),
                        tint = Color.LightGray
                    )
                    AppSpacerV(8)
                    AppTextMedium(
                        text = "Master plan image not found",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    AppDivider(color = Color.LightGray, thickness = 1.dp)
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        AppTextSemiBold(stringResource(R.string.property_location),
            modifier = Modifier.padding(bottom = 15.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(3.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    val sLink = projectDetailItem?.shareLink.takeIf { !it.isNullOrBlank() }
                        ?: "https://molumulk.tj/projectdrilldown/${projectDetailItem?.id ?: ""}"
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("project_location_lat", lat)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("project_location_lng", lng)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("developer_phone_number", developerPhoneNumber)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("share_link", sLink)
                    navController.navigate(Route.PropertyLocation.route)
                }
        ) {
            StaticMapPreview(
                lat = lat,
                lng = lng,
                apiKey = stringResource(R.string.google_map_key),
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(start = 20.dp, end = 20.dp, bottom = 5.dp)
    ) {
        AppTextSemiBold(stringResource(R.string.authorised_agencies))
    }
}
@Composable
fun Faq_View(
    navController: NavController,
    faqItems: List<FaqItem>,
    authorizedAgencies: List<ProjectAuthorizedAgency>,
    projectLocation: String = ""
) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 5.dp, horizontal = 20.dp)
    ) {
        PrimaryRoundButton(
            text = stringResource(R.string.see_all_authorised),
            onClick = {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("authorized_agencies_json", Gson().toJson(authorizedAgencies))
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("project_location", projectLocation)
                navController.navigate(Route.FindCompanies.route)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp),
            containerColor = colorResource(R.color.darkBlue),
            contentColor = Color.White,
            cornerRadius = 10.dp
        )
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .background(Background)){}
    AppDivider(color = Color.LightGray, thickness = 1.dp)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        AppTextSemiBold(
            text = stringResource(R.string.faq),
            modifier = Modifier.padding(bottom = 10.dp)
        )

        if (faqItems.isNotEmpty()) {
            val visibleItems = faqItems.take(4)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                visibleItems.forEachIndexed { index, faq ->
                    ExpandableFaqItem(title = faq.title, description = faq.description)
                    if (index != visibleItems.lastIndex) {
                        Divider(color = Color(0xFFE0E0E0))
                    }
                }
            }

            AppSpacerV(15)

            // See all button — only shown when there are FAQs
            PrimaryRoundButton(
                text = stringResource(R.string.see_all),
                onClick = {
                    val json = Gson().toJson(faqItems)
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("faq_json", json)
                    navController.navigate(Route.Faq.route)
                },
                modifier = Modifier.fillMaxWidth(),
                containerColor = colorResource(R.color.darkBlue),
                contentColor = Color.White,
                cornerRadius = 10.dp
            )
        } else {
            // No FAQs — show message, hide See all button
            AppTextRegular(
                text = "No FAQs available for this project",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }

    Row(modifier = Modifier
        .fillMaxWidth()
        .height(20.dp)
        .background(Background)){}
    AppDivider(color = Color.LightGray, thickness = 1.dp)

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(vertical = 20.dp, horizontal = 20.dp)
    ) {
        AppTextSemiBold(stringResource(R.string.similar_projects),
            modifier = Modifier.padding())

    }
    }
}

@Composable
fun Report_View(navController: NavController, reportedItemId: String?) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 20.dp, vertical = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextSemiBold(text = "Report this listing",
            modifier = Modifier.padding(bottom = 10.dp))

        AppTextLight(text = "If the Property or Agent is unusual or you faced some problems. please report us", fontSize = 15.sp,
            modifier = Modifier.padding(bottom = 10.dp, start = 15.dp, end = 15.dp),
            textAlign = TextAlign.Center)

        Button(
            onClick = {
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("reportType", "project")
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("reportedItemId", reportedItemId)
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("userType", "user")
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("reason", "incorrect-info")
                navController.navigate(Route.ReportProblem.route)
            },
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 5.dp,
                pressedElevation = 6.dp,
                disabledElevation = 0.dp
            ),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.white)
            )
        ) {
            AppIcon(resId = R.drawable.report_flag,
                contentDescription = "report",
                modifier = Modifier.size(18.dp),
                tint = Color.Black)
            AppSpacerH(5)

            AppTextMedium(text = stringResource(R.string.report), color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Composable
fun TagBox(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor,)
            .padding(horizontal = 8.dp)
    ) {
        AppTextMedium(text= text, fontSize = 11.sp, color =textColor)
    }
}

@Composable
fun ExpandableFaqItem(title: String, description: String) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .background(Color.White)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {

            AppTextMedium(text = title,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = Color.Black)

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(if (expanded) 180f else 0f),
                tint = Color.Gray
            )
        }

        AnimatedVisibility(visible = expanded) {
            AppTextMedium(text = description, fontSize = 13.sp, color = Color.Gray,
                modifier = Modifier.padding(top = 10.dp))
        }
    }
}
@Composable
fun MyGoogleMap(
    lat: Double = 25.2058,
    lng: Double = 55.2718,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val demoLoc = LatLng(lat, lng)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(demoLoc, 15f)
    }

    GoogleMap(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp)),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = false,
            zoomGesturesEnabled = false,
            rotationGesturesEnabled = false,
            tiltGesturesEnabled = false
        )
    ) {

        Marker(
            state = MarkerState(position = demoLoc),
            title = "Marker in DemoLoc",
            icon = bitmapDescriptorFromVector(
                context,
                R.drawable.ic_map_marker,
                100,
                130
            )        )
    }
}

fun bitmapDescriptorFromVectorLegacy(context: Context, vectorResId: Int): BitmapDescriptor {

    val drawable = ContextCompat.getDrawable(context, vectorResId)!!

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

private fun formatDateToDisplay(value: String?): String {
    if (value.isNullOrBlank()) return "N/A"
    val inputFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )
    val output = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    inputFormats.forEach { pattern ->
        runCatching {
            val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(value)
            if (date != null) return output.format(date)
        }
    }
    return value
}

private fun formatPaymentDate(value: String?): String {
    if (value.isNullOrBlank()) return ""
    val inputFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )
    val output = SimpleDateFormat("yyyy MMM", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    inputFormats.forEach { pattern ->
        runCatching {
            val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(value)
            if (date != null) return output.format(date)
        }
    }
    return value
}