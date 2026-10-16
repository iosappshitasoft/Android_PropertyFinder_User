package com.app.propertyfinder.screen

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.api.dto.response.helpFaqCategories
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.google.accompanist.systemuicontroller.rememberSystemUiController

data class FaqItemData(
    val question: String,
    val answer: String,
    val category: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HelpScreenPreview(){
    HelpScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController,
    propertyViewModel: PropertyViewModel = hiltViewModel()
) {
    val systerUiController = rememberSystemUiController()
    SideEffect {
        systerUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val helpCategories: List<helpFaqCategories> =
        masterDataState.data?.data?.helpFaqCategories.orEmpty()
    val helpFaqsState by propertyViewModel.helpFaqsState.collectAsStateWithLifecycle()

    var selectedCategoryValue by remember { mutableStateOf("all") }

    LaunchedEffect(Unit) {
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("helpfaqcategories") { }
        }
    }

    LaunchedEffect(helpCategories) {
        if (helpCategories.isNotEmpty() && selectedCategoryValue == "all") {
            selectedCategoryValue = helpCategories.first().value.orEmpty().ifBlank { "all" }
        }
    }

    LaunchedEffect(selectedCategoryValue) {
        propertyViewModel.getHelpFaqs(category = selectedCategoryValue.ifBlank { "all" })
    }

    val faqList = helpFaqsState.data?.data?.faqs
        ?.sortedBy { it.order ?: Int.MAX_VALUE }
        ?.map { item ->
            FaqItemData(
                question = item.question.orEmpty(),
                answer = item.answer.orEmpty(),
                category = item.categoryKey.orEmpty()
            )
        }
        .orEmpty()

    val filteredList = faqList.filter { faq ->
        val matchesSearch =
            faq.question.contains(searchText, ignoreCase = true) ||
                    faq.answer.contains(searchText, ignoreCase = true)
        matchesSearch
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "Help", fontSize = 18.sp, color = MolumulkCharcoal)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MolumulkCharcoal
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = {
                            expanded = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert, 
                                contentDescription = "Menu", 
                                tint = MolumulkCharcoal
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                            containerColor = Color.White,
                            offset = DpOffset(x = (-15).dp, y = 0.dp)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically ){
                                        Icon(
                                            painter = painterResource(id = R.drawable.report_flag),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = MolumulkCharcoal
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        AppTextMedium(text = "Report", color = MolumulkCharcoal, fontSize = 14.sp)
                                    }
                                },
                                onClick = {
                                    expanded = false
                                    navController.navigate(Route.ReportProblem.route)
                                },
                                modifier = Modifier.height(36.dp).width(150.dp)
                            )
                        }
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
                .background(Background),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = MolumulkDimens.ScreenHorizontalPadding, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    helpCategories.forEach { cat ->
                        val name = cat.name.orEmpty()
                        val value = cat.value.orEmpty()
                        if (name.isNotBlank() && value.isNotBlank()) {
                            HelpCategoryChip(
                                text = name,
                                isSelected = selectedCategoryValue.equals(value, ignoreCase = true)
                            ) {
                                selectedCategoryValue = value
                            }
                        }
                    }
                }
            }

            // Search Field
            item {
                Column (
                    modifier = Modifier.padding(horizontal = MolumulkDimens.ScreenHorizontalPadding)
                ){
                    AppSpacerV(16)
                    var isSearchFocused by remember { mutableStateOf(false) }
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                        border = BorderStroke(
                            1.dp, 
                            if (isSearchFocused) MolumulkPrimaryCoralRed else MolumulkBorderDivider
                        ),
                        color = Color.White
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(MolumulkDimens.SearchFieldHeight)
                                .padding(horizontal = 14.dp)
                                .background(Color.White),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = if (isSearchFocused) MolumulkPrimaryCoralRed else MolumulkMutedSlate,
                                modifier = Modifier.size(20.dp)
                            )

                            AppSpacerH(10)

                            BasicTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                singleLine = true,
                                cursorBrush = SolidColor(MolumulkPrimaryCoralRed),
                                textStyle = LocalTextStyle.current.copy(
                                    fontFamily = AppFontFamily,
                                    fontSize = 14.sp,
                                    color = MolumulkCharcoal,
                                    textAlign = TextAlign.Start
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .onFocusChanged { isSearchFocused = it.isFocused },
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (searchText.isEmpty()) {
                                            AppTextRegular(
                                                text = "Search",
                                                color = MolumulkMutedSlate,
                                                fontSize = 14.sp
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }
                    }
                    AppSpacerV(16)
                }
            }

            // FAQ List
            if (helpFaqsState.isLoading) {
                item {
                    AppTextSemiBold(
                        text = "Loading FAQs...",
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MolumulkMutedSlate
                    )
                }
            } else if (filteredList.isEmpty()) {
                item {
                    AppTextSemiBold(
                        text = "No results found",
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MolumulkMutedSlate
                    )
                }
            } else {
                itemsIndexed(filteredList) { index, faq ->
                    val shape = when {
                        filteredList.size == 1 -> RoundedCornerShape(MolumulkDimens.CardCornerRadius)
                        index == 0 -> RoundedCornerShape(
                            topStart = MolumulkDimens.CardCornerRadius,
                            topEnd = MolumulkDimens.CardCornerRadius
                        )
                        index == filteredList.lastIndex -> RoundedCornerShape(
                            bottomStart = MolumulkDimens.CardCornerRadius,
                            bottomEnd = MolumulkDimens.CardCornerRadius
                        )
                        else -> RoundedCornerShape(0.dp)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MolumulkDimens.ScreenHorizontalPadding)
                            .clip(shape)
                            .background(Color.White)
                    ) {
                        ExpandableFaqItem(faq)

                        if (index != filteredList.lastIndex) {
                            Divider(color = MolumulkBorderDivider)
                        }
                    }
                }
                item {
                    AppSpacerV(24)
                }
            }
        }
    }
}

@Composable
fun HelpCategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MolumulkLightCoralTint else Color.White
    val borderColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
    val textColor = if (isSelected) MolumulkPrimaryCoralRed else MolumulkCharcoal

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .background(backgroundColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        AppTextMedium(
            text = text,
            color = textColor,
            fontSize = 13.sp
        )
    }
}

@Composable
fun ExpandableFaqItem(faq: FaqItemData) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .background(Color.White)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTextSemiBold(
                text = faq.question,
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = MolumulkCharcoal
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(if (expanded) 180f else 0f),
                tint = if (expanded) MolumulkPrimaryCoralRed else MolumulkMutedSlate
            )
        }

        AnimatedVisibility(visible = expanded) {
            AppTextRegular(
                text = faq.answer,
                color = MolumulkMutedSlate,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}