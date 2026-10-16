package com.app.propertyfinder.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.FloatRange
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.dto.response.MortgageQuoteCalculatorSnapshot
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.UnfocusBorderColor
import kotlin.math.roundToInt

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MortgageScreenPreView(){
    MortgageCalculatorScreen(navController = rememberNavController())
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageCalculatorScreen(navController: NavController) {
    val viewModel: PropertyViewModel = hiltViewModel()
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()

    var mortgageSelectedIndex by remember { mutableStateOf(0) }
    var selectedResidencyValue by remember { mutableStateOf<String?>(null) }
    var latestCalculatorSnapshot by remember {
        mutableStateOf(
            MortgageQuoteCalculatorSnapshot(
                purchasePrice = 0,
                residencyStatus = "",
                downPayment = 0,
                downPaymentPct = 0,
                loanAmount = 0,
                loanPeriod = 0,
                interestRate = 0,
                monthlyPayment = 0,
                totalInterest = 0
            )
        )
    }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getMasterData("residencystatus,pricerange") { response ->
            when (response) {
                is ApiResult.Success -> Unit
                is ApiResult.Error -> Unit
            }
        }
    }

    val residencyStatusList = masterDataState.data?.data?.residencyStatus.orEmpty()
    val mortgageChipList = if (residencyStatusList.isNotEmpty()) {
        residencyStatusList.map { it.name.orEmpty() }
    } else {
        // Fallback while API loads / returns empty
        listOf("UAE national", "UAE resident", "Non-resident")
    }

    // Keep selected value in sync with selected index + API result.
    LaunchedEffect(mortgageChipList, mortgageSelectedIndex) {
        val item = residencyStatusList.getOrNull(mortgageSelectedIndex)
        selectedResidencyValue = item?.value
    }

    val priceRanges = masterDataState.data?.data?.priceRange.orEmpty()
    val sliderMinPrice = priceRanges.firstOrNull()?.value?.toFloatOrNull() ?: 330000f
    val sliderMaxPrice = priceRanges.lastOrNull()?.value?.toFloatOrNull() ?: 200000000f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.mortgage_calculator), fontSize = 20.sp)
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
                actions = {
                    Box {
                        IconButton(onClick = {
                            expanded = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            shape = RoundedCornerShape(12.dp),
                            containerColor = Color.White,
                            offset = DpOffset(x = (-15).dp, y = 0.dp)   // 👈 Move left 15.dp
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.mortgage_dot_menu_icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Get a mortgage quote")
                                    }
                                },
                                onClick = {
                                    expanded = false
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "mortgage_calculator_snapshot",
                                        latestCalculatorSnapshot
                                    )
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "mortgage_calculator_residency_status",
                                        selectedResidencyValue.orEmpty()
                                    )
                                    navController.navigate(Route.MortgageQuote.route)
                                }   ,
                                modifier = Modifier.height(32.dp)
                            )
                        }

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
                .background(Color(0xFFF5F5F5))
                .padding(vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ✅ Scrollable Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                mortgageChipList.forEachIndexed { index, text ->

                    MortgageChip(
                        text = text,
                        isSelected = mortgageSelectedIndex == index
                    ) {
                        mortgageSelectedIndex = index
                        selectedResidencyValue = residencyStatusList.getOrNull(index)?.value
                    }
                }
            }

            AppSpacerV(35)

            MortgageCalculatorContent(
                priceMin = sliderMinPrice,
                priceMax = sliderMaxPrice,
                residencyStatusCode = selectedResidencyValue.orEmpty(),
                onSnapshotUpdated = { snapshot ->
                    latestCalculatorSnapshot = snapshot
                }
            )
        }
    }
}


