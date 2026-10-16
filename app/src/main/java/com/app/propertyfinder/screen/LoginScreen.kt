package com.app.propertyfinder.screen

import android.app.Activity
import android.util.Base64
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterRequest
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.api.viewmodel.PushNotificationViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppEditText
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppLoader
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.ForgotPasswordRow
import com.app.propertyfinder.ui.components.OrDivider
import com.app.propertyfinder.ui.components.OtpVerificationDialog
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.SocialLoginRow
import com.app.propertyfinder.ui.components.TermsText
import com.app.propertyfinder.ui.theme.PropertyFinderTheme

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff v2.0)
// =========================================================================
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.DeviceInfoUtils
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.CustomToastMessage
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppTextRegular
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import org.json.JSONObject

@Composable
fun LoginScreen(navController: NavController, isSignUp: Boolean = false) {

    val viewModel: AuthViewModel = hiltViewModel()

    val pushNotificationViewModel: PushNotificationViewModel = hiltViewModel()
    val context = LocalContext.current
    val activity = context as? Activity
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
    val sendOtpState by viewModel.sendOtpState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as Activity).window
        val controller = WindowCompat.getInsetsController(window, view)

        // false = white status bar icons/text
        controller.isAppearanceLightStatusBars = false

        // false = white navigation bar icons
        controller.isAppearanceLightNavigationBars = false

        onDispose {
            // restore if needed
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }


    remember { mutableStateOf(isSignUp) }
    var isSignUp by remember { mutableStateOf(false) }
    var emailOrPhone by remember { mutableStateOf("") }
