package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FaqPreview(){
    FAQScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController){
    val faqJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("faq_json")
        .orEmpty()
    val faqItems = remember(faqJson) {
        if (faqJson.isBlank()) {
            faqList
        } else {
            runCatching {
                val type = object : TypeToken<List<FaqItem>>() {}.type
                Gson().fromJson<List<FaqItem>>(faqJson, type)
            }.getOrDefault(faqList)
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = "FAQ", fontSize = 20.sp)
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
                .verticalScroll(rememberScrollState())
        ) {
            FaqView(faqItems)
        }
    }
}

@Composable
fun FaqView(faqItems: List<FaqItem>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color.White)) {

        faqItems.forEachIndexed { index, faq ->
            ExpandableFaqItem(title = faq.title, description = faq.description)

            if (index != faqItems.lastIndex) {
                Divider(color = Color(0xFFE0E0E0))
            }
        }
    }
}