package com.app.propertyfinder.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.NearbyPlaces
import com.app.propertyfinder.api.dto.response.PriceInsightsChartSeries
import com.app.propertyfinder.api.dto.response.PriceInsightsResponse
import com.app.propertyfinder.api.dto.response.PropertyAmenityDetail
import com.app.propertyfinder.api.dto.response.PropertyDetail
import com.app.propertyfinder.api.dto.response.PropertyDetailResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.data.source.PricePoint
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppLoader
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppRatingBarWhite
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.DividerColor
import com.app.propertyfinder.ui.theme.OTPVerifiedBtn
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TabPrimary
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.QrCodeEncoder
import com.app.propertyfinder.utils.formatListedTiming
import com.app.propertyfinder.utils.openUrlInChromeOrDefault
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.app.propertyfinder.utils.resolveCdnOrAbsoluteMediaUrl
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** CDN base for amenity icon filenames from the API (e.g. `pool-icon.png`). */
private const val AMENITY_ICON_BASE_URL = "https://d1dp1oh0ra5b0z.cloudfront.net/img/amenities/"

private const val DEFAULT_MAP_LAT = 25.2058
private const val DEFAULT_MAP_LNG = 55.2718

private fun regulatoryField(value: String?): String =
    value?.takeIf { it.isNotBlank() } ?: "—"

/** Formats a price integer as compact string: 1000 → "1K", 1_500_000 → "1.5M" */
private fun formatCompactPrice(price: Int): String {
    return when {
        price >= 1_000_000 -> {
            val m = price / 1_000_000.0
            if (m % 1 == 0.0) "${m.toInt()}M" else "${String.format("%.1f", m)}M"
        }
        price >= 1_000 -> {
            val k = price / 1_000.0
            if (k % 1 == 0.0) "${k.toInt()}K" else "${String.format("%.1f", k)}K"
        }
        else -> price.toString()
    }
}

private fun NavHostController.navigateToProjectLocationMap(
    lat: Double,
    lng: Double,
    shareLink: String? = null,
    agentPhoneNumber: String? = null
) {
    currentBackStackEntry?.savedStateHandle?.set("project_location_lat", lat)
    currentBackStackEntry?.savedStateHandle?.set("project_location_lng", lng)
    currentBackStackEntry?.savedStateHandle?.set("share_link", shareLink)
    currentBackStackEntry?.savedStateHandle?.set("developer_phone_number", agentPhoneNumber.orEmpty())
    currentBackStackEntry?.savedStateHandle?.set("phone_error_message", "Agency number not found")
    navigate(Route.PropertyLocation.route)
}

