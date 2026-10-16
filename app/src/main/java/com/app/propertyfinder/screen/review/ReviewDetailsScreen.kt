package com.app.propertyfinder.screen.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.screen.Reviews
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewDetailPreview(){
    ReviewDetailsScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailsScreen(navController: NavController) {
    val reviewObj = navController.previousBackStackEntry
        ?.savedStateHandle?.get<Reviews>("reviewObj")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
                .padding(horizontal = 20.dp, vertical = 15.dp)
                .verticalScroll(rememberScrollState())
        ) {
            AppTextSemiBold(text = "Palm Jumeirah’s reviews")
            AppSpacerV(10)
            AppTextMedium(text = reviewObj?.title ?: "Title")
            AppSpacerV(7)
            Row (verticalAlignment = Alignment.CenterVertically){
                AppRatingBar(rating = reviewObj?.rating ?: 0f, maxStars = 5, starSize = 15)
                AppSpacerH(4)
                AppTextLight(text = "${reviewObj?.rating ?: "0"}/5", color = colorResource(R.color.textprimary))
            }
            AppSpacerV(15)
            Row {
                AppTextLight(text = reviewObj?.postedDate ?: "Posted 5 days ago by")
                AppSpacerH(5)
                AppTextLight(text = reviewObj?.postedBy ?: "Susheel Hrishikesh", color = colorResource(R.color.textprimary))
            }
            AppSpacerV(20)
            AppTextRegular(text = reviewObj?.description ?: "Description", lineHeight = 25.sp )
        }
    }
}