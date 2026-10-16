package com.app.propertyfinder.screen

import android.os.Parcelable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.helper.chart.PriceMarkerView
import com.app.propertyfinder.model.home.PropertyModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.CornerButton
import com.app.propertyfinder.ui.components.PropertyItem
import com.app.propertyfinder.ui.theme.Background
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.parcelize.Parcelize

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResidencePreview(){
    ResidenceDetailsScreen(navController = rememberNavController())
}

@Parcelize
data class Reviews(
    val title: String,
    val rating: Float,
    val hotel: String,
    val description: String,
    val postedDate: String,
    val postedBy: String
) : Parcelable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidenceDetailsScreen(navController: NavController){
    val KeyHighlightsItems = listOf(
        "Donec hendrerit feugiat sodales. Donec non dignissim odio.",
        "Duis auctor ante vitae purus gravida consequat.",
        "Suspendisse potenti. Nulla non turpis at nisi elementum viverra.",
        "Pellentesque habitant morbi tristique senectus",
        "Pellentesque habitant morbi tristique senectus",
        "Etiam eget consectetur nibh, ut mollis ex."
    )
    val similarTowerDemoList = listOf(
        SimilarTowerItem(
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        ),
        SimilarTowerItem(
            "https://images.unsplash.com/photo-1512453979798-5ea266f8880c",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        ), SimilarTowerItem(
            "https://media.istockphoto.com/id/2194431945/photo/eiffel-tower-silhouette-framed-by-trees-overlooking-the-seine-river.jpg?s=1024x1024&w=is&k=20&c=PpotXKIEG6sAE5vBRCV6Wqk9fWwe88Jw232synz-SVg=",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        ),
        SimilarTowerItem(
            "https://images.unsplash.com/photo-1512453979798-5ea266f8880c",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        ), SimilarTowerItem(
            "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        ),
        SimilarTowerItem(
            "https://images.unsplash.com/photo-1512453979798-5ea266f8880c",
            "Downtown Dubai",
            "Burj Khalifa",
            4.8
        )
    )
    val residenceReviewList = listOf(
        Reviews(
            "Excellent amenities",
            4.8f,
            "Sofitel Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 5 days ago by",
            "Susheel Hrishikesh"
        ),

        Reviews(
            "Good properties",
            4.7f,
            "Hilton Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 2 days ago by",
            "John"
        ),

        Reviews(
            "Excellent places",
            4.9f,
            "Taj Hotel",
            "Quisque vel odio metus. Aliquam sit amet diam aliquet arcu dignissim faucibus vel ac lorem.",
            "Posted 1 day ago by",
            "Kumar"
        )
    )
    val demoPropertyList = listOf(
        PropertyModel(
            tagType = "",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Dubai Hills Estate, Rosehill",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            false,
            "ver"
        ),
        PropertyModel(
            tagType = "",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Palm Tower Residence",
            location = "Dubai, Palm Jumeirah",
            price = "6,500,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c",
            listedDays = "",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            false,
            "ver"
        ),
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Palm Jumeirah", fontSize = 20.sp)
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
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                /*top image slider and description contend item*/
                item { TopImageSlider(navController) }
                item { About_KeyHighlights(navController) }
                items(KeyHighlightsItems) { text ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        AppTextSemiBold(text = "•", modifier = Modifier.padding(end = 8.dp))
                        AppTextMedium(text = text, fontSize = 14.sp)
                    }
                }
                item { PriceTrendsView() }
                /*Palm Jumeirah properties location*/
                item {
                    Column {
                        Column (modifier = Modifier.padding(horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally){
                            AppSpacerV(25)
                            AppDivider(color = Color.LightGray, thickness = 1.dp)
                            AppSpacerV(25)
                            AppTextSemiBold(modifier = Modifier, text = "Palm Jumeirah properties location")
                            AppSpacerV(15)
                            LocationsDropdown()
                        }
                        AppSpacerV(20)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            items(similarTowerDemoList) { item ->
                                SimilarTowersItem(item)
                            }
                        }
                        AppSpacerV(15)
                        CornerButton(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            text = stringResource(R.string.view_all),
                            onClick = {navController.navigate(Route.PropertyLocation.route)})
                        AppSpacerV(25)
                        AppDivider(color = Color.LightGray, thickness = 1.dp)
                        AppSpacerV(25)
                    }
                }
                /*Frequently asked questions section*/
                item {
                    Column {
                        Column (modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp)){
                            AppTextSemiBold(modifier = Modifier, text = stringResource(R.string.frequently_asked_questions))
                            AppSpacerV(15)
                            Column (
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                val visibleItems = faqList.take(4)   // 👈 show only 2

                                Column(modifier = Modifier.fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.White)) {

                                    visibleItems.forEachIndexed { index, faq ->
                                        ExpandableFaqItem(title = faq.title, description = faq.description)

                                        if (index != visibleItems.lastIndex) {
                                            Divider(color = Color(0xFFE0E0E0))
                                        }
                                    }
                                }

                                AppSpacerV(15)
                                CornerButton(modifier = Modifier.fillMaxWidth(), text = "View all Faq",
                                    onClick = {navController.navigate(Route.Faq.route)})
                            }
                        }
                        AppSpacerV(25)
                        AppDivider(color = Color.LightGray, thickness = 1.dp)
                        AppSpacerV(25)
                    }
                }
                /*Reviews from residents*/
                item {
                    Column {
                        Column (modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp)){
                            AppTextSemiBold(modifier = Modifier, text = stringResource(R.string.reviews_from_residents))
                            AppSpacerV(15)
                        }
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(residenceReviewList) { item ->
                                ResidenceReviewsItem(item, navController)
                            }
                        }
                        AppSpacerV(25)
                        AppDivider(color = Color.LightGray, thickness = 1.dp)
                        AppSpacerV(25)
                    }
                }
                item {
                    Column {
                        Column (modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp)){
                            AppTextSemiBold(modifier = Modifier, text = stringResource(R.string.explore_properties))
                            AppSpacerV(15)
                        }
                        LazyRow() {
                            items(demoPropertyList) { property ->
                                PropertyItem(property,"row")
                            }
                        }
                        AppSpacerV(25)
                        CornerButton(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                            text = stringResource(R.string.see_all_properties),
                            onClick = {navController.navigate(Route.PropertyProjectList.createRoute("property"))})
                        AppSpacerV(35)
                    }
                }
            }
        }
    }
}