/** @Preview only — same [PropertyAmenityDetail] shape as the API (CDN icon filenames). */
private val previewPropertyAmenitiesSample = listOf(
    PropertyAmenityDetail(name = "Swimming Pool", icon = "pool-icon.png"),
    PropertyAmenityDetail(name = "Air Conditioning", icon = "ac-icon.png"),
    PropertyAmenityDetail(name = "Parking", icon = "parking-icon.png"),
    PropertyAmenityDetail(name = "Security", icon = "security-icon.png"),
    PropertyAmenityDetail(name = "Gym", icon = "gym-icon.png")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyDetailScreen(navController: NavHostController, propertyId: String, viewModel: PropertyViewModel = hiltViewModel()) {
    val userViewModel: UserViewModel = androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel()
    val snackbarManager = rememberSnackbarManager()
    val context = LocalContext.current
    LaunchedEffect(propertyId) {
        viewModel.getPropertyDetails(propertyId) { /* handled via state */ }
        viewModel.getPriceInsights(propertyId)
    }

    val createInquiryState by userViewModel.createInquiryState.collectAsStateWithLifecycle()
    val propertyDetailsState by viewModel.propertyDetailsState.collectAsState()
    val detailsResponse: ApiEnvelope<PropertyDetailResponse>? = propertyDetailsState.data
    val propertyDetails = detailsResponse?.data?.property

    val priceInsightsState by viewModel.priceInsightsState.collectAsStateWithLifecycle()
    val priceInsights = priceInsightsState.data?.data
    var selectedTransactionType by remember { mutableStateOf("Sold for") }

    val titleText = propertyDetails?.title.orEmpty()
    val locationText = propertyDetails?.location?.fullAddress
        ?: listOfNotNull(propertyDetails?.location?.zone, propertyDetails?.location?.city).joinToString(", ")
//    val priceText = listOfNotNull(propertyDetails?.price?.toString(), propertyDetails?.currency).joinToString(" ")
    val priceText = propertyDetails?.let { details ->
        if (details.rentPricing?.yearly != null) {
            "${details.rentPricing.yearly} ${details.currency.orEmpty()} / Yearly"
        } else {
            "${details.price ?: ""} ${details.currency.orEmpty()}".trim()
        }
    } ?: ""
    val bedsText = propertyDetails?.bedrooms?.let { "$it Bed" } ?: "-"
    val bathsText = propertyDetails?.bathrooms?.let { "$it Bath" } ?: "-"
    val areaText = propertyDetails?.area?.sqft?.let { "$it sqft" }
        ?: propertyDetails?.area?.sqm?.let { "$it sqm" }
        ?: "-"
    val amenities = propertyDetails?.amenities.orEmpty()
    val imageBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROPERTY_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/" }
    val propertyVideoBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROPERTY_VID,
        "https://d1dp1oh0ra5b0z.cloudfront.net/vid/property/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/vid/property/" }
    val nearbyPlaces = detailsResponse?.data?.nearbyPlaces
    val aroundPlaceItems = remember(nearbyPlaces) { nearbyPlaces.flattenAroundPlaces() }

    // Price range from related properties
    val relatedProperties = detailsResponse?.data?.relatedProperties.orEmpty()
    val isRentListing = propertyDetails?.listingType?.transaction == "rent"
    val relatedPriceRange = remember(relatedProperties, isRentListing) {
        val prices = relatedProperties.mapNotNull { it.price }.filter { it > 0 }
        if (prices.isEmpty()) null
        else {
            val currency = relatedProperties.firstOrNull { !it.currency.isNullOrBlank() }?.currency ?: "AED"
            val suffix = if (isRentListing) "/year" else ""
            val min = formatCompactPrice(prices.min())
            val max = formatCompactPrice(prices.max())
            "$min - $max $currency$suffix"
        }
    }

    val propertyFeatures = remember(propertyDetails) {
        listOfNotNull(
            propertyDetails?.propertyType?.name?.takeIf { it.isNotBlank() }?.let { "It's a $it" },
            propertyDetails?.area?.sqft?.let { "$it sqft" } ?: propertyDetails?.area?.sqm?.let { "$it sqm" },
            propertyDetails?.completionStatus?.takeIf { it.isNotBlank() }?.let { status ->
                status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
            },
            propertyDetails?.furnishedStatus?.takeIf { it.isNotBlank() }?.let { status ->
                status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
            }
        )
    }

    val coordList = propertyDetails?.location?.coordinates?.coordinates.orEmpty()
    val mapLng = coordList.getOrNull(0) ?: DEFAULT_MAP_LNG
    val mapLat = coordList.getOrNull(1) ?: DEFAULT_MAP_LAT

    val regulatoryQrPayload = remember(
        propertyDetails?.id,
        propertyDetails?.dldPermitUrl,
        propertyDetails?.dldPermitNumber,
        propertyDetails?.regulatoryInformation?.dldPermitUrl,
        propertyDetails?.regulatoryInformation?.dldPermitNumber
    ) {
        propertyDetails?.regulatoryInformation?.dldPermitUrl?.takeIf { it.isNotBlank() }
            ?: propertyDetails?.dldPermitUrl?.takeIf { it.isNotBlank() }
            ?: propertyDetails?.regulatoryInformation?.dldPermitNumber?.takeIf { it.isNotBlank() }
            ?: propertyDetails?.dldPermitNumber?.takeIf { it.isNotBlank() }
    }
    var regulatoryQrBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(regulatoryQrPayload) {
        regulatoryQrBitmap = if (regulatoryQrPayload.isNullOrBlank()) {
            null
        } else {
            withContext(Dispatchers.Default) {
                QrCodeEncoder.encodeAsImageBitmap(regulatoryQrPayload, 512)
            }
        }
    }

    var localSavedOverride by remember(propertyId) { mutableStateOf<Boolean?>(null) }
    val isSavedEffective = localSavedOverride ?: (propertyDetails?.isSaved == true)

    val systemBarsHeight = with(LocalDensity.current) {
        WindowInsets.systemBars.getTop(this).toDp()
    }

    val sortedPropertyImages = propertyDetails?.images.orEmpty()
        .sortedBy { it.order ?: Int.MAX_VALUE }
    val galleryUrls: List<String> = sortedPropertyImages.mapNotNull { img ->
        img.url?.takeIf { it.isNotBlank() }?.let { path -> imageBaseUrl + path }
    }

    var selectedImageIndex by remember(propertyId) { mutableStateOf(0) }

    LaunchedEffect(propertyId, sortedPropertyImages) {
        val primaryIdx = sortedPropertyImages.indexOfFirst { it.isPrimary == true }
        selectedImageIndex = when {
            primaryIdx >= 0 -> primaryIdx
            else -> 0
        }
    }

    val heroIndex = if (galleryUrls.isEmpty()) 0
    else selectedImageIndex.coerceIn(0, galleryUrls.lastIndex)
    val heroImageUrl = galleryUrls.getOrNull(heroIndex)

    val fullVideoTourUrl = remember(propertyDetails?.videoTour, propertyVideoBaseUrl) {
        resolveCdnOrAbsoluteMediaUrl(propertyDetails?.videoTour, propertyVideoBaseUrl)
    }
    val virtualTourUrl = propertyDetails?.virtualTour360?.trim().orEmpty()
    val hasVirtualTour = virtualTourUrl.isNotEmpty()

    val navigateToMediaFullView = remember(navController, galleryUrls, heroIndex, titleText, fullVideoTourUrl) {
        { openVideoFirst: Boolean ->
            navController.currentBackStackEntry?.savedStateHandle?.apply {
                set("images", galleryUrls)
                set("selectedImageIndex", heroIndex)
                set("imageTitle", titleText)
                set("videoTourFullUrl", fullVideoTourUrl)
                set("openVideoFirst", openVideoFirst)
            }
            navController.navigate(Route.ImageFullView.route)
        }
    }

    val heroSectionHeight = 160.dp + systemBarsHeight
    val thumbRowHeight = 80.dp
    val videoSectionHeight = 180.dp
    val sheetOverlap = 170.dp
    val scrollTopPadding = heroSectionHeight + thumbRowHeight + videoSectionHeight - sheetOverlap

    val amenitiesLabel = when {
        amenities.isEmpty() -> "Amenities"
        amenities.size >= 40 -> "40+ Amenities"
        else -> "${amenities.size}+ Amenities"
    }

    if (propertyDetailsState.isLoading){
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center) {
            AppLoader()
        }
    }

    if (createInquiryState.isLoading){
        AppLoadingDialog()
    }
    Column(modifier = Modifier.fillMaxSize().background(colorResource(R.color.screen_bg_color))) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // —— Fixed media (does not scroll): hero, thumbnails, video ——
            Column(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heroSectionHeight)) {
                    if (heroImageUrl != null) {
                        SubcomposeAsyncImage(
                            model = heroImageUrl,
                            contentDescription = titleText,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(
                                    Modifier.fillMaxSize().background(Color.DarkGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppImage(
                                        resId = R.drawable.ic_placeholder,
                                        contentDescription = null,
                                        modifier = Modifier.size(80.dp)
                                    )
                                }
                            },
                            error = {
                                Image(
                                    painter = painterResource(R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.welcome),
                            contentDescription = titleText,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().height(thumbRowHeight).padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    repeat(3) { index ->
                        val url = galleryUrls.getOrNull(index)
                        Box(
                            modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(4.dp))
                        ) {
                            if (url != null) {
                                SubcomposeAsyncImage(
                                    model = url,
                                    contentDescription = "Gallery ${index + 1}",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    loading = {
                                        Box(Modifier.fillMaxSize().background(Color.LightGray))
                                    },
                                    error = {
                                        Image(
                                            painter = painterResource(R.drawable.ic_placeholder),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                )
                            } else {
                                Image(
                                    painter = painterResource(R.drawable.ic_placeholder),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(videoSectionHeight)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clip(RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp))
                        .clickable(enabled = fullVideoTourUrl != null) {
                            navigateToMediaFullView(true)
                        }
                ) {
                    val videoThumbnailUrl = galleryUrls.getOrNull(3) ?: heroImageUrl
                    if (videoThumbnailUrl != null) {
                        SubcomposeAsyncImage(
                            model = videoThumbnailUrl,
                            contentDescription = "Video Thumbnail",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            alpha = 0.7f,
                            loading = {
                                Box(Modifier.fillMaxSize().background(Color.DarkGray))
                            },
                            error = {
                                Box(Modifier.fillMaxSize().background(Color.DarkGray))
                            }
                        )
                    }

                    AppImage(
                        resId = R.drawable.ic_video_play,
                        modifier = Modifier.size(45.dp).align(Alignment.Center).offset(y = (-20).dp)
                    )
                }
            }

            // —— Scrollable sheet overlaps fixed media ——
//            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = scrollTopPadding)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(modifier = Modifier.fillMaxWidth()){
                        Box(modifier = Modifier.fillMaxWidth().height(scrollTopPadding)
                            .clickable(enabled = galleryUrls.isNotEmpty() || fullVideoTourUrl != null) {
                                navigateToMediaFullView(false)
                            })
                    }
                }
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 30.dp)){
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

                        Row(modifier = Modifier.fillMaxWidth().padding(start = 15.dp).align(Alignment.BottomCenter).padding(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val developerImageBaseUrl = PrefUtils.getString(
                                Constants.TAG_URL_DEVELOPER_IMG,
                                "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/"
                            ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/" }
                            AppSubComposeAsyncImage(
                                model = buildImageUrl(developerImageBaseUrl, propertyDetails?.agency?.profilePicture),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.width(70.dp).height(50.dp).clip(RoundedCornerShape(8.dp)),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(38.dp).clip(CircleShape)
                                    .background(if (isSavedEffective) MolumulkPrimaryCoralRed else Color.White)
                                    .clickable {
                                        val id = propertyDetails?.id ?: return@clickable
                                        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                                        if (loginType == Constants.LOGIN_USER) {
                                            if (isSavedEffective) {
                                                userViewModel.removeFromSaved(
                                                    token = TokenManager.accessToken,
                                                    propertyId = id
                                                ) { result ->
                                                    when (result) {
                                                        is ApiResult.Success -> {
                                                            snackbarManager.showSuccess(result.body.message.toString())
                                                            localSavedOverride = false
                                                        }
                                                        is ApiResult.Error -> {
                                                            snackbarManager.showError(result.message)
                                                            Log.e("REMOVE_SAVED", "Error: ${result.message}")
                                                        }
                                                    }
                                                }
                                            } else {
                                                userViewModel.addToSaved(
                                                    token = TokenManager.accessToken,
                                                    propertyId = id
                                                ) { result ->
                                                    when (result) {
                                                        is ApiResult.Success -> {
                                                            snackbarManager.showSuccess(result.body.message.toString())
                                                            localSavedOverride = true
                                                        }
                                                        is ApiResult.Error -> {
                                                            snackbarManager.showError(result.message)
                                                            Log.e("ADD_TO_SAVED", "Error: ${result.message}")
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
                                    contentAlignment = Alignment.Center) {
                                    AppImage(
                                        resId = if (isSavedEffective) R.drawable.heart_filled else R.drawable.heart,
                                        contentDescription = "Save",
                                        modifier = Modifier.size(16.dp),
                                        contentScale = ContentScale.Fit,
                                        tint = if (isSavedEffective) Color.White else colorResource(R.color.textprimary)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(end = 15.dp)
                                        .height(38.dp)
                                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                                        .background(Color.White)
                                        .clickable {
                                            val propertyId = propertyDetailsState.data?.data?.property?.id
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("reportType", "property")
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("reportedItemId", propertyId)
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("userType", "user")
                                            navController.currentBackStackEntry
                                                ?.savedStateHandle
                                                ?.set("reason", "fraud")
                                            navController.navigate(Route.ReportProblem.route)
                                        }
                                        .padding(horizontal = 16.dp)
                                ) {
                                    AppImage(
                                        resId = R.drawable.report_flag,
                                        contentDescription = null,
                                        modifier = Modifier.size(width = 12.dp, height = 15.dp)
                                    )
                                    AppTextSemiBold(
                                        text = stringResource(R.string.report),
                                        color = TextPrimary,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Surface(modifier = Modifier.fillMaxWidth(), color = Background,) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).padding(horizontal = 20.dp)) {

                            AppTextBold(text = titleText, fontSize = 15.sp)

                            AppSpacerV(8)

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(id = R.drawable.location),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = TextSecondary
                                )
                                AppSpacerH(6)
                                AppTextLight(text = locationText, color = TextSecondary )
                            }

                            AppSpacerV(15)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.bad),
                                        contentDescription = "Bedrooms",
                                        modifier = Modifier.size(13.dp),
                                        tint = TextPrimary
                                    )
                                    AppTextMedium(text = bedsText, fontSize = 12.sp)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.bath),
                                        contentDescription = "Bathrooms",
                                        modifier = Modifier.size(13.dp),
                                        tint = TextPrimary
                                    )
                                    AppTextMedium(text = bathsText, fontSize = 12.sp)
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.sqft),
                                        contentDescription = "Area",
                                        modifier = Modifier.size(13.dp),
                                        tint = TextPrimary
                                    )
                                    AppTextMedium(text = areaText, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth().background(Background).padding(vertical = 20.dp)) {
                        // Verified container
                        if (propertyDetails?.isVerified == true) {
                            Row(
                                modifier = Modifier.weight(1f).background(OTPVerifiedBtn)
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                AppImage(
                                    resId = R.drawable.ic_sharp_verified_user, // Verified check icon
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(14.dp),
                                )
                                AppSpacerH(6)
                                AppTextMedium(
                                    text = "VERIFIED",
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        // Counts with amenity container
                        Row(
                            modifier = Modifier.weight(1f).background(DividerColor).padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppTextMedium(text = amenitiesLabel, color = TextSecondary, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }

                        // View on map container
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .background(TextPrimary)
                                .clickable {
                                    val sLink = propertyDetails?.shareLink.takeIf { !it.isNullOrBlank() }
                                        ?: "https://molumulk.tj/propertydrilldown/${propertyDetails?.id ?: ""}"
                                    navController.navigateToProjectLocationMap(mapLat, mapLng, sLink, propertyDetails?.agent?.phoneNumber)
                                }
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            AppImage(resId = R.drawable.ic_map_detail, // Map icon
                                contentDescription = "View on map",
                                modifier = Modifier.size(14.dp)
                            )
                            AppSpacerH(5)
                            AppTextMedium(text = "View on map", color = Color.White, fontSize = 12.sp)
                        }
                    }
                }

                item{
                    Column(modifier = Modifier.background(Background).padding(horizontal = 20.dp)) {
                        // Project Details heading
                        AppTextMedium(fontSize = 20.sp, text = "Project details")

                        AppSpacerV(8)


                        // Description text
                        AppTextRegular(
                            text = propertyDetails?.description?.takeIf { it.isNotBlank() }
                                ?: "Beautiful villa located in the prestigious Palm Jumeirah area. This stunning property offers luxurious living with modern amenities and breathtaking views of the Arabian Gulf.",
                            color = TextPrimary, lineHeight = 25.sp
                        )

                        AppSpacerV(10)

                        // Amenities list with bullet points
                        if (propertyFeatures.isNotEmpty()) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                propertyFeatures.forEach { feature ->
                                    AmenityItem(feature)
                                }
                            }
                        }

                        AppSpacerV(20)

                        PrimaryRoundButton(
                            text = "View full details",
                            onClick = { navController.navigate(Route.ProjectDetails.createPropertyRoute(propertyId)) },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MolumulkPrimaryCoralRed,
                            cornerRadius = MolumulkDimens.PrimaryCtaRadius
                        )
                    }
                }
                // Full width divider
                item {
                    Column(modifier = Modifier.background(Background)) {
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)  }
                }

                // Around this property section
                item {
                    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {
                        AppTextMedium(fontSize = 20.sp, text = "Around this property")
                        // Horizontal scroll view with white background containers
                        AppSpacerV(15)
                        if (aroundPlaceItems.isEmpty()) {
                            AppTextRegular(text = "No nearby places available")
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 0.dp)
                            ) {
                                items(
                                    items = aroundPlaceItems,
                                    key = { "${it.categoryLabel}_${it.placeName}_${it.distanceText}" }
                                ) { row ->
                                    AroundPropertyItem(
                                        categoryLabel = row.categoryLabel,
                                        placeName = row.placeName,
                                        distanceText = row.distanceText
                                    )
                                }
                            }
                        }
                        AppSpacerV(25)
                    }
                }

                // 360 view container - 230dp height with 10dp radius
                item {
                    val onOpenVirtualTour = {
                        openUrlInChromeOrDefault(context, virtualTourUrl, "PropertyDetail")
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Background)
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .height(230.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black)
                            .clickable(enabled = hasVirtualTour, onClick = onOpenVirtualTour),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 20.dp)
                        ) {

                            // Icon
                            AppImage(
                                resId = R.drawable.threesixty,
                                contentDescription = "360 view",
                                modifier = Modifier
                                    .width(54.dp)
                                    .height(40.dp)
                            )

                            AppSpacerV(15)

                            // Text
                            AppTextMedium(
                                text = "Experience Project\nin 360 view",
                                color = Color.White,
                                fontSize = 20.sp,
                                maxLines = 2,
                                textAlign = TextAlign.Center
                            )

                            AppSpacerV(15)

                            // Button
                            PrimaryRoundButton(
                                text = "View now",
                                onClick = onOpenVirtualTour,
                                modifier = Modifier.padding(horizontal = 15.dp),
                                containerColor = TabPrimary,
                                cornerRadius = MolumulkDimens.PrimaryCtaRadius,
                                enabled = hasVirtualTour
                            )
                        }
                    }
                }


                item {
                    AppSpacerH(25)
                }
                item {
                    if (amenities.isNotEmpty()) {
                        PropertyAmenitiesSection(
                            amenities = amenities,
                            modifier = Modifier.fillMaxWidth().background(Background)
                                .padding(horizontal = 20.dp),
                            onShowAllClick = {
                                val navItems = amenities.map { a ->
                                    AllAmenitiesNavItem(
                                        title = a.name.orEmpty(),
                                        iconUrl = a.image?.takeIf { it.isNotBlank() }
                                            ?.let { AMENITY_ICON_BASE_URL + it }
                                    )
                                }
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("amenities_json", Gson().toJson(navItems))
                                navController.navigate(Route.AllAmenities.route)
                            }
                        )
                    }
                }
                item {
                    Column(modifier = Modifier.background(Background)) {
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)
                    }
                }
                item {
                    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {
                        // Title and Sold dropdown in same row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppTextMedium(fontSize = 20.sp, text = "Similar transaction")

                            // Sold dropdown
                            Box {
                                var dropdownExpanded by remember { mutableStateOf(false) }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                                        .background(Color.White)
                                        .clickable { dropdownExpanded = true }
                                        .padding(horizontal = 15.dp, vertical = 2.dp)
                                        .height(33.dp)
                                ) {
                                    AppTextSemiBold(text = if (selectedTransactionType == "Sold for") "Sold" else "Rented", fontSize = 12.sp)
                                    AppImage(
                                        resId = R.drawable.arrow_down,
                                        contentDescription = "Dropdown",
                                        modifier = Modifier.size(8.dp)
                                    )
                                }

                                DropdownMenu(
                                    expanded = dropdownExpanded,
                                    onDismissRequest = { dropdownExpanded = false },
                                    modifier = Modifier
                                        .width(165.dp)
                                        .background(Color.White, RoundedCornerShape(10.dp))
                                        .clip(RoundedCornerShape(10.dp))
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Sold for", fontSize = 12.sp) },
                                        onClick = {
                                            selectedTransactionType = "Sold for"
                                            dropdownExpanded = false
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Rented for", fontSize = 12.sp) },
                                        onClick = {
                                            selectedTransactionType = "Rented for"
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        AppSpacerV(15)
                    }
                }

                val transactions = if (selectedTransactionType == "Sold for") {
                    priceInsights?.sold?.transactions.orEmpty()
                } else {
                    priceInsights?.rent?.transactions.orEmpty()
                }

                if (transactions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Background)
                                .padding(horizontal = 20.dp, vertical = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppTextRegular(text = "No transactions found", color = TextSecondary)
                        }
                    }
                } else {
                    items(transactions.take(3)) { transaction ->
                        Box(modifier = Modifier.background(Background).padding(horizontal = 20.dp, vertical = 5.dp)) {
                            TransactionItem(
                                propertyName = transaction.title.orEmpty(),
                                location = transaction.subtitle.orEmpty(),
                                price = "${transaction.currency.orEmpty()} ${transaction.priceFormatted.orEmpty()}",
                                details = "${transaction.areaFormatted.orEmpty()} sqft",
                                date = transaction.date.orEmpty(),
                                status = ""
                            )
                        }
                    }
                }

                item {
                    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {
                        AppSpacerV(20)

                        // See all transaction primary button
                        PrimaryRoundButton(
                            text = "See all transaction",
                            onClick = { navController.navigate(Route.TransactionOverview.route) },
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = MolumulkPrimaryCoralRed,
                            cornerRadius = MolumulkDimens.PrimaryCtaRadius
                        )
                    }
                }

                item {
                    Column(modifier = Modifier.background(Background)) {
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)
                        PriceTrendsSection(priceInsights, navController)
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)
                    }
                }
                propertyDetails?.let { pd ->
                    item {
                        Column(modifier = Modifier.background(Background)) {
                            PropertyLocationSection(mapLat, mapLng, navController, pd, relatedPriceRange)
                            AppSpacerV(25)
                            AppDivider()
                            AppSpacerV(25)
                        }
                    }
                    item {
                        AgentSection(pd, navController)
                    }
                }
                item {
                    Column (modifier = Modifier.background(Background)){
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)
                    }
                }
                // Regulatory Information Section
                item {
                    propertyDetails?.let { p ->
                        val regulatoryInfo = p.regulatoryInformation
                        val reference = when {
                            !regulatoryInfo?.referenceId.isNullOrBlank() -> regulatoryInfo?.referenceId
                            !p.referenceId.isNullOrBlank() -> p.referenceId
                            else -> p.id
                        }
                        RegulatoryInformationCard(
                            reference = regulatoryField(reference),
                            agencyName = regulatoryField(regulatoryInfo?.agencyName ?: p.agency?.agencyName),
                            brokerLicense = regulatoryField(
                                regulatoryInfo?.brokerLicenseNumber
                                    ?: p.agency?.brokerLicenseNumber
                                    ?: p.agency?.brokerLicense
                            ),
                            zoneName = regulatoryField(regulatoryInfo?.zoneName ?: p.location?.zone),
                            dldPermitNumber = regulatoryField(regulatoryInfo?.dldPermitNumber ?: p.dldPermitNumber),
                            listedTiming = formatListedTiming(p.publishedAt),
                            qrBitmap = regulatoryQrBitmap
                        )
                    }
                }

                item {
                    Column (modifier = Modifier.background(Background)){
                        AppSpacerV(25)
                        AppDivider()
                        AppSpacerV(25)
                    }
                }

                // Property Image Section (between regulatory and reporting)
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth().background(Background).padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                navController.navigate(Route.RentBuyCalculator.route)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.details_com),
                            contentDescription = "Property promotional image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                // Report Listing Section
                item {
                    ReportListingSection(onReportClick = {
                        val propertyId = propertyDetailsState.data?.data?.property?.id
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("reportType", "property")
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("reportedItemId", propertyId)
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("userType", "user")
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("reason", "fraud")
                        navController.navigate(Route.ReportProblem.route)
                    })
                }
            }

            Row(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(35.dp).clickable { navController.popBackStack() }) {
                    AppImage(
                        resId = R.drawable.ic_bg_back_arrow,
                        contentDescription = stringResource(R.string.back),
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(modifier = Modifier.size(35.dp).clickable {
                    val sLink = propertyDetails?.shareLink.takeIf { !it.isNullOrBlank() }
                        ?: "https://molumulk.tj/propertydrilldown/${propertyDetails?.id ?: ""}"
                    val shareText = "${propertyDetails?.title ?: ""}\n$sLink".trim()

                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "Share")
                        putExtra(Intent.EXTRA_TEXT, shareText) }
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

        Column {
            snackbarManager.CustomSnackbarHost(
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        // Bottom Action Bar - WhatsApp (left), Price (center), Call (right)
        Surface(modifier = Modifier.fillMaxWidth().navigationBarsPadding(), color = Color.Black, shadowElevation = 8.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                AppImage(
                    resId = R.drawable.ic_call_btn,
                    contentDescription = "call",
                    modifier = Modifier.size(50.dp).clickable{
                        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                        if (loginType == Constants.LOGIN_USER) {
                            val request = buildMap<String, Any> {
                                val propertyId = propertyDetailsState.data?.data?.property?.id
                                val agentId = propertyDetailsState.data?.data?.property?.agent?.id
                                put("propertyId", propertyId.toString())
                                put("agentId", agentId.toString())
                                put("inquiryType", "call")
                                put("message", "I'm interested in viewing this property")
                            }
                            userViewModel.createInquiry(token = TokenManager.accessToken,
                                request = request) { response ->
                                when (response) {
                                    is ApiResult.Success -> {
                                        val agencyPhoneNumber = propertyDetailsState.data?.data?.property?.agency?.phoneNumber
                                        openDialer(context,agencyPhoneNumber)
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(response.message)
                                    }
                                }
                            }
                        } else{
                            snackbarManager.showInfo("Sign up or log in to contact the agent")
                        }
                    })

                // Price in center
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AppTextRegular(text = "Price", color = TextSecondary, fontSize = 12.sp)
                    AppTextSemiBold(
                        text = priceText,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                AppImage(
                    resId = R.drawable.ic_whatsapp_btn,
                    contentDescription = "whatsapp",
                    modifier = Modifier.size(50.dp).clickable{
                        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                        if (loginType == Constants.LOGIN_USER) {
                            val request = buildMap<String, Any> {
                                val propertyId = propertyDetailsState.data?.data?.property?.id
                                val agentId = propertyDetailsState.data?.data?.property?.agent?.id
                                put("propertyId", propertyId.toString())
                                put("agentId", agentId.toString())
                                put("inquiryType", "whatsapp")
                                put("message", "I'm interested in viewing this property")
                            }
                            userViewModel.createInquiry(
                                token = TokenManager.accessToken,
                                request = request
                            ) { response ->
                                when (response) {
                                    is ApiResult.Success -> {
                                        val whatsappUrl = response.body.data?.whatsappUrl
                                        openWhatsApp(context, whatsappUrl!!)
                                        userViewModel.clearCreateInquiryState()
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(response.message)
                                    }
                                }
                            }
                        } else{
                            snackbarManager.showInfo("Sign up or log in to contact the agent")
                        }
                    },
                )

            }
        }
    }
}


@Composable
fun ReportListingSection(
    onReportClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().background(Background)
        .padding(horizontal = 20.dp, vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppTextMedium(text = "Report this listing", fontSize = 20.sp )

        AppSpacerV(10)

        AppTextRegular(
            text = "If the Property or Agent is unusual or\nyou faced some problems, please report us",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        AppSpacerV(25)

        OutlinedButton(
            modifier = Modifier
                .height(40.dp)
                .background(Color.White, RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)),
            shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
            border = BorderStroke(1.dp, MolumulkBorderDivider),
            onClick = onReportClick,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.report_flag),
                contentDescription = "Report",
                tint = TextPrimary,
                modifier = Modifier.size(16.dp)
            )
            AppSpacerH(6)
            AppTextMedium(
                text = "Report",
                fontSize = 13.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun RegulatoryInformationCard(
    reference: String,
    agencyName: String,
    brokerLicense: String,
    zoneName: String,
    dldPermitNumber: String,
    listedTiming: String,
    qrBitmap: ImageBitmap?
) {
    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {

        AppTextMedium(fontSize = 20.sp, text = "Regulatory information")

        AppSpacerV(15)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoItem("Reference", reference)
                InfoItem("Agency name", agencyName)
                InfoItem("Broker License", brokerLicense)
                InfoItem("Zone name", zoneName)
                InfoItem("DLD Permit Number", dldPermitNumber)
                InfoItem("Listed timing", listedTiming)
            }

            AppSpacerH(12)

            Column(modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .background(Color.White).padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (qrBitmap != null) {
                        Image(
                            bitmap = qrBitmap,
                            contentDescription = "QR Code — scan to verify DLD permit",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        AppTextRegular(text = "—", fontSize = 11.sp )
                    }
                }
                AppSpacerV(6)
                AppTextRegular(
                    modifier = Modifier.width(100.dp),
                    text = "Scan to check DLD permit number",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun InfoItem(
    label: String,
    value: String
) {
    Column {
        AppTextLight(text = label )

        AppSpacerV(3)

        AppTextSemiBold(text = value, fontSize = 13.sp ) }
}

@Composable
fun AmenityDisplayItem(
    icon: Int,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(20.dp),
                tint = TextPrimary
            )
        }

        // Text
        AppTextMedium(
            text = text,
            fontSize = 13.sp,
            color = TextPrimary
        )
    }
}

/**
 * Amenity row with remote icon (full URL, e.g. amenities CDN base + API `icon` filename).
 * Uses [placeholder] when [iconUrl] is null or blank.
 */
@Composable
fun AmenityDisplayItem(iconUrl: String?, text: String, placeholder: Int = R.drawable.ic_placeholder) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(5.dp))
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(5.dp), clip = false)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (iconUrl.isNullOrBlank()) {
                Icon(
                    painter = painterResource(id = placeholder),
                    contentDescription = text,
                    modifier = Modifier.size(20.dp),
                    tint = TextPrimary
                )
            } else {
                AppSubComposeAsyncImage(
                    model = iconUrl,
                    modifier = Modifier.size(20.dp),
                    placeholder = placeholder,
                    contentScale = ContentScale.Fit
                )
            }
        }

        AppTextSemiBold(text = text, fontSize = 13.sp)
    }
}

