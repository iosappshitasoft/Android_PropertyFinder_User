package com.app.propertyfinder.screen

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.api.dto.response.PropertyListItem
import com.app.propertyfinder.api.dto.response.sortByProperty
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.getTimeAgo
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.google.android.gms.maps.GoogleMap

// ---------->>> Change Map type Bottom sheet
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapTypeBottomSheet(selectedType: Int, onDismiss: () -> Unit, onSelected: (Int) -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {

        Column(modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)) {

            AppTextSemiBold(text = stringResource(R.string.choose_map),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center)
            AppSpacerV(15)

            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {

                MapOptionItem(title = "Default",
                    imageRes = R.drawable.ic_default_map_type,
                    isSelected = selectedType == GoogleMap.MAP_TYPE_NORMAL
                ) {
                    onSelected(GoogleMap.MAP_TYPE_NORMAL)
                }

                MapOptionItem(
                    title = "Satellite",
                    imageRes = R.drawable.ic_satelite_map_type,
                    isSelected = selectedType == GoogleMap.MAP_TYPE_SATELLITE
                ) {
                    onSelected(GoogleMap.MAP_TYPE_SATELLITE)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
@Composable
fun MapOptionItem(title: String, imageRes: Int, isSelected: Boolean, onClick: () -> Unit) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {

        Box {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        color = if (isSelected)
                            colorResource(R.color.splash_primary)
                        else
                            Color.LightGray,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(22.dp)
                        .background(
                            colorResource(R.color.splash_primary),
                            CircleShape
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
            }
        }

        AppSpacerV(8)

        AppTextMedium(text = title)
    }
}
// Change Map type Bottom sheet <<<----------

// ---------->>> New Project marker bottom sheet
@Preview(
    showBackground = true,
    backgroundColor = 0xFFEDEDED,
    showSystemUi = true
)
@Composable
fun PreviewNewProjectBottomSheet() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.LightGray),
        contentAlignment = Alignment.BottomCenter
    ) {
        NewProjectBottomSheet(
            selectedItem = null,
            onPropertyClick = {},
            onDismiss = {}
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewProjectBottomSheet(
    selectedItem: PropertyListItem?,
    onPropertyClick : () -> Unit,
    onDismiss: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val imageUrl = selectedItem?.images?.firstOrNull { it.isPrimary == true }?.url
        ?: selectedItem?.images?.firstOrNull()?.url
    val imageModel = imageUrl?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/$it" }
    val logoUrl = selectedItem?.developer?.logo
        ?.takeIf { it.isNotBlank() }
        ?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/developer/$it" }
    val titleText = selectedItem?.title.orEmpty().ifBlank { "Rosehill by Emaar" }
    val locationText = selectedItem?.location?.fullAddress.orEmpty()
        .ifBlank { "Dubai, Dubai Hills Estate, Rosehill" }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White, sheetState = sheetState) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            )
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(3.dp).height(210.dp)
                        .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
                        .clickable { onPropertyClick() }
                ) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_placeholder),
                        error = painterResource(R.drawable.ic_placeholder)
                    )
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 3.dp, start = 3.dp)
                            .size(width = 56.dp, height = 36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_placeholder),
                        error = painterResource(R.drawable.ic_placeholder)
                    )

                    // Tags bottom right
                    Column(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 5.dp, bottom = 5.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        TagBox(text = "off-plan", Color.White, Color.Black)
                        AppSpacerV(5)
                        TagBox(text = "Delivery date : Q1 2029", Color.White, Color.Black)
                    }
                }

                AppTextMedium(
                    text = titleText,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .clickable { onPropertyClick() }
                )
                AppSpacerV(4)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .clickable { onPropertyClick() }
                ) {
                    AppIcon(resId = R.drawable.location)
                    AppSpacerH(4)
                    AppTextLight(text = locationText)
                }
                AppDivider(modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp, color = colorResource(R.color.screen_bg_color))
                // 🔹 Bottom Section
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        AppTextLight(text = stringResource(R.string.launch_price),)
                        AppTextMedium(text = stringResource(R.string.coming_soon) )
                    }

                    Button(
                        onClick = {
                            val message = Uri.encode("Hello, I am interested in this project.")
                            context.startActivity(
                                Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/?text=$message"))
                            )
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.whatsapp_green)
                        )
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically){
                            AppIcon(resId = R.drawable.whatapp, modifier = Modifier.size(20.dp))
                            AppSpacerH(5)
                            AppTextMedium(text = "Whatsapp", color = Color.White)
                        }
                    }
                }
                AppSpacerV(10)
            }
        }
    }
}

// New Project marker bottom sheet <<<----------

