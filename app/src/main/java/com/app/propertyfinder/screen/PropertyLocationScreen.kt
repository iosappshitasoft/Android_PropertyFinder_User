package com.app.propertyfinder.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.googlemap_helper.ProjectLocationMapView
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.utils.CustomToastMessage

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PropertyLocationPreview(){
    PropertyLocationScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyLocationScreen(navController: NavController){
    val context = LocalContext.current
    val lat = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Double>("project_location_lat") ?: 25.2058
    val lng = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<Double>("project_location_lng") ?: 55.2718
    val developerPhone = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("developer_phone_number").orEmpty().trim()
    val phoneErrorMessage = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("phone_error_message") ?: "Developer phone number is not available"

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = stringResource(R.string.property_location),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Background)
        ) {
            ProjectLocationMapView(
                lat = lat,
                lng = lng,
                interactive = true,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Button(
                    onClick = {
                        when {
                            developerPhone.isEmpty() -> {
                                CustomToastMessage.showError(
                                    context,
                                    phoneErrorMessage
                                )
                            }
                            else -> {
                                val normalizedPhone = developerPhone.removePrefix("+")
                                val waUri = Uri.parse("https://wa.me/$normalizedPhone")
                                val waIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                    setPackage("com.whatsapp")
                                }
                                val waBusinessIntent = Intent(Intent.ACTION_VIEW, waUri).apply {
                                    setPackage("com.whatsapp.w4b")
                                }
                                try {
                                    context.startActivity(waIntent)
                                } catch (e1: Exception) {
                                    try {
                                        context.startActivity(waBusinessIntent)
                                    } catch (e2: Exception) {
                                        CustomToastMessage.showError(
                                            context,
                                            "WhatsApp is not installed on this device"
                                        )
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.light_green))
                ) {
                    AppIcon(
                        resId = R.drawable.whatapp,
                        contentDescription = "whatsapp",
                        modifier = Modifier.size(18.dp),
                        tint = Color.White
                    )
                    AppSpacerH(6)
                    AppTextMedium(text = "Whatsapp", color = Color.White)
                }
            }
        }
    }
}