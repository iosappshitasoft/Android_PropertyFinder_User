package com.app.propertyfinder.ui.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.R
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.ui.theme.OTPVerifiedBtn
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PrimaryColorDisable
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.TabPrimary
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



enum class OtpVerificationState {
    NORMAL,
    VERIFYING,
    SUCCESS,
    ERROR
}

@Composable
fun OtpVerificationDialog(
    otpType: String,
    email: String,
    mobileNumber: String,
    onDismiss: () -> Unit,
    onVerify: (String) -> Unit,
    onResend: (onSuccess: () -> Unit) -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        OtpVerificationContent(
            email = email,
            mobileNumber = mobileNumber,
            onVerify = onVerify,
            onResend = onResend,
            onCancel = onDismiss
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OtpVerificationDialogPreview() {
    PropertyFinderTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            OtpVerificationContent(
                email = "user@example.com",
                mobileNumber = "",
                onVerify = {},
                onResend = { onSuccess -> }
            )
        }
    }
}

@Composable
fun OtpVerificationContent(
    email: String,
    mobileNumber: String,
    onVerify: (String) -> Unit,
    onResend: (onSuccess: () -> Unit) -> Unit,
    onCancel: () -> Unit = {}
) {
    val viewModel: AuthViewModel = hiltViewModel()
    val verifyOtpState by viewModel.verifyOtpState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // ✅ Single string state — max 6 digits only, no spaces needed
    var otpValue by remember { mutableStateOf("") }

    var verificationState by remember { mutableStateOf(OtpVerificationState.NORMAL) }
    var errorMessage by remember { mutableStateOf("") }
    var isVerified by remember { mutableStateOf(false) }

    // Single hidden field captures everything — one FocusRequester
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var isFieldFocused by remember { mutableStateOf(false) }
    var tappedIndex by remember { mutableStateOf(-1) }

    // Triggers re-focus + keyboard after resend success clears the OTP fields
    var resendTrigger by remember { mutableStateOf(0) }
    LaunchedEffect(resendTrigger) {
        if (resendTrigger > 0) {
            delay(50)  // let recomposition settle
            focusManager.clearFocus(force = true)  // release focus first
            delay(50)  // small gap so Android sees a real focus change
            focusRequester.requestFocus()            // re-acquire focus
            keyboardController?.show()               // explicitly pop keyboard
        }
    }


    fun verifyOtp(otp: String) {
        verificationState = OtpVerificationState.VERIFYING
        errorMessage = ""
        val request = hashMapOf<String, Any>(
            "email" to email,
            "otp" to otp
        )
        viewModel.verifyOtp(request) { response ->
            when (response) {
                is ApiResult.Success -> {
                    verificationState = OtpVerificationState.SUCCESS
                    isVerified = true
                    coroutineScope.launch {
                        delay(1000)
                        onVerify(otp)
                    }
                }
                is ApiResult.Error -> {
                    verificationState = OtpVerificationState.ERROR
                    errorMessage = response.message
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {

            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
            ) {
                AppIcon(
                    resId = R.drawable.ic_cancel,
                    contentDescription = "Cancel",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.otpvalidimg),
                    contentDescription = null,
                    modifier = Modifier
                        .size(88.dp)
                        .padding(top = 30.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppTextSemiBold(
                    stringResource(R.string.verify_your_account),
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AppTextRegular(
                    text = stringResource(R.string.enter_the_6_digit_code_sent_to_your_email),
                    fontSize = 13.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(3.dp))

                AppTextMedium(email, color = TabPrimary)

                Spacer(modifier = Modifier.height(30.dp))

                // ─── OTP Box Row + hidden real TextField ───────────────────
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    // ✅ THE REAL INPUT — invisible, sits behind the boxes
                    // All typing AND backspace goes here — no per-box logic needed
                    BasicTextField(
                        value = otpValue,
                        onValueChange = { newVal ->
                            val filtered = newVal.filter { it.isDigit() }.take(6)
                            otpValue = filtered
                            // ✅ Auto-move focus indicator to current cursor position after type/delete
                            tappedIndex = -1  // Reset so focusedIndex falls back to otpValue.length naturally
                            if (verificationState == OtpVerificationState.ERROR) {
                                verificationState = OtpVerificationState.NORMAL
                                errorMessage = ""
                            }
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            // Make it invisible but still interactable
                            .onFocusChanged {
                                isFieldFocused = it.isFocused
                                if (!it.isFocused) tappedIndex = -1  // Reset when keyboard dismissed
                            }
                            .size(1.dp)
                            .alpha(0f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        cursorBrush = SolidColor(Color.Transparent),
                        textStyle = TextStyle(color = Color.Transparent)
                    )

                    // ✅ VISUAL BOXES — purely display, clicking opens keyboard
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(
                            space = 6.dp,
                            alignment = Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                tappedIndex = -1  // No specific box tapped — go to next empty slot
                                focusRequester.requestFocus()
                                keyboardController?.show()
                            }
                    ) {
                        repeat(6) { i ->
                            val char = otpValue.getOrNull(i)?.toString() ?: ""

                            // ✅ Determine which box should show as focused:
                            // - If user tapped a specific box → show that box as focused
                            // - If user didn't tap a specific box → show next empty slot
                            // - Only when keyboard is actually open
                            val focusedIndex = when {
                                !isFieldFocused -> -1
                                tappedIndex != -1 -> tappedIndex
                                otpValue.length == 6 -> -1      // ✅ All filled — no box highlighted
                                else -> otpValue.length         // ✅ Next empty slot
                            }
                            val isFocused = i == focusedIndex

                            // Blinking cursor animation
                            var cursorVisible by remember { mutableStateOf(true) }
                            LaunchedEffect(isFocused) {
                                if (isFocused) {
                                    while (true) {
                                        cursorVisible = true
                                        delay(500)
                                        cursorVisible = false
                                        delay(500)
                                    }
                                } else {
                                    cursorVisible = false
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .border(
                                        width = 1.dp,
                                        color = when (verificationState) {
                                            OtpVerificationState.ERROR -> PrimaryColor
                                            OtpVerificationState.SUCCESS -> OTPVerifiedBtn
                                            // ✅ Show primary border on focused box only
                                            else -> if (isFocused) PrimaryColor else Color.Gray.copy(alpha = 0.3f)
                                        },
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .background(color = Color.Transparent, shape = RoundedCornerShape(8.dp))
                                    .clickable {
                                        // ✅ Remember which box was tapped, then open keyboard
                                        tappedIndex = i
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (char.isNotEmpty()) {
                                    Text(
                                        text = char,
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.W500,
                                            textAlign = TextAlign.Center,
                                            color = when (verificationState) {
                                                OtpVerificationState.ERROR -> PrimaryColor
                                                else -> TextPrimary
                                            }
                                        )
                                    )
                                } else if (isFocused && cursorVisible) {
                                    // Blinking cursor — only in empty focused box
                                    Box(
                                        modifier = Modifier
                                            .width(1.5.dp)
                                            .height(20.dp)
                                            .background(PrimaryColor)
                                    )
                                }
                            }
                        }
                    }
                }
                // ───────────────────────────────────────────────────────────

                if (verificationState == OtpVerificationState.ERROR) {
                    Spacer(modifier = Modifier.height(8.dp))
                    AppTextRegular(
                        text = errorMessage,
                        fontSize = 12.sp,
                        color = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (otpValue.length == 6 && verificationState != OtpVerificationState.VERIFYING) {
                            verifyOtp(otpValue)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp)
                        .height(48.dp),
                    enabled = otpValue.length == 6
                            && verificationState != OtpVerificationState.VERIFYING
                            && verificationState != OtpVerificationState.SUCCESS,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            verificationState == OtpVerificationState.SUCCESS || isVerified -> OTPVerifiedBtn
                            else -> PrimaryColor
                        },
                        disabledContainerColor = if (isVerified) OTPVerifiedBtn else PrimaryColorDisable
                    ),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        when (verificationState) {
                            OtpVerificationState.VERIFYING -> {
                                Text(stringResource(R.string.verifying), color = Color.White)
                            }
                            OtpVerificationState.SUCCESS -> {
                                AppIcon(
                                    resId = R.drawable.otpverified,
                                    contentDescription = "Verified",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.verified), color = Color.White)
                            }
                            else -> {
                                Text(
                                    stringResource(R.string.verify),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(25.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 30.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextRegular(
                        text = stringResource(R.string.didnt_receive_email),
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    AppSpacerH(8)
                    AppTextSemiBold(
                        text = stringResource(R.string.resend_it),
                        fontSize = 14.sp,
                        color = TabPrimary,
                        modifier = Modifier.clickable {
                            onResend {
                                // API success → clear OTP fields and reset state
                                otpValue = ""
                                verificationState = OtpVerificationState.NORMAL
                                errorMessage = ""
                                resendTrigger++  // trigger keyboard re-focus
                            }
                        }
                    )
                }
            }
        }
    }
}

// Helper extension — add this at bottom of file or in a utils file
fun Modifier.alpha(alpha: Float): Modifier = this.then(
    Modifier.graphicsLayer { this.alpha = alpha }
)