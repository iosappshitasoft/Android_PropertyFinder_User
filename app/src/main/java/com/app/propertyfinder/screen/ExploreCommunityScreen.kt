package com.app.propertyfinder.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.helper.chart.PriceMarkerView
import com.app.propertyfinder.model.home.PropertyModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppRatingBar
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.components.CornerButton
import com.app.propertyfinder.ui.components.PropertyItem
import com.app.propertyfinder.ui.theme.Background
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.min

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewExplore(){
    ExploreCommunityScreen(navController = rememberNavController())
}

data class SimilarTowerItem(
    val image: String,
    val location: String,
    val title: String,
    val rating: Double
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreCommunityScreen(navController: NavController) {
    data class PropertyType(
        val title: String,
        val count: String
    )
    val propertyList = listOf(
        PropertyType("Studio", "14 Available"),
        PropertyType("1 Bed apartment", "10 Available"),
        PropertyType("2 Beds apartment", "8 Available"),
        PropertyType("3 Beds apartment", "5 Available"),
        PropertyType("4 Beds apartment", "3 Available"),
        PropertyType("Villa", "2 Available")
    )

    val demoPropertyList = listOf(
        PropertyModel(
            tagType = "damac",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "OFF-PLAN",
            deliveryDate = "Delivery date : Q1 2029",
            saleStarted = "",
            paymentPlan = "",
            title = "Rosehill by Emaar",
            location = "Dubai, Dubai Hills Estate, Rosehill",
            price = "Coming soon",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "",
            isWhatsapp = true,
            bed = "",
            bath = "",
            sqft = "",
            true,
            "ver"
        ),
        PropertyModel(
            tagType = "emaar",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "OFF-PLAN",
            deliveryDate = "Delivery date : Q1 2029",
            saleStarted = "Sale started : 21 march 2025",
            paymentPlan = "2 payment plan available",
            title = "Palm Tower Residence",
            location = "Dubai, Palm Jumeirah",
            price = "6,500,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c",
            listedDays = "",
            isWhatsapp = true,
            bed = "",
            bath = "",
            sqft = "",
            true,
            "ver"
        ),
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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Seven palm", fontSize = 20.sp)
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
        ) {
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {
                /*top image slider and description contend item*/
                item {
                    TopImageSliderView(navController)
                }

                items(propertyList.take(4)) { item ->
                    PropertyTypeItem(title = item.title, count = item.count)
                    AppSpacerV(10)
                }
                item {
                    KeyInformation(navController)
                }
                items(demoPropertyList) { property ->
                    PropertyItem(property,"column")
                }
                item {
                    NearestPoints_PriceInsightsView(navController)
                }
                item {
                    NearbyPrices_PropertiesAvailableAreaView()
                }
                item {
                    FrequentlyAskedQuestions(navController)
                }
                item {
                    AppSpacerV(15)
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        items(similarTowerDemoList) { item ->
                            SimilarTowersItem(item)
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        AppSpacerV(15)
                        CornerButton(text = "View all areas",
                            onClick = {navController.navigate(Route.PropertyLocation.route)})
                        AppSpacerV(30)
                    }
                }
            }
        }
    }
}

@Composable
fun SimilarTowersItem( item: SimilarTowerItem) {
    Box( modifier = Modifier
        .width(220.dp)
        .height(300.dp)
        .clip(RoundedCornerShape(10.dp))){
        AppSubComposeAsyncImage(
            model = item.image,
        )

        Column (modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 8.dp, end = 8.dp)){
            AppTextMedium(modifier = Modifier
                .clip(RoundedCornerShape(99.dp))
                .background(Color.Black.copy(alpha = 0.7f))
                .padding(horizontal = 15.dp, vertical = 4.dp),
                text = item.location, color = Color.White)
        }

        Column (modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .padding(bottom = 15.dp, start = 15.dp),){
            AppTextMedium(modifier = Modifier,
                text = item.title, color = Color.White)
            AppSpacerV(5)
            Row (verticalAlignment = Alignment.CenterVertically){
                AppRatingBar(rating = 4.8f, maxStars = 5, starSize = 15)
                AppTextLight(text = "4.8/5", color = Color.White)
            }
        }
    }
}