@Composable
fun MortgageChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) TextPrimary else Color.White,
        border = if (!isSelected) BorderStroke(1.dp, UnfocusBorderColor) else null,
        modifier = Modifier.height(33.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            AppTextSemiBold(
                text = text,
                modifier = Modifier.padding(horizontal = 15.dp),
                color = if (isSelected) Color.White else TextPrimary,
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageCalculatorContent(
    priceMin: Float,
    priceMax: Float,
    residencyStatusCode: String,
    onSnapshotUpdated: (MortgageQuoteCalculatorSnapshot) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    var purchasePrice by remember { mutableStateOf(2000000f) }
    
    // Synchronize initial purchase price with master data minimum if current is below validation limit
    LaunchedEffect(priceMin) {
        if (purchasePrice < priceMin && priceMin > 0) {
            purchasePrice = priceMin
        }
    }
    var downPaymentPercent by remember { mutableStateOf(0.175f) } // 350,000 / 2,000,000
    var loanAmount by remember { mutableStateOf(0.825f) }
    var loanPeriod by remember { mutableStateOf(5) }
    var interestRate by remember { mutableStateOf(4.5f) }
    var showMortgageSheet by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val mortgageViewModel: com.app.propertyfinder.api.viewmodel.MortgageViewModel =
        hiltViewModel()
    val mortgageCalcState by mortgageViewModel.calculateState.collectAsStateWithLifecycle()

    var mortgageData by remember { mutableStateOf<com.app.propertyfinder.api.dto.response.MortgageCalculateData?>(null) }
    var apiSliderConfig by remember { mutableStateOf(mortgageData?.sliderConfig) }

    var upfrontCostsData by remember { mutableStateOf<com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsData?>(null) }

    val coroutineScope = rememberCoroutineScope()
    var lastRequestKey by remember { mutableStateOf<String?>(null) }

    // Keep request + UI values in range based on latest slider config.
    LaunchedEffect(mortgageCalcState.data?.data) {
        val newData = mortgageCalcState.data?.data
        if (newData != null) {
            mortgageData = newData
            apiSliderConfig = newData.sliderConfig
        }
    }

    // Call calculate API on any input change (chip/slider).
    LaunchedEffect(
        residencyStatusCode,
        purchasePrice,
        downPaymentPercent,
        loanPeriod,
        interestRate
    ) {
        if (residencyStatusCode.isBlank()) return@LaunchedEffect

        val purchaseInt = purchasePrice.toInt().coerceAtLeast(1)
        val downPaymentInt = (purchaseInt * downPaymentPercent).toInt().coerceAtLeast(0)

        val requestKey =
            "$purchaseInt|$residencyStatusCode|$downPaymentInt|$loanPeriod|$interestRate"

        if (lastRequestKey == requestKey) return@LaunchedEffect

        kotlinx.coroutines.delay(400)

        lastRequestKey = requestKey

        mortgageViewModel.calculate(
            request = com.app.propertyfinder.api.dto.response.MortgageCalculateRequest(
                purchasePrice = purchaseInt,
                residencyStatus = residencyStatusCode,
                downPayment = downPaymentInt,
                loanPeriod = loanPeriod,
                interestRate = interestRate.toDouble()
            )
        ) { }
    }

    LaunchedEffect(
        residencyStatusCode,
        purchasePrice,
        downPaymentPercent,
        loanAmount,
        loanPeriod,
        interestRate,
        mortgageData?.output?.monthlyPayment,
        mortgageData?.output?.totalInterest
    ) {
        onSnapshotUpdated(
            MortgageQuoteCalculatorSnapshot(
                purchasePrice = purchasePrice.toInt(),
                residencyStatus = residencyStatusCode,
                downPayment = (purchasePrice * downPaymentPercent).toInt(),
                downPaymentPct = (downPaymentPercent * 100).toInt(),
                loanAmount = (purchasePrice * loanAmount).toInt(),
                loanPeriod = loanPeriod,
                interestRate = interestRate.toInt(),
                monthlyPayment = (mortgageData?.output?.monthlyPayment ?: 0.0).toInt(),
                totalInterest = (mortgageData?.output?.totalInterest ?: 0.0).toInt()
            )
        )
    }

    val rotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f
    )

    LaunchedEffect(priceMin, priceMax, mortgageData?.sliderConfig) {
        val cfg = mortgageData?.sliderConfig
        val min = cfg?.purchasePrice?.min?.toFloat() ?: priceMin
        val max = cfg?.purchasePrice?.max?.toFloat() ?: priceMax
        purchasePrice = purchasePrice.coerceIn(min, max)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .padding(start = 35.dp, end = 35.dp)
        ) {

            val principalVal = mortgageData?.output?.principal ?: (purchasePrice * loanAmount).toDouble()
            val interestVal = mortgageData?.output?.interest ?: (mortgageData?.output?.totalInterest ?: 0.0)
            val totalVal = principalVal + interestVal
            
            val principalSweep = if (totalVal > 0) (principalVal / totalVal).toFloat() * 180f else 140f
            val interestSweep = 180f - principalSweep

            Canvas(modifier = Modifier.fillMaxSize()) {
                // Principal Arc
                drawArc(
                    color = PrimaryColor,
                    startAngle = 180f,
                    sweepAngle = principalSweep,
                    useCenter = false,
                    style = Stroke(width = 70f, cap = StrokeCap.Round)
                )

                // Interest Arc
                drawArc(
                    color = PrimaryBlue,
                    startAngle = 180f + principalSweep,
                    sweepAngle = interestSweep,
                    useCenter = false,
                    style = Stroke(width = 70f, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppTextMedium(text = "Principal", color = Color.Gray, fontSize = 13.sp)
                val principalAmountText =
                    (mortgageData?.output?.principal?.toInt() ?: (purchasePrice * loanAmount).toInt())
                        .toString().formatWithCommas()
                AppTextSemiBold(text = "${principalAmountText} AED", color = Color.Black, fontSize = 20.sp)
                AppSpacerV(8)

                Button(
                    onClick = {
                        showMortgageSheet = true
                        upfrontCostsData = null

                        if (residencyStatusCode.isBlank()) return@Button

                        val purchaseInt = purchasePrice.toInt()
                        val downPaymentInt =
                            (purchaseInt * downPaymentPercent).toInt()
                        val loanAmountInt =
                            (purchaseInt * loanAmount).toInt()
                        val loanPeriodInt = loanPeriod.toInt()
                        val interestRateDouble = interestRate.toDouble()

                        mortgageViewModel.calculateUpfrontCosts(
                            request = com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsRequest(
                                purchasePrice = purchaseInt,
                                downPayment = downPaymentInt,
                                loanAmount = loanAmountInt,
                                loanPeriod = loanPeriodInt,
                                interestRate = interestRateDouble,
                                residencyStatus = residencyStatusCode
                            )
                        ) { result ->
                            if (result is com.app.propertyfinder.api.network.ApiResult.Success) {
                                upfrontCostsData = result.body.data
                            }
                        }
                    },
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White
                    )
                ) {
                    AppTextMedium(text = "View upfront costs", color = Color.Black)
                }

                if (showMortgageSheet) {
                    MortgageBottomSheet(
                        sheetState = sheetState,
                        onDismiss = { showMortgageSheet = false },
                        upfrontCostsData = upfrontCostsData,
                        mortgageData = mortgageData,
                        downPaymentAmount = (purchasePrice * downPaymentPercent).toInt()
                    )
                }
            }
        }

        // Legend
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.offset(y = (-50).dp)
        ) {
            LegendItem(PrimaryColor, "Principal")
            LegendItem(PrimaryBlue, "Interest")
        }

        // Sliders Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-20).dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                val sliderConfig = mortgageData?.sliderConfig
                val purchaseSafe = purchasePrice.coerceAtLeast(1f)

                val purchaseRangeMin = sliderConfig?.purchasePrice?.min?.toFloat() ?: purchasePrice
                val purchaseRangeMax = sliderConfig?.purchasePrice?.max?.toFloat() ?: (purchasePrice * 1.2f)

                val downMinAmt =
                    sliderConfig?.downPayment?.min?.toFloat() ?: (purchaseSafe * 0.1f)
                val downMaxAmt =
                    sliderConfig?.downPayment?.max?.toFloat() ?: (purchaseSafe * 0.8f)
                val downMinPct = (downMinAmt / purchaseSafe).coerceIn(0f, 1f)
                val downMaxPct = (downMaxAmt / purchaseSafe).coerceIn(0f, 1f)
                val downRange = minOf(downMinPct, downMaxPct)..maxOf(downMinPct, downMaxPct)

                val loanMinAmt =
                    sliderConfig?.loanAmount?.min?.toFloat() ?: (purchaseSafe * 0.1f)
                val loanMaxAmt =
                    sliderConfig?.loanAmount?.max?.toFloat() ?: (purchaseSafe * 0.8f)
                val loanMinPct = (loanMinAmt / purchaseSafe).coerceIn(0f, 1f)
                val loanMaxPct = (loanMaxAmt / purchaseSafe).coerceIn(0f, 1f)
                val loanRange = minOf(loanMinPct, loanMaxPct)..maxOf(loanMinPct, loanMaxPct)

                val loanPeriodRange = (sliderConfig?.loanPeriod?.min ?: 1)..(sliderConfig?.loanPeriod?.max ?: 25)
                val interestRange = (sliderConfig?.interestRate?.min?.toFloat() ?: 1.0f)..(sliderConfig?.interestRate?.max?.toFloat() ?: 10f)

                // 🔹 Purchase Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = "Purchase price", color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${
                            purchasePrice.toInt().toString().formatWithCommas()
                        } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )
                }

                CustomMortgageSlider(
                    value = purchasePrice,
                    onValueChange = { purchasePrice = it },
                    valueRange = purchaseRangeMin..purchaseRangeMax
                )

                AppSpacerV(5)

                // 🔹 Down Payment Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = "Down payment", color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    Row {
                        AppTextMedium(
                            text = "${
                                (purchasePrice * downPaymentPercent).toInt().toString()
                                    .formatWithCommas()
                            } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                        )
                        val downPct = (downPaymentPercent * 100).roundToInt()
                        AppTextMedium(
                            text = "($downPct%)",
                            color = colorResource(R.color.splash_primary), fontSize = 14.sp
                        )
                    }
                }

                CustomMortgageSlider(
                    value = downPaymentPercent,
                    onValueChange = { newDown ->
                        val clampedDown = newDown.coerceIn(0f, 1f)
                        downPaymentPercent = clampedDown
                        loanAmount = (1f - clampedDown).coerceIn(0f, 1f)
                    },
                    valueRange = 0f..1f
                )

                AppSpacerV(5)

                // 🔹 Remaining Sliders (Expandable)
                if (isExpanded) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppTextLight(
                            text = "Loan amount", color = colorResource(R.color.textlight),
                            fontSize = 13.sp
                        )
                        Row {
                            AppTextMedium(
                                text = "${
                                    (purchasePrice * loanAmount).toInt().toString()
                                        .formatWithCommas()
                                } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                            )
                            val downPct = (downPaymentPercent * 100).roundToInt()
                            val loanPct = 100 - downPct
                            AppTextMedium(
                                text = "($loanPct%)",
                                color = colorResource(R.color.splash_primary), fontSize = 14.sp
                            )
                        }
                    }
                    CustomMortgageSlider(
                        value = loanAmount,
                        onValueChange = { newLoan ->
                            val clampedLoan = newLoan.coerceIn(0f, 1f)
                            loanAmount = clampedLoan
                            downPaymentPercent = (1f - clampedLoan).coerceIn(0f, 1f)
                        },
                        valueRange = 0f..1f
                    )
                    AppSpacerV(5)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppTextLight(
                            text = "Loan period", color = colorResource(R.color.textlight),
                            fontSize = 13.sp
                        )
                        Row {
                            AppTextMedium(
                                text = "${loanPeriod} Years",
                                color = colorResource(R.color.textprimary),
                                fontSize = 14.sp
                            )
                        }
                    }
                    CustomMortgageIntSlider(
                        value = loanPeriod.toInt(),
                        onValueChange = { loanPeriod = it.toInt() },
                        valueRange = loanPeriodRange
                    )
                    AppSpacerV(5)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AppTextLight(
                            text = "Interest rate", color = colorResource(R.color.textlight),
                            fontSize = 13.sp
                        )
                        Row {
                            AppTextMedium(
                                text = "${interestRate}",
                                color = colorResource(R.color.textprimary),
                                fontSize = 14.sp
                            )
                        }
                    }
                    CustomMortgageInterestSlider(
                        value = interestRate,
                        onValueChange = { interestRate = it },
                        valueRange = interestRange
                    )
                }

                IconButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_down),
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(rotation)
                            .size(16.dp),
                        tint = Color.Unspecified
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        // Bottom Info Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val monthlyPaymentValue =
                mortgageData?.output?.monthlyPayment?.toInt() ?: 81336
            val totalInterestValue =
                mortgageData?.output?.totalInterest?.toInt() ?: 23904
            val interestRateValue =
                mortgageData?.input?.interestRate?.toDouble() ?: interestRate.toDouble()
            val interestRateText =
                String.format(java.util.Locale.US, "%.2f", interestRateValue)

            InfoCard(
                title = "Monthly payment",
                value = "${monthlyPaymentValue.toString().formatWithCommas()} AED",
                modifier = Modifier.weight(1f)
            )

            InfoCard(
                title = "Interest (${interestRateText}%)",
                value = "${totalInterestValue.toString().formatWithCommas()} AED",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Red Button
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFEA3934)
            )
        ) {
            Text(
                "${mortgageData?.output?.propertiesInBudget ?: 2116} properties found",
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMortgageSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Slider(
        value = value,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        valueRange = valueRange,

        thumb = {
            Image(
                painter = painterResource(id = R.drawable.price_slider_round),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },

        track = { sliderState ->

            val fraction =
                (sliderState.value - sliderState.valueRange.start) /
                        (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        colorResource(R.color.slider_unselected),
                        RoundedCornerShape(50)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            colorResource(R.color.black),
                            RoundedCornerShape(50)
                        )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMortgageIntSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { newValue ->
            onValueChange(newValue.toInt())
        },
        valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
        thumb = {
            Image(
                painter = painterResource(id = R.drawable.price_slider_round),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },

        track = { sliderState ->

            val fraction =
                (sliderState.value - sliderState.valueRange.start) /
                        (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        colorResource(R.color.slider_unselected),
                        RoundedCornerShape(50)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            colorResource(R.color.black),
                            RoundedCornerShape(50)
                        )
                )
            }
        }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomMortgageInterestSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Slider(
        value = value.toFloat(),
        onValueChange = { newValue ->
            // 🔴 NEW: Round value to nearest 0.5
            val stepped = (newValue / 0.5f).roundToInt() * 0.5f
            onValueChange(stepped)
        },
        valueRange = valueRange,
        steps = ((valueRange.endInclusive - valueRange.start) / 0.5f).toInt() - 1,

        thumb = {
            Image(
                painter = painterResource(id = R.drawable.price_slider_round),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },

        track = { sliderState ->

            val fraction =
                (sliderState.value - sliderState.valueRange.start) /
                        (sliderState.valueRange.endInclusive - sliderState.valueRange.start)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        colorResource(R.color.slider_unselected),
                        RoundedCornerShape(50)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(
                            colorResource(R.color.black),
                            RoundedCornerShape(50)
                        )
                )
            }
        }
    )
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(15.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color)
        )
        AppSpacerH(5)
        AppTextRegular(text = text, fontSize = 12.sp)
    }
}

