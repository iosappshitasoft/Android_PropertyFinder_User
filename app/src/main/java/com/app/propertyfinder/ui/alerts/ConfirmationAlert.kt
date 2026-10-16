package com.app.propertyfinder.ui.alerts

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.theme.PrimaryColor

@Composable
fun ConfirmationAlert(
    title: String,
    yesText: String = "Yes",
    noText: String = "No",
    onYesClick: () -> Unit,
    onNoClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Close button
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                ) {
                    AppIcon(
                        resId = R.drawable.ic_cancel,
                        contentDescription = "Close",
                        tint = Color.Black,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AppTextBold(
                        text = title,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // No Button
                        Box(
                            modifier = Modifier
                                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onNoClick() }
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppTextBold(text = noText, fontSize = 10.sp, color = Color.Black)
                        }

                        AppSpacerH(16)

                        // Yes Button
                        Box(
                            modifier = Modifier
                                .background(PrimaryColor, RoundedCornerShape(8.dp))
                                .clickable { onYesClick() }
                                .padding(horizontal = 24.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AppTextBold(text = yesText, fontSize = 10.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
