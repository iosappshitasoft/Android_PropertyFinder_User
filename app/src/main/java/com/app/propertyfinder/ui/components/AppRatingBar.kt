package com.app.propertyfinder.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.R

@Composable
fun AppRatingBar(
    rating: Float,
    maxStars: Int = 5,
    starSize: Int = 20,
    filledColor: Color = Color(0xFFFFCB2B),
    emptyColor: Color = Color.LightGray,
) {

    Row {

        for (i in 1..maxStars) {

            val icon = when {
                rating >= i -> R.drawable.ic_rating_star_filled
                rating >= i - 0.5 -> R.drawable.ic_rating_star_half
                else -> R.drawable.ic_rating_star_empty
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(starSize.dp),
                tint = Color.Unspecified
            )
        }
    }
}
@Composable
fun AppRatingBarWhite(
    rating: Float,
    maxStars: Int = 5,
    starSize: Int = 20,
    filledColor: Color = Color(0xFFFFCB2B),
    emptyColor: Color = Color.LightGray,
) {

    Row {

        for (i in 1..maxStars) {

            val icon = when {
                rating >= i -> R.drawable.ic_rating_star_filled
                rating >= i - 0.5 -> R.drawable.ic_rating_star_half
                else -> R.drawable.ic_rating_star_empty
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(starSize.dp),
                tint = Color.Unspecified
            )
        }
    }
}
@Composable
fun AppValueChangeRatingBar(
    rating: Float,
    maxStars: Int = 5,
    starSize: Int = 20,
    filledColor: Color = Color(0xFFFFCB2B),
    emptyColor: Color = Color.LightGray,
    onRatingChanged: (Float) -> Unit = {}
) {

    Row {

        for (i in 1..maxStars) {

            val icon = when {
                rating >= i -> R.drawable.ic_rating_star_filled
                rating >= i - 0.5 -> R.drawable.ic_rating_star_half
                else -> R.drawable.ic_rating_star_empty
            }

            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(starSize.dp).clickable {
                    onRatingChanged(i.toFloat())
                },
                tint = Color.Unspecified
            )
        }
    }
}