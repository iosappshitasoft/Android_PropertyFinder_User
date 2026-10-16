package com.app.propertyfinder.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.*
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.MolumulkSuccessGreen
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.view.setPadding
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.compose.ui.graphics.asImageBitmap
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.github.dhaval2404.imagepicker.ImagePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.util.Log
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.UpdateProfileRequest
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.FileUtils
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.ui.alerts.ConfirmationAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(navController: NavController) {
    val context = LocalContext.current
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("United Arab Emirates") }
    var mobileCountry by remember { mutableStateOf("United Arab Emirates") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var isDeletingAccount by remember { mutableStateOf(false) }
    var showDeleteAlert by remember { mutableStateOf(false) }
    val snackbarManager = rememberSnackbarManager()
    var snackbarColor by remember { mutableStateOf(PrimaryColor) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    val viewModel: PropertyViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()

    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val profileState by authViewModel.profileState.collectAsStateWithLifecycle()

    val countryOptions = masterDataState.data?.data?.countries.orEmpty()
    val userProfile = profileState.data?.data?.user
    val selectedCountryObj = countryOptions.firstOrNull { it.name == country }
    val selectedMobileCountryObj = countryOptions.firstOrNull { it.name == mobileCountry }
    val selectedCountryPhoneCode = selectedMobileCountryObj?.phoneCode?.takeIf { it.isNotBlank() } ?: ""

    val countrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCountrySheet by remember { mutableStateOf(false) }

    val mobileCountrySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showMobileCountrySheet by remember { mutableStateOf(false) }


    // Error States
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var lastNameError by remember { mutableStateOf<String?>(null) }
    var mobileError by remember { mutableStateOf<String?>(null) }

    // We use the same CountryBottomSheet for the mobile flag picker.
    // No CountryCodePicker instance is needed anymore.

    // Image State
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var profileBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val cropImageUri =  savedStateHandle
        ?.getStateFlow<String?>("cropped_image", null)
        ?.collectAsState()

    val hasChanges = remember(firstName, lastName, mobileNumber, country, mobileCountry, cropImageUri?.value, userProfile) {
        userProfile?.let { profile ->
            val initialPhoneCode = profile.country?.phoneCode.orEmpty()
            val initialMobile = stripCountryCode(profile.phoneNumber.orEmpty(), initialPhoneCode)

            firstName != profile.firstName.orEmpty() ||
            lastName != profile.lastName.orEmpty() ||
            mobileNumber != initialMobile ||
            country != profile.country?.name.orEmpty() ||
            mobileCountry != profile.country?.name.orEmpty() ||
            !cropImageUri?.value.isNullOrEmpty()
        } ?: false
    }

    LaunchedEffect(Unit) {
        // Load countries/languages for the bottom sheet
        viewModel.getMasterData("languages,countries") { /* ignore master data errors here */ }

        val isLoggedIn = PrefUtils.getString(Constants.TAG_LOGIN, "") == Constants.LOGIN_USER
        if (isLoggedIn) {
            TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
            if (TokenManager.accessToken.isNotBlank()) {
                isLoading = true
                authViewModel.getProfileData(TokenManager.accessToken) { result ->
                    isLoading = false
                    if (result is ApiResult.Error) {
                        scope.launch {
                            snackbarManager.showError(result.message)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(userProfile, countryOptions) {
        if (userProfile != null) {
            firstName = userProfile.firstName.orEmpty()
            lastName = userProfile.lastName.orEmpty()
            email = userProfile.email.orEmpty()
            val profilePhoneCode = userProfile.phoneCode.orEmpty()
            mobileNumber = userProfile.phoneNumberWithoutCode.orEmpty().ifBlank {
                stripCountryCode(userProfile.phoneNumber.orEmpty(), profilePhoneCode)
            }

            val apiCountryName = userProfile.country?.name.orEmpty()
            if (apiCountryName.isNotBlank()) {
                country = apiCountryName
            }

            if (profilePhoneCode.isNotBlank()) {
                val mobileCountryByPhoneCode = countryOptions.firstOrNull {
                    it.phoneCode.orEmpty() == profilePhoneCode
                }?.name.orEmpty()
                if (mobileCountryByPhoneCode.isNotBlank()) {
                    mobileCountry = mobileCountryByPhoneCode
                }
            } else if (apiCountryName.isNotBlank()) {
                mobileCountry = apiCountryName
            }
        }
    }

    if (isLoading || isDeletingAccount) {
        AppLoader()
    }

    if (showCountrySheet) {
        CountryBottomSheet(
            onDismiss = { showCountrySheet = false },
            sheetState = countrySheetState,
            selectedOption = country,
            onOptionSelected = { selected ->
                country = selected
                showCountrySheet = false
            },
            countries = countryOptions
        )
    }

    if (showMobileCountrySheet) {
        CountryBottomSheet(
            onDismiss = { showMobileCountrySheet = false },
            sheetState = mobileCountrySheetState,
            selectedOption = mobileCountry,
            onOptionSelected = { selected ->
                mobileCountry = selected
                showMobileCountrySheet = false
            },
            countries = countryOptions
        )
    }

    if (showDeleteAlert) {
        ConfirmationAlert(
            title = "Are you sure you want to delete your account?",
            onYesClick = {
                showDeleteAlert = false
                val token = TokenManager.accessToken.trim()
                isDeletingAccount = true
                authViewModel.deleteUserAccount(token) { result ->
                    isDeletingAccount = false
                    when (result) {
                        is ApiResult.Success -> {
                            scope.launch {
                                snackbarColor = Color.Green
                                snackbarManager.showSuccess(
                                    result.body.message
                                        ?: "Account deleted successfully"
                                )
                                delay(1000)
                                PrefUtils.clear()
                                TokenManager.accessToken = ""
                                TokenManager.refreshToken = ""
                                navController.navigate(Route.Welcome.route) {
                                    popUpTo(Route.Home.route) { inclusive = true }
                                }
                            }
                        }
                        is ApiResult.Error -> {
                            scope.launch {
                                snackbarColor = Color.Red
                                snackbarManager.showError(result.message)
                            }
                        }
                    }
                }
            },
            onNoClick = { showDeleteAlert = false },
            onDismiss = { showDeleteAlert = false }
        )
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(
                hostState = snackbarManager.snackbarHostState,
                containerColor = snackbarColor
            )
        },
        topBar = {
            EditProfileTopBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize().background(Background)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)) {
            AppSpacerV(20)

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.wrapContentSize()) {
                    val placeholder = R.drawable.baseline_account_circle_24
                    val imageModifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)

                    if (!cropImageUri?.value.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(cropImageUri.value),
                            contentDescription = null,
                            modifier = imageModifier,
                            contentScale = ContentScale.FillBounds
                        )
                    } else {
                        val userImgUrl = userProfile?.profilePicture
                            ?.takeIf { it.isNotBlank() }
                            ?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/user/$it" }

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = userImgUrl,
                                error = painterResource(placeholder),
                                placeholder = painterResource(placeholder)
                            ),
                            contentDescription = null,
                            modifier = imageModifier,
                            contentScale = ContentScale.FillBounds
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor)
                            .clickable {
                                val currentImage = cropImageUri?.value
                                    ?: userProfile?.profilePicture
                                        ?.takeIf { it.isNotBlank() }
                                        ?.let { "https://d1dp1oh0ra5b0z.cloudfront.net/img/user/$it" }
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "current_profile_image",
                                    currentImage
                                )
                                navController.navigate(Route.ProfileImage.route)
                            }
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(
                            R.drawable.camearicon,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            AppSpacerV(30)

            // Split First Name / Last Name
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EditProfileField(
                    label = stringResource(R.string.first_name),
                    value = firstName,
                    onValueChange = {
                        firstName = it
                        if (firstName.isNotBlank()) firstNameError = null
                    },
                    modifier = Modifier.weight(1f),
                    errorMessage = firstNameError
                )

                EditProfileField(
                    label = stringResource(R.string.last_name),
                    value = lastName,
                    onValueChange = {
                        lastName = it
                        if (lastName.isNotBlank()) lastNameError = null
                    },
                    modifier = Modifier.weight(1f),
                    errorMessage = lastNameError
                )
            }

            AppSpacerV(16)

            // Email Address (Disabled style)
            EditProfileField(
                label = stringResource(R.string.email_address),
                value = email,
                onValueChange = {
                    email = it
                },
                enabled = false
            )

            AppSpacerV(16)
            // Mobile Number with Flag
            AppTextBold(textAlign = TextAlign.Left, text = stringResource(R.string.mobile_number), fontSize = 14.sp)
            AppSpacerV(8)
            val mobileBorderColor = if (mobileError != null) Color.Red else MolumulkBorderDivider
            Row(
                modifier = Modifier.fillMaxWidth().height(MolumulkDimens.SearchFieldHeight)
                    .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country flag picker for mobile number (uses the same CountryBottomSheet)
                val selectedFlagUrl = selectedMobileCountryObj?.flag

                Box(
                    modifier = Modifier.fillMaxHeight()
                        .border(1.dp, mobileBorderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .background(Color.White, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .clickable { showMobileCountrySheet = true }
                        .padding(start = 10.dp, end = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                        if (!selectedFlagUrl.isNullOrBlank()) {
                            AppSubComposeAsyncImage(
                                model = selectedFlagUrl,
                                modifier = Modifier.width(40.dp).height(30.dp).clip(RoundedCornerShape(5.dp)),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            AppImage(
                                resId = R.drawable.welcome,
                                modifier = Modifier.size(24.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        AppSpacerH(6)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                AppSpacerH(10)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(MolumulkDimens.SearchFieldHeight)
                        .border(1.dp, mobileBorderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .background(Color.White, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selectedCountryPhoneCode.isNotBlank()) {
                        AppTextRegular(
                            text = selectedCountryPhoneCode,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        AppSpacerH(8)
                    }
                    AppBasicTextField(
                        value = mobileNumber,
                        onValueChange = {
                            mobileNumber = it.filter(Char::isDigit)
                            if (mobileNumber.length >= 8) mobileError = null
                        },
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            color = TextPrimary
                        ),
                        keyboardType = KeyboardType.Number,
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                innerTextField()
                            }
                        }
                    )
                }
            }
            if (mobileError != null) {
                AppSpacerV(4)
                AppTextRegular(text = mobileError ?: "", color = Color.Red, fontSize = 12.sp)
            }

            AppSpacerV(16)

            // Country Dropdown
            AppTextBold(text = stringResource(R.string.country), fontSize = 14.sp)
            AppSpacerV(8)
            Box(modifier = Modifier.fillMaxWidth().height(MolumulkDimens.SearchFieldHeight)
                .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                .border(1.dp, MolumulkBorderDivider, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                .background(Color.White, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                .clickable { showCountrySheet = true }
                .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextRegular(text = country, fontSize = 14.sp, color = TextPrimary)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            Row (modifier = Modifier.weight(1f)){  }

            // Delete Account
            AppTextMedium(
                text = stringResource(R.string.delete_my_account),
                color = MolumulkPrimaryCoralRed,
                modifier = Modifier.fillMaxWidth().clickable(enabled = !isDeletingAccount && !isLoading) {
                    val token = TokenManager.accessToken.trim()
                    if (token.isBlank()) {
                        scope.launch { snackbarManager.showError("Please log in again") }
                        return@clickable
                    }
                    showDeleteAlert = true
                },
                textAlign = TextAlign.Center
            )

            AppSpacerV(20)

            // Custom floating success banner like Figma
            if (showSuccessBanner) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                            .background(MolumulkSuccessGreen, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        AppTextBold(
                            text = "Your profile Changes done",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Save Button
            PrimaryRoundButton(
                text = stringResource(R.string.save),
                onClick = {
                    var isValid = true
                    if (firstName.isBlank()) {
                        firstNameError = "Kindly enter a first name"
                        isValid = false
                    }
                    if (lastName.isBlank()) {
                        lastNameError = "Kindly enter a last name"
                        isValid = false
                    }
                    if (mobileNumber.isBlank()) {
                        mobileError = "Kindly enter the mobile number"
                        isValid = false
                    } else if (mobileNumber.length < 8) {
                        mobileError = "kindly enter a valid mobile number"
                        isValid = false
                    }

                    val selectedCountryId = selectedCountryObj?._id.orEmpty()
                    if (selectedCountryId.isNullOrBlank()) {
                        isValid = false
                        scope.launch { snackbarManager.showError("Please select a valid country") }
                    }
                    if (selectedCountryPhoneCode.isBlank()) {
                        isValid = false
                        scope.launch { snackbarManager.showError("Please select a valid phone code") }
                    }

                    if (isValid) {
                        isLoading = true

                        val performUpdate = {
                            val request = UpdateProfileRequest(
                                firstName = firstName.trim(),
                                lastName = lastName.trim(),
                                phoneNumber = mobileNumber.filter(Char::isDigit),
                                countryId = selectedCountryId,
                                phoneCode = selectedCountryPhoneCode,
                                phoneNumberWithoutCode = mobileNumber.filter(Char::isDigit)
                            )
                            authViewModel.updateUserProfile(
                                token = TokenManager.accessToken,
                                request = request
                            ) { result ->
                                isLoading = false
                                when (result) {
                                    is ApiResult.Success -> {
                                        scope.launch {
                                            showSuccessBanner = true
                                            delay(1500)
                                            showSuccessBanner = false
                                            navController.popBackStack()
                                        }
                                    }
                                    is ApiResult.Error -> {
                                        scope.launch {
                                            snackbarColor = Color.Red
                                            snackbarManager.showError(result.message)
                                        }
                                    }
                                }
                            }
                        }

                        val localImageUri = cropImageUri?.value
                        val needsUpload = localImageUri?.startsWith("content://") == true || 
                                          localImageUri?.startsWith("file://") == true

                        if (needsUpload) {
                            val uri = Uri.parse(localImageUri!!)
                            val imagePart = FileUtils.createProfilePicturePart(context, uri)
                            if (imagePart != null) {
                                authViewModel.uploadProfilePicture(
                                    token = TokenManager.accessToken,
                                    profilePicture = imagePart
                                ) { uploadResult ->
                                    when (uploadResult) {
                                        is ApiResult.Success -> performUpdate()
                                        is ApiResult.Error -> {
                                            isLoading = false
                                            scope.launch {
                                                snackbarColor = Color.Red
                                                snackbarManager.showError("Image upload failed: ${uploadResult.message}")
                                            }
                                        }
                                    }
                                }
                            } else {
                                isLoading = false
                                scope.launch {
                                    snackbarColor = Color.Red
                                    snackbarManager.showError("Unable to process selected image")
                                }
                            }
                        } else {
                            performUpdate()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight),
                enabled = hasChanges,
                containerColor = MolumulkPrimaryCoralRed,
                disabledContainerColor = MolumulkPrimaryCoralRed.copy(alpha = 0.5f),
                cornerRadius = MolumulkDimens.PrimaryCtaRadius
            )

            AppSpacerV(20)
        }
    }
}

private fun stripCountryCode(phoneNumber: String, phoneCode: String): String {
    val raw = phoneNumber.trim()
    val code = phoneCode.trim()
    if (raw.isBlank()) return ""
    if (code.isBlank()) return raw.filter(Char::isDigit)

    val normalizedCodeDigits = code.replace("+", "").filter(Char::isDigit)
    val rawDigits = raw.replace("+", "").filter(Char::isDigit)
    return if (rawDigits.startsWith(normalizedCodeDigits)) {
        rawDigits.removePrefix(normalizedCodeDigits)
    } else {
        rawDigits
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileTopBar(navController: NavController) {
    TopAppBar(
        title = {
            AppTextBold(text = stringResource(R.string.personal_info), fontSize = 20.sp)
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MolumulkCharcoal
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
    )
}

@Preview(showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    PropertyFinderTheme {
        EditProfileScreen(rememberNavController())
    }
}
