package com.app.propertyfinder.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.propertyfinder.R

@Composable
fun FilterDropDownChipRow(
    filters: Map<String, List<String>>,
    selectedValues: MutableMap<String, String>,
    onSelectionChanged: (Map<String, String>) -> Unit = {}
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        filters.forEach { (title, options) ->
            item {
                val currentValue = selectedValues[title]
                val isSelected = !currentValue.isNullOrEmpty()

                ChipDropdown(
                    title = title,
                    selectedValue = currentValue ?: title,
                    options = options,
                    isSelected = isSelected
                ) { value ->
                    selectedValues[title] = value
                    onSelectionChanged(selectedValues.toMap())
                }
            }
        }
    }
}

@Composable
fun ChipDropdown(
    title: String,
    selectedValue: String,
    options: List<String>,
    isSelected: Boolean,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) Color.Black else Color.White)
                .border(1.dp, Color.LightGray, RoundedCornerShape(20.dp))
                .clickable { expanded = true }
                .padding(horizontal = 20.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppTextMedium(
                text = selectedValue,
                fontSize = 14.sp,
                color = if (isSelected) Color.White else Color.Black
            )

            Spacer(modifier = Modifier.width(4.dp))

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color.Black
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Color.White
        ) {
            options.forEach { item ->
                DropdownMenuItem(
                    text = {
                        AppTextMedium(text = item, color = colorResource(R.color.textprimary))
                    },
                    onClick = {
                        expanded = false
                        onSelected(item)
                    }
                )
            }
        }
    }
}