@Composable
fun InfoCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    upfrontCostsData: com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsData?,
    mortgageData: com.app.propertyfinder.api.dto.response.MortgageCalculateData?,
    downPaymentAmount: Int
) {

    // if keyboard is opened first hide keyboard after hided bottom sheet
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isKeyboardOpen by remember { mutableStateOf(false) }

    var selectedRating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }

    val downPaymentValue = upfrontCostsData?.paymentBreakdown?.downPayment ?: downPaymentAmount
    val totalPurchaseCostsValue = upfrontCostsData?.paymentBreakdown?.totalPurchaseCosts
        ?: (mortgageData?.output?.totalPayment?.toInt() ?: 0)

    // Keep existing hardcoded values as fallback while upfront-costs is loading.
    val landDepartmentFeeValue =
        upfrontCostsData?.paymentBreakdown?.landDepartmentFee ?: 3_798_580
    val registrationTrusteeFeeValue =
        upfrontCostsData?.paymentBreakdown?.registrationTrusteeFee ?: 4_200
    val mortgageRegistrationFeeValue =
        upfrontCostsData?.paymentBreakdown?.mortgageRegistrationFee ?: 178_041
    val realEstateAgencyFeeValue =
        upfrontCostsData?.paymentBreakdown?.realEstateAgencyFee ?: 1_993_950
    val bankArrangementFeeValue =
        upfrontCostsData?.paymentBreakdown?.mortgageArrangementFee ?: 747_731
    val mortgageValuationFeeValue =
        upfrontCostsData?.paymentBreakdown?.adminFee ?: 3_150

    val totalAmountRequiredUpfront =
        upfrontCostsData?.totalAmountRequiredUpfront ?: 30_463_153

    val downPaymentText = "${downPaymentValue.toString().formatWithCommas()} AED"
    val totalPurchaseCostsText = "${totalPurchaseCostsValue.toString().formatWithCommas()} AED"

    BackHandler {
        if (isKeyboardOpen) {
            keyboardController?.hide()
            focusManager.clearFocus()
        } else {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextBold(text = stringResource(R.string.estimated_upfront))
            AppSpacerV(15)
            AppTextSemiBold(text = stringResource(R.string.payment_breakdown))
            AppSpacerV(10)
            Card( shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(colorResource(R.color.screen_bg_color))
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PaymentBreakdownRow(stringResource(R.string.down_payment),
                        downPaymentText,colorResource(R.color.screen_bg_color))
                }
            }
            AppSpacerV(8)

            Card( shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(colorResource(R.color.screen_bg_color))
            ) {
                Column(
                    modifier = Modifier.padding(5.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PaymentBreakdownRow(stringResource(R.string.total_purchase_costs),
                        totalPurchaseCostsText,colorResource(R.color.screen_bg_color))
                    PaymentBreakdownRow(stringResource(R.string.land_department_fee),
                        "${landDepartmentFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))
                    PaymentBreakdownRow(stringResource(R.string.registration_trustee_fee),
                        "${registrationTrusteeFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))
                    PaymentBreakdownRow(stringResource(R.string.mortgage_registration_fee),
                        "${mortgageRegistrationFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))
                    PaymentBreakdownRow(stringResource(R.string.real_estate_agency_fee),
                        "${realEstateAgencyFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))
                    PaymentBreakdownRow(stringResource(R.string.bank_arrangement_fee),
                        "${bankArrangementFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))
                    PaymentBreakdownRow(stringResource(R.string.mortgage_valuation_fee),
                        "${mortgageValuationFeeValue.toString().formatWithCommas()} AED",colorResource(R.color.white))

                }
            }
            AppSpacerV(15)
            AppTextRegular(text = stringResource(R.string.payment_breakdown))
            AppSpacerV(10)
            Card( shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(colorResource(R.color.screen_bg_color))
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 5.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    PaymentBreakdownRow1(stringResource(R.string.total_purchase_costs),
                        "4,936 AED",colorResource(R.color.screen_bg_color))
                    AppDivider(thickness = 0.5.dp, color = Color(0xFFE0DFDE))
                    PaymentBreakdownRow1(stringResource(R.string.land_department_fee),
                        "3.75%",colorResource(R.color.screen_bg_color))
                    AppDivider(thickness = 0.5.dp, color = Color(0xFFE0DFDE))
                    PaymentBreakdownRow1(stringResource(R.string.registration_trustee_fee),
                        "71,215,500 AED",colorResource(R.color.screen_bg_color))
                    AppDivider(thickness = 0.5.dp, color = Color(0xFFE0DFDE))
                    PaymentBreakdownRow1(stringResource(R.string.mortgage_registration_fee),
                        "25 years",colorResource(R.color.screen_bg_color))
                }
            }

            AppSpacerV(20)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                colorResource(R.color.screen_bg_color)
                            )
                        )
                    )
            )
            AppSpacerV(20)
            AppTextLight(
                text = stringResource(R.string.total_amount_required),
                fontSize = 13.sp,
                color = Color.Gray
            )
            AppSpacerV(4)

            AppTextBold(
                text = "${totalAmountRequiredUpfront.toString().formatWithCommas()} AED",
                color = colorResource(R.color.splash_primary)
            )
        }
    }
}
@Composable
fun PaymentBreakdownRow(title: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(color)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppTextLight(text = title, fontSize = 12.sp, color = colorResource(R.color.textlight))

        AppTextSemiBold(text = value, fontSize = 14.sp)
    }
}
@Composable
fun PaymentBreakdownRow1(title: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(color)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AppTextLight(text = title, fontSize = 12.sp, color = colorResource(R.color.textlight))

        AppTextSemiBold(text = value, fontSize = 14.sp)
    }
}

fun String.formatWithCommas(): String {
    return "%,d".format(this.toInt())
}