@Composable
fun ResidenceReviewsItem(list : Reviews, navController: NavController) {

    Card(
        modifier = Modifier.width(260.dp).clickable{
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

@Composable
fun LocationsDropdown() {

    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Apartment location", "Villa location")
    var selected by remember { mutableStateOf(options[0]) }

    var textFieldWidth by remember { mutableStateOf(0) }

    Column {

        Box (modifier = Modifier.padding(horizontal = 40.dp)){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    }
                    .clip(RoundedCornerShape(99.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextSemiBold(text = selected, fontSize = 14.sp)

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = Color.White,
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldWidth.toDp() })
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selected = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PriceTrendsView() {
    Column {
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)
        Column (modifier = Modifier.padding(horizontal = 20.dp)){
            AppTextSemiBold(modifier = Modifier, text = stringResource(R.string.price_trends))
            AppSpacerV(15)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip("Rent")
                FilterChip("Apartment")
                FilterChip("Studio")
            }
            AppSpacerV(15)
            PriceChartSection()
            AppSpacerV(25)
            Card (modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(3.dp)){
                Column (modifier = Modifier.padding(3.dp)){
                    AppTextMedium(modifier = Modifier.padding(15.dp)
                        , text = "Average price")
                    KeyRowItems(text = "Studio", value = "118,000 AED/year",0,0)
                    AppDivider(color = Color.LightGray, thickness = 1.dp)
                    KeyRowItems(text = "1 Bedroom", value = "172,000 AED/year",0,0)
                    AppDivider(color = Color.LightGray, thickness = 1.dp)
                    KeyRowItems(text = "2 Bedroom", value = "250,000 AED/year",0,0)
                    AppDivider(color = Color.LightGray, thickness = 1.dp)
                    KeyRowItems(text = "3 Bedroom", value = "350,000 AED/year",0,0)
                    AppDivider(color = Color.LightGray, thickness = 1.dp)
                    KeyRowItems(text = "4 Bedroom", value = "360,500 AED/year",10,10)
                }
            }
        }
    }
}
@Composable
fun PriceChartSection() {

    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf("1 year") }

    val list = listOf("1 year", "2 year", "5 year")

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextMedium(text = "AED/Sqft")
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .clickable { expanded = true }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    AppTextMedium(selected)

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {

                    list.forEach {

                        DropdownMenuItem(
                            modifier = Modifier,
                            text = { AppTextMedium(it) },
                            onClick = {
                                selected = it
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
        AppSpacerH(12)

        PriceChartt()
    }
}
@Composable
fun PriceChartt() {
    val redColor = colorResource(R.color.splash_primary)

    AndroidView(
        modifier = Modifier.fillMaxWidth().height(260.dp),
        factory = { context ->

            val chart = LineChart(context)

            chart.description.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.legend.isEnabled = false

            val months = listOf(
                "Feb 25",
                "Apr 25",
                "Jun 25",
                "Aug 25",
                "Oct 25",
                "Dec 25"
            )

            val redValues = listOf(
                200f,
                300f,
                150f,
                380f,
                90f,
                350f
            )

            val redEntries = redValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val redSet = LineDataSet(redEntries, "").apply {

                color = redColor.toArgb()
                setCircleColor(redColor.toArgb())

                lineWidth = 2f
                circleRadius = 4f

                mode = LineDataSet.Mode.CUBIC_BEZIER

                setDrawFilled(true)

                fillDrawable =
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.red_chart_gradient
                    )

                setDrawValues(false)
            }

            chart.data = LineData(redSet)

            chart.xAxis.apply {

                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)

                granularity = 1f
                textColor = android.graphics.Color.GRAY

                valueFormatter = IndexAxisValueFormatter(months)
            }

            chart.axisLeft.apply {

                axisMinimum = 0f
                axisMaximum = 600f

                textColor = android.graphics.Color.GRAY
                setDrawGridLines(true)
                gridColor = android.graphics.Color.LTGRAY
            }

            chart.marker = PriceMarkerView(
                context,
                months,
                redValues,
                redValues,
                "residence"
            )

            chart.invalidate()

            chart
        }
    )
}
@Composable
fun About_KeyHighlights(navController: NavController) {
    Column {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            AppSpacerV(20)
            AppTextSemiBold(modifier = Modifier, text = "About Palm Jumeirah")
            AppSpacerV(10)
            AppTextMedium(text = "Palm Jumeirah is the world\"'\"s largest man-made island in Dubai," +
                    "United Arab Emirates. It spans 5.72 km² on the Arabian Gulf and is known for its unique palm tree" +
                    "shape and luxurious lifestyle. This iconic destination is home to some of the world's most luxurious" +
                    "hotels, including Atlantis The Palm, its famous aquarium and water park, the ultra-luxurious Atlantis" +
                    "The Royal, and the elegant Waldorf Astoria. Offering stunning waterfront views of both the sea and" +
                    "the Dubai skyline, Palm Jumeirah provides a scenic setting for relaxation and entertainment.\nPalm" +
                    "Jumeirah is the world's largest man-made island in Dubai, United Arab Emirates. It spans 5.72 km² on" +
                    "The Arabian Gulf and is known for its unique palm tree shape and luxurious lifestyle.")
            AppSpacerV(10)
            CornerButton(text = "View full details",
                onClick = {navController.navigate(Route.ProjectDetails.route)})
        }
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)
        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp), text = stringResource(R.string.key_highlights))
        AppSpacerV(15)
    }
}