/**
 * Amenities from API: shows first 4 rows with CDN icons; "Show all" only when [amenities].size > 4.
 */
@Composable
private fun PropertyAmenitiesSection(
    amenities: List<PropertyAmenityDetail>,
    modifier: Modifier = Modifier,
    spacerBeforeShowAllButton: Dp = 26.dp,
    onShowAllClick: () -> Unit = {}
) {
    if (amenities.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        AppSpacerV(20)
        AppTextMedium(fontSize = 20.sp, text = "Amenities")
        AppSpacerV(10)
        Column {
            amenities.take(4).forEach { amenity ->
                AmenityDisplayItem(
                    iconUrl = amenity.image?.takeIf { it.isNotBlank() }
                        ?.let { AMENITY_ICON_BASE_URL + it },
                    text = amenity.name.orEmpty()
                )
            }
        }
        AppSpacerV(10)
        val remainingCount = amenities.size - 4
        PrimaryRoundButton(
            text = if (amenities.size > 4) "Show all amenities($remainingCount)" else "Show all amenities",
            onClick = onShowAllClick,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MolumulkPrimaryCoralRed,
            cornerRadius = MolumulkDimens.PrimaryCtaRadius
        )
        /*if (amenities.size > 4) {
            AppSpacerV(10)
            PrimaryRoundButton(
                text = "Show all amenities($remainingCount)",
                onClick = onShowAllClick,
                modifier = Modifier.fillMaxWidth(),
                containerColor = TabPrimary,
                cornerRadius = 10.dp
            )
        }*/
    }
}

