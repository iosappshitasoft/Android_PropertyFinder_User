package com.app.propertyfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.*

@Composable
fun HomeSearchBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchIconClick: () -> Unit = {},
    onFilterClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(27.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔥 Change icon based on text
            AppImage(
                if (searchText.isNotEmpty()) R.drawable.ic_close_red else R.drawable.search,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onSearchIconClick() }
            )

            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = {
                    AppTextRegular(
                        modifier = Modifier.fillMaxSize(),
                        text = "Search city, area or building",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                },
                modifier = Modifier.weight(1f).fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    fontFamily = AppFontFamily,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            )

            // 🔥 Filter Click Controlled by Parent
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE))
                    .clickable { onFilterClick() },
                contentAlignment = Alignment.Center
            ) {
                AppImage(R.drawable.menu_filter, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
