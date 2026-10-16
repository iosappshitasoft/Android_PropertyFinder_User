package com.app.propertyfinder.screen

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.countries
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.alerts.ConfirmationAlert
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.FocusBorderColor
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
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.ui.theme.UnfocusBorderColor
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.CustomToastMessage
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class EmojiDataClass(
    val image : Int,
    val title : String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavHostController) {
    val viewModel: PropertyViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val userViewModel: UserViewModel = hiltViewModel()
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val profileState by authViewModel.profileState.collectAsStateWithLifecycle()
    val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
    val isSkipUser = loginType == Constants.SKIP_USER
    val hasUnreadNotifications = PrefUtils.getBoolean(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, false)
    val alertsState by userViewModel.alertsState.collectAsStateWithLifecycle()
    val contactedState by userViewModel.contactedPropertiesState.collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val rateUsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLanguageSheet by remember { mutableStateOf(false) }
    var showCountrySheet by remember { mutableStateOf(false) }
    var showMeasurementSheet by remember { mutableStateOf(false) }
    var showRateUsSheet by remember { mutableStateOf(false) }

    var selectedLanguage by remember { mutableStateOf("English") }
    var selectedCountry by remember { mutableStateOf("United Arab Emirates") }
    var selectedMeasurement by remember { mutableStateOf("Imperial (ft², mi)") }
    val languageOptions = listOf("العربية", "English")
    val countryOptions = masterDataState.data?.data?.countries.orEmpty()

    // Used for logout payload (from profile API)
    var logoutEmail by remember { mutableStateOf("") }
    var logoutUserId by remember { mutableStateOf("") }

    val snackbarManager = rememberSnackbarManager()
    val coroutineScope = rememberCoroutineScope()
    var showLogoutAlert by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
        viewModel.getMasterData("countries") { result ->
            when (result) {
                is ApiResult.Success -> Unit
                is ApiResult.Error -> Unit
            }
        }
        if (!isSkipUser && TokenManager.accessToken.isNotBlank()) {
            authViewModel.getProfileData(TokenManager.accessToken) { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val user = result.body.data?.user
                        logoutEmail = user?.email.orEmpty()
                        logoutUserId = user?.id.orEmpty()

                        val notificationSettings = user?.preferences?.notificationSettings

                        // Map server notificationSettings -> local toggles.
                        // SMS is not represented in local prefs (yet), so we ignore it.
                        PrefUtils.save(
                            "EMAIL_NOTIF_GENERAL",
                            notificationSettings?.email ?: true
                        )
                        PrefUtils.save(
                            "EMAIL_NOTIF_ALERTS",
                            notificationSettings?.email ?: true
                        )
                        PrefUtils.save(
                            "PUSH_NOTIF_GENERAL",
                            notificationSettings?.push ?: true
                        )
                        PrefUtils.save(
                            "PUSH_NOTIF_ALERTS",
                            notificationSettings?.push ?: true
                        )
                    }

                    is ApiResult.Error -> Unit
                }
            }
            userViewModel.getUserAlerts(TokenManager.accessToken, page = 1, limit = 20)
            userViewModel.getContactedProperties(TokenManager.accessToken, page = 1, limit = 20)
        }
    }
    val userProfile = profileState.data?.data?.user
    val savedAlertsCount = userProfile?.searchAlerts?.size ?: alertsState.data?.data?.items?.size ?: 0
    val contactedCount = userProfile?.contactedProperties?.size ?: contactedState.data?.data?.properties?.size ?: 0
    val profileName = listOfNotNull(userProfile?.firstName, userProfile?.lastName)
        .joinToString(" ")
        .ifBlank { "William Turner" }
    val profileEmail = userProfile?.email.orEmpty().ifBlank { "williamturner@gmail.com" }
    val userImage = userProfile?.profilePicture.orEmpty().ifBlank { "" }


    if (showLanguageSheet) {
        LanguageBottomSheet(
            onDismiss = { },
            sheetState = sheetState,
            selectedOption = selectedLanguage,
            onOptionSelected = { selectedLanguage = it; },
            languages = languageOptions
        )
    }

    if (showCountrySheet) {
        CountryBottomSheet(
            onDismiss = { },
            sheetState = sheetState,
            selectedOption = selectedCountry,
            onOptionSelected = { selectedCountry = it; },
            countries = countryOptions
        )
    }

    if (showMeasurementSheet) {
        MeasurementBottomSheet(
            onDismiss = { },
            sheetState = sheetState,
            selectedOption = selectedMeasurement,
            onOptionSelected = { selectedMeasurement = it }
        )
    }

    Scaffold(
        topBar = {
            AccountTopBar(navController, hasUnreadNotifications)
        },
        bottomBar = {
            AccountBottomNavigationBar(navController)
        },
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            
            
            // Profile Section
            if (isSkipUser) {
                GuestProfileSection(
                    onClick = {
                        navController.navigate(Route.Login.route) {
                            popUpTo(Route.Account.route) { inclusive = true }
                        }
                    }
                )
            } else {
                ProfileSection(onEditClick = {
                    navController.navigate(Route.EditProfile.route)
                }, name = profileName, email = profileEmail, userImg = userImage)
            }

            AppSpacerV(24)

            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
            if (loginType == Constants.LOGIN_USER) {
                // Saved Alerts & Contacted Properties
                AccountActionItem(
                    position = "top",
                    icon = R.drawable.alertnotification,
                    text = stringResource(R.string.saved_alerts),
                    badgeCount = savedAlertsCount,
                    onClick = {
                        navController.navigate(Route.SavedAlerts.route)
                    }
                )
                AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
                AccountActionItem(
                    position = "bottom",
                    icon = R.drawable.contact,
                    text = stringResource(R.string.contacted_properties),
                    badgeCount = contactedCount,
                    onClick = {
                        navController.navigate(Route.ContactedProperties.route)
                    }
                )
                AppSpacerV(24)
            }

            // Settings Section
            SectionHeader(text = stringResource(R.string.settings))
            AppSpacerV(8)
            SettingsItem(
                position = "top",
                icon = R.drawable.language, 
                text = stringResource(R.string.language), 
                value = selectedLanguage,
                onClick = { /*showLanguageSheet = true*/ }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.country, 
                text = stringResource(R.string.country), 
                value = selectedCountry, // Simplification: showing full name. Ideally map to Code or short name if needed.
                onClick = { }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.measurment, 
                text = stringResource(R.string.measurement_system), 
                value = if(selectedMeasurement.startsWith("Imperial")) "ft²" else "m²",
                onClick = { }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "bottom",
                icon = R.drawable.notifictionsetting, 
                text = stringResource(R.string.notification_settings),
                onClick = { navController.navigate(Route.NotificationSettings.route) }
            )

            AppSpacerV(24)

            // Real Estate Resources
            SectionHeader(text = stringResource(R.string.real_estate_resources))
            AppSpacerV(8)
            SettingsItem(
                position = "top",
                icon = R.drawable.findagent, 
                text = stringResource(R.string.find_an_agent),
                onClick = { navController.navigate(Route.Agents.route) }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.findcomapny, 
                text = stringResource(R.string.find_companies),
                onClick = { navController.navigate(Route.FindCompanies.route) }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.transaction, 
                text = stringResource(R.string.transaction_in_dubai),
                onClick = { navController.navigate(Route.TransactionOverview.route) }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.finddeveloper, 
                text = stringResource(R.string.find_developers),
                onClick = { navController.navigate(Route.FindDevelopers.route) }
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.calculator,
                text = stringResource(R.string.rent_vs_buy_calculator),
                onClick = {navController.navigate(Route.RentBuyCalculator.route)})
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "bottom",
                icon = R.drawable.morgeage,
                text = stringResource(R.string.mortgage_calculator),
                onClick = { navController.navigate(Route.MortgageCalculator.route)})

            AppSpacerV(24)

            // Other
            SectionHeader(text = stringResource(R.string.other))
            AppSpacerV(8)
            SettingsItem(
                position = "top",
                icon = R.drawable.aboutus,
                text = stringResource(R.string.about_us),
                onClick = {navController.navigate(Route.AboutUs.route)}
            )
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "",
                icon = R.drawable.star,
                text = stringResource(R.string.rate_us),
