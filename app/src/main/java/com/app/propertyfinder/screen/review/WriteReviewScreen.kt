package com.app.propertyfinder.screen.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.app.propertyfinder.screen.ReportSuccessPopup
import com.app.propertyfinder.ui.components.AppEditText
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppValueChangeRatingBar
import com.app.propertyfinder.ui.theme.Background


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Write_a_ReviewPreview(){
    WriteReviewScreen(navController = rememberNavController())
}

data class DemoRatingItem(
    val title: String,
    val rating: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteReviewScreen(navController: NavController) {

    var showSuccessPopup by remember { mutableStateOf(false) }

    var ratingList by remember {
        mutableStateOf(
            listOf(
                DemoRatingItem("Maintenance", 0f),
                DemoRatingItem("Staff/Security", 0f),
                DemoRatingItem("Gym/pool", 0f),
                DemoRatingItem("Children friendly", 0f),
                DemoRatingItem("Noise", 0f),
                DemoRatingItem("Traffic", 0f),
                DemoRatingItem("Guest parking", 0f)
            )
        )
    }
    if (showSuccessPopup) {
        ReportSuccessPopup(
            message = "Review sent successful",
            onDismiss = { showSuccessPopup = false }
        )
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.write_a_review), fontSize = 20.sp)
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
                actions = {
                    IconButton(onClick = {showSuccessPopup = true}) {
                        Icon(
                            painter = painterResource(R.drawable.ic_report_send),
                            contentDescription = "send",
                            tint = colorResource(R.color.splash_primary)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        Column( modifier = Modifier.fillMaxSize()
            .background(colorResource(R.color.screen_bg_color))
            .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(innerPadding) ,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    TextFieldsSection()
                }
                itemsIndexed(ratingList) { index, item ->
                    RatingRowItem(
                        title = item.title,
                        rating = item.rating,
                        onRatingChanged = { newRating ->

                            val updatedList = ratingList.toMutableList()
                            updatedList[index] = updatedList[index].copy(rating = newRating)

                            ratingList = updatedList
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TextFieldsSection(){

    var firstNameTf by remember { mutableStateOf("") }
    var emailTf by remember { mutableStateOf("") }
    var reviewForTf by remember { mutableStateOf("Palm Jumeirah") }
    var reviewTitleTf by remember { mutableStateOf("") }
    var reviewContentTf by remember { mutableStateOf("") }

    Column( modifier = Modifier.fillMaxSize()) {
        AppTextMedium(text = stringResource(R.string.first_name))
        AppSpacerV(10)
        AppEditText(
            hint = stringResource(R.string.enter_first_name),
            value = firstNameTf,
            onValueChange = { firstNameTf = it }
        )

        AppSpacerV(20)
        Row (verticalAlignment = Alignment.Bottom){
            AppTextMedium(text = stringResource(R.string.email_address))
            AppSpacerH(5)
            AppTextLight(text = stringResource(R.string.optional), fontSize = 11.sp)
        }

        AppSpacerV(10)
        AppEditText(
            hint = stringResource(R.string.enter_email_address),
            value = emailTf,
            onValueChange = { emailTf = it }
        )

        AppSpacerV(20)
        AppTextMedium(text = stringResource(R.string.review_for))
        AppSpacerV(10)
        AppEditText(
            hint = "",
            value = reviewForTf,
            onValueChange = { reviewForTf = it }
        )

        AppSpacerV(20)
        AppTextMedium(text = stringResource(R.string.write_a_review))
        AppSpacerV(10)
        AppEditText(
            hint = stringResource(R.string.enter_title),
            value = reviewTitleTf,
            onValueChange = { reviewTitleTf = it }
        )
        AppSpacerV(7)
        AppEditText(
            modifier = Modifier.height(150.dp),
            hint = stringResource(R.string.write_your_review_here),
            value = reviewContentTf,
            onValueChange = { reviewTitleTf = it }
        )
    }
}
@Composable
fun RatingRowItem(title: String, rating: Float, onRatingChanged: (Float) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppTextMedium(text = title, fontSize = 14.sp)
        AppValueChangeRatingBar(rating = rating, onRatingChanged = onRatingChanged)
    }
}