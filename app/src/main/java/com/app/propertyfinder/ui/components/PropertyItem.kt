package com.app.propertyfinder.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.AgencyPropertyItem
import com.app.propertyfinder.api.dto.response.PropertyListItem
import com.app.propertyfinder.screen.company.PROPERTY_IMAGE_BASE_URL
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.MolumulkSuccessGreen
import com.app.propertyfinder.ui.theme.MolumulkWhatsAppGreen
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.Locale.getDefault


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomePropertyItem(
    propertyModel: PropertyListItem,
    from: String,
    onClick: (String) -> Unit,
    onSaveClick: (String) -> Unit = {},
    isSaved: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                propertyModel.id?.let { clickedId ->
                    onClick(clickedId)
                }
            },
        shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
        border = androidx.compose.foundation.BorderStroke(1.dp, MolumulkBorderDivider),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier) {
            val propertyImageBaseUrl = PrefUtils.getString(
                Constants.TAG_URL_PROPERTY_IMG,
                "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/"
            ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/" }
            val projectImageBaseUrl = PrefUtils.getString(
                Constants.TAG_URL_PROJECT_IMG,
                "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/"
            ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/" }
            val developerImageBaseUrl = PrefUtils.getString(
                Constants.TAG_URL_DEVELOPER_IMG,
                "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/"
            ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/" }
            val primaryOrFirstImage = propertyModel.images
                .firstOrNull { it.isPrimary == true }
                ?.url
                ?: propertyModel.images.firstOrNull()?.url

            val imageUrl = if (from == "home") {
                primaryOrFirstImage?.let { propertyImageBaseUrl + it }.orEmpty()
            } else {
                primaryOrFirstImage?.let { projectImageBaseUrl + it }.orEmpty()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(3.dp)
                    .height(210.dp)
                    .clip(RoundedCornerShape(topEnd = MolumulkDimens.CardCornerRadius, topStart = MolumulkDimens.CardCornerRadius))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                )
                if (from == "home"){
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .width(70.dp)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSubComposeAsyncImage(
                            model = buildImageUrl(developerImageBaseUrl, propertyModel.agency?.profilePicture),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 3.dp, start = 3.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopStart)
                            .width(70.dp)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppSubComposeAsyncImage(
                            model = buildImageUrl(developerImageBaseUrl, propertyModel.developer?.logo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(top = 3.dp, start = 3.dp)
                                .clip(RoundedCornerShape(8.dp)),
                        )
                    }
                }

                if (from == "home") {
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = if (isSaved) MolumulkPrimaryCoralRed else Color.Black.copy(alpha = 0.5f)
                            )
                            .clickable {
                                propertyModel.id?.let { onSaveClick(it) }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(
                            resId = if (isSaved) R.drawable.heart_filled else R.drawable.heart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                // Tags bottom right
                Column(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 5.dp, bottom = 5.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (from == "home") {
                        if (propertyModel.isVerified == true) {
                            ColorTagBoxWithImage(
                                text = "VERIFIED",
                                bgColor = MolumulkSuccessGreen,
                                textColor = Color.White,
                                icon = R.drawable.verified_user
                            )
                            AppSpacerV(5)
                        }
                        val agentType = propertyModel.agent?.agentType.orEmpty()
                        if (agentType.isNotBlank() && agentType == "superagent") {
                            ColorTagBoxWithImage(
                                text = "SUPERAGENT",
                                bgColor = MolumulkPrimaryCoralRed,
                                textColor = Color.White,
                                icon = R.drawable.ic_super_agent
                            )
                            AppSpacerV(5)
                        }
                        if (propertyModel.propertyType?.name?.isNotEmpty() == true) {
                            ColorTagBox(
                                text = propertyModel.propertyType.name,
                                bgColor = Color.White.copy(alpha = 0.9f),
                                textColor = MolumulkCharcoal
                            )
                        }
                    } else {
                        val completionStatus = propertyModel.completionStatus.orEmpty()
                        val deliveryQuarter = propertyModel.deliveryQuarter.orEmpty()
                        val saleStarted = propertyModel.saleStarted.orEmpty()
                        val paymentPlansCount = propertyModel.paymentPlansCount ?: 0

                        if (completionStatus.isNotEmpty()){
                            ColorTagBox(text = completionStatus, Color.White, MolumulkCharcoal)
                            AppSpacerV(5)
                        }
                        if (deliveryQuarter.isNotEmpty()){
                            ColorTagBox(text = "DELIVERY DATE : $deliveryQuarter", Color.White, MolumulkCharcoal)
                            AppSpacerV(5)
                        }
                        if (saleStarted.isNotEmpty()){
                            val sale = "SALE STARTED : ${formatIsoDate(saleStarted)}"
                            ColorTagBox(text = sale, MolumulkPrimaryCoralRed, Color.White)
                            AppSpacerV(5)
                        }
                        if (paymentPlansCount > 0){
                            ColorTagBox(text = "$paymentPlansCount PAYMENT PLAN AVAILABLE",
                                MolumulkWhatsAppGreen, Color.White)
                        }
                    }
                }
                // Text bottom left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (from == "home") {
                        AppTextBold(
                            text = getTimeAgo(propertyModel.publishedAt),
                            color = Color.White,
                            fontSize = 10.sp
                        )
                    }
                }

            }
            AppSpacerV(8)

            AppTextBold(
                text = propertyModel.title.toString(),
                fontSize = 15.sp,
                color = MolumulkCharcoal,
                modifier = Modifier.padding(horizontal = 15.dp),
                lineHeight = 20.sp
            )
            AppSpacerV(4)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 15.dp)) {
                AppIcon(resId = R.drawable.location, tint = MolumulkMutedSlate)
                AppSpacerH(4)
                AppTextLight(
                    text = "${propertyModel.location?.city}, ${propertyModel.location?.zone}",
                    color = MolumulkMutedSlate
                )
            }
            AppDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                thickness = 1.dp,
                color = MolumulkBorderDivider
            )

            // 🔹 Bottom Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppTextLight(text = stringResource(R.string.launch_price), color = MolumulkMutedSlate)
                    if (from == "home") {
                        val yearlyRent = propertyModel.rentPricing?.yearly
                        val homePriceText = if (yearlyRent != null) {
                            "$yearlyRent ${propertyModel.currency.orEmpty()} / ${stringResource(R.string.yearly)}"
                        } else {
                            "${propertyModel.price} ${propertyModel.currency}"
                        }
                        AppTextBold(text = homePriceText.trim(), fontSize = 16.sp, color = MolumulkCharcoal)
                    } else if (from == "new_project") {
                        val launchPrice = propertyModel.price
                        val priceText = if (launchPrice != null && launchPrice > 0) {
                            "$launchPrice ${propertyModel.currency ?: "AED"}"
                        } else {
                            "Coming soon"
                        }
                        AppTextBold(text = priceText, fontSize = 16.sp, color = MolumulkCharcoal)
                    }
                }
                if (from == "new_project") {
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            val rawPhone = propertyModel.developer?.phoneNumber.orEmpty().trim()

                            when {
                                // 1. Phone number is missing
                                rawPhone.isEmpty() -> {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Developer phone number is not available",
                                        android.widget.Toast.LENGTH_LONG
                                    ).show()
                                }

                                else -> {
                                    // Normalize: strip leading '+' for the wa.me URL
                                    val normalizedPhone = rawPhone.removePrefix("+")

                                    // wa.me deep-link opens a direct chat without needing
                                    // the number to be saved on the device
                                    val waUri = Uri.parse("https://wa.me/$normalizedPhone")
                                    val waIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                        setPackage("com.whatsapp")
                                    }
                                    val waBusinessIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                        setPackage("com.whatsapp.w4b")
                                    }

                                    try {
                                        // Try WhatsApp first
                                        context.startActivity(waIntent)
                                    } catch (e1: Exception) {
                                        try {
                                            // Try WhatsApp Business
                                            context.startActivity(waBusinessIntent)
                                        } catch (e2: Exception) {
                                            // WhatsApp is not installed at all
                                            android.widget.Toast.makeText(
                                                context,
                                                "WhatsApp is not installed on this device",
                                                android.widget.Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MolumulkWhatsAppGreen
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(resId = R.drawable.whatapp, modifier = Modifier.size(19.dp))
                            AppSpacerH(5)
                            AppTextSemiBold(fontSize = 12.sp, text = "Whatsapp", color = Color.White)
                        }
                    }
                }
            }
            AppSpacerV(10)
            if (from == "home"){
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
                    BotAmenityItem(
                        icon = R.drawable.bad,
                        text = "${propertyModel.bedrooms} Bed",
                        modifier = Modifier.weight(1f)
                    )
                    AppSpacerH(4)
                    BotAmenityItem(
                        icon = R.drawable.bath,
                        text = "${propertyModel.bathrooms} Bath",
                        modifier = Modifier.weight(1f)
                    )
                    AppSpacerH(4)
                    BotAmenityItem(
                        icon = R.drawable.sqft, text = "${propertyModel.area?.sqft} sqft",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompanyPropertyItem(
    property: AgencyPropertyItem,
    onClick: () -> Unit,
    onSaveClick: (String) -> Unit = {},
    isSaved: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(modifier = Modifier) {
            val primaryImagePath = property.images.firstOrNull { it.isPrimary == true }?.url
                ?: property.images.firstOrNull()?.url
            val imageUrl = buildImageUrl(PROPERTY_IMAGE_BASE_URL, primaryImagePath)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 3.dp, start = 3.dp, top = 3.dp)
                    .height(210.dp)
                    .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                )
                AppImage(
                    resId = R.drawable.damac_tag,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 3.dp, start = 3.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (isSaved) MolumulkPrimaryCoralRed else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                        .clickable {
                            property.id?.let { onSaveClick(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AppImage(
                        resId = if (isSaved) R.drawable.heart_filled else R.drawable.heart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                // Text bottom left
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    AppTextLight(text = getTimeAgo(property.publishedAt), color = Color.White)
                }

            }
            AppSpacerV(8)

            AppTextMedium(text = property.title.toString(), modifier = Modifier.padding(horizontal = 15.dp))
            AppSpacerV(4)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 15.dp)) {
                AppIcon(resId = R.drawable.location)
                AppSpacerH(4)
                AppTextLight(text = "${property.location?.fullAddress}")
            }
            AppDivider(modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp, color = colorResource(R.color.screen_bg_color))

            // 🔹 Bottom Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppTextLight(text = stringResource(R.string.launch_price),)
                    AppSpacerV(5)
                    AppTextBold(text = "${property.price} ${property.currency}")
                }

            }
            AppSpacerV(10)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
                BotAmenityItem(
                    icon = R.drawable.bad,
                    text = "${property.bedrooms} Bed",
                    modifier = Modifier.weight(1f)
                )
                AppSpacerH(4)
                BotAmenityItem(
                    icon = R.drawable.bath,
                    text = "${property.bathrooms} Bath",
                    modifier = Modifier.weight(1f)
                )
                AppSpacerH(4)
                BotAmenityItem(
                    icon = R.drawable.sqft, text = "${property.area?.sqft} sqft",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompanyPropertyItem(
    property: PropertyListItem,
    onClick: () -> Unit,
    onSaveClick: (String) -> Unit = {},
    isSaved: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(modifier = Modifier) {
            val primaryImagePath = property.images.firstOrNull { it.isPrimary == true }?.url
                ?: property.images.firstOrNull()?.url
            val imageUrl = buildImageUrl(PROPERTY_IMAGE_BASE_URL, primaryImagePath)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 3.dp, start = 3.dp, top = 3.dp)
                    .height(210.dp)
                    .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
            ) {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                )
                AppImage(
                    resId = R.drawable.damac_tag,
                    contentDescription = null,
                    modifier = Modifier.padding(top = 3.dp, start = 3.dp),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(top = 8.dp, end = 8.dp)
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            color = if (isSaved) MolumulkPrimaryCoralRed else Color.Black.copy(
                                alpha = 0.5f
                            )
                        )
                        .clickable {
                            property.id?.let { onSaveClick(it) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    AppImage(
                        resId = if (isSaved) R.drawable.heart_filled else R.drawable.heart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    AppTextLight(text = getTimeAgo(property.publishedAt), color = Color.White)
                }
            }
            AppSpacerV(8)

            AppTextMedium(
                text = property.title.orEmpty(),
                modifier = Modifier.padding(horizontal = 15.dp)
            )
            AppSpacerV(4)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 15.dp)
            ) {
                AppIcon(resId = R.drawable.location)
                AppSpacerH(4)
                AppTextLight(text = property.location?.fullAddress.orEmpty())
            }
            AppDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 1.dp,
                color = colorResource(R.color.screen_bg_color)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    AppTextLight(text = stringResource(R.string.launch_price))
                    AppSpacerV(5)
                    AppTextBold(text = "${property.price} ${property.currency}")
                }
            }
            AppSpacerV(10)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
            ) {
                BotAmenityItem(
                    icon = R.drawable.bad,
                    text = "${property.bedrooms} Bed",
                    modifier = Modifier.weight(1f)
                )
                AppSpacerH(4)
                BotAmenityItem(
                    icon = R.drawable.bath,
                    text = "${property.bathrooms} Bath",
                    modifier = Modifier.weight(1f)
                )
                AppSpacerH(4)
                BotAmenityItem(
                    icon = R.drawable.sqft,
                    text = "${property.area?.sqft} sqft",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ColorTagBox(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        AppTextBold(text = text.uppercase(), fontSize = 10.sp, color = textColor, lineHeight = 16.sp)
    }
}
@Composable
fun ColorTagBoxWithImage(text: String, bgColor: Color, textColor: Color, icon: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppImage(resId = icon, modifier = Modifier.size(12.dp), tint = textColor)
            AppSpacerH(4)
            AppTextBold(text.uppercase(), fontSize = 10.sp, color = textColor, lineHeight = 16.sp)
        }
    }
}