//                onClick = { RateUsBottomSheet() }) // Placeholder icon
                onClick = { showRateUsSheet = true})

            if (showRateUsSheet) {
                RateUsBottomSheet(
                    rateUsSheetState = rateUsSheetState,
                    onDismiss = { showRateUsSheet = false }
                )
            }
            AppDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingsItem(
                position = "bottom",
                icon = R.drawable.needhelp,
                text = stringResource(R.string.need_help),
                onClick = {
                    navController.navigate(Route.Help.route)
                })

            AppSpacerV(32)
            if (loginType == Constants.LOGIN_USER) {
                Button(
                    onClick = {
                        showLogoutAlert = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(MolumulkDimens.PrimaryButtonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1F1F)),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                ) {
                    AppTextBold(text = stringResource(R.string.logout), color = Color.White, fontSize = 16.sp)
                }

                if (showLogoutAlert) {
                    ConfirmationAlert(
                        title = "Are you sure you want to logout?",
                        yesText = "Logout",
                        noText = "Cancel",
                        onYesClick = {
                            showLogoutAlert = false
                            val refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").orEmpty()
                            val email = logoutEmail
                            val userId = logoutUserId

                            if (refreshToken.isBlank() || email.isBlank() || userId.isBlank()) {
                                PrefUtils.clear()
                                TokenManager.accessToken = ""
                                TokenManager.refreshToken = ""
                                navController.navigate(Route.Welcome.route) {
                                    popUpTo(Route.Home.route) { inclusive = true }
                                }
                                navController.navigate(Route.Login.route)
                            } else {
                                authViewModel.logout(
                                    refreshToken = refreshToken,
                                    email = email,
                                    userId = userId
                                ) { result ->
                                    when (result) {
                                        is ApiResult.Success -> {
                                            CustomToastMessage.showSuccess(context, result.body.message ?: "success")

                                            // Clear local stored data and tokens.
                                            PrefUtils.clear()
                                            TokenManager.accessToken = ""
                                            TokenManager.refreshToken = ""

                                            coroutineScope.launch {
                                                delay(500)
                                                navController.navigate(Route.Welcome.route) {
                                                    popUpTo(Route.Home.route) { inclusive = true }
                                                }
                                                navController.navigate(Route.Login.route)
                                            }
                                        }

                                        is ApiResult.Error -> {
                                            CustomToastMessage.showError(context, result.message)
                                        }
                                    }
                                }
                            }
                        },
                        onNoClick = { showLogoutAlert = false },
                        onDismiss = { showLogoutAlert = false }
                    )
                }

                AppSpacerV(32)
            }
        }
    }
}

