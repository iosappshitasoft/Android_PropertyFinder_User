package com.app.propertyfinder.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.theme.MolumulkDimens


@Composable
fun ProfileImageScreen(navController: NavController) {
    val currentBackStackEntry = navController.currentBackStackEntry
    val previousBackStackEntry = navController.previousBackStackEntry
    val currentProfileImage = remember(currentBackStackEntry, previousBackStackEntry) {
        currentBackStackEntry?.savedStateHandle?.get<String>("current_profile_image")
            ?: previousBackStackEntry?.savedStateHandle?.get<String>("current_profile_image")
    }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                navController.currentBackStackEntry?.savedStateHandle?.set("selected_image_uri", it.toString())
                navController.navigate(Route.ImageCrop.route)
            }
        }

    Scaffold(containerColor = Color.Black) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navController.popBackStack() }
                )
                AppSpacerH(16)
                AppTextMedium(text = "Profile image", color = Color.White, fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Central Image
            Image(
                painter = rememberAsyncImagePainter(
                    model = currentProfileImage,
                    error = painterResource(id = R.drawable.welcome),
                    placeholder = painterResource(id = R.drawable.welcome)
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Upload Button
                Button(
                    onClick = {
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(MolumulkDimens.PrimaryButtonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202020)),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                ) {
                    Icon(
                        imageVector = Icons.Default.FileUpload,
                        contentDescription = null,
                        tint = Color.White
                    )
                    AppSpacerH(8)
                    Text("Upload", color = Color.White)
                }

                // Take Photo Button
                Button(
                    onClick = {
                        navController.currentBackStackEntry?.savedStateHandle?.set(
                            "current_profile_image",
                            currentProfileImage
                        )
                        navController.navigate(Route.TakePhoto.route)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(MolumulkDimens.PrimaryButtonHeight),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF202020)),
                    shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White
                    )
                    AppSpacerH(8)
                    Text("Take photo", color = Color.White)
                }
            }

            AppSpacerV(20)
        }
    }
}
