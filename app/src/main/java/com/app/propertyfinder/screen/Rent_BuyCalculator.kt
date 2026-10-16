package com.app.propertyfinder.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.RentVsBuyAdvancedRequest
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateData
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateRequest
import com.app.propertyfinder.api.dto.response.RentVsBuyMonthlyPaymentBreakdown
import com.app.propertyfinder.api.dto.response.RentVsBuyPaymentBreakdown
import com.app.propertyfinder.api.dto.response.RentVsBuyYearlyComparisonItem
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.RentVsBuyViewModel
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.components.AppDivider
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryBlue
import com.app.propertyfinder.ui.theme.TextPrimary
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

@Preview
@Composable
fun RentButCalculatorPreview(){
    RentBuyCalculatorScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentBuyCalculatorScreen(navController: NavController){
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val viewModel: PropertyViewModel = hiltViewModel()
    val rentVsBuyViewModel: RentVsBuyViewModel = hiltViewModel()
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val rentVsBuyCalculateState by rentVsBuyViewModel.calculateState.collectAsStateWithLifecycle()
    val rentVsBuyBreakdownState by rentVsBuyViewModel.paymentBreakdownState.collectAsStateWithLifecycle()

    var chipSelectedIndex by remember { mutableStateOf(0) }
    var selectedResidencyValue by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getMasterData("residencystatus") { response ->
            when (response) {
                is ApiResult.Success -> Unit
                is ApiResult.Error -> Unit
            }
        }
    }

    val residencyStatusList = masterDataState.data?.data?.residencyStatus.orEmpty()
    val topViewChipList = if (residencyStatusList.isNotEmpty()) {
        residencyStatusList.map { it.name.orEmpty() }
    } else {
        listOf(
            "UAE national",
            "UAE resident",
            "Non-resident"
        )
    }

    // Keep selected value in sync with selected index + API result.
    LaunchedEffect(topViewChipList, chipSelectedIndex) {
        val item = residencyStatusList.getOrNull(chipSelectedIndex)
        selectedResidencyValue = item?.value
    }

    val sliderMinPrice = 330000f
    val sliderMaxPrice = 200000000f

    var annualRentPrice by remember { mutableStateOf(50000f) }
    var purchasePrice by remember { mutableStateOf(330000f) }
    var downPayment by remember { mutableStateOf(0.20f) }
    var mortgageLoanPeriod by remember { mutableStateOf(25) }
    var fixedMortgagePeriod by remember { mutableStateOf(25) }
    var fixedInterestRate by remember { mutableStateOf(5) }
    var reversionRate by remember { mutableStateOf(5) }
    var annualHomePrice by remember { mutableStateOf(0) }
    var annualRentRate by remember { mutableStateOf(3) }
    var isPriceInitialized by remember { mutableStateOf(false) }
    var lastRequestKey by remember { mutableStateOf<String?>(null) }

    val rawCalculateData = rentVsBuyCalculateState.data?.data
    var lastValidCalculateData by remember { mutableStateOf<RentVsBuyCalculateData?>(null) }
    if (rawCalculateData != null) {
        lastValidCalculateData = rawCalculateData
    }
    val calculateData: RentVsBuyCalculateData? = lastValidCalculateData

    val rawBreakdownData = rentVsBuyBreakdownState.data?.data?.paymentBreakdown
    var lastValidBreakdownData by remember { mutableStateOf<RentVsBuyPaymentBreakdown?>(null) }
    if (rawBreakdownData != null) {
        lastValidBreakdownData = rawBreakdownData
    }
    val paymentBreakdownData: RentVsBuyPaymentBreakdown? = lastValidBreakdownData ?: calculateData?.paymentBreakdown

    val sliderConfig = rawCalculateData?.sliderConfig ?: calculateData?.sliderConfig

    var annualRentRange by remember { mutableStateOf(10000f..1000000f) }
    var purchaseRange by remember { mutableStateOf(330000f..200000000f) }

    LaunchedEffect(sliderConfig) {
        sliderConfig?.let { config ->
            val rMin = config.monthlyRent?.min?.toFloat()?.times(12f) ?: 0f
            val rMax = config.monthlyRent?.max?.toFloat()?.times(12f) ?: sliderMaxPrice
            annualRentRange = rMin..rMax

            val pMin = config.purchasePrice?.min?.toFloat() ?: sliderMinPrice
            val pMax = config.purchasePrice?.max?.toFloat() ?: sliderMaxPrice
            purchaseRange = pMin..pMax
        }
    }

    val annualRentMin = annualRentRange.start
    val annualRentMax = annualRentRange.endInclusive
    val purchaseMin = purchaseRange.start
    val purchaseMax = purchaseRange.endInclusive

    val purchaseSafe = purchasePrice.coerceAtLeast(1f)
    val downMinPct = 0.20f
    val downMaxPct = 0.80f
    val downRange = downMinPct..downMaxPct
    val loanPeriodRange = 1..25
    val interestRange = 1..10
    val annualRentRateRange = -20..20
    val annualHomePriceRange = -20..20

    LaunchedEffect(sliderMinPrice, sliderMaxPrice) {
        if (!isPriceInitialized) {
            isPriceInitialized = true
        }
    }

    LaunchedEffect(sliderConfig) {
        if (!isPriceInitialized || sliderConfig == null) return@LaunchedEffect
        purchasePrice = purchasePrice.coerceIn(purchaseMin, purchaseMax)
        annualRentPrice = annualRentPrice.coerceIn(annualRentMin, annualRentMax)
        downPayment = downPayment.coerceIn(downRange.start, downRange.endInclusive)
        mortgageLoanPeriod = mortgageLoanPeriod.coerceIn(loanPeriodRange.first, loanPeriodRange.last)
        fixedMortgagePeriod = fixedMortgagePeriod.coerceIn(loanPeriodRange.first, loanPeriodRange.last)
        fixedInterestRate = fixedInterestRate.coerceIn(interestRange.first, interestRange.last)
        reversionRate = reversionRate.coerceIn(interestRange.first, interestRange.last)
        annualRentRate = annualRentRate.coerceIn(annualRentRateRange.first, annualRentRateRange.last)
        annualHomePrice = annualHomePrice.coerceIn(annualHomePriceRange.first, annualHomePriceRange.last)
    }

    LaunchedEffect(
        selectedResidencyValue,
        annualRentPrice,
        purchasePrice,
        downPayment,
        mortgageLoanPeriod,
        fixedMortgagePeriod,
        fixedInterestRate,
        reversionRate,
        annualHomePrice,
        annualRentRate,
        isPriceInitialized
    ) {
        val residencyCode = selectedResidencyValue.orEmpty()
        if (!isPriceInitialized || residencyCode.isBlank()) return@LaunchedEffect

        val purchaseInt = purchasePrice.roundToInt().coerceAtLeast(1)
        val annualRentInt = annualRentPrice.roundToInt().coerceAtLeast(1)
        val monthlyRentInt = (annualRentInt / 12f).roundToInt().coerceAtLeast(1)
        val downPaymentInt = (purchaseInt * downPayment).roundToInt().coerceAtLeast(0)

        val request = RentVsBuyCalculateRequest(
            purchasePrice = purchaseInt,
            residencyStatus = residencyCode,
            downPayment = downPaymentInt,
            mortgageLoanPeriod = mortgageLoanPeriod,
            annualRent = annualRentInt,
            advanced = RentVsBuyAdvancedRequest(
                fixedMortgagePeriod = fixedMortgagePeriod,
                fixedInterestRate = fixedInterestRate.toDouble(),
                reversionRate = reversionRate.toDouble(),
                annualRentGrowthRate = annualRentRate,
                annualHomePriceGrowthRate = annualHomePrice
            )
        )

        val requestKey = listOf(
            request.purchasePrice,
            request.residencyStatus,
            request.downPayment,
            request.mortgageLoanPeriod,
            request.annualRent,
            request.advanced.fixedMortgagePeriod,
            request.advanced.fixedInterestRate,
            request.advanced.reversionRate,
            request.advanced.annualRentGrowthRate,
            request.advanced.annualHomePriceGrowthRate
        ).joinToString("|")

        if (requestKey == lastRequestKey) return@LaunchedEffect
        delay(350)
        lastRequestKey = requestKey

        rentVsBuyViewModel.calculate(request) { }
    }

    var actionExpanded by remember { mutableStateOf(false) }
    var isComparison by remember { mutableStateOf(true) }
    var showBreakdownSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.rent_vs_buy_calculator), fontSize = 20.sp)
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
                            actionExpanded = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu"
                            )
                        }

                        DropdownMenu(
                            expanded = actionExpanded,
                            onDismissRequest = { actionExpanded = false },
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
                                    actionExpanded = false
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                topViewChipList.forEachIndexed { index, text ->

                    MortgageChip(
                        text = text,
                        isSelected = chipSelectedIndex == index
                    ) {
                        chipSelectedIndex = index
                        selectedResidencyValue = residencyStatusList.getOrNull(index)?.value
                    }
                }
            }

            AppSpacerV(35)

            Column (modifier = Modifier.padding(horizontal = 20.dp)){
                Row {
                    if (isComparison){
                        AppTextBold(text = stringResource(R.string.comparison), color = Color(0xFFEA3934), fontSize =  15.sp,
                        modifier = Modifier.clickable{
                            isComparison = true
                        })
                    } else {
                        AppTextMedium(text = stringResource(R.string.comparison), color = TextPrimary.copy(alpha = 0.3f), fontSize =  13.sp,
                            modifier = Modifier.clickable{
                                isComparison = true
                            })
                    }

                    AppSpacerH(15)

                    if (isComparison){
                        AppTextMedium(
                            text = stringResource(R.string.monthly_payment),
                            color = TextPrimary.copy(alpha = 0.3f), fontSize =  13.sp,
                            modifier = Modifier.clickable{
                                isComparison = false
                            })
                    } else {
                        AppTextBold(text = stringResource(R.string.monthly_payment), color = Color(0xFFEA3934), fontSize = 15.sp,
                            modifier = Modifier.clickable{
                                isComparison = false
                            })
                    }
                }

                if (isComparison){
                    ComparisonChartWithTime(
                        yearlyComparison = calculateData?.yearlyComparison.orEmpty()
                    )
                } else {
                    MonthlyPaymentChart(
                        monthlyBreakdown = paymentBreakdownData?.monthly
                    )
                }
                AppSpacerV(20)
                Row (modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center){
                    Button(
                        onClick = {
                            val residencyCode = selectedResidencyValue.orEmpty()
                            if (residencyCode.isNotBlank()) {
                                val purchaseInt = purchasePrice.roundToInt().coerceAtLeast(1)
                                val annualRentInt = annualRentPrice.roundToInt().coerceAtLeast(1)
                                val monthlyRentInt = (annualRentInt / 12f).roundToInt().coerceAtLeast(1)
                                val downPaymentInt = (purchaseInt * downPayment).roundToInt().coerceAtLeast(0)
                                rentVsBuyViewModel.calculatePaymentBreakdown(
                                    request = RentVsBuyCalculateRequest(
                                        purchasePrice = purchaseInt,
                                        residencyStatus = residencyCode,
                                        downPayment = downPaymentInt,
                                        loanPeriod = mortgageLoanPeriod,
                                        mortgageLoanPeriod = mortgageLoanPeriod,
                                        interestRate = fixedInterestRate.toDouble(),
                                        monthlyRent = monthlyRentInt,
                                        annualRent = annualRentInt,
                                        comparisonYears = mortgageLoanPeriod,
                                        rentIncreaseAnnualPct = annualRentRate,
                                        advanced = RentVsBuyAdvancedRequest(
                                            fixedMortgagePeriod = fixedMortgagePeriod,
                                            fixedInterestRate = fixedInterestRate.toDouble(),
                                            reversionRate = reversionRate.toDouble(),
                                            annualRentGrowthRate = annualRentRate,
                                            annualHomePriceGrowthRate = annualHomePrice
                                        )
                                    )
                                ) { }
                            }
                            showBreakdownSheet = true
                        },
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.darkBlue)
                        )
                    ) {
                        AppTextMedium(stringResource(R.string.payment_breakdown), color = Color.White)
                    }
                }
                if (showBreakdownSheet) {
                    BreakdownBottomSheet (
                        sheetState = sheetState,
                        onDismiss = { showBreakdownSheet = false },
                        paymentBreakdown = paymentBreakdownData
                    )
                }
                AppSpacerV(20)
                PriceSliderView(
                    annualRentPrice = annualRentPrice,
                    onAnnualRentPriceChange = { annualRentPrice = it },
                    purchasePrice = purchasePrice,
                    onPurchasePriceChange = { purchasePrice = it },
                    downPayment = downPayment,
                    onDownPaymentChange = { downPayment = it },
                    mortgageLoanPeriod = mortgageLoanPeriod,
                    onMortgageLoanPeriodChange = { mortgageLoanPeriod = it },
                    fixedMortgagePeriod = fixedMortgagePeriod,
                    onFixedMortgagePeriodChange = { fixedMortgagePeriod = it },
                    fixedInterestRate = fixedInterestRate,
                    onFixedInterestRateChange = { fixedInterestRate = it },
                    reversionRate = reversionRate,
                    onReversionRateChange = { reversionRate = it },
                    annualHomePrice = annualHomePrice,
                    onAnnualHomePriceChange = { annualHomePrice = it },
                    annualRentRate = annualRentRate,
                    onAnnualRentRateChange = { annualRentRate = it },
                    annualRentRange = annualRentMin..annualRentMax,
                    purchaseRange = purchaseMin..purchaseMax,
                    downPaymentRange = downRange,
                    mortgageLoanPeriodRange = loanPeriodRange,
                    fixedMortgagePeriodRange = loanPeriodRange,
                    fixedInterestRateRange = interestRange,
                    reversionRateRange = interestRange,
                    annualHomePriceRange = annualHomePriceRange,
                    annualRentRateRange = annualRentRateRange
                )
                AppSpacerV(16)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val savedIfBuying =
                        abs(calculateData?.totals?.differenceRentMinusBuy ?: 0.0).toInt().toString().formatWithCommas()
                    val lostIfRenting =
                        abs(calculateData?.totals?.differenceRentMinusNetBuy ?: 0.0).toInt().toString().formatWithCommas()
                    InfoCard(title = stringResource(R.string.saved_if_buying), value = "$savedIfBuying AED", modifier = Modifier.weight(1f))
                    InfoCard(title = stringResource(R.string.lost_if_renting), value = "$lostIfRenting AED", modifier = Modifier.weight(1f))
                }
                AppSpacerV(15)
            }
        }
    }
}