@Composable
fun AgentSection(propertyDetail: PropertyDetail, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {
        AppTextMedium(fontSize = 20.sp, text = "Provided by")

        AppSpacerV(15)
        Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(Color.White)){
            Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp, start = 12.dp, end = 12.dp)) {
                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.LightGray),
                    contentAlignment = Alignment.Center) {
                    val agentBaseUrl = PrefUtils.getString(Constants.TAG_URL_AGENT_IMG,"")
                    SubcomposeAsyncImage(
                        model = "${agentBaseUrl+propertyDetail.agent?.profilePicture}",
                        contentDescription = null,
                        modifier = Modifier.size(59.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        error = {
                            Box(modifier = Modifier.size(59.dp).clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                AppImage(
                                    contentScale = ContentScale.Crop,
                                    resId = R.drawable.baseline_account_circle_24,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    )
                }

                AppSpacerH(20)

                Column(modifier = Modifier.weight(1f)) {
                    val agentType = propertyDetail.agent?.agentType
                    Row(modifier = Modifier.background(color = if (agentType == "agent") colorResource(R.color.darkBlue) else PrimaryColor)
                        .padding(horizontal = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AppImage(resId = if (agentType == "agent") R.drawable.ic_agent else R.drawable.ic_super_agent,
                            modifier = Modifier.size(14.dp)
                        )
                        AppTextBold(text = if (agentType == "agent") "AGENT" else "SUPERAGENT",
                            color = Color.White, fontSize = 10.sp, lineHeight = 16.sp)
                    }

                    AppSpacerV(5)
                    AppTextBold(text = propertyDetail.agent?.fullName.toString(), fontSize = 13.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppRatingBarWhite(rating = propertyDetail.agent?.ratings?.average?.toFloat()!!,
                            maxStars = 5, starSize = 12)
                        AppSpacerH(6)

                        // Rating value
                        AppTextRegular(fontSize = 12.sp, text = "${propertyDetail.agent?.ratings?.average}/${propertyDetail.agent?.ratings?.totalCount}")
                    }
                }
            }
            AppSpacerV(15)
            AppDivider()
            AppSpacerV(8)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(text = "Response time", fontSize = 13.sp)
                AppTextSemiBold(text = propertyDetail.agent?.responseTime!!, fontSize = 13.sp )
            }

            AppSpacerV(8)
            AppDivider()
            AppSpacerV(8)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(text = "Closed deals", fontSize = 13.sp)
                AppTextSemiBold(text = propertyDetail.agent?.closedDeals.toString(), fontSize = 13.sp )
            }

            AppSpacerV(8)
            AppDivider()

            AppSpacerV(8)

            // Language section
            val languagesText = propertyDetail.agent?.languages.orEmpty()
                .mapNotNull { it.name }
                .filter { it.isNotBlank() }
                .joinToString(", ")
                .ifBlank { "--" }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(text = "Language", fontSize = 13.sp)
                AppTextSemiBold(text = languagesText, fontSize = 13.sp )
            }

            AppSpacerV(20)
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp).
            border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius))) {
                PrimaryRoundButton(
                    text = "see agent properties (${propertyDetail.agent?.statistics?.totalListings})",
                    onClick = {
                        val agentId = propertyDetail.agent?.id?.toString() ?: return@PrimaryRoundButton
                        navController.navigate(Route.Agents_Properties.createRoute(agentId))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = Color.White,
                    contentColor = TextPrimary,
                    cornerRadius = MolumulkDimens.PrimaryCtaRadius
                )
            }

            AppSpacerV(20)

            Row(modifier = Modifier.fillMaxWidth().padding(5.dp)
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(Background)
                .clickable {
                    val agencyId = propertyDetail.agency?.id?.toString() ?: return@clickable
                    navController.navigate("company_detail/$agencyId")
                }
                .padding(horizontal = 18.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val developerImageBaseUrl = PrefUtils.getString(Constants.TAG_URL_AGENCY_IMG,"")
                    AppSubComposeAsyncImage(
                        model = buildImageUrl(developerImageBaseUrl.toString(), propertyDetail.agency?.profilePicture),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.width(60.dp).height(45.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )

                    // Text
                    AppTextBold(
                        text = propertyDetail.agency?.agencyName ?: "--",
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                AppImage(resId = R.drawable.ic_arrow_right,
                    modifier = Modifier.size(20.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceTrendsSection(priceInsights: PriceInsightsResponse?, navController: NavHostController) {
    val chartData = priceInsights?.trends?.chart
    val categories = chartData?.categories.orEmpty()

    val series1 = chartData?.series?.getOrNull(0)
    val series2 = chartData?.series?.getOrNull(1)

    val data = categories.mapIndexed { index, category ->
        val value = series1?.data?.getOrNull(index)?.toFloat() ?: 0f
        PricePoint(category, value)
    }.ifEmpty {
        listOf(
            PricePoint("Feb 25", 0f),
            PricePoint("Mar 25", 260f),
            PricePoint("Apr 25", 220f),
            PricePoint("May 25", 280f),
            PricePoint("Jun 25", 300f),
            PricePoint("Jul 25", 250f),
            PricePoint("Aug 25", 180f),
            PricePoint("Sep 25", 200f),
            PricePoint("Oct 25", 240f),
            PricePoint("Nov 25", 320f),
            PricePoint("Dec 25", 420f)
        )
    }

    // Secondary data for blue line
    val secondaryData = categories.mapIndexed { index, category ->
        val value = series2?.data?.getOrNull(index)?.toFloat() ?: 0f
        PricePoint(category, value)
    }.ifEmpty {
        listOf(
            PricePoint("Feb 25", 0f),
            PricePoint("Mar 25", 190f),
            PricePoint("Apr 25", 280f),
            PricePoint("May 25", 310f),
            PricePoint("Jun 25", 350f),
            PricePoint("Jul 25", 290f),
            PricePoint("Aug 25", 220f),
            PricePoint("Sep 25", 240f),
            PricePoint("Oct 25", 310f),
            PricePoint("Nov 25", 360f),
            PricePoint("Dec 25", 480f)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth().background(Background)
            .padding(horizontal = 20.dp)
    ) {

        // Title
        AppTextMedium(fontSize = 20.sp, text = "Price Trends" )

        AppSpacerV(10)

        AppTextRegular(text = priceInsights?.trends?.summaryTitle ?: "5 bedrooms villas sold in Al Bahja Hills and Al Bahja" )

        AppSpacerV(20)

        PriceLineChart(
            data = data,
            secondaryData = secondaryData,
            periodOptions = priceInsights?.trends?.periodOptions.orEmpty(),
            yAxisMax = chartData?.yAxisMax?.toFloat() ?: 600f,
            series = chartData?.series.orEmpty(),
            categories = categories,
            modifier = Modifier.fillMaxWidth().height(250.dp)
        )

        AppSpacerV(10)

        AveragePriceCard(
            price = priceInsights?.trends?.averagePriceFormatted?.takeIf { it.isNotBlank() } ?: "N/A",
            subtitle = priceInsights?.trends?.priceCompareNote.orEmpty()
        )

        AppSpacerV(20)

        PrimaryRoundButton(
            text = "Discover insights",
            onClick = {navController.navigate(Route.ExploreCommunity.route)},
            modifier = Modifier.fillMaxWidth(),
            containerColor = MolumulkPrimaryCoralRed,
            cornerRadius = MolumulkDimens.PrimaryCtaRadius
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceLineChart(
    data: List<PricePoint>,
    secondaryData: List<PricePoint> = emptyList(),
    periodOptions: List<String> = emptyList(),
    yAxisMax: Float = 600f,
    series: List< PriceInsightsChartSeries> = emptyList(),
    categories: List<String> = emptyList(),
    modifier: Modifier = Modifier
) {
    val maxValue = if (yAxisMax > 0f) yAxisMax else 600f
    val minValue = 0f

    val options = periodOptions.ifEmpty { listOf("1 year", "2 years", "3 years", "5 years") }
    var selectedOption by remember(options) { mutableStateOf(options.firstOrNull() ?: "") }

    var selectionIndex by remember(data) { mutableStateOf((data.size - 1).coerceAtLeast(0)) }
    var isTooltipVisible by remember(data) { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    Column(modifier = modifier) {
        // Header row with AED/Sqft and year dropdown
        Row(modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextBold(fontSize = 12.sp, text = "AED/Sqft" )

            // Year dropdown
            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    Row(
                        modifier = Modifier
                            .menuAnchor()
                            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                            .background(Color.White)
                            .clickable { expanded = true }
                            .padding(horizontal = 14.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AppTextSemiBold(fontSize = 12.sp, text = selectedOption)

                        AppIcon(resId = R.drawable.arrow_down,
                            contentDescription = "Dropdown",
                            modifier = Modifier.size(8.dp)
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        options.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = item, fontSize = 12.sp)
                                },
                                onClick = {
                                    selectedOption = item
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(data) {
                        // Show tooltip while finger is pressed / dragging.
                        // Hide immediately when finger lifts or is cancelled.
                        val axisPadding = 45.dp.toPx()
                        awaitPointerEventScope {
                            while (true) {
                                // Wait for the first finger down
                                val down = awaitFirstDown(requireUnconsumed = false)
                                down.consume()

                                fun offsetToIndex(x: Float): Int {
                                    val chartWidth = size.width - axisPadding - 20.dp.toPx()
                                    val spacingX = if (data.size > 1) chartWidth / (data.size - 1) else 0f
                                    if (data.isEmpty() || spacingX <= 0f) return 0
                                    val touchX = x - axisPadding
                                    return (touchX / spacingX + 0.5f).toInt().coerceIn(0, data.size - 1)
                                }

                                // Show tooltip at pressed position
                                selectionIndex = offsetToIndex(down.position.x)
                                isTooltipVisible = true

                                // Track moves while finger is held
                                var isDone = false
                                while (!isDone) {
                                    val event = awaitPointerEvent()
                                    val pointer = event.changes.firstOrNull() ?: break
                                    when {
                                        pointer.pressed -> {
                                            // Finger still down — update selected index
                                            pointer.consume()
                                            selectionIndex = offsetToIndex(pointer.position.x)
                                        }
                                        else -> {
                                            // Finger lifted — hide tooltip
                                            pointer.consume()
                                            isTooltipVisible = false
                                            isDone = true
                                        }
                                    }
                                }
                            }
                        }
                    }
            ) {
                val axisPadding = 45.dp.toPx()
                val chartWidth = size.width - axisPadding - 20.dp.toPx()
                val chartHeight = size.height - axisPadding - 40.dp.toPx()

                val spacingX = if (data.size > 1) chartWidth / (data.size - 1) else 0f
                val graphBottom = size.height - axisPadding

                // Colors from theme
                val colorRed = PrimaryColor
                val colorBlue = TabPrimary

                val points = data.mapIndexed { index, point ->
                    val x = axisPadding + spacingX * index
                    val yRatio = (point.value - minValue) / (maxValue - minValue)
                    val y = graphBottom - (yRatio * chartHeight)
                    Offset(x, y)
                }

                val secondaryPoints = if (secondaryData.isNotEmpty()) {
                    secondaryData.mapIndexed { index, point ->
                        val x = axisPadding + spacingX * index
                        val yRatio = (point.value - minValue) / (maxValue - minValue)
                        val y = graphBottom - (yRatio * chartHeight)
                        Offset(x, y)
                    }
                } else emptyList()

                // Draw Y-axis labels
                val step = (maxValue / 6).toInt()
                val yAxisValues = if (step > 0) {
                    (0..6).map { (maxValue - it * step).toInt() }
                } else {
                    listOf(600, 500, 400, 300, 200, 100, 0)
                }
                yAxisValues.forEach { value ->
                    val yRatio = (value - minValue) / (maxValue - minValue)
                    val y = graphBottom - (yRatio * chartHeight)
                    
                    val textLayoutResult = textMeasurer.measure(
                        text = value.toString(),
                        style = TextStyle(
                            color = TextSecondary.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            fontFamily = AppFontFamily
                        )
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = axisPadding - 12.dp.toPx() - textLayoutResult.size.width,
                            y = y - textLayoutResult.size.height / 2f
                        )
                    )
                }

                // Draw Axis Lines
                // Y-axis (Start line)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(axisPadding, 0f),
                    end = Offset(axisPadding, graphBottom),
                    strokeWidth = 1.dp.toPx()
                )
                // X-axis (Bottom line)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(axisPadding, graphBottom),
                    end = Offset(size.width - 20.dp.toPx(), graphBottom),
                    strokeWidth = 1.dp.toPx()
                )

                // Vertical selection line
                if (isTooltipVisible && points.size > selectionIndex) {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.3f),
                        start = Offset(points[selectionIndex].x, 0f),
                        end = Offset(points[selectionIndex].x, graphBottom),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Draw curves and gradients
                fun drawSmoothLine(pts: List<Offset>, color: Color) {
                    if (pts.size < 2) return
                    val path = Path().apply {
                        moveTo(pts.first().x, pts.first().y)
                        for (i in 0 until pts.size - 1) {
                            val p1 = pts[i]
                            val p2 = pts[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2.5f, p1.y)
                            val controlPoint2 = Offset(p2.x - (p2.x - p1.x) / 2.5f, p2.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                        }
                    }

                    val fillPath = Path().apply {
                        moveTo(pts.first().x, graphBottom)
                        lineTo(pts.first().x, pts.first().y)
                        for (i in 0 until pts.size - 1) {
                            val p1 = pts[i]
                            val p2 = pts[i + 1]
                            val controlPoint1 = Offset(p1.x + (p2.x - p1.x) / 2.5f, p1.y)
                            val controlPoint2 = Offset(p2.x - (p2.x - p1.x) / 2.5f, p2.y)
                            cubicTo(controlPoint1.x, controlPoint1.y, controlPoint2.x, controlPoint2.y, p2.x, p2.y)
                        }
                        lineTo(pts.last().x, graphBottom)
                        lineTo(pts.last().x, graphBottom)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(color.copy(alpha = 0.4f), Color.Transparent),
                            startY = pts.minOf { it.y },
                            endY = graphBottom
                        )
                    )
                    drawPath(
                        path = path,
                        color = color,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                drawSmoothLine(secondaryPoints, colorBlue)
                drawSmoothLine(points, colorRed)

                // Selection dots
                if (isTooltipVisible && points.size > selectionIndex) {
                    // Red dot
                    drawCircle(color = colorRed, radius = 5.dp.toPx(), center = points[selectionIndex])
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = points[selectionIndex])
                    
                    if (secondaryPoints.size > selectionIndex) {
                        // Blue dot
                        drawCircle(color = colorBlue, radius = 5.dp.toPx(), center = secondaryPoints[selectionIndex])
                        drawCircle(color = Color.White, radius = 3.dp.toPx(), center = secondaryPoints[selectionIndex])
                    }
                }

                // Draw X-axis labels (centered on points)
                data.forEachIndexed { index, point ->
                    if (index % 2 == 0) {
                        val x = axisPadding + spacingX * index
                        val textLayoutResult = textMeasurer.measure(
                            text = point.month,
                            style = TextStyle(
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = AppFontFamily
                            )
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                x = x - textLayoutResult.size.width / 2f,
                                y = graphBottom + 12.dp.toPx()
                            )
                        )
                    }
                }
            }


            // Tooltip
            if (isTooltipVisible && data.isNotEmpty() && selectionIndex in data.indices) {
                val tooltipTitle = categories.getOrNull(selectionIndex) ?: data.getOrNull(selectionIndex)?.month.orEmpty()
                val s1 = series.getOrNull(0)
                val s2 = series.getOrNull(1)

                val val1 = s1?.data?.getOrNull(selectionIndex)?.toInt() ?: 0
                val val2 = s2?.data?.getOrNull(selectionIndex)?.toInt() ?: 0

                val s1Name = s1?.name.orEmpty()
                val val1Text = if (s1Name.contains("@")) {
                    "${s1Name.substringBefore("@").trim()} @ $val1 AED/Sqft"
                } else {
                    "$s1Name @ $val1 AED/Sqft"
                }

                val s2Name = s2?.name.orEmpty()
                val val2Text = if (s2Name.contains("@")) {
                    "${s2Name.substringBefore("@").trim()} @ $val2 AED/Sqft"
                } else {
                    "$s2Name @ $val2 AED/Sqft"
                }

                PriceTooltip(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(x = 40.dp, y = (-20).dp),
                    title = tooltipTitle,
                    value1 = val1Text,
                    value2 = val2Text,
                    color1 = PrimaryColor,
                    color2 = TabPrimary
                )
            }
        }
    }
}

@Composable
fun PropertyLocationSection(
    lat: Double,
    lng: Double,
    navController: NavHostController,
    propertyDetail: PropertyDetail,
    priceRange: String? = null
) {
    val propertyImageBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROPERTY_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/" }
    val primaryPropertyImage = propertyDetail.images
        .sortedBy { it.order ?: Int.MAX_VALUE }
        .firstOrNull { it.isPrimary == true }
        ?.url
        ?: propertyDetail.images.firstOrNull()?.url
    val locationCardImageUrl = primaryPropertyImage?.let { propertyImageBaseUrl + it }

    Column(modifier = Modifier.fillMaxWidth().background(Background).padding(horizontal = 20.dp)) {
        // Title
        AppTextMedium(fontSize = 20.sp, text = "Property Location")

        AppSpacerV(15)

        Column(modifier = Modifier.clip(RoundedCornerShape(10.dp))
            .background(Color.White).padding(5.dp)
        ) {
           /* Box(modifier = Modifier.fillMaxWidth()
                .height(200.dp).clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
                .clickable { navController.navigateToProjectLocationMap(lat, lng) }) {
                ProjectLocationMapView(lat = lat, lng = lng, modifier = Modifier.fillMaxSize())
            }*/
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        val sLink = propertyDetail.shareLink.takeIf { !it.isNullOrBlank() }
                            ?: "https://molumulk.tj/propertydrilldown/${propertyDetail.id ?: ""}"
                        navController.navigateToProjectLocationMap(lat, lng, sLink, propertyDetail.agent?.phoneNumber)
                    }
            ) {
                StaticMapPreview(
                    lat = lat,
                    lng = lng,
                    apiKey = stringResource(R.string.google_map_key),
                    modifier = Modifier.fillMaxSize()
                )
            }
            AppSpacerV(10)
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                SubcomposeAsyncImage(
                    model = locationCardImageUrl,
                    modifier = Modifier.size(75.dp).clip(RoundedCornerShape(10.dp)),
                    contentDescription = "image description",
                    contentScale = ContentScale.Crop,
                    loading = {
                        AppImage(
                            resId = R.drawable.ic_placeholder,
                            modifier = Modifier.size(75.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                    },
                    error = {
                        AppImage(
                            resId = R.drawable.ic_placeholder,
                            modifier = Modifier.size(75.dp),
                            contentDescription = "image description",
                            contentScale = ContentScale.FillBounds
                        )
                    }
                )
                AppSpacerH(10)
                Column(modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    AppTextBold(text = propertyDetail.title!!, fontSize = 13.sp, lineHeight = 16.sp)
                    AppSpacerV(2)
                    AppTextLight(text = propertyDetail.location?.fullAddress!!, lineHeight = 16.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        AppRatingBar(rating = 0f, maxStars = 5, starSize = 12)
                        AppTextRegular(text = "0/5", fontSize = 12.sp )
                    }
                }

                AppImage(
                    resId  = R.drawable.ic_arrow_right,
                    modifier = Modifier.size(28.dp),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }
            if (priceRange != null) {
                AppSpacerV(10)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Background)
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AppImage(
                        resId  = R.drawable.ic_coins,
                        modifier = Modifier.width(15.dp).height(13.dp),
                        contentDescription = "",
                        contentScale = ContentScale.FillBounds
                    )
                    AppSpacerH(8)
                    AppTextMedium(fontSize = 12.sp, text = "Price range : ")
                    AppTextBold(fontSize = 12.sp, text = priceRange)
                }
            }
        }
    }
}

@Composable
fun StaticMapPreview(
    lat: Double,
    lng: Double,
    apiKey: String,
    modifier: Modifier = Modifier
) {
    val staticMapUrl = remember(lat, lng, apiKey) {
        "https://maps.googleapis.com/maps/api/staticmap" +
                "?center=$lat,$lng" +
                "&zoom=15" +
                "&size=1200x600" +
                "&scale=2" +
                "&maptype=roadmap" +
                "&key=$apiKey"
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(staticMapUrl)
                .crossfade(false) // lighter
                .memoryCacheKey("map_$lat$lng")
                .diskCacheKey("map_$lat$lng")
                .build(),
            contentDescription = "Project location map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Native beautiful map pin rendered perfectly at the center!
        // Offset y is set to half the icon height so the bottom tip of the pin aligns precisely to the center coordinate
        AppImage(
            resId = R.drawable.ic_map_marker,
            modifier = Modifier
                .size(42.dp)
                .offset(y = (-21).dp),
            contentDescription = "Map Marker"
        )
    }
}

@Composable
fun AveragePriceCard(price: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
            .background(Color.Black).padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppSpacerV(15)
        AppTextBold(
            modifier = Modifier.padding(horizontal = 15.dp),
            text = "Average sale price is",
            color = Color.White,
            fontSize = 15.sp
        )

        AppSpacerV(10)
        AppTextMedium(
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 15.dp),
            text = price,
            color = MolumulkPrimaryCoralRed,
            fontSize = 20.sp
        )

        AppSpacerV(20)

        Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 8.dp)) {
            AppTextRegular(
                textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(),
                fontSize = 12.sp, text = subtitle, color = Color.White, lineHeight = 14.sp)
        }
    }
}

@Composable
fun PriceTooltip(
    modifier: Modifier = Modifier,
    title: String,
    value1: String,
    value2: String,
    color1: Color,
    color2: Color
) {
    Column(
        modifier = modifier
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(16.dp)
            .widthIn(min = 180.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppTextSemiBold(
            text = title,
            fontSize = 14.sp,
            color = TextPrimary
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(color1, CircleShape))
            AppSpacerH(8)
            AppTextRegular(text = value1, fontSize = 12.sp, color = TextPrimary)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).background(color2, CircleShape))
            AppSpacerH(8)
            AppTextRegular(text = value2, fontSize = 12.sp, color = TextPrimary)
        }
    }
}

@Composable
fun AmenityItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(TextPrimary))
        AppTextRegular(text = text)
    }
}