// ---------->>> Rent marker bottom sheet

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentBottomSheet(
    selectedItem: PropertyListItem?,
    sortOptions: List<sortByProperty>,
    selectedSortValue: String?,
    onSortSelected: (String?) -> Unit,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onDismiss: () -> Unit,
    onPropertyClick: () -> Unit,
    onResidenceClick: ()-> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White, sheetState = sheetState) {
        RentBottomSheetContent(
            selectedItem = selectedItem,
            sortOptions = sortOptions,
            selectedSortValue = selectedSortValue,
            onSortSelected = onSortSelected,
            isSaved = isSaved,
            onSaveClick = onSaveClick,
            onPropertyClick = onPropertyClick,
            onResidenceClick = onResidenceClick
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RentBottomSheetContent(
    selectedItem: PropertyListItem?,
    sortOptions: List<sortByProperty>,
    selectedSortValue: String?,
    onSortSelected: (String?) -> Unit,
    isSaved: Boolean,
    onSaveClick: () -> Unit,
    onPropertyClick: () -> Unit,
    onResidenceClick: ()-> Unit
) {
    var expandedDropDown by remember { mutableStateOf(false) }
    val selectedDropDown = sortOptions.firstOrNull { it.value == selectedSortValue }?.name
        ?: sortOptions.firstOrNull()?.name
        ?: "Sort"
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp)
                .height(210.dp).clickable{onResidenceClick()}
                .clip(RoundedCornerShape(10.dp))
        ) {
            AppImage(
                resId = R.drawable.residence_bg,
                contentDescription = null,
                modifier = Modifier.height(210.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp,
                vertical = 5.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 5.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween){
                Column {
                    AppTextMedium(text = "Nasma Residence", color = Color.White)
                    AppTextLight(text = "insights", color = Color.White)
                }
                AppImage(resId = R.drawable.ic_round_white_right)
            }
            Row (modifier = Modifier.fillMaxWidth().height(45.dp)
                .align(Alignment.BottomCenter)
                .padding(horizontal = 3.dp, vertical = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF2046B6)),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center){
                AppImage(resId = R.drawable.ic_coins, tint = Color.White)
                AppSpacerH(10)
                AppDivider(color = Color.White, modifier = Modifier.fillMaxHeight().width(0.8.dp))
                AppSpacerH(10)
                AppTextLight(text = "Price range : ", color = Color.White)
                AppTextMedium(text = "80k - 1.5M AWD/year", color = Color.White)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppTextSemiBold(text = "167,456 properties")
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .clickable { expandedDropDown = true }
                        .border(width = 1.dp, color = colorResource(R.color.screen_bg_color),
                            shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextLight(text = selectedDropDown, color = colorResource(R.color.textprimary))
                    AppSpacerH(4)

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                // 🔹 Dropdown Menu
                DropdownMenu(
                    expanded = expandedDropDown,
                    onDismissRequest = { expandedDropDown = false },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(end = 30.dp),
                    containerColor = Color.White
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                AppTextMedium(text = option.name.orEmpty())
                            },
                            onClick = {
                                onSortSelected(option.value)
                                expandedDropDown = false
                            }
                        )
                    }
                }
            }
        }

        val titleText = selectedItem?.title.orEmpty().ifBlank { "Rosehill by Emaar" }
        val locationText = selectedItem?.location?.fullAddress.orEmpty()
            .ifBlank { "Dubai, Dubai Hills Estate, Rosehill" }
        val priceText = buildString {
            val price = selectedItem?.price
            val currency = selectedItem?.currency.orEmpty()
            if (price != null) append("${price} $currency")
        }.ifBlank { "9,000,000 AED/year" }
        val imageUrl = selectedItem?.images?.firstOrNull { it.isPrimary == true }?.url
            ?: selectedItem?.images?.firstOrNull()?.url
        val imageModel = imageUrl?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/$it" }
        val logoUrl = selectedItem?.agency?.profilePicture
            ?.takeIf { it.isNotBlank() }
            ?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/agency/$it" }
        val listedText = selectedItem?.publishedAt?.let { "Listed ${getTimeAgo(it)}" }
            ?: "Listed recently"

        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 5.dp
            ),
            onClick = { onPropertyClick() }

        ) {
            Column(modifier = Modifier) {
                Box(modifier = Modifier.fillMaxWidth().padding(3.dp).height(210.dp).clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(210.dp),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_placeholder),
                        error = painterResource(R.drawable.ic_placeholder)
                    )
                    AsyncImage(
                        model = logoUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 3.dp, start = 3.dp)
                            .size(width = 56.dp, height = 36.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.ic_placeholder),
                        error = painterResource(R.drawable.ic_placeholder)
                    )
                    Box(
                        modifier = Modifier
                            .align(alignment = Alignment.TopEnd)
                            .padding(top = 8.dp, end = 8.dp)
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = if (isSaved) MolumulkPrimaryCoralRed else Color.Black.copy(alpha = 0.5f)
                            )
                            .clickable { onSaveClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(
                            resId = if (isSaved) R.drawable.heart_filled else R.drawable.heart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Tags bottom right
                    Column(
                        modifier = Modifier.align(Alignment.BottomEnd).padding(end = 5.dp, bottom = 5.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        if (selectedItem?.isVerified == true) {
                            TagBoxWithImage(text = "VERIFIED", colorResource(R.color.light_green), Color.White, R.drawable.verified_user)
                            AppSpacerV(5)
                        }
                        val isNormalAgent = selectedItem?.agent?.agentType.equals("agent", ignoreCase = true)
                        TagBoxWithImage(
                            text = if (isNormalAgent) "AGENT" else "SUPERAGENT",
                            bgColor = if (isNormalAgent) Color(0xFF0832AE) else Color(0xFFEA3934),
                            textColor = Color.White,
                            icon = if (isNormalAgent) R.drawable.ic_agent else R.drawable.ic_super_agent
                        )
                        AppSpacerV(5)
                        selectedItem?.propertyType?.name?.takeIf { it.isNotBlank() }?.let {
                            TagBox(text = it.uppercase(), Color.White, Color.Black)
                        }
                    }
                    // Text bottom left
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        AppTextLight(text = listedText, color = Color.White)
                    }
                }

                AppTextMedium(
                    text = titleText,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                )
                AppSpacerV(4)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                ) {
                    AppIcon(resId = R.drawable.location)
                    AppSpacerH(4)
                    AppTextLight(text = locationText)
                }
                AppDivider(modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp, color = colorResource(R.color.screen_bg_color))
                // 🔹 Bottom Section
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        AppTextLight(text = stringResource(R.string.price),)
                        AppTextMedium(text = priceText)
                    }
                }
                AppSpacerV(10)
            }
        }
    }
}
@Composable
fun TagBoxWithImage(text: String, bgColor: Color, textColor: Color,icon: Int) {
    Box(
        modifier = Modifier.background(bgColor).padding(horizontal = 8.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
            AppImage(resId = icon)
            AppSpacerH(5)
            AppTextMedium(text= text, fontSize = 11.sp, color =textColor)
        }
    }
}

