package com.app.propertyfinder.screen.unit

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.ProjectUnitLayout
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.screen.company.buildImageUrl
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UnitListPreview() {
    UnitListScreen(rememberNavController())
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(navController: NavController){
    val userViewModel: UserViewModel = hiltViewModel()
    val context = LocalContext.current
    val snackbarManager = rememberSnackbarManager()

    val layoutsJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("unit_list_layouts_json")
    val toolbarTitle = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("unit_list_title")
        .orEmpty()
        .ifBlank { "Units" }
    val projectIdForInquiry = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("project_id_for_inquiry")
        .orEmpty()
    val layouts: List<ProjectUnitLayout> = remember(layoutsJson) {
        if (layoutsJson.isNullOrBlank()) emptyList()
        else runCatching {
            Gson().fromJson<List<ProjectUnitLayout>>(
                layoutsJson,
                object : TypeToken<List<ProjectUnitLayout>>() {}.type
            )
        }.getOrDefault(emptyList())
    }

    var showGuestInquiryDialog by remember { mutableStateOf(false) }
    var pendingLayoutId by remember { mutableStateOf<String?>(null) }
    var guestName by remember { mutableStateOf(PrefUtils.getString(Constants.TAG_USER_NAME, "").orEmpty()) }
    var guestEmail by remember { mutableStateOf(PrefUtils.getString(Constants.TAG_USER_EMAIL, "").orEmpty()) }
    var guestPhone by remember { mutableStateOf(PrefUtils.getString(Constants.TAG_USER_PHONE, "").orEmpty()) }

    fun createInquiryAndOpenWhatsApp(layoutId: String, name: String, email: String, phone: String, withBearer: Boolean) {
        if (projectIdForInquiry.isBlank()) {
            snackbarManager.showError("Project id is missing")
            return
        }
        val request = hashMapOf<String, Any>(
            "projectId" to projectIdForInquiry,
            "layoutId" to layoutId,
            "inquiryType" to "whatsapp",
            "message" to "I'm interested in this project",
            "name" to name,
            "email" to email,
            "phoneNumber" to phone
        )
        val tokenForApi = if (withBearer) TokenManager.accessToken else null
        userViewModel.createInquiry(
            token = tokenForApi,
            request = request
        ) { result ->
            when (result) {
                is ApiResult.Success -> {
                    snackbarManager.showSuccess(result.body.message.orEmpty().ifBlank { "Inquiry created" })
                    val whatsappUrl = result.body.data?.whatsappUrl
                    if (!whatsappUrl.isNullOrBlank()) {
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)))
                        }.onFailure {
                            snackbarManager.showError("Unable to open WhatsApp")
                        }
                    }
                }
                is ApiResult.Error -> {
                    snackbarManager.showError(result.message)
                }
            }
        }
    }

    if (showGuestInquiryDialog) {
        AlertDialog(
            onDismissRequest = { showGuestInquiryDialog = false },
            title = { AppTextBold(text = "Enter your details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = guestName,
                        onValueChange = { guestName = it },
                        label = { androidx.compose.material3.Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = guestEmail,
                        onValueChange = { guestEmail = it },
                        label = { androidx.compose.material3.Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = guestPhone,
                        onValueChange = { guestPhone = it },
                        label = { androidx.compose.material3.Text("Phone number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (guestName.isBlank() || guestEmail.isBlank() || guestPhone.isBlank()) {
                        snackbarManager.showError("Please fill all details")
                        return@TextButton
                    }
                    showGuestInquiryDialog = false
                    pendingLayoutId?.let {
                        createInquiryAndOpenWhatsApp(
                            layoutId = it,
                            name = guestName.trim(),
                            email = guestEmail.trim(),
                            phone = guestPhone.trim(),
                            withBearer = false
                        )
                    }
                }) {
                    androidx.compose.material3.Text("Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGuestInquiryDialog = false }) {
                    androidx.compose.material3.Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { snackbarManager.CustomSnackbarHost() },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = toolbarTitle, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
        ) {
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                items(layouts) { layout ->
                    UnitLayoutItem(
                        layout = layout,
                        onInquireClick = { clickedLayoutId ->
                            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
                            if (loginType == Constants.SKIP_USER) {
                                pendingLayoutId = clickedLayoutId
                                showGuestInquiryDialog = true
                            } else {
                                val profileName = PrefUtils.getString(Constants.TAG_USER_NAME, "").orEmpty()
                                val profileEmail = PrefUtils.getString(Constants.TAG_USER_EMAIL, "").orEmpty()
                                val profilePhone = PrefUtils.getString(Constants.TAG_USER_PHONE, "").orEmpty()
                                if (profileName.isBlank() || profileEmail.isBlank() || profilePhone.isBlank()) {
                                    pendingLayoutId = clickedLayoutId
//                                    showGuestInquiryDialog = true
                                } else {
                                    createInquiryAndOpenWhatsApp(
                                        layoutId = clickedLayoutId,
                                        name = profileName,
                                        email = profileEmail,
                                        phone = profilePhone,
                                        withBearer = true
                                    )
                                }
                            }
                        }
                    )
                    AppSpacerV(15)
                }
            }
        }
    }
}

@Composable
fun UnitLayoutItem(layout: ProjectUnitLayout, onInquireClick: (String) -> Unit) {
    val projectImageBaseUrl = PrefUtils.getString(
        Constants.TAG_URL_PROJECT_IMG,
        "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/"
    ).orEmpty().ifBlank { "https://d1dp1oh0ra5b0z.cloudfront.net/img/project/" }
    val imageUrl = buildImageUrl(projectImageBaseUrl, layout.floorPlans.firstOrNull())
    val bathroomsText = "${layout.bathrooms ?: 0} Bath"
    val areaText = "${layout.areaSqft?.toInt() ?: 0} sqft"
    val titleText = layout.layoutName.orEmpty().ifBlank { "${layout.bedrooms ?: 0} Bed" }
    val amountText = layout.startingPrice?.amount?.let {
        "$it ${layout.startingPrice.currency ?: "AED"}"
    } ?: "Coming soon"

    Card(
        modifier = Modifier.clickable { },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()
                .padding(start = 3.dp, end = 3.dp, top = 3.dp)
                .clip(RoundedCornerShape(10.dp))) {

                // Image
                AppSubComposeAsyncImage(
                    model = imageUrl,
                    modifier = Modifier.fillMaxWidth().height(210.dp)
                )
            }
            // Bottom curved container
            Row(modifier = Modifier.fillMaxWidth().offset(y = (-20).dp)
                .clip(RoundedCornerShape(14.dp)),
            ) {

                BottomAmenityItem(
                    icon = R.drawable.bad,
                    text = bathroomsText,
                    modifier = Modifier.weight(1f)
                )

                BottomAmenityItem(
                    icon = R.drawable.bath,
                    text = areaText,
                    modifier = Modifier.weight(1f)
                )
            }
            AppSpacerV(5)
            AppTextBold(text = titleText,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().offset(y = (-8).dp),
                textAlign = TextAlign.Center)

            Button(
                modifier = Modifier.fillMaxWidth().padding(start = 15.dp, end = 15.dp, bottom = 10.dp, top = 10.dp),
                onClick = {
                    val id = layout.layoutId.orEmpty()
                    if (id.isNotBlank()) {
                        onInquireClick(id)
                    }
                },
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.whatsapp_green)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(resId = R.drawable.whatapp, modifier = Modifier.size(20.dp))
                    AppSpacerH(5)
                    AppTextSemiBold(fontSize = 12.sp, text = "Inquire", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun BottomAmenityItem(icon: Int, text: String, modifier: Modifier = Modifier) {

    Row (modifier = modifier.clip(RoundedCornerShape(8.dp))
        .background(Color.White)
        .padding(5.dp)){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(Background)
                .padding(vertical = 10.dp)
        ) {
            AppIcon(resId = icon)
            AppSpacerH(6)
            AppTextMedium(
                fontSize = 12.sp,
                text = text,
                color = colorResource(R.color.textprimary)
            )
        }
    }
}