@Composable
private fun GuestProfileSection(onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = colorResource(R.color.darkBlue),
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            AppImage(resId = R.drawable.baseline_account_circle_24, modifier = Modifier.size(40.dp))
            AppSpacerH(10)
            Column {
                AppTextMedium(
                    text = "Login / Signup your Account",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateUsBottomSheet(rateUsSheetState: SheetState, onDismiss: () -> Unit) {

    // if keyboard is opened first hide keyboard after hided bottom sheet
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isKeyboardOpen by remember { mutableStateOf(false) }

    var comment by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }  // 👈 ADD THIS

    val emojis = listOf(
        EmojiDataClass( R.drawable.ic_emoji_awful,"Awful"),
        EmojiDataClass( R.drawable.ic_emoji_poor,"Poor"),
        EmojiDataClass( R.drawable.ic_emoji_neutral,"Neutral"),
        EmojiDataClass( R.drawable.ic_emoji_good,"Good"),
        EmojiDataClass( R.drawable.ic_emoji_excellent,"Excellent")
    )
    var selectedRating by remember { mutableIntStateOf(-1) }
    BackHandler {
        if (isKeyboardOpen) {
            keyboardController?.hide()
            focusManager.clearFocus()
        } else {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rateUsSheetState,
        shape = RoundedCornerShape(topStart = 33.64.dp, topEnd = 33.64.dp),
        containerColor = Color.White
    ) {
        if (!isSubmitted) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AppTextSemiBold(
                    stringResource(R.string.give_feedback),
                    fontSize = 20.sp, color = MolumulkCharcoal
                )
                AppSpacerV(6)
                AppTextMedium(
                    stringResource(R.string.every_feedback),
                    color = MolumulkMutedSlate, 
                    fontSize = 13.sp
                )

                AppSpacerV(10)

                AppDivider(thickness = 1.dp, color = MolumulkBorderDivider)
                AppSpacerV(10)
                Column (modifier = Modifier.padding(start = 20.dp, end = 20.dp)){
                    AppTextBold(
                        text = stringResource(R.string.rate_your_experience),
                        fontSize = 14.sp, 
                        color = MolumulkCharcoal,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Emoji Rating Row
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        emojis.forEachIndexed { index, emoji ->
                            Column (
                                modifier = Modifier,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                                        .background(
                                            if (selectedRating >= index)
                                                MolumulkLightCoralTint
                                            else MolumulkOffWhite
                                        ).border(
                                            width = 1.dp,
                                            color = if (selectedRating >= index)
                                                MolumulkPrimaryCoralRed
                                            else
                                                MolumulkBorderDivider,
                                            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius)
                                        )
                                        .clickable {
                                            selectedRating = index
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    AppImage(
                                        modifier = Modifier.fillMaxSize(),
                                        resId = emoji.image,
                                        contentDescription = "emoji",
                                    )
                                }
                                AppSpacerV(4)
                                AppTextMedium(
                                    text = emoji.title, 
                                    fontSize = 11.sp, 
                                    color = if (selectedRating >= index) MolumulkPrimaryCoralRed else MolumulkMutedSlate
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    AppTextBold(
                        text = stringResource(R.string.do_you_want),
                        fontSize = 14.sp, 
                        color = MolumulkCharcoal,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    AppTextMedium(
                        stringResource(R.string.please_do_not),
                        color = MolumulkMutedSlate, 
                        fontSize = 12.sp, 
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        placeholder = {
                            AppTextMedium(
                                text = stringResource(R.string.write_your_comment),
                                color = MolumulkMutedSlate, 
                                fontSize = 13.sp
                            ) 
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = MolumulkPrimaryCoralRed,
                            unfocusedBorderColor = MolumulkBorderDivider,
                            cursorColor = MolumulkPrimaryCoralRed
                        )
                    )

                    AppSpacerV(20)

                    Button(
                        enabled = comment.isNotBlank(),
                        onClick = {
                            isSubmitted = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MolumulkDimens.PrimaryButtonHeight),
                        shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MolumulkPrimaryCoralRed,
                            disabledContainerColor = MolumulkPrimaryCoralRed.copy(alpha = 0.4f),
                            contentColor = Color.White,
                            disabledContentColor = Color.White
                        )
                    ) {
                        AppTextBold(
                            text = stringResource(R.string.send_feedback), 
                            color = Color.White, 
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            LaunchedEffect(Unit) {
                delay(2000)      // 2 seconds wait
                onDismiss()      // Close bottom sheet
            }

            ThankYouBottomContent()
        }

    }
}

@Composable
fun ThankYouBottomContent() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.feedback_success_img),
            contentDescription = "Thank You",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextSemiBold(
            text = "Thank you for your feedback",
            fontSize = 18.sp,
            color = MolumulkCharcoal
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextMedium(
            text = "We respect your time and effort",
            fontSize = 14.sp,
            color = MolumulkMutedSlate
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountTopBar(navController: NavHostController, hasUnreadNotifications: Boolean) {
    TopAppBar(
        title = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                AppTextBold(text = stringResource(R.string.account), fontSize = 20.sp)
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(Route.Notifications.route) }) {
                AppImage(
                    resId = if (hasUnreadNotifications) R.drawable.notification_icon else R.drawable.notifictionsetting,
                    modifier = Modifier.size(24.dp)
                )
            }
            AppSpacerH(16)
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
    )
}

@Composable
fun ProfileSection(onEditClick: () -> Unit, name: String, email: String,userImg: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        AppTextMedium(text = stringResource(R.string.profile), color = TextSecondary, fontSize = 12.sp)
        AppSpacerV(8)
        
        // Profile Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF),
                            Color(0xFFFFCCD0),
                            Color(0xFFF9F6F6),
                            Color(0xFFFFCCD0),
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                SubcomposeAsyncImage(
                    model = "https://d1dp1oh0ra5b0z.cloudfront.net/img/user/${userImg}",
                    contentDescription = null,
                    modifier = Modifier.size(48.dp).clip(CircleShape),
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
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            AppImage(
                                resId = R.drawable.baseline_account_circle_24,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                )
                AppSpacerH(12)
                
                // Name and Email
                Column(modifier = Modifier.weight(1f)) {
                    AppTextBold(text = name, fontSize = 13.sp)
                    AppTextRegular(text = email, fontSize = 12.sp, color = TextSecondary, lineHeight = 16.sp)
                }
                
                // Edit Button
                Button(
                    onClick = onEditClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    AppTextMedium(text = stringResource(R.string.edit), color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AccountActionItem(position : String, icon: Int, text: String, badgeCount: Int? = null, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(topEnd = if (position == "top") 10.dp else 0.dp,
            topStart = if (position == "top") 10.dp else 0.dp,
            bottomStart = if (position == "bottom") 10.dp else 0.dp,
            bottomEnd = if (position == "bottom") 10.dp else 0.dp),
        color = Color.White,
        shadowElevation = 0.dp, // Flat as per design, maybe slight shadow or border if needed
    ) {

        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppImage(resId = icon, modifier = Modifier.size(20.dp), tint = TextSecondary)
            AppSpacerH(16)
            AppTextSemiBold(text = text, fontSize = 14.sp, modifier = Modifier.weight(1f))
            
            if (badgeCount != null && badgeCount != 0) {
                Box(modifier = Modifier.size(25.dp).clip(RoundedCornerShape(5.dp))
                    .background(MolumulkPrimaryCoralRed),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextBold(text = badgeCount.toString(), color = Color.White, fontSize = 10.sp)
                }
                AppSpacerH(16)
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    AppTextMedium(
        text = text,
        color = TextSecondary,
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun SettingsItem(position: String,icon: Int, text: String, value: String? = null, onClick: () -> Unit = {}) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(56.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(topEnd = if (position == "top") 10.dp else 0.dp,
            topStart = if (position == "top") 10.dp else 0.dp,
            bottomStart = if (position == "bottom") 10.dp else 0.dp,
            bottomEnd = if (position == "bottom") 10.dp else 0.dp,),
        color = Color.White,
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             AppImage(resId = icon, modifier = Modifier.size(20.dp), tint = TextSecondary)
            AppSpacerH(16)
            AppTextSemiBold(text = text, fontSize = 14.sp, modifier = Modifier.weight(1f))
            
            if (value != null) {
                AppTextRegular(text = value, color = TextSecondary, fontSize = 12.sp)
                AppSpacerH(8)
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AccountBottomNavigationBar(navController: NavHostController) {
    // NEW: Clean white native bottom bar with top hairline divider and standard 64dp height
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MolumulkBorderDivider)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = MolumulkDimens.BottomNavHeight)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search Item
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.search,
                label = stringResource(R.string.search),
                isSelected = false,
                onClick = {
                    navController.navigate(Route.Home.route) { launchSingleTop = true }
                }
            )
            val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
            if (loginType == Constants.LOGIN_USER) {
                // Saved Item
                BottomNavItem(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.heart,
                    label = stringResource(R.string.saved),
                    isSelected = false,
                    onClick = {
                        navController.navigate(Route.Saved.route) { launchSingleTop = true }
                    }
                )
            }

            // Account Item (Selected)
            BottomNavItem(
                modifier = Modifier.weight(1f),
                iconRes = R.drawable.account,
                label = stringResource(R.string.account),
                isSelected = true,
                onClick = { }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    languages: List<String>
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 33.64.dp, topEnd = 33.64.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 24.dp)
                .imePadding()
        ) {
            AppTextBold(
                text = "Select Language",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            AppSpacerV(15)
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                languages.forEach { language ->
                    BottomSheetOptionItem(
                        text = language,
                        isSelected = selectedOption == language,
                        onClick = { onOptionSelected(language) },
                        isCentered = true
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    countries: List<countries>
) {
    var searchText by remember { mutableStateOf("") }
    val filteredCountries = remember(countries, searchText) {
        if (searchText.isBlank()) {
            countries
        } else {
            countries.filter { country ->
                country.name.orEmpty().contains(searchText.trim(), ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .navigationBarsPadding()
                .padding(bottom = 8.dp)
        ) {
            AppTextBold(
                text = "Select Country",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search country") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MolumulkPrimaryCoralRed,
                    unfocusedBorderColor = MolumulkBorderDivider,
                    cursorColor = MolumulkPrimaryCoralRed
                )
            )

            AppSpacerV(8)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 50.dp)
            ) {
                items(filteredCountries) { country ->
                    val countryName = country.name.orEmpty()
                    BottomSheetOptionItem(
                        text = countryName,
                        isSelected = selectedOption == countryName,
                        onClick = { onOptionSelected(countryName) },
                        isCentered = false,
                        flagUrl = country.flag
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementBottomSheet(
    onDismiss: () -> Unit,
    sheetState: SheetState,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
         Column(modifier = Modifier.padding(bottom = 32.dp)) {
            AppTextBold(
                text = "Measurement system",
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally)
            )
            
            BottomSheetOptionItem(text = "Metric (m², km)", isSelected = selectedOption == "Metric (m², km)", onClick = { onOptionSelected("Metric (m², km)") }, isCentered = true)
            BottomSheetOptionItem(text = "Imperial (ft², mi)", isSelected = selectedOption == "Imperial (ft², mi)", onClick = { onOptionSelected("Imperial (ft², mi)") }, isCentered = true)
        }
    }
}

@Composable
fun BottomSheetOptionItem(
    text: String, 
    isSelected: Boolean, 
    onClick: () -> Unit,
    isCentered: Boolean = false,
    flagResId: Int? = null,
    flagUrl: String? = null
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        color = if (isSelected) TextPrimary else Color(0xFFF5F5F5),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = if (isCentered) Arrangement.Center else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextBold(
                text = text, 
                fontSize = 16.sp, 
                color = if (isSelected) Color.White else Color.Black
            )
            
            if (!flagUrl.isNullOrBlank()) {
                AsyncImage(
                    model = flagUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else if (flagResId != null) {
                AppImage(
                    resId = flagResId,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun AccountScreenPreview() {
    PropertyFinderTheme {
        AccountScreen(navController = rememberNavController())
    }
}
