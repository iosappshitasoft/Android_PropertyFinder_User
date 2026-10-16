package com.app.propertyfinder.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.model.home.PropertyModel
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.FilterDropDownChipRow
import com.app.propertyfinder.ui.components.PropertyItem
import com.app.propertyfinder.ui.theme.Background

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PropertyProjectPreview(){
    PropertyProjectListScreen(navController = rememberNavController(),"")
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PropertyProjectListScreen(navController: NavController,fromScreen : String){
    val projectFilters = mapOf(
        "All" to listOf("All", "Buy", "Rent"),
        "Sort by" to listOf("Price Low", "Price High", "Newest"),
        "Location" to listOf("Dubai", "Abu Dhabi", "Sharjah"),
        "Beds" to listOf("Studio", "1 Bed", "2 Beds", "3 Beds"),
        "Price" to listOf("Price", "Price 1", " Price 2")
    )
    val propertiesFilters = mapOf(
        "Sort by" to listOf("Price Low", "Price High", "Newest"),
        "Location" to listOf("Dubai", "Abu Dhabi", "Sharjah"),
        "Property type" to listOf("Villa", "Apartments"),
        "Beds" to listOf("Studio", "1 Bed", "2 Beds", "3 Beds"),
        "Price" to listOf("Price", "Price 1", " Price 2"),
        "More filters" to listOf("Filter", "Filter 1", "Filter 2")
    )
    val selectedValues = remember { mutableStateMapOf<String, String>() }


    val demoPropertyList = listOf(
        PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),  PropertyModel(
            tagType = "damac",
            isShowHeart = true,
            isVerified = true,
            isSuperAgent = true,
            propertyType = "VILLA",
            off_plan = "",
            deliveryDate = "",
            saleStarted = "",
            paymentPlan = "",
            title = "Omniyat Bespoke  |  Villa",
            location = "Dubai, Palm jumeirah",
            price = "9,000,000 AED/year",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "Listed 4 days ago",
            isWhatsapp = false,
            bed = "2 Bed",
            bath = "3 Bath",
            sqft = "3,832 sqft",
            true,
            "hor"
        ),
    )

    val demoProjectList = listOf(
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
        ),PropertyModel(
            tagType = "emer",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "OFF-PLAN",
            deliveryDate = "Delivery date : Q1 2029",
            saleStarted = "Sale started : 21 march 2025",
            paymentPlan = "2 payment plan available",
            title = "Vida Residences Hillside",
            location = "Dubai, Dubai Hills Estate, Rosehill",
            price = "9,000,000 AED",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "",
            isWhatsapp = true,
            bed = "",
            bath = "",
            sqft = "",
            true,
            "ver"
        ),PropertyModel(
            tagType = "emer",
            isShowHeart = false,
            isVerified = false,
            isSuperAgent = false,
            propertyType = "",
            off_plan = "OFF-PLAN",
            deliveryDate = "Delivery date : Q1 2029",
            saleStarted = "Sale started : 21 march 2025",
            paymentPlan = "2 payment plan available",
            title = "Vida Residences Hillside",
            location = "Dubai, Dubai Hills Estate, Rosehill",
            price = "9,000,000 AED",
            imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
            listedDays = "",
            isWhatsapp = true,
            bed = "",
            bath = "",
            sqft = "",
            true,
            "ver"
        ),PropertyModel(
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
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = if (fromScreen == "project") "Projects" else "Properties",
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
                actions = {
                    IconButton(
                        onClick = {  }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Background)
                .padding(vertical = 12.dp)
        ) {
            FilterDropDownChipRow(
                filters = if (fromScreen == "project") projectFilters else propertiesFilters,
                selectedValues = selectedValues,
                onSelectionChanged = {
                    // use selected values here
                    // example:
                     Log.d("SelectedFilters", it.toString())
                }
            )

            AppTextMedium(modifier = Modifier.padding(horizontal = 20.dp, vertical = 15.dp),
                text = if (fromScreen == "project") "25 Projects are here" else "25 Properties are here"
                , fontSize = 15.sp)

            LazyColumn(modifier = Modifier.fillMaxSize()
                .weight(1f)) {

                items(if (fromScreen == "project")demoProjectList else demoPropertyList) { property ->
                    PropertyItem(property,"")
                }
            }
        }
    }
}