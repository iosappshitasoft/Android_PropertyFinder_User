package com.app.propertyfinder.screen

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterRequest
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.api.viewmodel.PushNotificationViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.DeviceInfoUtils
import com.app.propertyfinder.utils.PrefUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.cos
import kotlin.math.sin

//old Animated
/*
@Composable
fun SplashScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val pushNotificationViewModel: PushNotificationViewModel = hiltViewModel()
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val refreshTokenState by viewModel.refreshTokenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()

        propertyViewModel.getMasterData("supportedurls") { response ->
            when (response) {
                is ApiResult.Success -> {
                    response.body.data?.supportedUrls?.let { urls ->
                        PrefUtils.save(Constants.TAG_URL_PROJECT_IMG, urls.projectUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROJECT_VID, urls.projectUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROJECT_DOC, urls.projectUrl?.doc.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROPERTY_IMG, urls.propertyUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROPERTY_VID, urls.propertyUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AGENT_IMG, urls.agentUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AGENCY_IMG, urls.agencyUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_DEVELOPER_IMG, urls.developerUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_USER_IMG, urls.userUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AMENITY_IMG, urls.amenityUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_IMG, urls.awardUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_VID, urls.awardUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_DOC, urls.awardUrl?.doc.orEmpty())
                    }
                }
                is ApiResult.Error -> {
                    Log.e("SUPPORTED_URLS", response.message)
                }
            }
        }
    }
    // Animation States
    var isLogoVisible by remember { mutableStateOf(false) }
    val searchScale = remember { Animatable(0f) }
    val searchAngle = remember { Animatable(0f) }
    val searchRadius = remember { Animatable(0f) }
    
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 500
        ), label = "animated"
    )

    // Gradient Colors
    val gradientColors = listOf(
        Color(0xFFEA3934),
        Color(0xFFCC5826),
        Color(0xFFEA3934)
    )

    LaunchedEffect(key1 = true) {
        // Step 1: Search Icon Pops In
        launch {
             searchScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        // Ease radius out to start circling
        searchRadius.animateTo(30f, tween(500))
        
        // Step 2: "Searching" Motion (Circular)
        // Rotate 720 degrees (2 full circles)
        searchAngle.animateTo(
            targetValue = 720f,
            animationSpec = tween(1200, easing = LinearEasing)
        )
        
        // Bring radius back to center
        searchRadius.animateTo(0f, tween(300))

        // Step 3: Transition to Main Logo
        isLogoVisible = true
        startAnimation = true // Trigger the alpha animation for the logo
        
        // Step 4: Wait and Navigate
        delay(1500L)
        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
        if (loginType == Constants.LOGIN_USER) {
            viewModel.getProfileData(TokenManager.accessToken){ response ->
                when (response) {
                    is ApiResult.Success -> {
                        val user = response.body.data?.user
                        val fullName = listOfNotNull(user?.firstName, user?.lastName)
                            .joinToString(" ")
                            .trim()
                        PrefUtils.save(Constants.TAG_USER_ID, user?.id.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_EMAIL, user?.email.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_PHONE, user?.phoneNumber.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_NAME, fullName)
                        PrefUtils.save(
                            Constants.TAG_HAS_UNREAD_NOTIFICATIONS,
                            response.body.data?.hasUnreadNotifications ?: false
                        )
                        val bearerToken = "Bearer ${TokenManager.accessToken}"
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }

                    is ApiResult.Error -> {
                        val message = response.message
                        if (message == "Invalid or expired token"){
                            val request = hashMapOf<String, Any>(
                                "refreshToken" to TokenManager.refreshToken
                            )
                            viewModel.refreshToken(request){ response ->
                                when (response) {
                                    is ApiResult.Success -> {
                                        val newAccessToken = refreshTokenState.data?.data?.tokens?.accessToken.toString()
                                        val newRefreshToken = refreshTokenState.data?.data?.tokens?.refreshToken.toString()
                                        TokenManager.accessToken = newAccessToken
                                        TokenManager.refreshToken = newRefreshToken
                                        PrefUtils.save(Constants.TAG_ACCESS_TOKEN, newAccessToken)
                                        PrefUtils.save(Constants.TAG_REFRESH_TOKEN, newRefreshToken)

                                        val bearerToken = "Bearer $newAccessToken"
                                        FirebaseMessaging.getInstance().token
                                            .addOnSuccessListener { fcmToken ->
                                                if (fcmToken.isNullOrBlank()) return@addOnSuccessListener
                                                pushNotificationViewModel.registerPush(
                                                    bearerToken = bearerToken,
                                                    request = PushNotificationRegisterRequest(
                                                        token = fcmToken,
                                                        deviceId = DeviceInfoUtils.getDeviceId(context),
                                                        deviceModel = DeviceInfoUtils.getDeviceModel(),
                                                        deviceVersion = DeviceInfoUtils.getDeviceVersion()
                                                    )
                                                )
                                            }
                                        navController.navigate(Route.Home.route) {
                                            popUpTo(Route.Splash.route) { inclusive = true }
                                        }
                                    }

                                    is ApiResult.Error -> {
                                        val message = response.message
                                        navController.navigate(Route.Welcome.route) { popUpTo(Route.Splash.route) { inclusive = true } }
                                    }
                                }
                            }
                        } else {
                            navController.navigate(Route.Welcome.route) {
                                popUpTo(Route.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            }
        } else if (loginType == Constants.SKIP_USER) {
            navController.navigate(Route.Home.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Route.Welcome.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        }
    }

    // Calculate Offset for Circular Motion
    val rad = searchRadius.value
    val angRad = Math.toRadians(searchAngle.value.toDouble())
    val offX = (rad * cos(angRad)).toFloat()
    val offY = (rad * sin(angRad)).toFloat()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = gradientColors)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (!isLogoVisible) {
            // SEARCH / FINDER ICON
            Image(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "Searching...",
                modifier = Modifier
                    .size(80.dp)
                    .scale(searchScale.value)
                    .offset(x = offX.dp, y = offY.dp),
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White)
            )
        } else {
            // FINAL BRAND LOGO
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Using ic_splash_logo (transparent bg, white path) to overlay cleanly on the gradient
                Image(
                    painter = painterResource(id = R.drawable.ic_splash_logo),
                    contentDescription = "Splash Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alphaAnim.value),
                    contentScale = ContentScale.Fit // Fit safely within screen
                )
            }
        }
    }
}

@Composable
@Preview(showSystemUi = true)
fun SplashScreenPreview() {
    PropertyFinderTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color(0xFFEA3934),
                            Color(0xFFCC5826),
                            Color(0xFFEA3934)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_splash_logo),
                contentDescription = "Logo"
            )
        }
    }
}
*/