@Composable
fun FrequentlyAskedQuestions(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.frequently_asked_questions))
        AppSpacerV(10)
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            val visibleItems = faqList.take(4)   // 👈 show only 2

            Column(modifier = Modifier
                .fillMaxWidth()
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
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.similar_towers))
    }
}

@Composable
fun NearbyPrices_PropertiesAvailableAreaView() {
    Column (modifier = Modifier.fillMaxWidth()){
        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.nearby_prices))
        AppSpacerV(5)
        AppTextLight(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.average_price))
        PropertyPriceChart()

        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.properties_available))
        AppSpacerV(5)
        AppTextLight(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.bedrooms_properties))
        RoundedTopBarChart()
    }
}
//Nearby price chart
@Composable
fun PropertyPriceChart() {
    data class PropertyPrice(
        val name: String,
        val value: Int
    )
    val propertyData = listOf(
        PropertyPrice("Seven Palm", 280000),
        PropertyPrice("Cheval Maison\nThe Palm Dubai", 250000),
        PropertyPrice("NH Collection\nDubai The Palm", 220000),
        PropertyPrice("Dukes The Palm", 150000),
        PropertyPrice("Oceana Aegean", 80000)
    )
    val maxValue = 300000

    val gridLabels = listOf("100k", "150k", "200k", "250k", "300k")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        // Top scale labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.width(120.dp))

            gridLabels.forEach {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box {

            // Grid lines
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((propertyData.size * 48).dp)
                    .padding(start = 120.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                repeat(gridLabels.size) {

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(Color(0xFFE0E0E0))
                    )
                }
            }

            // Bars
            Column {

                propertyData.forEachIndexed { index, item ->

                    ChartRow(
                        label = item.name,
                        value = item.value,
                        maxValue = maxValue,
                        highlight = index == 0
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                }
            }
        }
    }
}
@Composable
fun ChartRow(label: String, value: Int, maxValue: Int, highlight: Boolean) {

    val percentage = value.toFloat() / maxValue

    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(text = label, modifier = Modifier.width(120.dp), fontSize = 12.sp)

        Box(modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(percentage)
                    .height(28.dp)
                    .clip(RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp))
                    .background(
                        if (highlight)
                            colorResource(R.color.splash_primary)
                        else
                            colorResource(R.color.light_red)
                    )
            )
        }
    }
}
data class ChartItem(
    val label: String,
    val value: Float
)
//Properties available in the area chart
@Composable
fun RoundedTopBarChart() {

    val maxValue = 600f

    val chartData = listOf(
        ChartItem("0.5M", 520f),
        ChartItem("1M", 270f),
        ChartItem("1.5M", 170f),
        ChartItem("2M", 80f),
        ChartItem("2.5M", 20f)
    )

    val yLabels = listOf(0,100,200,300,400,500,600)

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)) {

        Box {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(start = 40.dp, bottom = 30.dp)
            ) {

                val width = size.width
                val height = size.height

                val barWidth = width / 10f
                val spacing = width / chartData.size

                // grid lines
                yLabels.forEach { value ->

                    val y = height - (value / maxValue * height)

                    drawLine(
                        color = Color(0xFFE0E0E0),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 2f
                    )
                }

                chartData.forEachIndexed { index, item ->

                    val barHeight = (item.value / maxValue) * height

                    val left = index * spacing + spacing/2 - barWidth/2
                    val top = height - barHeight

                    val radius = min(barWidth/2, barHeight/2)

                    val path = Path().apply {

                        moveTo(left, height)

                        lineTo(left, top + radius)

                        quadraticBezierTo(
                            left,
                            top,
                            left + radius,
                            top
                        )

                        lineTo(left + barWidth - radius, top)

                        quadraticBezierTo(
                            left + barWidth,
                            top,
                            left + barWidth,
                            top + radius
                        )

                        lineTo(left + barWidth, height)

                        close()
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF7C84D6)
                    )
                }
            }

            // Y axis labels
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .height(230.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                yLabels.reversed().forEach {

                    Text(
                        text = it.toString(),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // bottom labels
        Row(
            modifier = Modifier.padding(start = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            chartData.forEach {

                Text(
                    text = it.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NearestPoints_PriceInsightsView(navController: NavController) {
    Column (modifier = Modifier.fillMaxWidth()){
        AppSpacerV(15)
        CornerButton(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            text = stringResource(R.string.view_all),
            onClick = {navController.navigate(Route.PropertyProjectList.createRoute("project"))})

        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.nearest_points))
        AppSpacerV(5)
        AppTextLight(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.all_distances))
        AppSpacerV(10)
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(3.dp)){
            Column (modifier = Modifier.padding(3.dp)){
                AppTextLight(modifier = Modifier.padding(start = 15.dp,
                    end = 15.dp, top = 10.dp, bottom = 5.dp)
                    , text = "Restaurants")
                NearestPointsItem(name = "San Beach", value = "205 m")
                NearestPointsItem(name = "Ula Dubai", value = "207 m")
                NearestPointsItem(name = "Replay Sports Bar", value = "217 m")
                Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    AppTextMedium(text = stringResource(R.string.read_more))
                    AppSpacerH(5)
                    AppImage(resId = R.drawable.arrow_down, modifier = Modifier.size(10.dp))
                }
            }
        }
        AppSpacerV(10)
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(3.dp)){
            Column (modifier = Modifier.padding(3.dp)){
                AppTextLight(modifier = Modifier.padding(start = 15.dp,
                    end = 15.dp, top = 10.dp, bottom = 5.dp)
                    , text = "Groceries")
                NearestPointsItem(name = "Kimgen And KB Cosmatic l.l.c", value = "205 m")
                NearestPointsItem(name = "Daiso Japan", value = "207 m")
                NearestPointsItem(name = "Ceylon Master Gems fze", value = "217 m")
                Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    AppTextMedium(text = stringResource(R.string.read_more))
                    AppSpacerH(5)
                    AppImage(resId = R.drawable.arrow_down, modifier = Modifier.size(10.dp))
                }
            }
        }
        AppSpacerV(10)
        Card (modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(3.dp)){
            Column (modifier = Modifier.padding(3.dp)){
                AppTextLight(modifier = Modifier.padding(start = 15.dp,
                    end = 15.dp, top = 10.dp, bottom = 5.dp)
                    , text = "Schools")
                NearestPointsItem(name = "San Beach", value = "205 m")
                NearestPointsItem(name = "Ula Dubai", value = "207 m")
                NearestPointsItem(name = "Replay Sports Bar", value = "217 m")
                Row(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    AppTextMedium(text = stringResource(R.string.read_more))
                    AppSpacerH(5)
                    AppImage(resId = R.drawable.arrow_down, modifier = Modifier.size(10.dp))
                }
            }
        }
        AppSpacerV(15)
        CornerButton(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
            text = stringResource(R.string.view_all),
            onClick = { navController.navigate(Route.NearestPoints.route) })
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        // Price Insights
        Column (modifier = Modifier.padding(horizontal = 20.dp)) {
            AppTextSemiBold( text = stringResource(R.string.price_insights))
            AppSpacerV(10)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip("Rent")
                FilterChip("Apartment")
                FilterChip("Studio")
            }
            AppSpacerV(20)

            AppTextMedium(text = "AED/sqft" )
            AppSpacerV(15)
            PriceChart()
            AppSpacerV(15)

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
                    KeyRowItems(text = "1 Bedroom", value = "172,000 AED/year",10,10)
                    AppDivider(color = Color.LightGray, thickness = 1.dp)
                    KeyRowItems(text = "2 Bedroom", value = "250,000 AED/year",10,10)
                }
            }
        }
        AppSpacerV(25)
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)
    }
}
@Composable
fun NearestPointsItem(name: String,value: String) {
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){

        AppTextMedium(text = name, fontSize = 13.sp )
        AppTextMedium(text = value, fontSize = 14.sp )
    }
}

