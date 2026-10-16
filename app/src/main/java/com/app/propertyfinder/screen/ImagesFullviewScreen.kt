package com.app.propertyfinder.screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.PropertyDetailVideoPlayer
import com.app.propertyfinder.ui.theme.Background
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FullViewPreview(){
    ImagesFullviewScreen(navController = rememberNavController())
}
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ImagesFullviewScreen(navController: NavController) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(Unit) {

        // Apply for this screen
        systemUiController.setStatusBarColor(
            color = Color.Black,
            darkIcons = false
        )

        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = false
        )

        onDispose {
            // Restore when leaving screen
            systemUiController.setStatusBarColor(
                color = Background,
                darkIcons = true
            )

            systemUiController.setNavigationBarColor(
                color = Background,
                darkIcons = true
            )
        }
    }

    val scope = rememberCoroutineScope()
    val images = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<List<String>>("images") ?: emptyList()
    val selectedIndex = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<Int>("selectedImageIndex") ?: 0
    val imageTitle = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("imageTitle").orEmpty().ifBlank { "Images" }

    val videoTourFullUrl = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("videoTourFullUrl")
        ?.takeIf { it.isNotBlank() }
    val openVideoFirst = navController
        .previousBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("openVideoFirst") == true

    val hasVideo = !videoTourFullUrl.isNullOrBlank()
    val imageCount = images.size
    val videoPageIndex = if (hasVideo) imageCount else -1
    val totalPages = imageCount + if (hasVideo) 1 else 0

    val initialPage = when {
        totalPages <= 0 -> 0
        openVideoFirst && hasVideo -> videoPageIndex
        imageCount == 0 && hasVideo -> 0
        else -> selectedIndex.coerceIn(0, (imageCount - 1).coerceAtLeast(0))
    }

    if (totalPages <= 0) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        AppTextBold(text = imageTitle, fontSize = 20.sp, color = Color.White)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
                )
            }
        ) { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text("No media", color = Color.White, fontSize = 14.sp)
            }
        }
        return
    }

    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, totalPages - 1),
        pageCount = { totalPages }
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = imageTitle, fontSize = 20.sp, color = Color.White)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()
            .padding(innerPadding)
            .background(Color.Black)
            .systemBarsPadding()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Image Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { page ->
                    when {
                        hasVideo && page == videoPageIndex ->
                            PropertyDetailVideoPlayer(
                                videoUrl = videoTourFullUrl!!,
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                autoPlay = openVideoFirst
                            )
                        page < imageCount ->
                            AppSubComposeAsyncImage(
                                model = images[page],
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                contentScale = ContentScale.Fit
                            )
                        else ->
                            Spacer(Modifier.fillMaxSize())
                    }
                }
            }

            // Bottom Controls
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // Left Button
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == 0)
                                Color.Gray.copy(alpha = 0.3f)
                            else
                                Color.Gray.copy(alpha = 0.6f)
                        )
                        .clickable(enabled = pagerState.currentPage != 0) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.White
                    )
                }

                // Counter
                Text(
                    text = "${pagerState.currentPage + 1}/$totalPages",
                    color = Color.White,
                    fontSize = 14.sp
                )

                // Right Button
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == images.lastIndex)
                                Color.Gray.copy(alpha = 0.3f)
                            else
                                Color.Gray.copy(alpha = 0.6f)
                        )
                        .clickable(enabled = pagerState.currentPage < totalPages - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        }
    }
}