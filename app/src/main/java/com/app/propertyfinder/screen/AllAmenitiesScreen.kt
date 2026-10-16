package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.theme.Background
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/** Nav payload stored in the previous entry's SavedStateHandle under `amenities_json`. */
data class AllAmenitiesNavItem(
    val title: String,
    val iconUrl: String? = null
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AllAmenitiesScreenPreview(){
    val navController = rememberNavController()
    AllAmenitiesScreen(navController)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllAmenitiesScreen(navController: NavController){
    val json = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("amenities_json")
        .orEmpty()
    val availableAmenities = remember(json) {
        if (json.isBlank()) {
            emptyList()
        } else {
            runCatching {
                val type = object : TypeToken<List<AllAmenitiesNavItem>>() {}.type
                Gson().fromJson<List<AllAmenitiesNavItem>>(json, type)
            }.getOrDefault(emptyList())
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = stringResource(R.string.amenities),
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            items(count = availableAmenities.size) { index ->
                val item = availableAmenities[index]
                AmenityDisplayItem(iconUrl = item.iconUrl, text = item.title)
            }
        }
    }
}