@Composable
fun FilterChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextMedium(text = text, color = Color.White, fontSize = 12.sp)
            AppSpacerH(3)
            AppIcon(resId = R.drawable.arrow_down,
                modifier = Modifier.size(10.dp),
                tint = Color.White
            )
        }
    }
}
    @Composable
fun PriceChart() {
    val redColor = colorResource(R.color.splash_primary)
    val blueColor = colorResource(R.color.darkBlue)

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        factory = { context ->
            val chart = LineChart(context)

            chart.description.isEnabled = false
            chart.axisRight.isEnabled = false
            chart.legend.isEnabled = false

            // months from previous months to current
            val months = listOf(
                "Feb 25",
                "Apr 25",
                "Jun 25",
                "Aug 25",
                "Oct 25",
                "Dec 25"
            )

            val redValues = listOf(200f, 230f, 310f, 420f, 390f, 320f)
            val blueValues = listOf(250f, 300f, 450f, 520f, 280f, 120f)

            val redEntries = redValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val blueEntries = blueValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }

            val redSet = LineDataSet(redEntries, "Palm").apply {

                color = redColor.toArgb()
                setCircleColor(redColor.toArgb())

                lineWidth = 2f
                circleRadius = 5f

                mode = LineDataSet.Mode.CUBIC_BEZIER

                setDrawFilled(true)

                fillDrawable =
                    ContextCompat.getDrawable(context, R.drawable.red_chart_gradient)
            }

            val blueSet = LineDataSet(blueEntries, "Abu").apply {

                color = blueColor.toArgb()
                setCircleColor(blueColor.toArgb())

                lineWidth = 2f
                circleRadius = 5f

                mode = LineDataSet.Mode.CUBIC_BEZIER

                setDrawFilled(true)

                fillDrawable =
                    ContextCompat.getDrawable(context, R.drawable.blue_chart_gradient)
            }

            chart.data = LineData(redSet, blueSet)

            // X axis months
            chart.xAxis.apply {

                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f

                valueFormatter = IndexAxisValueFormatter(months)
            }

            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.axisMaximum = 600f

            // attach popup
            chart.marker = PriceMarkerView(
                context,
                months,
                redValues,
                blueValues,
                "explore"
            )

            chart.invalidate()

            chart
        }
    )
}