@Composable
fun BotAmenityItem(icon: Int, text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .background(
                MolumulkOffWhite, RoundedCornerShape(8.dp)
            )
            .border(1.dp, MolumulkBorderDivider, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        AppIcon(resId = icon, tint = MolumulkCharcoal)
        AppSpacerH(4)

        AppTextMedium(fontSize = 12.sp, text = text, color = MolumulkCharcoal)
    }
}



@RequiresApi(Build.VERSION_CODES.O)
fun getTimeAgo(apiDate: String?): String {
    val raw = apiDate?.trim().orEmpty()
    if (raw.isEmpty()) return ""
    return try {
        val past = Instant.parse(raw)
        val now = Instant.now()
        if (past.isAfter(now)) {
            return "Recently listed"
        }
        val duration = Duration.between(past, now)

        val seconds = duration.seconds
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        when {
            seconds < 60 -> "just now"
            minutes < 60 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
            hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
            days < 7 -> if (days == 1L) "1 day ago" else "$days days ago"
            days < 30 -> {
                val weeks = days / 7
                if (weeks == 1L) "1 week ago" else "$weeks weeks ago"
            }
            days < 365 -> {
                val months = days / 30
                if (months == 1L) "1 month ago" else "$months months ago"
            }
            else -> {
                val years = days / 365
                if (years == 1L) "1 year ago" else "$years years ago"
            }
        }
    } catch (e: Exception) {
        "Invalid date"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatIsoDate(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return ""

    return try {
        val instant = Instant.parse(isoDate)
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
            .withZone(ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: Exception) {
        ""
    }
}