package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.theme.Background


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AvailablePreview(){
    AvailablePropertiesScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AvailablePropertiesScreen(navController: NavController) {
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Available in Seven Palm", fontSize = 20.sp)
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
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .weight(1f)) {

                items(propertyList.take(4)) { item ->
                    PropertyTypeItem(title = item.title, count = item.count)
                    AppSpacerV(10)
                }
            }
        }
    }
}