package com.app.propertyfinder.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PropertyFinderTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun View_full_details(navController: NavHostController){
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = "About agent", fontSize = 18.sp) }, // Hardcoded for now
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)){
            AppSpacerV(3)
            AppTextRegular("Quisque ac ex vitae eros porttitor congue sed sed massa. Nullam elementum lectus sit amet malesuada placerat. Phasellus feugiat lobortis est, ut euismod turpis luctus vel. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Etiam et augue dapibus, cursus orci eget, ultricies risus. Pellentesque a neque et dolor finibus consectetur eu sed metus. Cras convallis urna commodo, accumsan urna sed, accumsan odio. Donec pellentesque dui vel massa ullamcorper, nec ornare mi tristique. Suspendisse sit amet elit tortor. Pellentesque laoreet, eros eget tempor gravida, felis sem tincidunt enim, non ornare magna nisi in tellus. Fusce non maximus est. Maecenas vel metus ac turpis fringilla pulvinar. Morbi neque diam, suscipit et iaculis eget, accumsan interdum magna. Mauris dapibus quam eget dui aliquam, in interdum eros bibendum.", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ViewScreenPreview() {
    PropertyFinderTheme {
        View_full_details(navController = rememberNavController())
    }
}