@Composable
fun ComparisonChartWithTime(
    yearlyComparison: List<RentVsBuyYearlyComparisonItem>
) {
    if (yearlyComparison.isEmpty()) return

    val rentColor = Color(0xFFFF002E)
    val buyColor = Color(0xFF2E00AD)
    
    val rawMaxCost = yearlyComparison.maxOfOrNull { maxOf(it.cumulativeRent, it.cumulativeBuy) } ?: 1.0
    val maxCost by animateFloatAsState(targetValue = rawMaxCost.toFloat(), label = "maxCost")

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val labelWidth = 60.dp.toPx()
                val chartWidth = width - labelWidth - 20.dp.toPx()
                val chartHeight = height - 40.dp.toPx()
                val startX = labelWidth
                val midY = height / 2f
                
                // 1. Draw Grid Lines and Dynamic Labels
                val labelScale = if (maxCost > 1_000_000) 1_000_000.0 else 1_000.0
                val labelSuffix = if (maxCost > 1_000_000) " mln" else " k"
                
                val topLabelValue = (maxCost / labelScale)
                val midLabelValue = (topLabelValue / 2.0)
                
                val labels = listOf(
                    "${"%.1f".format(java.util.Locale.US, topLabelValue)}$labelSuffix",
                    "${"%.1f".format(java.util.Locale.US, midLabelValue)}$labelSuffix",
                    "-${"%.1f".format(java.util.Locale.US, midLabelValue)}$labelSuffix",
                    "-${"%.1f".format(java.util.Locale.US, topLabelValue)}$labelSuffix"
                )
                val yPositions = listOf(0.1f, 0.3f, 0.7f, 0.9f)
                
                yPositions.forEachIndexed { index, fraction ->
                    val y = height * fraction
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.5f),
                        start = Offset(startX, y),
                        end = Offset(width - 20.dp.toPx(), y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    
                    drawContext.canvas.nativeCanvas.drawText(
                        labels[index],
                        10.dp.toPx(),
                        y + 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 10.sp.toPx()
                            textAlign = android.graphics.Paint.Align.LEFT
                            isAntiAlias = true
                        }
                    )
                }

                // 1.5 Draw Vertical Axis Line
                drawLine(
                    color = Color.LightGray,
                    start = Offset(startX, 10.dp.toPx()),
                    end = Offset(startX, height - 10.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )

                // 1.6 Draw Central Horizontal Axis (Solid)
                drawLine(
                    color = Color.LightGray.copy(alpha = 0.5f),
                    start = Offset(startX, midY),
                    end = Offset(width - 20.dp.toPx(), midY),
                    strokeWidth = 1.dp.toPx()
                )

                // 2. Define Paths based on actual data
                val redPath = androidx.compose.ui.graphics.Path()
                val bluePath = androidx.compose.ui.graphics.Path()
                val redArea = androidx.compose.ui.graphics.Path()
                val blueArea = androidx.compose.ui.graphics.Path()

                redArea.moveTo(startX, midY)
                blueArea.moveTo(startX, midY)

                yearlyComparison.forEachIndexed { index, item ->
                    val x = startX + (index.toFloat() / (yearlyComparison.size - 1)) * chartWidth
                    val yRent = midY - (item.cumulativeRent.toFloat() / maxCost.toFloat()) * (midY - 20.dp.toPx())
                    val yBuy = midY + (item.cumulativeBuy.toFloat() / maxCost.toFloat()) * (height - 20.dp.toPx() - midY)

                    if (index == 0) {
                        redPath.moveTo(x, yRent)
                        bluePath.moveTo(x, yBuy)
                    } else {
                        redPath.lineTo(x, yRent)
                        bluePath.lineTo(x, yBuy)
                    }
                    redArea.lineTo(x, yRent)
                    blueArea.lineTo(x, yBuy)
                }

                redArea.lineTo(startX + chartWidth, midY)
                redArea.close()
                blueArea.lineTo(startX + chartWidth, midY)
                blueArea.close()

                // 3. Draw Areas (Fills)
                drawPath(
                    path = redArea,
                    brush = Brush.verticalGradient(
                        colors = listOf(rentColor.copy(alpha = 0.4f), Color.Transparent),
                        startY = 20.dp.toPx(),
                        endY = midY
                    )
                )

                drawPath(
                    path = blueArea,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, buyColor.copy(alpha = 0.4f)),
                        startY = midY,
                        endY = height - 20.dp.toPx()
                    )
                )

                // 4. Draw the actual lines on TOP
                drawPath(
                    path = redPath,
                    color = rentColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.5.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
                drawPath(
                    path = bluePath,
                    color = buyColor,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 1.5.dp.toPx(),
                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                )
            }
        }

        AppSpacerV(20)

        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically){
            LegendItem(stringResource(R.string.lost_if_renting), rentColor)
            AppSpacerH(25)
            LegendItem(stringResource(R.string.saved_if_buying), buyColor)
        }
    }
}