@Composable
fun TransactionItem(
    propertyName: String,
    location: String,
    price: String,
    details: String,
    date: String,
    status: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Color.White).padding(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column (modifier = Modifier.padding(top = 15.dp, start = 15.dp)){
                AppTextBold(text = propertyName, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AppImage(
                        resId = R.drawable.location, // Location icon
                        contentDescription = "Location",
                        modifier = Modifier.width(10.dp).height(15.dp),
                        tint = TextSecondary
                    )
                    AppTextLight(text = location)
                }
                AppSpacerV(5)
                AppTextMedium(text = "Price: "+price, fontSize = 12.sp)
            }
        }

        AppSpacerV(20)

        // Property details with grey background
        Row(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp))
            .background(Background)
            .padding(horizontal = 15.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextRegular(text = details, fontSize = 10.sp, modifier = Modifier.weight(1f))
            AppTextLight(text = date, fontSize = 10.sp)
        }
    }
}


private data class AroundPlaceRow(
    val categoryLabel: String,
    val placeName: String,
    val distanceText: String?
)

private fun NearbyPlaces?.flattenAroundPlaces(): List<AroundPlaceRow> {
    if (this == null) return emptyList()
    val out = mutableListOf<AroundPlaceRow>()
    val sections = listOf(
        "Restaurant" to restaurants,
        "School" to schools,
        "Hospital" to hospitals,
        "Transport" to transport,
        "Shopping mall" to shoppingMalls,
        "Hotel" to hotels
    )
    sections.forEach { (label, list) ->
        list.forEach { item ->
            val title = item.name?.takeIf { it.isNotBlank() }
                ?: item.vicinity?.takeIf { it.isNotBlank() }
                ?: ""
            if (title.isNotBlank()) {
                out.add(
                    AroundPlaceRow(
                        categoryLabel = label,
                        placeName = title,
                        distanceText = item.distance?.takeIf { it.isNotBlank() }
                    )
                )
            }
        }
    }
    return out
}

