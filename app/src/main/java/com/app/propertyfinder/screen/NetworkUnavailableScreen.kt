package com.app.propertyfinder.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.propertyfinder.R


@Composable
fun NetworkUnavailableScreen() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lost_connection))
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(color = Color.White),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                modifier = Modifier.size(200.dp),
                composition = composition,
                iterations = LottieConstants.IterateForever
            )
        }
    }
}

@Composable
@Preview
fun NetworkUnavailablePreview() {
    NetworkUnavailableScreen()
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun NetworkUnavailableDarkPreview() {
    NetworkUnavailableScreen()
}