// Rent marker bottom sheet <<<----------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceMapBottomSheet(onDismiss: () -> Unit, onExploreClick: () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White, sheetState = sheetState) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF4B00C6),
                                Color(0xFF3A00B8),
                                Color(0xFF2B0099),
                                Color(0xFF15005A),
                                Color(0xFF000000)
                            )
                        )
                    ).padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column (){
                        AppTextBold(text = "Nad Al sheba", color = Color.White)
                        AppSpacerV(3)
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            AppTextLight(text = "Average price : ",color = Color.White)
                            AppTextMedium(text = "1,457,0000 AED", color = Color.White)
                        }
                    }

                    Box(modifier = Modifier.size(45.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column (horizontalAlignment = Alignment.CenterHorizontally){
                            AppRatingBar(rating = 4.8f, maxStars = 1, starSize = 15)
                            AppSpacerV(2)
                            AppTextMedium(text = "4.8", color = Color.White)
                        }
                    }
                }
            }
            AppSpacerV(12)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(colorResource(R.color.splash_primary))
                    .clickable {
                        onDismiss()
                        onExploreClick()
                    },
                contentAlignment = Alignment.Center
            ) {
                AppTextMedium(text = stringResource(R.string.explore_community),
                    color = Color.White)
            }
            AppSpacerV(15)
            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                AppTextMedium(text = stringResource(R.string.community_properties))
                AppTextLight(text = stringResource(R.string.see_all))
            }
            AppSpacerV(10)
            LazyRow(
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(10) {
                    PropertyCard(
                        imageUrl = "https://images.unsplash.com/photo-1583608205776-bfd35f0d9f83?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                        title = "Omniyat Bespoke | Villa",
                        location = "Dubai, Palm jumeirah",
                        price = "9,000,000 AED/year",
                        beds = "2 Bed",
                        baths = "3 Bath",
                        area = "3,832 sqft"
                    )
                }
            }
        }
    }
}
@Composable
fun PropertyCard(
    imageUrl: String,
    title: String,
    location: String,
    price: String,
    beds: String,
    baths: String,
    area: String
) {

    Card(
        modifier = Modifier.width(280.dp).padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(170.dp)
                    .padding(end = 4.dp, start = 4.dp, top = 4.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.FillBounds
            )

            Column(modifier = Modifier.padding(4.dp)) {
                AppTextMedium(text = title, modifier = Modifier.padding(horizontal = 10.dp))
                AppSpacerV(4)

                Row(modifier = Modifier.padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(resId = R.drawable.location)
                    AppSpacerH(4)

                    AppTextLight(text = location )
                }
                AppSpacerV(8)

                AppTextSemiBold(text = price , modifier = Modifier.padding(horizontal = 10.dp))

                AppSpacerV(10)

                Row(modifier = Modifier.fillMaxWidth()) {

                    FeatureItem(icon = R.drawable.bad, text = beds, modifier = Modifier.weight(1f))

                    AppSpacerH(4)

                    FeatureItem(icon = R.drawable.bath, text = baths, modifier = Modifier.weight(1f))
                }

                FeatureItem(icon = R.drawable.sqft, text = area, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
            }
        }
    }
}
@Composable
fun FeatureItem(icon: Int, text: String, modifier: Modifier = Modifier) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.background(
            Color(0xFFF5F5F5),RoundedCornerShape(5.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        AppIcon(resId = icon)
        AppSpacerH(4)

        AppTextLight(text = text, color = colorResource(R.color.textprimary))
    }
}
// ---------->>> New Project marker bottom sheet
// end name here <<<----------

