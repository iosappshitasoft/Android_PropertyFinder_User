package com.app.propertyfinder.screen.unit

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.unit.DpOffset
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.ProjectUnitLayout
import com.app.propertyfinder.api.dto.response.ProjectUnitsFromDeveloper
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.screen.MortgageChip
import com.app.propertyfinder.screen.PaymentItemRow
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextSecondary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UnitPreview() {
    DeveloperUnitsScreen(rememberNavController())
}
data class BedType(
    val title: String,
    val price: String? = null,
    val comingSoon: Boolean = false,
    val comingSoonText: String = "Coming soon",
    val comingSoonTextColor: Color? = null,
    val layout: ProjectUnitLayout? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperUnitsScreen(navController: NavController){
    val unitsJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("units_from_developer_json")
    val unitsData: List<ProjectUnitsFromDeveloper> = remember(unitsJson) {
        if (unitsJson.isNullOrBlank()) emptyList()
        else runCatching {
            Gson().fromJson<List<ProjectUnitsFromDeveloper>>(
                unitsJson,
                object : TypeToken<List<ProjectUnitsFromDeveloper>>() {}.type
            )
        }.getOrDefault(emptyList())
    }
    val projectIdForInquiry = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("project_id_for_inquiry")
        .orEmpty()
    val towerOptions = remember(unitsData) {
        unitsData.mapIndexed { index, unit ->
            unit.buildingName?.takeIf { it.isNotBlank() } ?: "Tower ${index + 1}"
        }.ifEmpty { listOf("Tower 1") }
    }
    var selectedTowerIndex by remember { mutableStateOf(0) }
    val selectedTowerData = unitsData.getOrNull(selectedTowerIndex)
    val topViewChipList = remember(selectedTowerData) {
        selectedTowerData?.propertyTypeFilter?.mapNotNull { it.name }?.ifEmpty { listOf("All") }
            ?: listOf("All")
    }
    var chipSelectedIndex by remember { mutableStateOf(0) }
    val selectedTypeName = topViewChipList.getOrNull(chipSelectedIndex)
    val filteredLayouts = remember(selectedTowerData, selectedTypeName) {
        val layouts = selectedTowerData?.layouts.orEmpty()
        if (selectedTypeName.isNullOrBlank() || selectedTypeName == "All") {
            layouts
        } else {
            layouts.filter { it.propertyType?.name.equals(selectedTypeName, ignoreCase = true) }
        }
    }
    val bedList = remember(filteredLayouts) {
        filteredLayouts.map { layout ->
            layout.toBedType()
        }.ifEmpty {
            listOf(BedType("No units available", comingSoon = true))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Units from developer", fontSize = 20.sp)
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
            DeveloperUnitDropdown(
                options = towerOptions,
                selectedIndex = selectedTowerIndex,
                onSelectedIndexChange = {
                    selectedTowerIndex = it
                    chipSelectedIndex = 0
                }
            )
            AppSpacerV(20)
            Row(modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState()).padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topViewChipList.forEachIndexed { index, text ->
                    MortgageChip(text = text, isSelected = chipSelectedIndex == index) {
                        chipSelectedIndex = index
                    }
                }
            }
            AppSpacerV(20)
            LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)) {
                items(bedList) { item ->
                    BedTypeItem(
                        projectIdForInquiry = projectIdForInquiry,
                        item = item,
                        navController = navController
                    )
                    AppSpacerV(15)
                }
            }
        }
    }
}


@Composable
fun DeveloperUnitDropdown(
    options: List<String>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = options.getOrNull(selectedIndex).orEmpty()

    var textFieldWidth by remember { mutableStateOf(0) }

    Column {
        AppTextLight(
            text = "Select Tower",
            modifier = Modifier.padding(top = 5.dp).fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        AppSpacerV(5)
        Box (modifier = Modifier.padding(horizontal = 40.dp)){
            Row(modifier = Modifier.fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextSemiBold(text = selected, fontSize = 15.sp)

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    surface = Color.White
                ),
                shapes = MaterialTheme.shapes.copy(
                    extraSmall = RoundedCornerShape(10.dp)
                )
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    offset = DpOffset(0.dp, 8.dp),
                    modifier = Modifier
                        .width(with(LocalDensity.current) { textFieldWidth.toDp() })
                        .background(Color.White, shape = RoundedCornerShape(10.dp))
                ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelectedIndexChange(index)
                            expanded = false
                        }
                    )
                }
                }
            }
        }
    }
}

@Composable
fun BedTypeItem(
    projectIdForInquiry : String,
    item: BedType,
    navController: NavController
) {

    val bgColor = Color.White
    val textColor = if (item.comingSoon) TextSecondary else colorResource(R.color.textprimary)

    Card(modifier = Modifier.clickable(
        enabled = !item.comingSoon
    ) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("unit_list_layouts_json", Gson().toJson(listOfNotNull(item.layout)))
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("unit_list_title", item.title)
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.set("project_id_for_inquiry", projectIdForInquiry)
        navController.navigate(Route.UnitList.route)
    },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(75.dp)
            .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val arrowResId = if (item.comingSoon) R.drawable.ic_arrow_right_disabled else R.drawable.ic_arrow_right
            AppImage(resId = arrowResId)
            AppSpacerH(12)
            AppTextSemiBold(text = item.title, fontSize = 13.sp, color = textColor,)

            if (item.comingSoon) {
                AppTextMedium(modifier = Modifier.weight(1f),
                    text = item.comingSoonText, fontSize = 13.sp,
                    color = item.comingSoonTextColor ?: colorResource(R.color.textlight),
                    textAlign = TextAlign.End)
            } else {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    AppTextLight(text = "From")
                    AppSpacerV(5)
                    AppTextSemiBold(fontSize = 14.sp, text = item.price ?: "")
                }
            }
        }
    }
}

private fun ProjectUnitLayout.toBedType(): BedType {
    val title = "${bedrooms ?: 0} Bed"
    val amount = startingPrice?.amount
    val currency = startingPrice?.currency.orEmpty().ifBlank { "AED" }
    return when (contactStatus) {
        "unavailable" -> {
            BedType(title, comingSoon = true, comingSoonText = "Coming soon", layout = this)
        }
        "available-to-contact" -> {
            if (availableUnits == 0 || amount == null) {
                BedType(title, comingSoon = true, comingSoonText = "No units available", layout = this)
            } else {
                BedType(title, price = "$amount $currency", layout = this)
            }
        }
        else -> {
            BedType(title, comingSoon = true, comingSoonText = "Sold out", comingSoonTextColor = PrimaryColor, layout = this)
        }
    }
}