package com.app.propertyfinder.screen

import android.app.Activity
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.OrDivider
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.components.SocialButton
// Legacy tokens (kept for reference during migration)
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary

// =========================================================================
// NEW BRAND & UI DESIGN TOKENS (molumulk 2026 - Final Handoff v2.0)
// =========================================================================
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLogoNearBlack
import com.app.propertyfinder.ui.theme.MolumulkLogoRed
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@Composable
fun WelcomeScreen(navController: NavController) {
    val context = LocalContext.current
    val activity = context as? Activity
    val scope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()

    val webClientId = stringResource(R.string.google_client_id)
    val credentialManager = CredentialManager.create(context)

    Scaffold(
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(0.dp)) {
            // 🔹 Background Image
            Image(
                painter = painterResource(R.drawable.welcomescreen),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // 🔹 Overlay Content
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)
                .padding(start = 20.dp, end = 20.dp, bottom = 40.dp, top = 30.dp)) {

                // 🔸 Top Row
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // NEW: Brand logo dual-color styling (black "molu" + red "mulk")
                    val rawWelcome = stringResource(R.string.molumulk_welcome_to_app)
                    val moluIndex = rawWelcome.indexOf("molu")
                    val welcomeAnnotatedString = remember(rawWelcome) {
                        buildAnnotatedString {
                            if (moluIndex != -1) {
                                val prefix = rawWelcome.substring(0, moluIndex)
                                val mulkIndex = rawWelcome.indexOf("mulk", startIndex = moluIndex)
                                if (mulkIndex != -1) {
                                    val afterMulkIndex = mulkIndex + 4
                                    val suffix = if (afterMulkIndex < rawWelcome.length) rawWelcome.substring(afterMulkIndex) else ""

                                    withStyle(
                                        SpanStyle(
                                            color = MolumulkMutedSlate,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Normal,
                                            fontFamily = AppFontFamily
                                        )
                                    ) {
                                        append(prefix)
                                    }
                                    // "molu" in near black (#171717)
                                    withStyle(
                                        SpanStyle(
                                            color = MolumulkLogoNearBlack,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = AppFontFamily
                                        )
                                    ) {
                                        append("molu")
                                    }
                                    // "mulk" in brand red (#E01834)
                                    withStyle(
                                        SpanStyle(
                                            color = MolumulkLogoRed,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = AppFontFamily
                                        )
                                    ) {
                                        append("mulk")
                                    }
                                    if (suffix.isNotEmpty()) {
                                        withStyle(
                                            SpanStyle(
                                                color = MolumulkMutedSlate,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Normal,
                                                fontFamily = AppFontFamily
                                            )
                                        ) {
                                            append(suffix)
                                        }
                                    }
                                } else {
                                    append(rawWelcome)
                                }
                            } else {
                                append(rawWelcome)
                            }
                        }
                    }

                    Text(
                        text = welcomeAnnotatedString,
                        lineHeight = 16.sp
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            PrefUtils.save(Constants.TAG_LOGIN, Constants.SKIP_USER)
                            PrefUtils.save(Constants.TAG_ACCESS_TOKEN, "")
                            PrefUtils.save(Constants.TAG_REFRESH_TOKEN, "")
                            navController.navigate(Route.Home.route) {
                                popUpTo(Route.Welcome.route) { inclusive = true }
                            }
                        }
                    ) {
                        AppTextMedium(
                            text = stringResource(R.string.skip),
                            fontSize = 14.sp,
                            color = MolumulkCharcoal
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        AppIcon(
                            resId = R.drawable.ic_arrow_right,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

//                AppSpacerV(67)
//                AppTextBold( modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
//                    lineHeight = 40.sp, textAlign = TextAlign.Center,
//                    text = "Connect with", fontSize = 37.sp)
//                AppTextMedium( modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
//                    textAlign = TextAlign.Center, lineHeight = 40.sp,
//                    text = "Verified listed property", fontSize = 37.sp)

                Spacer(modifier = Modifier.weight(1f))

                // 🔸 Social Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SocialButton(
                        icon = R.drawable.ic_google,
                        text = stringResource(R.string.google),
                        backgroundColor = Color.White,
                        contentColor = MolumulkCharcoal,
                        borderColor = MolumulkBorderDivider,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
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

                    /*SocialButton(
                        icon = R.drawable.ic_apple,
                        text = stringResource(R.string.apple_id),
                        backgroundColor = Color.White,
                        contentColor = Color.Black,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val provider = OAuthProvider.newBuilder("apple.com")
                            val scopes: List<String> = listOf("email", "name")
                            provider.scopes = scopes
                            val mAuth = FirebaseAuth.getInstance()
                            
                            val pending = mAuth.pendingAuthResult
                            if (pending != null) {
                                pending.addOnSuccessListener { authResult ->
                                    Timber.d("checkPending:onSuccess:$authResult")
                                    val user = authResult.user
                                    if (user != null) {
                                        val loginId = user.uid
                                        val socialName = user.displayName ?: ""
                                        val socialEmail = user.email ?: ""
                                        
                                        Timber.d("Apple Sign-In - loginId: $loginId")
                                        Timber.d("Apple Sign-In - socialName: $socialName")
                                        Timber.d("Apple Sign-In - socialEmail: $socialEmail")
                                    } else {
                                        Toast.makeText(context, "Apple Sign-In failed: No user data received", Toast.LENGTH_SHORT).show()
                                    }
                                }.addOnFailureListener { e -> 
                                    Timber.w("checkPending:onFailure", e)
                                    Toast.makeText(context, "Apple Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Timber.d("pending: null")
                                if (activity != null) {
                                    mAuth.startActivityForSignInWithProvider(activity, provider.build())
                                        .addOnSuccessListener { authResult ->
                                            Timber.d("activitySignIn:onSuccess: ${authResult.user}")
                                            val user = authResult.user
                                            if (user != null) {
                                                val loginId = user.uid
                                                val socialName = user.displayName ?: ""
                                                val socialEmail = user.email ?: ""
                                                
                                                Timber.d("Apple Sign-In - loginId: $loginId")
                                                Timber.d("Apple Sign-In - socialName: $socialName")
                                                Timber.d("Apple Sign-In - socialEmail: $socialEmail")
                                            } else {
                                                Toast.makeText(context, "Apple Sign-In failed: No user data received", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .addOnFailureListener { e ->
                                            Timber.e("activitySignIn:onFailure: ${e.message}")
                                            Toast.makeText(context, "Apple Sign-In failed: ${e.message}", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Activity is null", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )*/
                }

                Spacer(modifier = Modifier.height(MolumulkDimens.SectionGap))

                // NEW: OrDivider with molumulk subtle divider and muted slate text
                OrDivider(
                    text = stringResource(R.string.or),
                    textColor = MolumulkMutedSlate,
                    dividerColor = MolumulkBorderDivider
                )

                Spacer(modifier = Modifier.height(MolumulkDimens.SectionGap))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    // NEW: Primary CTA with MolumulkPrimaryCoralRed (#FF5A5F)
                    SocialButton(
                        icon = R.drawable.ic_email,
                        text = stringResource(R.string.continue_with_email),
                        backgroundColor = MolumulkPrimaryCoralRed,
                        contentColor = Color.White,
                        borderColor = MolumulkPrimaryCoralRed,
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate(Route.Login.route)
                    }
                }
            }
        }

    }
}
@Preview(showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    PropertyFinderTheme {
        WelcomeScreen(navController = rememberNavController())
    }
}