@Composable
fun KeyInformation(navController: NavController) {
   Column(modifier = Modifier.fillMaxWidth()) {
       AppSpacerV(15)
       CornerButton(modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 20.dp),
           text = stringResource(R.string.view_all),
           onClick = {navController.navigate(Route.AvailableInProperties.route)})

       AppSpacerV(25)
       AppDivider(color = Color.LightGray, thickness = 1.dp)
       AppSpacerV(25)

       AppTextSemiBold(modifier = Modifier.padding(horizontal = 20.dp),
           text = stringResource(R.string.key_information))
       AppSpacerV(15)
       Card (modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 20.dp),
           colors = CardDefaults.cardColors(
               containerColor = Color.White
           ),
           elevation = CardDefaults.cardElevation(3.dp)){
           Column (modifier = Modifier.padding(3.dp)){
               AppTextMedium(modifier = Modifier.padding(15.dp)
                   , text = "Length of stay")
               KeyRowItems(text = "Short term", value = "2.51%",0,0)
               AppDivider(color = Color.LightGray, thickness = 1.dp)
               KeyRowItems(text = "Long term", value = "97.49%",10,10)
           }
       }
       AppSpacerV(20)
       Card (modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 20.dp),
           colors = CardDefaults.cardColors(
               containerColor = Color.White
           ),
           elevation = CardDefaults.cardElevation(3.dp)){
           Column (modifier = Modifier.padding(3.dp)){
               AppTextMedium(modifier = Modifier.padding(15.dp)
                   , text = "Renewal rate")
               KeyRowItems(text = "Avg", value = "97.49%",10,10)
           }
       }
       AppSpacerV(20)
       Card (modifier = Modifier
           .fillMaxWidth()
           .padding(horizontal = 20.dp),
           colors = CardDefaults.cardColors(
               containerColor = Color.White
           ),
           elevation = CardDefaults.cardElevation(3.dp)){
           Column (modifier = Modifier.padding(3.dp)){
               AppTextMedium(modifier = Modifier.padding(15.dp)
                   , text = "Rental yield")
               KeyRowItems(text = "Avg", value = "8.49%",10,10)
           }
       }
       AppSpacerV(25)
       AppDivider(color = Color.LightGray, thickness = 1.dp)
       AppSpacerV(25)
   }
}
@Composable
fun KeyRowItems(text: String,value : String,botLeftCorner:Int,botRightCorner: Int){
    Row (modifier = Modifier
        .fillMaxWidth()
        .clip(
            RoundedCornerShape(
                bottomEnd = if (botRightCorner != 0) botRightCorner.dp else 0.dp,
                bottomStart = if (botLeftCorner != 0) botLeftCorner.dp else 0.dp
            )
        )
        .background(colorResource(R.color.screen_bg_color))
        .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween){
        AppTextLight(text = text)
        AppTextMedium(text = value)
    }
}

