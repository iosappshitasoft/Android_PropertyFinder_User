package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppEditText
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewTravelTimes(){
    TravelTimesScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelTimesScreen(navController: NavController){
    var selectedTime by remember { mutableIntStateOf(10) }
    var selectedMode by remember { mutableStateOf("Walking") }
    var searchLocationTxt by remember { mutableStateOf("") }

    val times = listOf(10, 15, 20, 30, 45, 60)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.travel_times), fontSize = 20.sp)
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
                ),

            )
        })
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Column (modifier = Modifier.fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp)) {
                AppTextMedium(text = stringResource(R.string.where_want))

                AppSpacerV(20)

                AppEditText(
                    hint = stringResource(R.string.location_search_placeholder),
                    value = searchLocationTxt,
                    onValueChange = { searchLocationTxt = it }
                )

                AppSpacerV(35)

                AppTextMedium(text = stringResource(R.string.select_travel_timing))

                AppSpacerV(20)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    times.forEach { time ->
                        val isSelected = selectedTime == time
                        Column(
                            modifier = Modifier.padding(end = 12.dp).clickable { selectedTime = time },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(50.dp).clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFF0832AE)
                                        else Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                AppTextSemiBold(text = "$time",
                                    color = if (isSelected) Color.White else colorResource(R.color.textprimary)
                                )
                            }
                            AppSpacerV(4)

                            AppTextLight(text = "min")
                        }
                    }
                }

                AppSpacerV(35)
            }

            Box(
                modifier = Modifier.fillMaxWidth().weight(1f).background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0832AE),
                            Color(0xFF062890),
                            Color(0xFF062890),
                            Color(0xFF062890),
                            Color(0xFF041D6A),
                            Color(0xFF021245),
                            Color(0xFF000518)
                        )
                    )
                ).padding(start = 20.dp, end = 20.dp, top = 35.dp, bottom = 20.dp)
            ) {
                Column (modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    AppTextMedium(text = stringResource(R.string.how_would)
                        , textAlign = TextAlign.Center, color = Color.White)
                    AppSpacerV(20)
                    // 🔹 Walking Option
                    TravelOptionCard(title = "Walking",
                        R.drawable.ic_walking,
                        isSelected = selectedMode == "Walking"
                    ) {
                        selectedMode = "Walking"
                    }

                    AppSpacerV(15)

                    // 🔹 Driving Option
                    TravelOptionCard(title = "Driving",
                        R.drawable.ic_driving,
                        isSelected = selectedMode == "Driving"
                    ) {
                        selectedMode = "Driving"
                    }
                    Column (modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Bottom){
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(colorResource(R.color.splash_primary))
                                .clickable { },
                            contentAlignment = Alignment.Center
                        ) {
                            AppTextMedium(text = stringResource(R.string.find),
                                color = Color.White)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TravelOptionCard(title: String,icon: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected)
                    Color.White
                else
                    Color.White.copy(alpha = 0.1f)
            )
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center){
            AppImage(resId = icon,
                tint = if (isSelected) Color.Black else Color.White)
            AppSpacerH(8)
            AppTextMedium(text = title,
                color = if (isSelected) colorResource(R.color.textprimary) else Color.White)
        }
    }
}