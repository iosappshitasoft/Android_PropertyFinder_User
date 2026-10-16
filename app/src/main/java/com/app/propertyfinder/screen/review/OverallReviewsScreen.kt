package com.app.propertyfinder.screen.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.screen.ResidenceReviewsItem
import com.app.propertyfinder.screen.Reviews
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.CornerButton
import com.app.propertyfinder.ui.components.PrimaryRoundButton
import com.app.propertyfinder.ui.theme.Background


data class RatingItem(
    val title: String,
    val rating: Int
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun OverallReviewPreview(){
    OverallReviewsScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverallReviewsScreen(navController: NavController) {

    val demoReviewList = listOf(
        Reviews(
            "Excellent amenities",
            4.8f,
            "Sofitel Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 5 days ago by",
            "Susheel Hrishikesh"
        ),

        Reviews(
            "Good properties",
            4.7f,
            "Hilton Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 2 days ago by",
            "John"
        ),

        Reviews(
            "Excellent places",
            4.9f,
            "Taj Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 1 day ago by",
            "Kumar"
        ),

        Reviews(
            "Excellent places",
            4.9f,
            "Taj Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 1 day ago by",
            "Kumar"
        ),

        Reviews(
            "Excellent places",
            4.9f,
            "Taj Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 1 day ago by",
            "Kumar"
        ),

        Reviews(
            "Excellent places",
            4.9f,
            "Taj Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 1 day ago by",
            "Kumar"
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Reviews for palm jumeirah", fontSize = 20.sp,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            modifier = Modifier.fillMaxSize().padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                item {
                    TopOverallRatingView(navController)
                }
                item {
                    Column (){
                        AppDivider(modifier = Modifier.padding(vertical = 20.dp),
                            thickness = 1.dp, color = Color.LightGray)

                        Column (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)){
                            AppTextMedium(text = stringResource(R.string.review),
                                fontSize = 15.sp)
                            AppSpacerV(10)
                        }
                    }
                }
                items(demoReviewList) { item ->
                    ReviewsCardItem(item,navController)
                }
            }
        }
    }
}


@Composable
fun TopOverallRatingView(navController: NavController) {
    val ratingList = listOf(
        RatingItem("Maintenance", 5),
        RatingItem("Staff / Security", 3),
        RatingItem("Gym / pool", 2),
        RatingItem("Children friendly", 4),
        RatingItem("Noise", 3),
        RatingItem("Traffic", 1),
        RatingItem("Guest parking", 4)
    )
    Column (modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)){
        AppTextMedium(text = "Overall rating for Palm Jumeirah",
            fontSize = 15.sp)
        AppSpacerV(10)
        Row (modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically){
            AppTextBold(text = "4.0", fontSize = 40.sp)
            AppSpacerH(10)
            Column {
                AppRatingBar(rating = 4f, maxStars = 5, starSize = 20)
                AppSpacerV(3)
                AppTextLight(text = "4.8/5 based on 45 Ratings",
                    color = colorResource(R.color.textprimary))
            }
        }
        AppSpacerV(15)

        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Column(modifier = Modifier.padding(16.dp)) {
                ratingList.forEach { item ->
                    RatingProgressItem(title = item.title, rating = item.rating)
                }
            }
        }

        AppSpacerV(15)
        PrimaryRoundButton(text = stringResource(R.string.write_a_review),
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(Route.WriteReview.route)
            })

    }
}

@Composable
fun RatingProgressItem(title: String, rating: Int, maxRating: Int = 5) {

    val progress = rating.toFloat() / maxRating

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AppTextLight(modifier = Modifier.width(130.dp),
            text = title, fontSize = 13.sp)

        Box(modifier = Modifier.weight(1f).height(6.dp),
            contentAlignment = Alignment.CenterStart
        ) {

            // Background bar
            Box(modifier = Modifier.fillMaxWidth().height(6.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.LightGray)
            )

            // Progress bar
            Box(modifier = Modifier.fillMaxWidth(progress)
                .height(4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(colorResource(R.color.green))
            )
        }

        AppSpacerH(10)

        AppTextMedium(text = rating.toString())
    }
}


@Composable
fun ReviewsCardItem(list : Reviews,navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp)
            .clickable{
                navController.currentBackStackEntry?.savedStateHandle?.set("reviewObj",list)
                navController.navigate(Route.ReviewDetails.route)},
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AppTextMedium(text = list.title, fontSize = 15.sp)
            AppSpacerV(5)
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppRatingBar(rating = list.rating, maxStars = 5, starSize = 15)
                AppSpacerH(5)
                AppTextMedium("4.8/5")
            }
            AppSpacerV(12)
            AppTextMedium(text = list.hotel, color = colorResource(R.color.splash_primary))
            AppSpacerV(5)
            AppTextMedium(text = list.description, maxLines = 4)
            AppSpacerV(5)
            Text(
                text = "Read more...",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            AppSpacerV(12)
            Row (){
                AppTextLight(text = list.postedDate, fontSize = 11.sp)
                AppSpacerH(3)
                AppTextSemiBold(text = list.postedBy,
                    overflow = TextOverflow.Ellipsis,fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}