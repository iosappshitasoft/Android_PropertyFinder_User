package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppEditText
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppLoader
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSpacerWeight
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.SuccessColor
import com.app.propertyfinder.utils.rememberSnackbarManager
import com.app.propertyfinder.utils.SnackbarType
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(navController: NavController) {

    val viewModel: AuthViewModel = hiltViewModel()
    val context = LocalContext.current
    val resetPasswordState by viewModel.resetPasswordState.collectAsStateWithLifecycle()
    val snackbarManager = rememberSnackbarManager()

    val handle = navController.previousBackStackEntry?.savedStateHandle
    val email = handle?.get<String>("email").orEmpty()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var snackbarColor by remember { mutableStateOf(PrimaryColor) }

    if (resetPasswordState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center) {
            AppLoader()
        }
    }
    val systemUiController = rememberSystemUiController()
    DisposableEffect(Unit) {
        // Apply for this screen
        systemUiController.setStatusBarColor(color = Color.Black, darkIcons = false)
        systemUiController.setNavigationBarColor(color = Color.Black, darkIcons = false)
        onDispose {
            // Restore when leaving screen
            systemUiController.setStatusBarColor(color = Background, darkIcons = true)
            systemUiController.setNavigationBarColor(color = Background, darkIcons = true)
        }
    }

    Scaffold(
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
        ) {
            // Background
            AppImage(
                resId = R.drawable.login_bg,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo placeholder
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent
                    ) {
                        AppImage(
                            resId = R.drawable.login_logo,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clickable { navController.popBackStack() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIcon(
                            resId = R.drawable.ic_chevron_left,
                            modifier = Modifier.size(20.dp)
                        )
                        AppSpacerH(4)
                        AppTextMedium(
                            stringResource(R.string.back),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                AppSpacerV(20)

                // Reset Password Title
                AppTextBold(
                    text = stringResource(R.string.reset_password_title),
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                AppSpacerV(40)

                // White Card Content
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 30.dp)
                    ) {
                        AppTextMedium(stringResource(R.string.new_password))
                        AppSpacerV(8)
                        AppEditText(
                            value = newPassword,
                            onValueChange = {
                                if (!isSuccess && !isLoading) {
                                    newPassword = it
                                    errorMessage = null
                                }
                            },
                            hint = stringResource(R.string.enter_new_password),
                            isPassword = true,
                            isError = errorMessage != null && newPassword.isEmpty()
                        )

                        AppSpacerV(24)

                        AppTextMedium(stringResource(R.string.confirm_password))
                        AppSpacerV(8)
                        AppEditText(
                            value = confirmPassword,
                            onValueChange = {
                                if (!isSuccess && !isLoading) {
                                    confirmPassword = it
                                    errorMessage = null
                                }
                            },
                            hint = stringResource(R.string.re_enter_password),
                            isPassword = true,
                            isError = errorMessage != null && (confirmPassword.isEmpty() || confirmPassword != newPassword)
                        )

                        AppSpacerWeight(1f)

                        // Smooth Button state change
                        PrimaryRoundButton(
                            text = if (isSuccess) "Successfully password changed" else stringResource(R.string.reset_password_btn),
                            modifier = Modifier.fillMaxWidth(),
                            cornerRadius = 26.dp,
                            containerColor = if (isSuccess) SuccessColor else PrimaryColor,
                            trailingIcon = if (isSuccess) Icons.Default.Check else null
                        ) {
                            if (isSuccess || isLoading) return@PrimaryRoundButton

                            // Validation Logic
                            when {
                                newPassword.isEmpty() || confirmPassword.isEmpty() -> {
                                    errorMessage = context.getString(R.string.err_password_empty)
                                    snackbarManager.showError(errorMessage!!)
                                }
                                newPassword != confirmPassword -> {
                                    errorMessage = context.getString(R.string.err_password_match)
                                    snackbarManager.showError(errorMessage!!)
                                }
                                else -> {
                                    val request = hashMapOf<String, Any>(
                                        "email" to email,
                                        "phoneNumber" to "",
                                        "newPassword" to newPassword,
                                    )
                                    viewModel.resetPassword(request){ response ->
                                        when (response) {
                                            is ApiResult.Success -> {
                                                errorMessage = null
                                                isSuccess = true
                                            }

                                            is ApiResult.Error -> {
                                                val message = response.message
                                                snackbarManager.showError(message)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        AppSpacerV(24)

                        AppTextBold(
                            text = stringResource(R.string.back_to_login),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable { if (!isLoading) navController.popBackStack() },
                            fontSize = 16.sp,
                            color = Color.Black
                        )

                        AppSpacerV(20)
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ChangePasswordScreenPreview() {
    PropertyFinderTheme {
        ChangePasswordScreen(navController = rememberNavController())
    }
}
