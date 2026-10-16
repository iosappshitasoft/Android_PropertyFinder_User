package com.app.propertyfinder.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.SuccessColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.SnackbarManager
import com.app.propertyfinder.utils.rememberSnackbarManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.ContactedPropertyItem
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

data class ContactedProperty(
    val id: String,
    val propertyId: String,
    val agentId: String?,
    val agentPhoneNumber: String?,
    val agentWhatsappNumber: String?,
    val agentEmail: String?,
    val imageUrl: String?,
    val title: String,
    val location: String,
    val beds: Int?,
    val baths: Int?,
    val area: String?,
    val date: String,
    val contactMethod: String?
)

private val ContactedCardWhatsApp = Color(0xFF25D366)
private val ContactedCardCall = Color(0xFFFF3B30)
private val ContactedCardMail = Color(0xFF2815B7)
private val ContactedCardFooterBg = Color(0xFFF7F7F7)
private val ContactedSelectionTopBar = Color(0xFFF2F2F2)
private val ContactedSelectionBadgeRed = MolumulkPrimaryCoralRed

fun ContactedPropertyItem.toUiModel(): ContactedProperty? {
    val contactId = id ?: return null

    property?.let { prop ->
        val locationText =
            prop.location?.fullAddress ?: listOfNotNull(prop.location?.building, prop.location?.city)
                .joinToString(", ").ifBlank { "-" }

        val primaryImageUrl = prop.images.firstOrNull { it.isPrimary == true }?.url
            ?: prop.images.firstOrNull()?.url

        return ContactedProperty(
            id = contactId,
            propertyId = prop.id ?: "",
            agentId = agent?.id,
            agentPhoneNumber = agent?.phoneNumber,
            agentWhatsappNumber = agent?.whatsappNumber,
            agentEmail = agent?.email,
            imageUrl = primaryImageUrl?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/property/$it" },
            title = prop.title ?: "-",
            location = locationText,
            beds = prop.bedrooms,
            baths = prop.bathrooms,
            area = formatSqftArea(prop.area?.sqft),
            date = formatContactedDate(contactedAt.orEmpty()),
            contactMethod = contactMethod
        )
    }

    project?.let { proj ->
        val pid = proj.id ?: return@let null
        val locationText = proj.location?.address
            ?: listOfNotNull(proj.location?.zone, proj.location?.city).joinToString(", ").ifBlank { "-" }
        val projectImgBase = PrefUtils.getString(
            Constants.TAG_URL_PROJECT_IMG,
            "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/"
        ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/" }
        val primaryImageUrl = proj.images.firstOrNull { it.isPrimary == true }?.url
            ?: proj.images.firstOrNull()?.url

        return ContactedProperty(
            id = contactId,
            propertyId = pid,
            agentId = agent?.id,
            agentPhoneNumber = agent?.phoneNumber,
            agentWhatsappNumber = agent?.whatsappNumber,
            agentEmail = agent?.email,
            imageUrl = primaryImageUrl?.let { projectImgBase + it },
            title = proj.projectName ?: "-",
            location = locationText,
            beds = null,
            baths = null,
            area = "",
            date = formatContactedDate(contactedAt.orEmpty()),
            contactMethod = contactMethod
        )
    }

    return null
}

private fun formatSqftArea(sqft: Int?): String {
    if (sqft == null) return ""
    return "${NumberFormat.getNumberInstance(Locale.US).format(sqft)}sqft"
}

private fun formatContactedDate(raw: String): String {
    if (raw.isBlank()) return ""
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )
    for (pattern in patterns) {
        try {
            val sdfIn = SimpleDateFormat(pattern, Locale.US).apply {
                if (pattern.contains("Z") || pattern.contains("X")) {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            }
            val parsed = sdfIn.parse(raw) ?: continue
            val sdfOut = SimpleDateFormat("d MMM yyyy", Locale.US)
            return sdfOut.format(parsed)
        } catch (_: Exception) {
            continue
        }
    }
    return raw
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactedPropertiesScreen(navController: NavHostController, userViewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val contactedState by userViewModel.contactedPropertiesState.collectAsStateWithLifecycle()
    val deleteContactedState by userViewModel.deleteContactedState.collectAsStateWithLifecycle()
    val deleteAllContactedState by userViewModel.deleteAllContactedState.collectAsStateWithLifecycle()

    var properties by remember { mutableStateOf<List<ContactedProperty>>(emptyList()) }
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }
    val snackbarManager = rememberSnackbarManager()

    androidx.compose.runtime.LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
        userViewModel.getContactedProperties(TokenManager.accessToken, page = 1, limit = 20)
    }

    androidx.compose.runtime.LaunchedEffect(contactedState.data) {
        val items = contactedState.data?.data?.properties.orEmpty()
        properties = items.mapNotNull { it.toUiModel() }
    }

    if (contactedState.isLoading || deleteContactedState.isLoading || deleteAllContactedState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            AppLoadingDialog()
        }
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(hostState = snackbarManager.snackbarHostState, containerColor = SuccessColor)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    if (isSelectionMode) {
                        AppTextBold(text = "${selectedIds.size} Selected", fontSize = 18.sp)
                    } else {
                        AppTextBold(text = "Contacted properties", fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    if (isSelectionMode) {
                        val isAllSelected = selectedIds.size == properties.size && properties.isNotEmpty()
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isAllSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider),
                            modifier = Modifier.padding(start = 16.dp).size(20.dp)
                        ) {
                           Checkbox(
                               checked = isAllSelected,
                               onCheckedChange = { 
                                   if (it) {
                                       selectedIds.clear()
                                       selectedIds.addAll(properties.map { p -> p.id })
                                   } else {
                                       selectedIds.clear()
                                   }
                               },
                               colors = CheckboxDefaults.colors(
                                   checkedColor = MolumulkPrimaryCoralRed,
                                   uncheckedColor = Color.Transparent,
                                   checkmarkColor = Color.White
                               )
                           )
                        }
                    } else {
                         IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                        }
                    }
                },
                actions = {
                    if (isSelectionMode) {
                        IconButton(onClick = {
                            isSelectionMode = false
                            selectedIds.clear()
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MolumulkCharcoal)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (isSelectionMode) ContactedSelectionTopBar else Background
                )
            )
        },
        bottomBar = {
            if (isSelectionMode && selectedIds.isNotEmpty()) {
                val deleteAllSelected =
                    selectedIds.size == properties.size && properties.isNotEmpty()
                Box(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = {
                            // API expects contacted-properties record ids: response has top-level `_id` inside each item.
                            val idsToDelete =
                                properties.filter { selectedIds.contains(it.id) }.map { it.id }
                            if (idsToDelete.isEmpty()) return@Button

                            userViewModel.deleteContactedProperties(
                                token = TokenManager.accessToken,
                                ids = idsToDelete
                            ) { result ->
                                when (result) {
                                    is ApiResult.Success -> {
                                        properties = properties.filter { !selectedIds.contains(it.id) }
                                        selectedIds.clear()
                                        isSelectionMode = false
                                        snackbarManager.showSuccess(result.body.message.toString())
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(result.message)
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MolumulkDimens.PrimaryButtonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                        shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        AppSpacerH(8)
                        AppTextBold(
                            text = if (deleteAllSelected) "Delete all" else "Delete",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        if (properties.isEmpty()) {
            EmptyContactedState(snackbarManager)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(properties) { property ->
                    ContactedPropertyItem(
                        contactedItems = contactedState.data?.data?.properties,
                        property = property,
                        isSelectionMode = isSelectionMode,
                        isSelected = selectedIds.contains(property.id),
                        onLongClick = {
                            isSelectionMode = true
                            if (!selectedIds.contains(property.id)) {
                                selectedIds.add(property.id)
                            }
                        },
                        onSelect = {
                            if (selectedIds.contains(property.id)) {
                                selectedIds.remove(property.id)
                                if (selectedIds.isEmpty()) {
                                    // Optional: Exit selection mode if no items selected? 
                                    // Design usually keeps mode active until X clicked.
                                }
                            } else {
                                selectedIds.add(property.id)
                            }
                        },
                        onDelete = {
                            userViewModel.deleteContactedProperties(
                                token = TokenManager.accessToken,
                                ids = listOf(property.id)
                            ) { result ->
                                when (result) {
                                    is ApiResult.Success -> {
                                        properties = properties.filter { it.id != property.id }
                                        snackbarManager.showSuccess(result.body.message.toString())
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(result.message)
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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ContactedPropertyItem(
    contactedItems : List<ContactedPropertyItem>?,
    property: ContactedProperty,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val cardShape = RoundedCornerShape(MolumulkDimens.CardCornerRadius)
    val context = LocalContext.current
    Surface(
        shape = cardShape,
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onSelect()
                },
                onLongClick = {
                    if (!isSelectionMode) onLongClick()
                }
            )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row (){
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                        .padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelectionMode) {
                        ContactedPropertySelectionLead(
                            isSelected = isSelected,
                            onClick = onSelect,
                            property.imageUrl ?: ""
                        )
                    } else {
                        AppSubComposeAsyncImage(
                            model = property.imageUrl,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                    AppSpacerH(12)

                    Column() {
                        AppTextBold(text = property.title, fontSize = 13.sp, lineHeight = 17.sp)
                        AppSpacerV(4)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = ImageVector.vectorResource(id = R.drawable.location),
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            AppSpacerH(4)
                            AppTextLight(text = property.location )
                        }
                    }
                }

                Box (modifier = Modifier.padding(top = 20.dp, end = 8.dp)){
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextSecondary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { menuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.width(165.dp),
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                        containerColor = Color.White
                    ) {
                        DropdownMenuItem(
                            text = { AppTextMedium(text = "Report") },
                            onClick = { menuExpanded = false },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.flag),
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { AppTextMedium(text = "Share") },
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Share")
                                    putExtra(Intent.EXTRA_TEXT, "Hello, this is shared text from my app") }
                                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
                                menuExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.share),
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { AppTextMedium(text = "Delete") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = ImageVector.vectorResource(R.drawable.delete),
                                    contentDescription = null,
                                    tint = TextSecondary
                                )
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 18.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactedActionPill(
                    icon = ImageVector.vectorResource(id = R.drawable.whatapp),
                    text = "Whatsapp",
                    backgroundColor = ContactedCardWhatsApp,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openWhatsAppChat(context, property.agentWhatsappNumber)
                    }
                )
                ContactedActionPill(
                    icon = ImageVector.vectorResource(id = R.drawable.call),
                    text = "Call",
                    backgroundColor = ContactedCardCall,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openDialer(context, property.agentPhoneNumber)
                    }
                )
                ContactedActionPill(
                    icon = ImageVector.vectorResource(id = R.drawable.mail),
                    text = "Mail",
                    backgroundColor = ContactedCardMail,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        openMailClient(context, property.agentEmail)
                    }
                )
            }

            val specsText = buildContactedSpecsLine(property.beds, property.baths, property.area)
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 5.dp, end = 5.dp, bottom = 5.dp)
                    .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                    .background(Background)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(text = specsText, fontSize = 10.sp)
                AppTextLight(text = property.date, fontSize = 10.sp)
            }
        }
    }
}

private fun openWhatsAppChat(context: android.content.Context, whatsappNumber: String?) {
    val phone = whatsappNumber?.filter { it.isDigit() }.orEmpty()
    if (phone.isBlank()) return

    val messageIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone"))
    try {
        context.startActivity(messageIntent)
    } catch (e: Exception) {
        Log.e("ContactedProperties", "Unable to open WhatsApp", e)
    }
}

private fun openMailClient(context: android.content.Context, email: String?) {
    val emailAddress = email?.trim().orEmpty()
    if (emailAddress.isBlank()) return

    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(emailAddress))
    }
    try {
        context.startActivity(emailIntent)
    } catch (e: Exception) {
        Log.e("ContactedProperties", "Unable to open email client", e)
    }
}

private fun buildContactedSpecsLine(beds: Int?, baths: Int?, area: String?): String {
    val bedText = "${beds ?: 0} Bed"
    val bathText = "${baths ?: 0} Bath"
    val areaText = "${area?.takeIf { it.isNotBlank() } ?: "0"} sqft"

    return "$bedText   |   $bathText   |   $areaText"
}

@Composable
private fun ContactedPropertySelectionLead(
    isSelected: Boolean,
    onClick: () -> Unit,
    imageUrl : String
) {
    val size = 56.dp
    val shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
    if (isSelected) {
        Box(
            modifier = Modifier.size(size).clip(shape)
                .background(MolumulkPrimaryCoralRed)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            AppSubComposeAsyncImage(
                model = imageUrl,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                .background(MolumulkPrimaryCoralRed.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center){
                AppImage(
                    resId = R.drawable.tick,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    } else {
        AppSubComposeAsyncImage(
            model = imageUrl,
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactedActionPill(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        modifier = modifier.height(MolumulkDimens.FilterChipHeight)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            AppSpacerH(4)
            AppTextBold(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}


@Composable
fun EmptyContactedState(snackbarManager: SnackbarManager) {
     Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
         // Placeholder image
        AppImage(
             resId = R.drawable.contacted, // Reusing existing placeholder or similar
             modifier = Modifier.size(250.dp).padding(16.dp)
        )
        AppSpacerV(16)
        AppTextRegular(text = "You haven't contacted any properties yet", color = TextSecondary, fontSize = 16.sp)
    }
}

@Preview
@Composable
fun ContactedPreview() {
    ContactedPropertiesScreen(rememberNavController())
}