//new white screen with App icon
@Composable
fun SplashScreen(navController: NavController) {
    val viewModel: AuthViewModel = hiltViewModel()
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val pushNotificationViewModel: PushNotificationViewModel = hiltViewModel()
    val context = LocalContext.current
    val profileState by viewModel.profileState.collectAsStateWithLifecycle()
    val refreshTokenState by viewModel.refreshTokenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()

        propertyViewModel.getMasterData("supportedurls") { response ->
            when (response) {
                is ApiResult.Success -> {
                    response.body.data?.supportedUrls?.let { urls ->
                        PrefUtils.save(Constants.TAG_URL_PROJECT_IMG, urls.projectUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROJECT_VID, urls.projectUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROJECT_DOC, urls.projectUrl?.doc.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROPERTY_IMG, urls.propertyUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_PROPERTY_VID, urls.propertyUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AGENT_IMG, urls.agentUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AGENCY_IMG, urls.agencyUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_DEVELOPER_IMG, urls.developerUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_USER_IMG, urls.userUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AMENITY_IMG, urls.amenityUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_IMG, urls.awardUrl?.img.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_VID, urls.awardUrl?.vid.orEmpty())
                        PrefUtils.save(Constants.TAG_URL_AWARD_DOC, urls.awardUrl?.doc.orEmpty())
                    }
                }
                is ApiResult.Error -> {
                    Log.e("SUPPORTED_URLS", response.message)
                }
            }
        }
    }

    // Smooth Fade-In Effect
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAnim = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "fade_in_logo"
    )

    LaunchedEffect(key1 = true) {
        startAnimation = true
        delay(1500L) // Display time before navigation

        val loginType = PrefUtils.getString(Constants.TAG_LOGIN, "").orEmpty()
        if (loginType == Constants.LOGIN_USER) {
            viewModel.getProfileData(TokenManager.accessToken) { response ->
                when (response) {
                    is ApiResult.Success -> {
                        val user = response.body.data?.user
                        val fullName = listOfNotNull(user?.firstName, user?.lastName)
                            .joinToString(" ")
                            .trim()
                        PrefUtils.save(Constants.TAG_USER_ID, user?.id.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_EMAIL, user?.email.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_PHONE, user?.phoneNumber.orEmpty())
                        PrefUtils.save(Constants.TAG_USER_NAME, fullName)
                        PrefUtils.save(
                            Constants.TAG_HAS_UNREAD_NOTIFICATIONS,
                            response.body.data?.hasUnreadNotifications ?: false
                        )
                        navController.navigate(Route.Home.route) {
                            popUpTo(Route.Splash.route) { inclusive = true }
                        }
                    }

                    is ApiResult.Error -> {
                        val message = response.message
                        if (message == "Invalid or expired token") {
                            val request = hashMapOf<String, Any>(
                                "refreshToken" to TokenManager.refreshToken
                            )
                            viewModel.refreshToken(request) { response ->
                                when (response) {
                                    is ApiResult.Success -> {
                                        val newAccessToken = refreshTokenState.data?.data?.tokens?.accessToken.toString()
                                        val newRefreshToken = refreshTokenState.data?.data?.tokens?.refreshToken.toString()
                                        TokenManager.accessToken = newAccessToken
                                        TokenManager.refreshToken = newRefreshToken
                                        PrefUtils.save(Constants.TAG_ACCESS_TOKEN, newAccessToken)
                                        PrefUtils.save(Constants.TAG_REFRESH_TOKEN, newRefreshToken)

                                        val bearerToken = "Bearer $newAccessToken"
                                        FirebaseMessaging.getInstance().token
                                            .addOnSuccessListener { fcmToken ->
                                                if (fcmToken.isNullOrBlank()) return@addOnSuccessListener
                                                pushNotificationViewModel.registerPush(
                                                    bearerToken = bearerToken,
                                                    request = PushNotificationRegisterRequest(
                                                        token = fcmToken,
                                                        deviceId = DeviceInfoUtils.getDeviceId(context),
                                                        deviceModel = DeviceInfoUtils.getDeviceModel(),
                                                        deviceVersion = DeviceInfoUtils.getDeviceVersion()
                                                    )
                                                )
                                            }
                                        navController.navigate(Route.Home.route) {
                                            popUpTo(Route.Splash.route) { inclusive = true }
                                        }
                                    }

                                    is ApiResult.Error -> {
                                        navController.navigate(Route.Welcome.route) {
                                            popUpTo(Route.Splash.route) { inclusive = true }
                                        }
                                    }
                                }
                            }
                        } else {
                            navController.navigate(Route.Welcome.route) {
                                popUpTo(Route.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            }
        } else if (loginType == Constants.SKIP_USER) {
            navController.navigate(Route.Home.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        } else {
            navController.navigate(Route.Welcome.route) {
                popUpTo(Route.Splash.route) { inclusive = true }
            }
        }
    }

    // Screen Layout with White Background & Centered Logo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher), // Or R.drawable.ic_splash_logo
            contentDescription = "App Logo",
            modifier = Modifier
                .size(120.dp) // Adjust size as desired
                .alpha(alphaAnim.value),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
@Preview(showSystemUi = true)
fun SplashScreenPreview() {
    PropertyFinderTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.ic_launcher),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
        }
    }
}