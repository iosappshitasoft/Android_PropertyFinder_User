package com.app.propertyfinder.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.propertyfinder.ui.theme.DividerColor
import com.app.propertyfinder.ui.theme.TextSecondary

@Composable
fun OrDivider(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextSecondary,
    dividerColor: Color = DividerColor
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = dividerColor
        )

        AppTextMedium(
            text = text,
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Divider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp),
            color = dividerColor
        )
    }
}