@Composable
fun MonthlyPaymentChart(
    monthlyBreakdown: RentVsBuyMonthlyPaymentBreakdown?
) {
    val rentColor = Color(0xFFFF002E)
    val buyOtherColor = Color(0xFF2E00AD)
    val buyInterestColor = Color(0xFF6472E0)
    val buyPrincipalColor = Color(0xFFA5B1F5)

    val rawRentAmt = monthlyBreakdown?.rentPayment ?: 0.0
    val rawPrincipalAmt = monthlyBreakdown?.mortgagePrincipal ?: 0.0
    val rawInterestAmt = monthlyBreakdown?.mortgageInterest ?: 0.0
    val rawOtherAmt = (monthlyBreakdown?.maintenance ?: 0.0) +
            (monthlyBreakdown?.homeInsurance ?: 0.0) +
            (monthlyBreakdown?.serviceCharges ?: 0.0) +
            (monthlyBreakdown?.mortgageProtection ?: 0.0)
    
    val totalBuy = rawPrincipalAmt + rawInterestAmt + rawOtherAmt
    val rawMaxVal = maxOf(rawRentAmt, totalBuy).coerceAtLeast(1.0) * 1.2 // 20% headroom

    val rentAmt by animateFloatAsState(targetValue = rawRentAmt.toFloat(), label = "rent")
    val principalAmt by animateFloatAsState(targetValue = rawPrincipalAmt.toFloat(), label = "principal")
    val interestAmt by animateFloatAsState(targetValue = rawInterestAmt.toFloat(), label = "interest")
    val otherAmt by animateFloatAsState(targetValue = rawOtherAmt.toFloat(), label = "other")
    val maxVal by animateFloatAsState(targetValue = rawMaxVal.toFloat(), label = "maxVal")

    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height
                val labelWidth = 50.dp.toPx()
                val chartWidth = width - labelWidth - 40.dp.toPx()
                val startX = labelWidth
                val barWidth = 14.51.dp.toPx()
                
                val gridHeight = height - 40.dp.toPx()
                val topMargin = 20.dp.toPx()
                val bottomMargin = 20.dp.toPx()
                val usableHeight = height - topMargin - bottomMargin
                
                // Dynamic Labels
                val labelCount = 6
                val labels = List(labelCount) { index ->
                    val v = maxVal * (1.0 - index.toDouble() / (labelCount - 1))
                    if (v >= 1000) "${(v/1000).toInt()}k" else v.toInt().toString()
                }
                
                labels.forEachIndexed { index, label ->
                    val y = topMargin + (index * (usableHeight / (labelCount - 1)))
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.6f),
                        start = Offset(startX, y),
                        end = Offset(width - 20.dp.toPx(), y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        label,
                        10.dp.toPx(),
                        y + 4.dp.toPx(),
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.GRAY
                            textSize = 10.sp.toPx()
                            isAntiAlias = true
                        }
                    )
                }

                // Solid Vertical Axis
                drawLine(
                    color = Color.LightGray,
                    start = Offset(startX, topMargin),
                    end = Offset(startX, height - bottomMargin),
                    strokeWidth = 1.dp.toPx()
                )

                // Solid Horizontal Baseline
                drawLine(
                    color = Color.LightGray,
                    start = Offset(startX, height - bottomMargin),
                    end = Offset(width - 20.dp.toPx(), height - bottomMargin),
                    strokeWidth = 1.dp.toPx()
                )

                val spacing = (chartWidth - (barWidth * 2)) / 3f

                // Rent Bar (Dynamic height)
                val rentH = (rentAmt / maxVal).toFloat() * usableHeight
                val rentRect = androidx.compose.ui.geometry.Rect(
                    offset = Offset(startX + spacing, height - rentH - bottomMargin),
                    size = androidx.compose.ui.geometry.Size(barWidth, rentH)
                )
                val rentPath = androidx.compose.ui.graphics.Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            rect = rentRect,
                            topLeft = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                            topRight = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                            bottomLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
                            bottomRight = androidx.compose.ui.geometry.CornerRadius.Zero
                        )
                    )
                }
                drawPath(path = rentPath, color = rentColor)

                // Buying Stacked Bar (Dynamic heights)
                val h1 = (principalAmt / maxVal).toFloat() * usableHeight // Principal
                val h2 = (interestAmt / maxVal).toFloat() * usableHeight // Interest
                val h3 = (otherAmt / maxVal).toFloat() * usableHeight // Other
                
                var currentY = height - bottomMargin
                
                // Dark Blue (Other)
                if (h3 > 0) {
                    drawRect(
                        color = buyOtherColor,
                        topLeft = Offset(startX + spacing * 2 + barWidth, currentY - h3),
                        size = androidx.compose.ui.geometry.Size(barWidth, h3)
                    )
                    currentY -= h3
                }

                // Medium Blue (Interest)
                if (h2 > 0) {
                    drawRect(
                        color = buyInterestColor,
                        topLeft = Offset(startX + spacing * 2 + barWidth, currentY - h2),
                        size = androidx.compose.ui.geometry.Size(barWidth, h2)
                    )
                    currentY -= h2
                }

                // Light Blue (Principal - Rounded Top)
                if (h1 > 0) {
                    val principalRect = androidx.compose.ui.geometry.Rect(
                        offset = Offset(startX + spacing * 2 + barWidth, currentY - h1),
                        size = androidx.compose.ui.geometry.Size(barWidth, h1)
                    )
                    val principalPath = androidx.compose.ui.graphics.Path().apply {
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = principalRect,
                                topLeft = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                                topRight = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx()),
                                bottomLeft = androidx.compose.ui.geometry.CornerRadius.Zero,
                                bottomRight = androidx.compose.ui.geometry.CornerRadius.Zero
                            )
                        )
                    }
                    drawPath(path = principalPath, color = buyPrincipalColor)
                }
            }
        }

        AppSpacerV(25)

        // Exact Legend as per Image
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                LegendItem(stringResource(R.string.renting), rentColor)
                AppSpacerH(16)
                LegendItem(stringResource(R.string.buying), buyOtherColor)
            }
        }
    }
}

