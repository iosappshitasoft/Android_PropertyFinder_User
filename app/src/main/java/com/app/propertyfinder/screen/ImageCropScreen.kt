package com.app.propertyfinder.screen

import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.AuthViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.SuccessColor
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@Composable
fun ImageCropScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarManager = rememberSnackbarManager()
    var snackbarColor by remember { mutableStateOf(SuccessColor) }
    var isUploading by remember { mutableStateOf(false) }

    val currentBackStackEntry = navController.currentBackStackEntry
    val previousBackStackEntry = navController.previousBackStackEntry
    val selectedImageUri = remember(currentBackStackEntry, previousBackStackEntry) {
        currentBackStackEntry?.savedStateHandle?.get<String>("selected_image_uri")
            ?: previousBackStackEntry?.savedStateHandle?.get<String>("selected_image_uri")
    }

    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotation by remember { mutableStateOf(0f) }

    Scaffold(
        containerColor = Color.Black,
        snackbarHost = {
            AppSnackbarHost(
                hostState = snackbarManager.snackbarHostState,
                containerColor = snackbarColor
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    null,
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        if (!isUploading) navController.popBackStack()
                    }
                )

                Text(
                    text = "Crop",
                    color = Color.White,
                    fontSize = 18.sp
                )

                Icon(
                    Icons.Default.Refresh,
                    null,
                    tint = Color.White,
                    modifier = Modifier.clickable {
                        if (!isUploading) rotation += 90f
                    }
                )
            }

            // Crop Area
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp, bottom = 140.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(modifier = Modifier.size(280.dp).clip(CircleShape)) {
                    Image(
                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                translationX = offset.x
                                translationY = offset.y
                                rotationZ = rotation
                            }
                            .pointerInput(isUploading) {
                                if (!isUploading) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale *= zoom
                                        offset += pan
                                    }
                                }
                            }
                            .blur(if (isUploading) 10.dp else 0.dp)
                    )
                }
            }

            // Bottom Buttons
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.spacedBy(60.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = Color.Red,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            if (!isUploading) navController.popBackStack()
                        }
                )

                Icon(
                    Icons.Default.Photo,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )

                 IconicBox(
                    Icons.Default.Check,
                    null,
                    tint = Color.Green,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable {
                            if (selectedImageUri.isNullOrBlank()) {
                                snackbarColor = Color.Red
                                snackbarManager.showError("Please choose an image first")
                                return@clickable
                            }

                            // Just pass the local URI back to EditProfileScreen
                            try {
                                navController.getBackStackEntry(Route.EditProfile.route)
                                    .savedStateHandle["cropped_image"] = selectedImageUri
                            } catch (_: Exception) {
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("cropped_image", selectedImageUri)
                            }
                            navController.popBackStack(Route.EditProfile.route, false)
                        }
                )
            }
        }
    }
}

@Composable
fun IconicBox(icon: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, tint: Color, modifier: Modifier) {
    Icon(icon, contentDescription, tint = tint, modifier = modifier)
}