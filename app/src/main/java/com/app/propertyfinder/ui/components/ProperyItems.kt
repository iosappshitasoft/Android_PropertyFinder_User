package com.app.propertyfinder.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.app.propertyfinder.R
import com.app.propertyfinder.model.home.PropertyModel
import com.app.propertyfinder.screen.FeatureItem

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun  PropertyItemPreview(){
    val propertyModel = PropertyModel(
        tagType = "damac",
        isShowHeart = true,
        isVerified = true,
        isSuperAgent = true,
        propertyType = "VILLA",
        off_plan = "",
        deliveryDate = "",
        saleStarted = "",
        paymentPlan = "",
        title = "Omniyat Bespoke  |  Villa",
        location = "Dubai, Palm jumeirah",
        price = "9,000,000 AED/year",
        imageUrl = "https://images.unsplash.com/photo-1574739782661-e603aa0dda2b",
        listedDays = "Listed 4 days ago",
        isWhatsapp = false,
        bed = "",
        bath = "3 Bath",
        sqft = "3,832 sqft",
        true,
        "ver"
    )
    PropertyItem(propertyModel = propertyModel,"")
}

@Composable
fun PropertyItem(propertyModel : PropertyModel,orientation : String){
    Card(
        modifier = Modifier.fillMaxWidth().widthIn(max = 320.dp).padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        )
    ) {
        Column(modifier = Modifier) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(end = 3.dp, start = 3.dp, top = 3.dp).height(210.dp)
                    .clip(RoundedCornerShape(topEnd = 10.dp, topStart = 10.dp))
            ) {
                SubcomposeAsyncImage(
                    model = propertyModel.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.ic_placeholder),
                                contentDescription = null,
                                modifier = Modifier.size(150.dp)
                            )
                        }
                    }
                )
                if (propertyModel.tagType.isNotEmpty()) {
                    AppImage(
                        resId = if (propertyModel.tagType == "damac") R.drawable.damac_tag else R.drawable.emaar_tag,
                        contentDescription = null,
                        modifier = Modifier.padding(top = 3.dp, start = 3.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                if (propertyModel.isShowHeart) {
                    Box(
                        modifier = Modifier.align(alignment = Alignment.TopEnd).size(50.dp)
                            .padding(5.dp).background(
                                color = Color.Black.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        AppImage(
                            resId = R.drawable.heart,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                        )
                    }
                }

                // Tags bottom right
                Column(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(end = 5.dp, bottom = 5.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    if (propertyModel.isVerified) {
                        TagBoxWithImage(text = "VERIFIED", colorResource(R.color.light_green),
                            Color.White, R.drawable.verified_user
                        )
                        AppSpacerV(5)
                    }
                    if (propertyModel.isSuperAgent) {
                        TagBoxWithImage(text = "SUPERAGENT", colorResource(R.color.splash_primary),
                            Color.White, R.drawable.agent
                        )
                        AppSpacerV(5)
                    }
                    if (propertyModel.propertyType.isNotEmpty()){
                        TagBox(text = propertyModel.propertyType, Color.White, Color.Black)
                        AppSpacerV(5)
                    }
                    if (propertyModel.off_plan.isNotEmpty()){
                        TagBox(text = propertyModel.off_plan, Color.White, Color.Black)
                        AppSpacerV(5)
                    }
                    if (propertyModel.deliveryDate.isNotEmpty()){
                        TagBox(text = propertyModel.deliveryDate, Color.White, Color.Black)
                        AppSpacerV(5)
                    }
                    if (propertyModel.saleStarted.isNotEmpty()){
                        TagBox(text = propertyModel.saleStarted, colorResource(R.color.splash_primary),Color.White)
                        AppSpacerV(5)
                    }
                    if (propertyModel.paymentPlan.isNotEmpty()){
                        TagBox(text = propertyModel.paymentPlan, colorResource(R.color.light_green),Color.White)
                        AppSpacerV(5)
                    }
                }
                // Text bottom left
                if (propertyModel.listedDays.isNotEmpty()) {
                    Column(
                        modifier = Modifier.align(Alignment.BottomStart)
                            .padding(start = 8.dp, bottom = 8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        AppTextLight(text = propertyModel.listedDays, color = Color.White)
                    }
                }
            }
            AppSpacerV(5)

            AppTextMedium(text = propertyModel.title, modifier = Modifier.padding(horizontal = 15.dp))
            AppSpacerV(4)
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 15.dp)) {
                AppIcon(resId = R.drawable.location)
                AppSpacerH(4)
                AppTextLight(text = propertyModel.location)
            }
            if (propertyModel.isShowPriceTxt){
                AppDivider(modifier = Modifier.padding(vertical = 16.dp),
                    thickness = 1.dp, color = colorResource(R.color.screen_bg_color))
            }
            // 🔹 Bottom Section
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    if (propertyModel.isShowPriceTxt){
                        AppTextLight(text = stringResource(R.string.launch_price),)
                        AppSpacerV(5)
                    }
                    AppTextMedium(text = propertyModel.price)
                }
                if (propertyModel.isWhatsapp) {
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.whatsapp_green)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AppIcon(resId = R.drawable.whatapp, modifier = Modifier.size(20.dp))
                            AppSpacerH(5)
                            AppTextMedium(text = "Whatsapp", color = Color.White)
                        }
                    }
                }
            }
            AppSpacerV(10)
            if (propertyModel.bed.isNotEmpty()) {
                if (propertyModel.bottomAmenityOrientation == "ver") {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                        BottomAmenityItem(
                            icon = R.drawable.bad,
                            text = propertyModel.bed,
                            modifier = Modifier.weight(1f)
                        )
                        AppSpacerH(4)
                        BottomAmenityItem(
                            icon = R.drawable.bath,
                            text = propertyModel.bath,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    BottomAmenityItem(
                        icon = R.drawable.sqft, text = propertyModel.sqft,
                        modifier = Modifier.fillMaxWidth().padding(4.dp)
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)) {
                        BottomAmenityItem(
                            icon = R.drawable.bad,
                            text = propertyModel.bed,
                            modifier = Modifier.weight(1f)
                        )
                        AppSpacerH(4)
                        BottomAmenityItem(
                            icon = R.drawable.bath,
                            text = propertyModel.bath,
                            modifier = Modifier.weight(1f)
                        )
                        AppSpacerH(4)
                        BottomAmenityItem(
                            icon = R.drawable.sqft, text = propertyModel.sqft,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TagBox(text: String, bgColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(bgColor, )
            .padding(horizontal = 8.dp)
    ) {
        AppTextMedium(text= text, fontSize = 11.sp, color =textColor)
    }
}
@Composable
fun TagBoxWithImage(text: String, bgColor: Color, textColor: Color,icon: Int) {
    Box(
        modifier = Modifier.background(bgColor).padding(horizontal = 8.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
            AppImage(resId = icon)
            AppSpacerH(5)
            AppTextMedium(text= text, fontSize = 11.sp, color =textColor)
        }
    }
}


@Composable
fun BottomAmenityItem(icon: Int, text: String, modifier: Modifier = Modifier) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.background(
            Color(0xFFF5F5F5),RoundedCornerShape(5.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        AppIcon(resId = icon)
        AppSpacerH(4)

        AppTextLight(text = text, color = colorResource(R.color.textprimary))
    }
}