@Composable
fun AroundPropertyItem(
    categoryLabel: String = "School",
    placeName: String = "The new town school",
    distanceText: String? = "2.5 km"
) {
    Column(modifier = Modifier.width(270.dp).clip(RoundedCornerShape(10.dp))
        .background(Color.White).clickable { }.padding(15.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier.size(15.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id =
                        if (categoryLabel == "Restaurant") R.drawable.ic_restaurant
                        else if (categoryLabel == "School") R.drawable.ic_school
                        else if (categoryLabel == "Hospital") R.drawable.ic_hospital
                        else if (categoryLabel == "Transport") R.drawable.ic_transport
                        else if (categoryLabel == "Shopping mall") R.drawable.ic_shopping_mall
                        else if (categoryLabel == "Hotel") R.drawable.ic_hotel
                        else R.drawable.welcome
                    ),
                    contentDescription = "Place",
                    modifier = Modifier.fillMaxSize(),
                    tint = MolumulkPrimaryCoralRed
                )
            }

            AppTextBold(fontSize = 12.sp, color = TextSecondary, text = categoryLabel)
        }
        AppSpacerV(5)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AppTextBold(text = placeName, fontSize = 13.sp,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)

            if (!distanceText.isNullOrBlank()) {
                Spacer(modifier = Modifier.width(8.dp))
                AppTextRegular(text = distanceText, fontSize = 12.sp)
            }
        }
    }
}
fun openWhatsApp(context: Context, whatsappUrl: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun openDialer(context: Context, phoneNumber: String?) {
    if (phoneNumber.isNullOrBlank()) return

    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}

