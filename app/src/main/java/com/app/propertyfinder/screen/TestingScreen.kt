package com.app.propertyfinder.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Preview
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TestingScreen() {
    val chips = listOf("Sold", "Property type", "Beds", "Price")
    val properties = remember {
        List(15) {
            PropertyUi(
                title = "Omniyat Bespoke",
                location = "Dubai, Palm Jumeirah",
                price = "13,050,000 AED",
                specs = "Apartment  |  3,832 ft²  |  1 bed",
                date = "30 May 2025",
                status = if (it % 2 == 0) "READY" else "OFF-PLAN"
            )
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dubai Transactions",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // Banner area - this part hides on scroll
            item {
                BannerSection()
            }

            // Search + filter row stays pinned at top
            stickyHeader {
                StickySearchAndFilters(chips = chips)
            }

            // Overview card scrolls normally
            item {
                Spacer(modifier = Modifier.height(12.dp))
                TransactionOverviewSection()
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Property list
            items(properties) { property ->
                PropertyCard(property = property)
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE94235))
                ) {
                    Text(
                        text = "See all transactions",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun BannerSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF7A5B3A),
                            Color(0xFFA98D69),
                            Color(0xFF6D7C9A)
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Dubai transaction data from the Land Department.",
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun StickySearchAndFilters(chips: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        SearchBarFake()

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            chips.forEachIndexed { index, chip ->
                FilterChipItem(
                    text = chip,
                    selected = index == 0
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun SearchBarFake() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFFF5F5F5))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Search location in dubai",
            color = Color.Gray,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun FilterChipItem(
    text: String,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Color.Black else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) Color.Black else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = if (selected) Color.White else Color.Black,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun TransactionOverviewSection() {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Black)
            .padding(14.dp)
    ) {
        Text(
            text = "Transaction overview",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SmallTab("YTD", false)
            SmallTab("1 Week", true)
            SmallTab("1 Month", false)
            SmallTab("3 Months", false)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("2.8M", "Avg. price", "+5.53%", true, Modifier.weight(1f))
            StatCard("1,853", "Avg. price/sqft", "+7.44%", true, Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("5.4%", "Rental Yield", "-1.44%", false, Modifier.weight(1f))
            StatCard("130.4K", "Transactions", "+6.95%", true, Modifier.weight(1f))
        }
    }
}

@Composable
private fun SmallTab(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .border(
                1.dp,
                if (selected) Color.White else Color(0xFF444444),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else Color.White,
            fontSize = 11.sp
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    change: String,
    positive: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF121212))
            .padding(vertical = 14.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = Color.LightGray,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (positive) Color(0xFF00A86B) else Color(0xFFD32F2F))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = change,
                color = Color.White,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun PropertyCard(property: PropertyUi) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (property.status == "READY") Color(0xFFDAF7E8)
                            else Color(0xFFF0F0F0)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = property.status,
                        fontSize = 10.sp,
                        color = if (property.status == "READY") Color(0xFF0E9F6E) else Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = property.location,
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price ${property.price}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = property.specs,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = property.date,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

data class PropertyUi(
    val title: String,
    val location: String,
    val price: String,
    val specs: String,
    val date: String,
    val status: String
)