//    var emailOrPhone by remember { mutableStateOf("user4@example.com") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }
    val isForgotPasswordState = remember { mutableStateOf(false) }
    val showOtpDialog = remember { mutableStateOf(false) }
    val shownewaccountOtpDialog = remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val webClientId = stringResource(R.string.google_client_id)
    val credentialManager = CredentialManager.create(context)

    // String resources for error messages (resolved at composable level)
    val errInvalidEmail = "kindly enter a valid email address"
    val errInvalidCredentials = stringResource(R.string.err_invalid_credentials)
    val errPasswordTooShort = "Password should be at least 8 characters"
    val errEmailRequired = "Kindly enter the email"
    val errPasswordRequired = "Kindly enter the password"
    stringResource(R.string.login_signup_success)

    // Back button functionality
    fun handleBackPress() {
        isForgotPasswordState.value = false
        showOtpDialog.value = false
        shownewaccountOtpDialog.value = false
        emailOrPhone = ""
        password = ""
        rememberMe = false
        isSignUp = false
//        navController.popBackStack()
        navController.navigate(Route.Welcome.route)
    }
    if (loginState.isLoading || sendOtpState.isLoading || registerState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center) {
            AppLoadingDialog()
        }
    }

    LaunchedEffect(registerState.data, registerState.errorMessage) {
        registerState.data?.let {
            CustomToastMessage.showSuccess(context, it.message ?: "Register success")
            shownewaccountOtpDialog.value = true
            viewModel.clearRegisterState()
        }
        registerState.errorMessage?.let { err ->
            CustomToastMessage.showError(context, err)
            viewModel.clearRegisterState()
        }
    }
    if (showOtpDialog.value) {
        OtpVerificationDialog(
            otpType = "forgot",
            email = emailOrPhone,
            mobileNumber = "",
            onDismiss = { showOtpDialog.value = false },
            onVerify = { otp ->
                showOtpDialog.value = false
                val handle = navController.currentBackStackEntry?.savedStateHandle
                handle?.set("email", emailOrPhone)
                navController.navigate(Route.Change_Password.route)
            },
            onResend = { onSuccess ->
                val request = hashMapOf<String, Any>(
                    "email" to emailOrPhone,
                )
                viewModel.sendOtp(request) { response ->
                    when (response) {
                        is ApiResult.Success -> {
                            CustomToastMessage.showSuccess(context, response.body.message ?: "OTP sent successfully")
                            onSuccess()
                        }
                        is ApiResult.Error -> {
                            CustomToastMessage.showError(context, response.message)
                        }
                    }
                }
            }
        )
    }

    if (shownewaccountOtpDialog.value) {
        OtpVerificationDialog(
            otpType = "newAcc",
            email = emailOrPhone,
            mobileNumber = "",
            onDismiss = { shownewaccountOtpDialog.value = false },
            onVerify = { otp ->
                shownewaccountOtpDialog.value = false
                val handle = navController.currentBackStackEntry?.savedStateHandle
                handle?.set("email", emailOrPhone)
                navController.navigate(Route.Contact_Details.route)
            },
            onResend = { onSuccess -> }
        )
    }


    Scaffold(
        snackbarHost = {}
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppImage(
                resId = R.drawable.login_bg,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().padding(
                start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                top = paddingValues.calculateTopPadding(),
                end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                bottom = 0.dp
            )) {
            fun isValidEmail(email: String): Boolean {
                return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
            }

            fun isValidPassword(value: String): Boolean {
                return value.length >= 8
            }

            fun isValidForgotPasswordEmail(value: String): Boolean {
                return isValidEmail(value)
            }
                Row(modifier = Modifier.align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 20.dp)
                    .clickable {
                        navController.navigate(Route.Welcome.route )
                               },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppIcon(
                        resId = R.drawable.ic_chevron_left,
                        modifier = Modifier.size(20.dp)
                    )
                    AppSpacerH(8)
                    AppTextRegular(
                        text = stringResource(R.string.back),
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }
                Row(modifier = Modifier.align(Alignment.TopStart)
                    .padding(top = 40.dp, start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppImage(
                        resId = R.mipmap.ic_launcher,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, top = 140.dp)) {
                    AppTextBold(
                        text = if (isForgotPasswordState.value)
                            stringResource(R.string.didyouforgotyoupass)
                        else if (isSignUp)
                            stringResource(R.string.createyouaccount)
                        else
                            stringResource(R.string.welcomeback),
                        color = Color.White,
                    )
                    AppSpacerV(5)
                }

                // 🔹 White Card Content
                Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).background(color = Color.White,
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .fillMaxHeight(0.78f)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            start = 20.dp,
                            top = 20.dp,
                            end = 20.dp,
                            bottom = if (isForgotPasswordState.value) {
                                310.dp + paddingValues.calculateBottomPadding()
                            } else {
                                20.dp + paddingValues.calculateBottomPadding()
                            }
                        )) {

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isForgotPasswordState.value) {
                        LoginTabs(
                            isSignUp = isSignUp,
                            onSignInClick = { isSignUp = false },
                            onSignUpClick = { isSignUp = true }
                        )

                        AppSpacerV(24)
                    }
                    AppTextBold(
                        fontSize = 14.sp,
                        text = stringResource(R.string.email_address),
                        color = MolumulkCharcoal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    AppEditText(
                        hint = stringResource(R.string.enter_email_address),
                        value = emailOrPhone,
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                        onValueChange = {
                            emailOrPhone = it
                            emailError = null
                        },
                        errorMessage = emailError,
                        maxLength = 30
                    )

                    if (!isForgotPasswordState.value) {
                        Spacer(modifier = Modifier.height(16.dp))
                        AppTextBold(
                            fontSize = 14.sp,
                            text = stringResource(R.string.password),
                            color = MolumulkCharcoal
                        )
                        AppSpacerV(8)
                        AppEditText(
                            value = password,
                            onValueChange = {
                                password = it
                                passwordError = null
                            },
                            hint = stringResource(R.string.enter_password),
                            keyboardType = KeyboardType.Email,
                            isPassword = true,
                            errorMessage = passwordError
                        )

                        AppSpacerV(12)

                        if (!isSignUp) {
                            ForgotPasswordRow(
                                onForgotPasswordClick = {
                                    isForgotPasswordState.value = true
                                },
                                rememberMeChecked = rememberMe,
                                onRememberMeChange = { rememberMe = it }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // NEW: Primary CTA with MolumulkPrimaryCoralRed (#FF5A5F) & MolumulkDimens.PrimaryCtaRadius
                    PrimaryRoundButton(
                        text = if (isForgotPasswordState.value) stringResource(R.string.continue_text) else if (isSignUp) stringResource(R.string.continue_text) else stringResource(R.string.login_account),
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MolumulkPrimaryCoralRed,
                        cornerRadius = MolumulkDimens.PrimaryCtaRadius
                    ) {
                        if (isForgotPasswordState.value) {
                            emailError = when {
                                emailOrPhone.isBlank() -> {
                                    keyboardController?.hide()
                                    errEmailRequired
                                }
                                !isValidForgotPasswordEmail(emailOrPhone) -> {
                                    keyboardController?.hide()
                                    errInvalidEmail
                                }
                                else -> null
                            }


                            if (emailError != null) {
                                CustomToastMessage.showError(context, emailError!!)
                            } else {
                                val request = hashMapOf<String, Any>(
                                    "email" to emailOrPhone,
                                )
                                viewModel.sendOtp(request){ response ->
                                    when (response) {
                                        is ApiResult.Success -> {
                                            showOtpDialog.value = true
                                            CustomToastMessage.showSuccess(context, "success")
                                        }

                                        is ApiResult.Error -> {
                                            val message = response.message
                                            CustomToastMessage.showError(context, message)
                                        }
                                    }
                                }
                            }
                        } else {
                            // Validate login/signup form
                            emailError = when {
                                emailOrPhone.isBlank() -> errEmailRequired
                                !isValidEmail(emailOrPhone) -> errInvalidEmail
                                else -> null
                            }

                            if (!isForgotPasswordState.value) {
                                passwordError = when {
                                    password.isBlank() -> errPasswordRequired
                                    !isValidPassword(password) -> errPasswordTooShort
                                    else -> null
                                }
                            }

                            when {
                                emailError != null || passwordError != null -> {
                                    val errorToDisplay = emailError ?: passwordError
                                    errorToDisplay?.let { CustomToastMessage.showError(context, it) }
                                }

                                else -> {
                                    if (isSignUp) {
                                        val handle = navController.currentBackStackEntry?.savedStateHandle
                                        val firstName = emailOrPhone.substringBefore("@")
                                        handle?.set("email", emailOrPhone)
                                        handle?.set("password", password)
                                        handle?.set("firstName", firstName)
                                        viewModel.register(
                                            firstName = firstName,
                                            lastName = "user",
                                            email = emailOrPhone,
                                            password = password,
                                            phone = "",
                                            auth = "email"
                                        )
                                    } else {
//                                        navController.navigate(Route.Home.route)
                                        viewModel.login(
                                            email = emailOrPhone,
                                            phone = "",
                                            password = password,
                                            rememberMe = rememberMe
                                        ) { response ->

                                            when (response) {
                                                is ApiResult.Success -> {
                                                    val fullResponse = response.body
                                                    val code = response.code
                                                    val loginData = response.body.data
                                                    val accessToken = loginData?.tokens?.accessToken.toString()
                                                    val refreshToken = loginData?.tokens?.refreshToken.toString()
                                                    PrefUtils.save(Constants.TAG_ACCESS_TOKEN, accessToken)
                                                    PrefUtils.save(Constants.TAG_REFRESH_TOKEN, refreshToken)
                                                    PrefUtils.save(Constants.TAG_LOGIN, Constants.LOGIN_USER)

                                                    // After login success: register FCM token + device info to backend.
                                                    FirebaseMessaging.getInstance().token
                                                        .addOnSuccessListener { fcmToken ->
                                                            if (fcmToken.isNullOrBlank()) return@addOnSuccessListener
                                                            pushNotificationViewModel.registerPush(
                                                                bearerToken = "Bearer $accessToken",
                                                                request = PushNotificationRegisterRequest(
                                                                    token = fcmToken,
                                                                    deviceId = DeviceInfoUtils.getDeviceId(context),
                                                                    deviceModel = DeviceInfoUtils.getDeviceModel(),
                                                                    deviceVersion = DeviceInfoUtils.getDeviceVersion()
                                                                )
                                                            )
                                                        }

                                                    CustomToastMessage.showSuccess(context, "success")
                                                    coroutineScope.launch {
                                                        delay(1000)
                                                        navController.navigate(Route.Home.route) {
                                                            popUpTo(Route.Welcome.route) { inclusive = true }
                                                        }
                                                    }
                                                }

                                                is ApiResult.Error -> {
                                                    val message = response.message
                                                    val code = response.code
                                                    val rawBody = response.rawBody
                                                    CustomToastMessage.showError(context, message)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!isForgotPasswordState.value) {
                        AppSpacerV(24)

                        // NEW: OrDivider with molumulk subtle divider and muted slate text
                        OrDivider(
                            text = stringResource(R.string.or),
                            textColor = MolumulkMutedSlate,
                            dividerColor = MolumulkBorderDivider
                        )

                        AppSpacerV(16)

                        SocialLoginRow(
                            onGoogleClick = {
                                coroutineScope.launch {
                                    try {
                                        val signInWithGoogleOption = GetSignInWithGoogleOption
                                            .Builder(serverClientId = webClientId).build()

                                        val request = GetCredentialRequest.Builder()
                                            .addCredentialOption(signInWithGoogleOption)
                                            .build()

                                        val result = credentialManager.getCredential(
                                            request = request,
                                            context = activity ?: return@launch
                                        )
                                        when (val credential = result.credential) {
                                            is CustomCredential -> {
                                                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                                    val displayName = googleIdTokenCredential.displayName ?: ""
                                                    val firstName = googleIdTokenCredential.givenName ?: displayName
                                                    val lastName = googleIdTokenCredential.familyName ?: "user"
                                                    val idToken = googleIdTokenCredential.idToken
                                                    val payload = idToken.split(".")[1]
                                                    val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
                                                    val decoded = String(decodedBytes)
                                                    val json = JSONObject(decoded)
                                                    val email = json.optString("email")
                                                    val phoneNumber = googleIdTokenCredential.phoneNumber
                                                    val bundleData = googleIdTokenCredential.data
                                                    val userJsonObj = JSONObject()
                                                    for (key in bundleData.keySet()) {
                                                        val v = bundleData.get(key)
                                                        when (v) {
                                                            null -> userJsonObj.put(key, "")
                                                            is String -> userJsonObj.put(key, v)
                                                            is Boolean -> userJsonObj.put(key, v)
                                                            is Int -> userJsonObj.put(key, v)
                                                            is Long -> userJsonObj.put(key, v)
                                                            else -> userJsonObj.put(key, v.toString())
                                                        }
                                                    }

                                                    navController.currentBackStackEntry?.savedStateHandle?.apply {
                                                        set("contactFlow", "google")
                                                        set("email", email)
                                                        set("firstName", firstName)
                                                        set("lastName", lastName)
                                                        set("idToken", idToken)
                                                        set("googlePhone", phoneNumber.orEmpty())
                                                        set("userJson", userJsonObj.toString())
                                                    }
                                                    navController.navigate(Route.Contact_Details.route)

                                                } else {
                                                    Toast.makeText(context, "Unexpected credential type", Toast.LENGTH_LONG).show()
                                                }
                                            }

                                            else -> {
                                                Toast.makeText(context, "Unexpected credential response", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    } catch (e: GetCredentialCancellationException) {
                                        Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
                                    } catch (e: NoCredentialException) {
                                        Toast.makeText(context, "No Google account found on this device", Toast.LENGTH_LONG).show()
                                    } catch (e: GetCredentialException) {
                                        Toast.makeText(context, "Credential error: ${e.message}", Toast.LENGTH_LONG).show()
                                    } catch (e: GoogleIdTokenParsingException) {
                                        Toast.makeText(context, "Token parsing failed. Update googleid library.", Toast.LENGTH_LONG).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Google login failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        )

                        if (isSignUp) {
                            AppSpacerV(20)
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                TermsText()
                            }
                            AppSpacerV(10)
                        }
                    }

                    }
                    if (isForgotPasswordState.value) {
                        Row(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter)
                            .padding(bottom = paddingValues.calculateBottomPadding()).height(300.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            AppImage(
                                resId = R.drawable.forgotpass,
                                contentDescription = stringResource(R.string.forgot_password_desc),
                                modifier = Modifier.height(300.dp).wrapContentWidth(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }
                }
            }
        }
    }
}

    @Preview(showSystemUi = true)
    @Composable
    fun AuthScreenPreview() {
        PropertyFinderTheme {
            LoginScreen(navController = rememberNavController())
        }
    }