@Composable
fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(15.dp).clip(RoundedCornerShape(5.dp)).background(color))
        AppSpacerH(6)
        AppTextRegular(text = label, fontSize = 12.sp, color = colorResource(R.color.textlight))
    }
}

@Composable
fun MortgagePaymentItem(name: String,amount : String, color: Color){
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row (verticalAlignment = Alignment.CenterVertically){
            Box(modifier = Modifier.size(10.dp)
                .clip(RoundedCornerShape(99.dp))
                .background(color = color))
            AppSpacerH(3)
            AppTextMedium(text = name, fontSize = 13.sp)
        }
        AppTextSemiBold(text = amount, fontSize = 15.sp)
    }
}

@Composable
fun PriceSliderView(
    annualRentPrice: Float,
    onAnnualRentPriceChange: (Float) -> Unit,
    purchasePrice: Float,
    onPurchasePriceChange: (Float) -> Unit,
    downPayment: Float,
    onDownPaymentChange: (Float) -> Unit,
    mortgageLoanPeriod: Int,
    onMortgageLoanPeriodChange: (Int) -> Unit,
    fixedMortgagePeriod: Int,
    onFixedMortgagePeriodChange: (Int) -> Unit,
    fixedInterestRate: Int,
    onFixedInterestRateChange: (Int) -> Unit,
    reversionRate: Int,
    onReversionRateChange: (Int) -> Unit,
    annualHomePrice: Int,
    onAnnualHomePriceChange: (Int) -> Unit,
    annualRentRate: Int,
    onAnnualRentRateChange: (Int) -> Unit,
    annualRentRange: ClosedFloatingPointRange<Float>,
    purchaseRange: ClosedFloatingPointRange<Float>,
    downPaymentRange: ClosedFloatingPointRange<Float>,
    mortgageLoanPeriodRange: IntRange,
    fixedMortgagePeriodRange: IntRange,
    fixedInterestRateRange: IntRange,
    reversionRateRange: IntRange,
    annualHomePriceRange: IntRange,
    annualRentRateRange: IntRange
) {
    var isSliderExpanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (isSliderExpanded) 180f else 0f
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppTextLight(
                    text = stringResource(R.string.annual_rent),
                    color = colorResource(R.color.textlight),
                    fontSize = 13.sp
                )
                AppTextMedium(
                    text = "${
                        annualRentPrice.toInt().toString().formatWithCommas()
                    } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                )
            }
            PriceCustomSlider(
                value = annualRentPrice,
                onValueChange = onAnnualRentPriceChange,
                valueRange = annualRentRange
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AppTextLight(
                    text = stringResource(R.string.purchase_price),
                    color = colorResource(R.color.textlight),
                    fontSize = 13.sp
                )
                AppTextMedium(
                    text = "${
                        purchasePrice.toInt().toString().formatWithCommas()
                    } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                )
            }
            PriceCustomSlider(
                value = purchasePrice,
                onValueChange = onPurchasePriceChange,
                valueRange = purchaseRange
            )
            if (isSliderExpanded) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.down_payment),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    Row {
                        AppTextMedium(
                            text = "${
                                (purchasePrice * downPayment).toInt().toString()
                                    .formatWithCommas()
                            } AED", color = colorResource(R.color.textprimary), fontSize = 14.sp
                        )
                        AppTextMedium(
                            text = "${(downPayment * 100).toInt()}%",
                            color = colorResource(R.color.splash_primary), fontSize = 14.sp
                        )
                    }

                }
                PriceCustomSlider(
                    value = downPayment,
                    onValueChange = onDownPaymentChange,
                    valueRange = downPaymentRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.mortgage_loan_period),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${mortgageLoanPeriod} Years",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                YearsCustomSlider(
                    value = mortgageLoanPeriod,
                    onValueChange = onMortgageLoanPeriodChange,
                    valueRange = mortgageLoanPeriodRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.fixed_mortgage_period),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${fixedMortgagePeriod} Years",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                YearsCustomSlider(
                    value = fixedMortgagePeriod,
                    onValueChange = onFixedMortgagePeriodChange,
                    valueRange = fixedMortgagePeriodRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.fixed_interest_rate),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${fixedInterestRate}",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                YearsCustomSlider(
                    value = fixedInterestRate,
                    onValueChange = onFixedInterestRateChange,
                    valueRange = fixedInterestRateRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.reversion_rate),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${reversionRate}",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                YearsCustomSlider(
                    value = reversionRate,
                    onValueChange = onReversionRateChange,
                    valueRange = reversionRateRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.annual_home_price),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${(annualHomePrice).toInt()}%",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                PercentageCenterSlider(
                    value = annualHomePrice,
                    onValueChange = onAnnualHomePriceChange,
                    valueRange = annualHomePriceRange
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AppTextLight(
                        text = stringResource(R.string.annual_rent_growth_rate),
                        color = colorResource(R.color.textlight),
                        fontSize = 13.sp
                    )
                    AppTextMedium(
                        text = "${(annualRentRate).toInt()}%",
                        color = colorResource(R.color.textprimary), fontSize = 14.sp
                    )

                }
                PercentageCenterSlider(
                    value = annualRentRate,
                    onValueChange = onAnnualRentRateChange,
                    valueRange = annualRentRateRange
                )
            }


            IconButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { isSliderExpanded = !isSliderExpanded }) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceCustomSlider(
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
fun YearsCustomSlider(
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
fun PercentageCenterSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,   // ✅ Pass range from outside
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth()
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

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth().height(5.dp)
                ) {

                    val trackWidth = constraints.maxWidth.toFloat()

                    val rangeStart = sliderState.valueRange.start
                    val rangeEnd = sliderState.valueRange.endInclusive
                    val totalRange = rangeEnd - rangeStart

                    val currentFraction =
                        (sliderState.value - rangeStart) / totalRange

                    val centerFraction =
                        (0f - rangeStart) / totalRange

                    val currentX = trackWidth * currentFraction
                    val centerX = trackWidth * centerFraction

                    val left = min(currentX, centerX)
                    val right = max(currentX, centerX)

                    // Background track
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                colorResource(R.color.slider_unselected),
                                RoundedCornerShape(50)
                            )
                    )

                    // Active track (from center)
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(left.toInt(), 0) }
                            .width((right - left).toDp())
                            .fillMaxHeight()
                            .background(
                                colorResource(R.color.black),
                                RoundedCornerShape(50)
                            )
                    )
                }
            }
        )

        // Bottom labels dynamic
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppTextLight("${valueRange.first}%", fontSize = 12.sp, color = Color.Gray)
            AppTextLight(
                "${(valueRange.first + valueRange.last) / 2}%",
                fontSize = 12.sp,
                color = Color.Gray
            )
            AppTextLight("${valueRange.last}%", fontSize = 12.sp, color = Color.Gray)
        }
    }
}
@Composable
fun Float.toDp() = with(LocalDensity.current) { this@toDp.toDp() }


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    paymentBreakdown: RentVsBuyPaymentBreakdown?
) {
    var expandedPosition by remember { mutableStateOf<Int?>(1) }
    val summary = paymentBreakdown?.overHorizon?.summary

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 15.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppTextBold(text = stringResource(R.string.payment_breakdown))
            AppSpacerV(20)
            BreakdownItem(
                title = stringResource(id = R.string.total_purchase_costs),
                isExpand = expandedPosition == 1,
                position = 1,
                paymentBreakdown = paymentBreakdown,
                onClick = {
                    expandedPosition = if (expandedPosition == 1) null else 1
                }
            )
            BreakdownItem(
                title = stringResource(id = R.string.recurring_payments),
                isExpand = expandedPosition == 2,
                position = 2,
                paymentBreakdown = paymentBreakdown,
                onClick = {
                    expandedPosition = if (expandedPosition == 2) null else 2
                }
            )

            BreakdownItem(
                title = stringResource(id = R.string.recurring_payments),
                isExpand = expandedPosition == 3,
                position = 3,
                paymentBreakdown = paymentBreakdown,
                onClick = {
                    expandedPosition = if (expandedPosition == 3) null else 3
                }
            )

            AppSpacerV(10)
            AppTextBold(text = "Net costs over 25 years", fontSize = 16.sp)
            AppSpacerV(13)
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                PriceInfoCard(stringResource(R.string.saved_if_buying),
                    "${abs(paymentBreakdown?.overHorizon?.summary?.netCostBuyAfterSale ?: 0.0).toInt().toString().formatWithCommas()} AED",colorResource(R.color.splash_primary),
                    Modifier.weight(1f))
                PriceInfoCard(stringResource(R.string.lost_if_renting),
                    "${abs(paymentBreakdown?.overHorizon?.summary?.netCostRent ?: 0.0).toInt().toString().formatWithCommas()} AED",colorResource(R.color.darkBlue),
                    Modifier.weight(1f))
            }
            AppSpacerV(20)

        }
    }
}