@Composable
fun TopImageSliderView(navController: NavController) {
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
            ), navController = navController
        )

        Column (
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 25.dp)
        ){
            TagBoxWithImage1(text = "VERIFIED", bgColor = colorResource(R.color.light_green),
                textColor = Color.White, icon = R.drawable.verified_user )

            AppSpacerV(5)
            AppTextSemiBold(text = "Seven palm")
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
        AppDivider(color = Color.LightGray, thickness = 1.dp)
        AppSpacerV(25)

        AppTextBold(modifier = Modifier.padding(horizontal = 20.dp),
            text = stringResource(R.string.all_available),
            fontSize = 17.sp)
        AppSpacerV(15)

    }
}

@Composable
fun PropertyTypeItem(title: String, count: String) {

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                AppTextMedium(text = title)
                AppSpacerV(7)
                AppTextLight(text = count )
            }

            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(resId = R.drawable.ic_arrow_right)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PropertyImageSlider(images: List<String>,navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { images.size })

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)) {
        HorizontalPager(state = pagerState) { page ->
            SubcomposeAsyncImage(
                model = images[page],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_placeholder),
                            contentDescription = null,
                            modifier = Modifier.size(150.dp)
                        )
                    }
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .height(35.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(horizontal = 14.dp)
                    .clickable{
                        navController.currentBackStackEntry?.savedStateHandle?.set("images", images)
                        navController.navigate(Route.ImageFullView.route)
                    }
            ) {
                Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(
                        resId = R.drawable.ic_gallery_frame,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    AppSpacerH(5)

                    AppTextMedium(
                        text = "${pagerState.currentPage + 1}/${images.size}",
                        color = Color.White,
                    )
                }
            }
            AppSpacerH(10)

            Box(
                modifier = Modifier
                    .height(35.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable { }
                    .padding(horizontal = 16.dp)
            ) {

                Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(
                        resId = R.drawable.location,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    AppSpacerH(5)
                    AppTextMedium(text = stringResource(R.string.location),)
                }
            }
        }
    }
}

@Composable
fun TagBoxWithImage1(text: String, bgColor: Color, textColor: Color,icon: Int) {
    Box(
        modifier = Modifier
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
            AppImage(resId = icon)
            AppSpacerH(5)
            AppTextMedium(text= text, fontSize = 11.sp, color =textColor)
        }
    }
}