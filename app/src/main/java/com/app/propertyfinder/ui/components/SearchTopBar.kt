package com.app.propertyfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onBackClick: () -> Unit,
    onSearchDone: () -> Unit
) {
    TopAppBar(
        title = {
            SearchBarTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                onDone = onSearchDone
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
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

@Composable
fun SearchBarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Row (modifier = Modifier.fillMaxWidth().padding(end = 20.dp).clip(RoundedCornerShape(99.dp))
        .background(Color.White).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically){
        AppImage(resId = R.drawable.search, Modifier.size(20.dp), tint = TextSecondary)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.W500),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            cursorBrush = SolidColor(TextPrimary),
            modifier = Modifier.fillMaxWidth().height(49.dp),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        AppTextLight(text = "Search here")
                    }
                    innerTextField()
                }
            }
        )
    }
}





@Composable
fun SearchBarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String = "Search here",
    onDone: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(49.dp)
            .clip(RoundedCornerShape(99.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(99.dp))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppImage(
            resId = R.drawable.search,
            modifier = Modifier.size(20.dp),
            tint = TextSecondary
        )

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                fontSize = 12.sp,
                color = Color.Black
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            cursorBrush = SolidColor(Color.Black),
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        AppTextRegular(
                            text = hint,
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}