@Composable
fun BreakdownItem(
    title: String,
    isExpand: Boolean,
    position: Int,
    paymentBreakdown: RentVsBuyPaymentBreakdown?,
    onClick: () -> Unit
){
    val initial = paymentBreakdown?.overHorizon?.initial
    val recurring = paymentBreakdown?.overHorizon?.recurring
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp, vertical = 8.dp)
            .clickable{onClick()},
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.screen_bg_color)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(3.dp)
        ) {
            Row (modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){
                AppTextSemiBold(fontSize = 14.sp, text = title, color = if (isExpand) PrimaryBlue
                else colorResource(R.color.textprimary))
                AppIcon(modifier = Modifier.size(13.dp),
                    resId = if (isExpand) R.drawable.up_arrow else R.drawable.arrow_down,
                    tint = if (isExpand) PrimaryBlue
                    else colorResource(R.color.textprimary))
            }
            AnimatedVisibility(visible = isExpand) {
                if (position == 1) {
                    Column (modifier = Modifier.fillMaxWidth()){
                        Row (modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)){
                            Column (modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start){
                                AppTextLight(text = stringResource(R.string.rent))
                                AppTextBold(fontSize = 13.sp,
                                    text = "${(initial?.rent?.total ?: 0.0).toInt().toString().formatWithCommas()} AED"
                                )
                            }
                            Column (modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start){
                                AppTextLight(text = stringResource(R.string.buy))
                                AppTextBold(fontSize = 13.sp,
                                    text = "${(initial?.buy?.total ?: 0.0).toInt().toString().formatWithCommas()} AED"
                                )
                            }
                        }
                        AppSpacerV(10)
                        TotalPurchaseCostItem(stringResource(R.string.down_payment),
                            "--","${(initial?.buy?.downPayment ?: 0.0).toInt().toString().formatWithCommas()} AED",0,0)
                        AppDivider(color = colorResource(R.color.screen_bg_color), thickness = 1.dp)
                        TotalPurchaseCostItem(stringResource(R.string.agent_fees),
                            "${(initial?.rent?.agentFees ?: 0.0).toInt().toString().formatWithCommas()} AED",
                            "${(initial?.buy?.agentFees ?: 0.0).toInt().toString().formatWithCommas()} AED",0,0)
                        AppDivider(color = colorResource(R.color.screen_bg_color), thickness = 1.dp)
                        TotalPurchaseCostItem(stringResource(R.string.bank_fees),
                            "--","${(initial?.buy?.bankFees ?: 0.0).toInt().toString().formatWithCommas()} AED",0,0)
                        AppDivider(color = colorResource(R.color.screen_bg_color), thickness = 1.dp)
                        TotalPurchaseCostItem(stringResource(R.string.government_fees),
                            "${(initial?.rent?.governmentFees ?: 0.0).toInt().toString().formatWithCommas()} AED",
                            "${(initial?.buy?.governmentFees ?: 0.0).toInt().toString().formatWithCommas()} AED",10,10)
                    }
                } else if (position == 2){
                    Column(modifier = Modifier.fillMaxWidth()) {
                        TotalPurchaseCostItem(
                            stringResource(R.string.recurring_payments),
                            "${(recurring?.rent?.total ?: 0.0).toInt().toString().formatWithCommas()} AED",
                            "${(recurring?.buy?.total ?: 0.0).toInt().toString().formatWithCommas()} AED",
                            10,
                            10
                        )
                    }
                } else if (position == 3){
                    // expanded view
                }
            }
        }
    }
}

@Composable
fun TotalPurchaseCostItem(text: String, rent: String,buy: String,
        botLeftCorner: Int, botRightCorner: Int){
    Row (modifier = Modifier.clip(shape = RoundedCornerShape(
            bottomStart = if (botLeftCorner == 0) 0.dp else botLeftCorner.dp,
            bottomEnd = if (botRightCorner == 0) 0.dp else botRightCorner.dp))
        .background(Color.White)
        .padding(vertical = 5.dp, horizontal = 10.dp)){
        AppTextLight(modifier = Modifier.weight(0.45f),
            text = text)

        Column (modifier = Modifier.weight(0.55f)){
            Row (){
                AppTextMedium(text = "${stringResource(R.string.rent)}:")
                AppSpacerH(3)
                AppTextSemiBold(text = rent)
            }
            Row (){
                AppTextMedium(text = "${stringResource(R.string.buy)}:")
                AppSpacerH(3)
                AppTextSemiBold(text = buy, fontSize = 15.sp)
            }
        }
    }
}


@Composable
fun PriceInfoCard(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            AppTextLight(title, fontSize = 12.sp, color = Color.White)
            AppTextBold(value, fontSize = 14.sp, color = Color.White)
        }
    }
}


