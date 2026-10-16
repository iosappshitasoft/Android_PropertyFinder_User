package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PriceMapPreview(){
    PriceMapScreen(navController = rememberNavController())
}

data class PriceMapArea(
    val name: String,
    val avgPrice: String,
    val color: Color,
    val points: List<LatLng>,
    val center: LatLng
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceMapScreen(navController: NavController) {
    var expandedDropDown by remember { mutableStateOf(false) }
    var selectedDropDown by remember { mutableStateOf("Dubai") }
    var showBottomSheet by remember { mutableStateOf(false) }

    val options = listOf("Dubai", "Dubai1")
    var dropDownWidth by remember { mutableStateOf(0) }
    val density = androidx.compose.ui.platform.LocalDensity.current

    val dubai = LatLng(25.2048, 55.2708)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(dubai, 11f)
    }

    // Dummy nearby areas around Dubai
    val priceAreas = remember {
        listOf(
            PriceMapArea(
                name = "Downtown Dubai",
                avgPrice = "AED 2.1M",
                color = Color(0x66E53935), // red
                center = LatLng(25.1972, 55.2744),
                points = listOf(
                    LatLng(25.194438, 55.267485),
                    LatLng(25.197838, 55.269681),
                    LatLng(25.198798, 55.270016),
                    LatLng(25.199492, 55.271104),
                    LatLng(25.200276, 55.271080),
                    LatLng(25.201976, 55.271643),
                    LatLng(25.202539, 55.271859),
                    LatLng(25.202858, 55.271840),
                    LatLng(25.202883, 55.273658),
                    LatLng(25.194969, 55.288596),
                    LatLng(25.193526, 55.286151),
                    LatLng(25.191410, 55.282891),
                    LatLng(25.185468, 55.277571),
                    LatLng(25.191224, 55.269049),
                    LatLng(25.194421, 55.267386),
                )
            ),
            PriceMapArea(
                name = "Business Bay",
                avgPrice = "AED 1.6M",
                color = Color(0x66FB8C00), // orange
                center = LatLng(25.1860, 55.2640),
                points = listOf(
                    LatLng(25.1905, 55.2580),
                    LatLng(25.1905, 55.2705),
                    LatLng(25.1810, 55.2705),
                    LatLng(25.1810, 55.2580)
                )
            )
        )
    }

    var selectedArea by remember { mutableStateOf<PriceMapArea?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.price_map), fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    Box {
                        AppTextMedium(
                            modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                            text = stringResource(R.string.reset),
                            color = colorResource(R.color.splash_primary),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->

        if (showBottomSheet) {
            PriceMapBottomSheet(
                onDismiss = { showBottomSheet = false },
                onExploreClick = {
                    navController.navigate(Route.ExploreCommunity.route)
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .onGloballyPositioned {
                            dropDownWidth = it.size.width
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White)
                            .clickable { expandedDropDown = true }
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.screen_bg_color),
                                shape = RoundedCornerShape(99.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextSemiBold(
                            text = selectedDropDown,
                            color = colorResource(R.color.textprimary)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = expandedDropDown,
                        onDismissRequest = { expandedDropDown = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(with(density) { dropDownWidth.toDp() }),
                        containerColor = Color.White
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                text = { AppTextMedium(text = option) },
                                onClick = {
                                    selectedDropDown = option
                                    expandedDropDown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                AppTextLight(text = stringResource(R.string.data_based))
                Spacer(modifier = Modifier.height(15.dp))

                PriceRangeBar()
                Spacer(modifier = Modifier.height(5.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            LocalContext.current,
                            R.raw.map_style_gray
                        )
                    ),
                    onMapClick = {
                        selectedArea = null
                    }
                ) {
                    priceAreas.forEach { area ->
                        Polygon(
                            points = area.points,
                            fillColor = if (selectedArea?.name == area.name) {
                                area.color.copy(alpha = 0.75f)
                            } else {
                                area.color.copy(alpha = 0.45f)
                            },
                            strokeColor = area.color.copy(alpha = 0.95f),
                            strokeWidth = if (selectedArea?.name == area.name) 6f else 4f,
                            clickable = true,
                            onClick = {
                                selectedArea = area
                                showBottomSheet = true
                            }
                        )
                        Marker(
                            state = MarkerState(position = area.center),
                            title = area.name,
                            snippet = area.avgPrice,
                            onClick = {
                                selectedArea = area
                                showBottomSheet = true
                                false
                            }
                        )
                    }
                }

                selectedArea?.let { area ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                            Text(
                                text = area.name,
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Average price: ${area.avgPrice}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterSection()
                }
            }
        }
    }
}



/*@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceMapScreen(navController: NavController) {
    var expandedDropDown by remember { mutableStateOf(false) }
    var selectedDropDown by remember { mutableStateOf("Dubai") }
    var showBottomSheet by remember { mutableStateOf(false) }

    val options = listOf("Dubai", "Dubai1")
    var dropDownWidth by remember { mutableStateOf(0) }
    val density = LocalDensity.current

    val dubai = LatLng(25.2048, 55.2708)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(dubai, 11f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.price_map), fontSize = 20.sp)
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
                    Box {
                        AppTextMedium(modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                            text = stringResource(R.string.reset),
                            color = colorResource(R.color.splash_primary),
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { innerPadding ->
        if (showBottomSheet){
            PriceMapBottomSheet (
                onDismiss = { showBottomSheet = false },
                onExploreClick = {
                    navController.navigate(Route.ExploreCommunity.route)
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .onGloballyPositioned {
                            dropDownWidth = it.size.width
                        }
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(99.dp))
                            .background(Color.White)
                            .clickable { expandedDropDown = true }
                            .border(
                                width = 1.dp,
                                color = colorResource(R.color.screen_bg_color),
                                shape = RoundedCornerShape(99.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AppTextSemiBold(
                            text = selectedDropDown,
                            color = colorResource(R.color.textprimary)
                        )

                        AppSpacerH(4)

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = expandedDropDown,
                        onDismissRequest = { expandedDropDown = false },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .width(with(density) { dropDownWidth.toDp() }),
                        containerColor = Color.White
                    ) {

                        options.forEach { option ->

                            DropdownMenuItem(
                                text = {
                                    AppTextMedium(text = option)
                                },
                                onClick = {
                                    selectedDropDown = option
                                    expandedDropDown = false
                                }
                            )
                        }
                    }
                }

                AppSpacerV(10)
                AppTextLight(text = stringResource(R.string.data_based))
                AppSpacerV(15)

                PriceRangeBar()
                AppSpacerV(5)

            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                            LocalContext.current,
                            R.raw.map_style_gray
                        )
                    ),
                    onMapClick = { latLng ->
//                        clickedLocation = latLng
                        showBottomSheet = true
                    }
                )

                Row (modifier = Modifier.align(Alignment.BottomCenter)
                    .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,) {
                    FilterSection()
                }
            }
        }
    }
}*/

@Composable
fun PriceRangeBar() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Gradient Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFB7D4DD),   // light blue
                            Color(0xFF1F9AA8),   // medium teal
                            Color(0xFF003F45)    // dark teal
                        )
                    )
                )
        )
        AppSpacerV(8)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                AppTextSemiBold(text = "375k AED", fontSize = 14.sp)
                AppTextLight(text = stringResource(R.string.lowest))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppTextSemiBold(text = "104M AED", fontSize = 14.sp)
                AppTextLight(text = stringResource(R.string.median))
            }

            Column(horizontalAlignment = Alignment.End) {
                AppTextSemiBold(text = "9M AED", fontSize = 14.sp)
                AppTextLight(text = stringResource(R.string.highest))
            }
        }
    }
}

@Composable
fun FilterSection() {

    var buy by remember { mutableStateOf("Buy") }
    var type by remember { mutableStateOf("Apartment") }
    var beds by remember { mutableStateOf("1 bed") }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        FilterDropdownChip(
            selectedItem = buy,
            items = listOf("Buy", "Rent")
        ) { buy = it }

        FilterDropdownChip(
            selectedItem = type,
            items = listOf("Apartment", "Villa", "Townhouse")
        ) { type = it }

        FilterDropdownChip(
            selectedItem = beds,
            items = listOf("1 bed", "2 bed", "3 bed")
        ) { beds = it }
    }
}
@Composable
fun FilterDropdownChip(
    selectedItem: String,
    items: List<String>,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFF222222))
                .clickable { expanded = true }
                .padding(horizontal = 15.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            AppTextMedium(text = selectedItem, color = Color.White)
            AppSpacerV(4)

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) {
            items.forEach { item ->

                DropdownMenuItem(
                    modifier = Modifier,
                    text = { Text(item) },
                    onClick = {
                        expanded = false
                        onItemSelected(item)
                    }
                )
            }
        }
    }
}