@Composable
fun TopImageSlider(navController: NavController) {
    Column {
        PropertyImageSlider(
            images = listOf(
                "https://images.unsplash.com/photo-1605146769289-440113cc3d00?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1565402170291-8491f14678db?q=80&w=1417&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1558036117-15d82a90b9b1?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1723110994499-df46435aa4b3?q=80&w=879&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://media.istockphoto.com/id/2189820429/photo/golden-hour-modern-middle-class-neighborhood-dense-population.jpg?s=1024x1024&w=is&k=20&c=1hg-rGaK1Sw93bKdP1h1JRFgMLLj8Zq_nRvVXoegLiA=",
                "https://images.unsplash.com/photo-1531971589569-0d9370cbe1e5?q=80&w=881&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1512915922686-57c11dde9b6b?q=80&w=873&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1647579350413-a6ada4e480ed?q=80&w=1325&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1668911494509-14baf3b42fda?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://media.istockphoto.com/id/2189820429/photo/golden-hour-modern-middle-class-neighborhood-dense-population.jpg?s=1024x1024&w=is&k=20&c=1hg-rGaK1Sw93bKdP1h1JRFgMLLj8Zq_nRvVXoegLiA=",
                "https://images.unsplash.com/photo-1531971589569-0d9370cbe1e5?q=80&w=881&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1512915922686-57c11dde9b6b?q=80&w=873&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1647579350413-a6ada4e480ed?q=80&w=1325&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1668911494509-14baf3b42fda?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://media.istockphoto.com/id/2189820429/photo/golden-hour-modern-middle-class-neighborhood-dense-population.jpg?s=1024x1024&w=is&k=20&c=1hg-rGaK1Sw93bKdP1h1JRFgMLLj8Zq_nRvVXoegLiA=",
                "https://images.unsplash.com/photo-1531971589569-0d9370cbe1e5?q=80&w=881&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1512915922686-57c11dde9b6b?q=80&w=873&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1647579350413-a6ada4e480ed?q=80&w=1325&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                "https://images.unsplash.com/photo-1668911494509-14baf3b42fda?q=80&w=870&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            ), navController
        )

        Column (
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp)
        ){
            AppSpacerV(5)
            AppTextSemiBold(text = "Palm Jumeirah")
            AppSpacerV(5)

            Row (modifier = Modifier.clickable{
                navController.navigate(Route.OverallReviews.route)
            },
                verticalAlignment = Alignment.CenterVertically){
                AppRatingBar(rating = 4.8f, maxStars = 5, starSize = 15)
                AppSpacerH(4)
                AppTextLight(text = "4.8/5 based on 45 Ratings", color = colorResource(R.color.textprimary))
            }
        }
        Row (modifier = Modifier.fillMaxWidth()
            .horizontalScroll(rememberScrollState())){
            AppSpacerH(20)
            Card ( modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(99.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White),
                border = BorderStroke(0.5.dp,Color.LightGray)
            ){
                AppTextMedium(modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 13.sp,
                    text = "One of the most luxurious areas in Dubai")
            }
            AppSpacerH(10)
            Card ( modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(99.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White),
                border = BorderStroke(0.5.dp,Color.LightGray)
            ){
                AppTextMedium(modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 13.sp,
                    text = "Beachfront")
            }
            AppSpacerH(10)
            Card ( modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(99.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White),
                border = BorderStroke(0.5.dp,Color.LightGray)
            ){
                AppTextMedium(modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp), fontSize = 13.sp,
                    text = "One of the most luxurious areas in Dubai")
            }
            AppSpacerH(20)

        }
    }
}