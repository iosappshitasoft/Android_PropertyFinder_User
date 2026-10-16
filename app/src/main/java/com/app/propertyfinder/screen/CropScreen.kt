package com.app.propertyfinder.screen

import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextMedium
import com.canhub.cropper.CropImageView

//import com.nicos.imagepickerandroid.PickSingleImage
//import com.nicos.imagepickerandroid.ScaleBitmapModel



@Composable
fun CropScreen(navController: NavController) {
    val context = LocalContext.current

    var cropImageView: CropImageView? by remember { mutableStateOf(null) }
    
    // Stable URI retrieval: Check current, then previous backstack entry
    val uriString = remember(navController.currentBackStackEntry, navController.previousBackStackEntry) {
        navController.currentBackStackEntry?.savedStateHandle?.get<String>("temp_image_uri")
            ?: navController.previousBackStackEntry?.savedStateHandle?.get<String>("temp_image_uri")
    }
    
    val imageUri = remember(uriString) { 
        if (uriString != null) Uri.parse(uriString) else null
    }

    var isImageLoading by remember { mutableStateOf(false) }
    var loadingError by remember { mutableStateOf<String?>(null) }

    // Simplified: We handle loading inside AndroidView factory to ensure it only happens once
    // when the view is actually ready.

    // Listener for loading completion
    DisposableEffect(cropImageView) {
        val listener = CropImageView.OnSetImageUriCompleteListener { _, _, error ->
            android.util.Log.d("CropScreen", "Image load complete. Error: $error")
            if (error != null) {
                error.printStackTrace()
            }
            ContextCompat.getMainExecutor(context).execute {
                isImageLoading = false
                if (error != null) {
                    loadingError = error.message ?: "Failed to load image"
                } else {
                    loadingError = null
                }
            }
        }
        cropImageView?.setOnSetImageUriCompleteListener(listener)
        onDispose {
            cropImageView?.setOnSetImageUriCompleteListener(null)
        }
    }


    Scaffold(
        containerColor = Color.Black
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        imageVector = Icons.Default.Undo,
                        contentDescription = "Rotate Left",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).clickable { cropImageView?.rotateImage(-90) }
                    )
                    
                    AppTextMedium(text = "Crop", color = Color.White, fontSize = 20.sp)

                    Icon(
                        imageVector = Icons.Default.Redo,
                        contentDescription = "Rotate Right",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp).clickable { cropImageView?.rotateImage(90) }
                    )
                }

                // Cropper View / Loading / Empty
                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    if (imageUri == null) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(64.dp))
                            AppTextMedium(text = "No image selected", color = Color.Gray)
                        }
                    } else {
                        AndroidView(
                            factory = { ctx ->
                                CropImageView(ctx).apply {
                                    this.cropShape = CropImageView.CropShape.OVAL
                                    this.guidelines = CropImageView.Guidelines.ON
                                    this.setAspectRatio(1, 1)
                                    this.setFixedAspectRatio(true)
                                    
                                    // Load image immediately when view is created
                                    android.util.Log.d("CropScreen", "AndroidView factory: Loading $imageUri")
                                    isImageLoading = true
                                    loadingError = null
                                    this.setImageUriAsync(imageUri)
                                    
                                    cropImageView = this
                                }
                            },
                            modifier = Modifier.fillMaxSize(),
                            update = { _ -> }
                        )
                    }

                    if (isImageLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }

                    if (loadingError != null) {
                        Column(
                            modifier = Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(48.dp))
                            AppSpacerV(8)
                            AppTextMedium(text = loadingError!!, color = Color.Red, fontSize = 14.sp)
                        }
                    }
                }

                // Bottom Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp, start = 24.dp, end = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Cancel (Red X)
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp).clickable { navController.popBackStack() }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Upload Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF202020))
                                .clickable {
                                    //pickSingleImage()
                                           },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.FileUpload, contentDescription = "Upload", tint = Color.White)
                        }

                        // Camera Button
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF202020))
                                .clickable { navController.navigate(Route.TakePhoto.route) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White)
                        }
                    }

                    // Confirm (Green Tick)
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = Color.Green,
                        modifier = Modifier.size(32.dp).clickable {
                             cropImageView?.setOnCropImageCompleteListener { _, result ->
                                if (result.isSuccessful) {
                                    val croppedUri = result.uriContent
                                    ContextCompat.getMainExecutor(context).execute {
                                        try {
                                            // Deliver to EditProfile
                                            navController.getBackStackEntry(Route.EditProfile.route)
                                                .savedStateHandle.set("profile_image_uri", croppedUri.toString())
                                            
                                            // Pop back to EditProfile
                                            navController.popBackStack(Route.EditProfile.route, false)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            // Fallback pop if entry not found
                                            navController.popBackStack()
                                        }
                                    }
                                } else {
                                    result.error?.printStackTrace()
                                }
                            }
                            cropImageView?.croppedImageAsync()
                        }
                    )
                }
            }
        }
    }
}
