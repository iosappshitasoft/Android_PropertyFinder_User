package com.app.propertyfinder.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.api.dto.response.ProjectPaymentPlanItem
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


data class PaymentItem(
    val icon: Int,
    val percentage: String,
    val title: String,
    val subtitle: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentPlansScreenPreview(){
    val navController = rememberNavController()
    PaymentPlansScreen(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentPlansScreen(navController: NavController) {
    val paymentPlansJson = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.get<String>("payment_plans_json")
    val plans = remember(paymentPlansJson) {
        if (paymentPlansJson.isNullOrBlank()) {
            emptyList()
        } else {
            runCatching {
                val listType = object : TypeToken<List<ProjectPaymentPlanItem>>() {}.type
                Gson().fromJson<List<ProjectPaymentPlanItem>>(paymentPlansJson, listType).orEmpty()
            }.getOrDefault(emptyList())
        }
    }
    var selectedIndex by remember(plans) { mutableStateOf(0) }
    val selectedPlan = plans.getOrNull(selectedIndex)
    val paymentList = remember(selectedPlan) {
        if (selectedPlan == null) {
            emptyList()
        } else {
            val items = mutableListOf<PaymentItem>()
            selectedPlan.downPayment?.let { down ->
                items.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_top,
                        percentage = "${down.percentage ?: 0}%",
                        title = "Down payment",
                        subtitle = "At sales launch"
                    )
                )
            }
            selectedPlan.duringConstruction?.installments.orEmpty().forEach { inst ->
                items.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_center,
                        percentage = "${inst.percentage ?: 0}%",
                        title = "During Construction",
                        subtitle = formatPaymentDate(inst.date)
                    )
                )
            }
            selectedPlan.onHandover?.let { handover ->
                items.add(
                    PaymentItem(
                        icon = R.drawable.ic_payment_bottom,
                        percentage = "${handover.percentage ?: 0}%",
                        title = "On Handover",
                        subtitle = "On Handover"
                    )
                )
            }
            items
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(
                        text = stringResource(R.string.payment_plans),
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Dropdown
            InstallmentDropdown(
                plans = plans,
                selectedIndex = selectedIndex,
                onSelected = { selectedIndex = it },
                installmentsCount = selectedPlan?.duringConstruction?.installments.orEmpty().size
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy((-20).dp),
                contentPadding = PaddingValues()
            ) {
                itemsIndexed(paymentList) { index, item ->
                    val isLastItem = index == paymentList.lastIndex
                    PaymentItemRow(item = item, index,isLastItem)
                }
            }
        }
    }
}

@Composable
fun PaymentItemRow(item: PaymentItem, position: Int, isLastItem: Boolean) {

    // 👇 Reduce opacity gradually
    val alphaValue = (1f - (position * 0.18f)).coerceIn(0.2f, 1f)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(85.dp), contentAlignment = Alignment.Center) {
            Icon(
                painter = painterResource(id = item.icon),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(alphaValue)
                    .offset(y = if (isLastItem) (-5).dp else 0.dp),
                tint = Color.Unspecified
            )
            AppTextMedium(text = item.percentage, color = Color.White, fontSize = 14.sp,)
        }
        AppSpacerH(4)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(bottom = 13.dp)
                .clip(RoundedCornerShape(size = 11.dp))
                .background(Color.White)
            ,
            verticalArrangement = Arrangement.Center
        ) {
            AppTextSemiBold(text = item.title, fontSize = 14.sp,
                modifier = Modifier.padding(start = 15.dp))
            AppSpacerV(4)
            AppTextLight(text = item.subtitle, fontSize = 12.sp, color = Color.Gray,
                modifier = Modifier.padding(start = 15.dp))
        }
    }
}
@Composable
fun InstallmentDropdown(
    plans: List<ProjectPaymentPlanItem>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    installmentsCount: Int
) {
    var expanded by remember { mutableStateOf(false) }
    val options = remember(plans) {
        plans.mapIndexed { index, plan -> plan.planName?.takeIf { it.isNotBlank() } ?: "Plan ${index + 1}" }
    }
    val selected = options.getOrNull(selectedIndex) ?: "No plans"

    var textFieldWidth by remember { mutableStateOf(0) }

    Column {

        Box (modifier = Modifier.padding(horizontal = 40.dp)){

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFieldWidth = coordinates.size.width
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White)
                    .clickable { expanded = true }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextSemiBold(text = selected, fontSize = 14.sp)

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldWidth.toDp() })
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
        AppTextLight(
            text = "$installmentsCount installments are available",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 5.dp).fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

private fun formatPaymentDate(value: String?): String {
    if (value.isNullOrBlank()) return ""
    val inputFormats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd"
    )
    val output = SimpleDateFormat("yyyy MMM", Locale.ENGLISH).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    inputFormats.forEach { pattern ->
        runCatching {
            val parser = SimpleDateFormat(pattern, Locale.ENGLISH).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = parser.parse(value)
            if (date != null) return output.format(date)
        }
    }
    return value
}
