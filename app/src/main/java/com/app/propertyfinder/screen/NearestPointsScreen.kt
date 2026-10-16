package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.theme.Background


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NearestPreview(){
    NearestPointsScreen(navController = rememberNavController())
}
data class PlaceItem(
    val name: String,
    val distance: String
)

data class PlaceSection(
    val title: String,
    val items: List<PlaceItem>
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NearestPointsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Points of interest", fontSize = 20.sp)
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
                .background(Background)
                .padding(vertical = 12.dp)
        ) {
            val sections = listOf(
                PlaceSection(
                    "Restaurants",
                    listOf(
                        PlaceItem("San Beach", "205 m"),
                        PlaceItem("Ula Dubai", "207 m"),
                        PlaceItem("Replay Sports Bar", "217 m"),
                        PlaceItem("San Beach", "205 m"),
                        PlaceItem("Ula Dubai", "207 m"),
                        PlaceItem("Replay Sports Bar", "217 m")
                    )
                ),
                PlaceSection(
                    "Groceries",
                    listOf(
                        PlaceItem("Kimgen And KB Cosmetic L.L.C", "205 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                    )
                ),
                PlaceSection(
                    "Schools",
                    listOf(
                        PlaceItem("San Beach", "205 m"),
                        PlaceItem("Ula Dubai", "207 m"),
                        PlaceItem("Replay Sports Bar", "217 m"),
                        PlaceItem("Ula Dubai", "207 m"),
                        PlaceItem("Replay Sports Bar", "217 m"),
                        PlaceItem("Ula Dubai", "207 m"),
                        PlaceItem("Replay Sports Bar", "217 m"),
                    )
                ),
                PlaceSection(
                    "Groceries",
                    listOf(
                        PlaceItem("Kimgen And KB Cosmetic L.L.C", "205 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                        PlaceItem("Daiso Japan", "207 m"),
                        PlaceItem("Ceylon Master Gems fze", "217 m"),
                    )
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(sections) { section ->
                    PlaceSectionCard(section)
                }
            }
        }
    }
}

@Composable
fun PlaceSectionCard(section: PlaceSection) {

    var expanded by remember { mutableStateOf(false) }

    val visibleItems =
        if (expanded) section.items
        else section.items.take(3)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            AppTextLight(text = section.title, fontSize = 13.sp)
            AppSpacerV(8)
            visibleItems.forEach { item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    AppTextMedium(text = item.name, fontSize = 13.sp )
                    AppTextMedium(text = item.distance, fontSize = 14.sp )
                }
            }

            if (section.items.size > 3) {
                AppSpacerV(6)
                Row(
                    modifier = Modifier
                        .clickable { expanded = !expanded }
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AppTextMedium(text = if (expanded) "Read less" else "Read more")
                    AppSpacerH(5)
                    AppIcon(resId = if (expanded) R.drawable.up_arrow else R.drawable.arrow_down,
                        Modifier.size(15.dp))
                }
            }
        }
    }
}