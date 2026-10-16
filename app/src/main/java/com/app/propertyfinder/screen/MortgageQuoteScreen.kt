package com.app.propertyfinder.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.dto.response.MortgageQuoteCalculatorSnapshot
import com.app.propertyfinder.api.dto.response.MortgageQuoteRequest
import com.app.propertyfinder.api.dto.response.buyingProcess
import com.app.propertyfinder.api.dto.response.countries
import com.app.propertyfinder.api.dto.response.employmentstatus
import com.app.propertyfinder.api.dto.response.loantype
import com.app.propertyfinder.api.dto.response.residencyStatus
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.MortgageViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.utils.rememberSnackbarManager

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MortgagePreview() {
    MortgageQuoteScreen(navController = rememberNavController())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MortgageQuoteScreen(navController: NavController) {
    val viewModel: PropertyViewModel = hiltViewModel()
    val mortgageViewModel: MortgageViewModel = hiltViewModel()
    val masterDataState by viewModel.masterDataState.collectAsStateWithLifecycle()
    val quoteState by mortgageViewModel.quoteState.collectAsStateWithLifecycle()
    val snackbarManager = rememberSnackbarManager()
    var snackbarColor by remember { mutableStateOf(PrimaryColor) }

    var currentStep by remember { mutableStateOf(0) }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var popupMessage by remember { mutableStateOf("") }
    var popupEmail by remember { mutableStateOf("") }

    val loanTypes = masterDataState.data?.data?.loantype.orEmpty()
    val residenceStatuses = masterDataState.data?.data?.residencyStatus.orEmpty()
    val buyingProcesses = masterDataState.data?.data?.buyingProcess.orEmpty()
    val employmentStatuses = masterDataState.data?.data?.employmentstatus.orEmpty()
    val countries = masterDataState.data?.data?.countries.orEmpty()

    var selectedLoanType by remember { mutableStateOf("") }
    var selectedResidence by remember { mutableStateOf("") }
    var selectedProcess by remember { mutableStateOf("") }
    var selectedEmployment by remember { mutableStateOf("") }
    var selectedCountryName by remember { mutableStateOf("") }

    var propertyPrice by remember { mutableStateOf("") }
    var monthlySalary by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCountrySheet by remember { mutableStateOf(false) }

    val calcSnapshotHandle = navController.previousBackStackEntry?.savedStateHandle
    val calculatorSnapshot =
        calcSnapshotHandle?.get<MortgageQuoteCalculatorSnapshot>("mortgage_calculator_snapshot")
    val calculatorResidencyStatus =
        calcSnapshotHandle?.get<String>("mortgage_calculator_residency_status").orEmpty()

    LaunchedEffect(Unit) {
        viewModel.getMasterData("loantype,residencystatus,buyingprocess,employmentstatus,countries") {}
    }

    LaunchedEffect(loanTypes, residenceStatuses, buyingProcesses, employmentStatuses, countries) {
        if (selectedLoanType.isBlank()) selectedLoanType = loanTypes.firstOrNull()?.name.orEmpty()
        if (selectedResidence.isBlank()) selectedResidence = residenceStatuses.firstOrNull()?.name.orEmpty()
        if (selectedProcess.isBlank()) selectedProcess = buyingProcesses.firstOrNull()?.name.orEmpty()
        if (selectedEmployment.isBlank()) selectedEmployment = employmentStatuses.firstOrNull()?.name.orEmpty()
        if (selectedCountryName.isBlank()) {
            selectedCountryName = countries.firstOrNull()?.name.orEmpty().ifBlank { "United Arab Emirates" }
        }
    }

    val selectedCountryObj = countries.firstOrNull { it.name == selectedCountryName }
    val selectedCountryCode = selectedCountryObj?.phoneCode.orEmpty().ifBlank { "+971" }

    if (showCountrySheet) {
        CountryBottomSheet(
            onDismiss = { showCountrySheet = false },
            sheetState = sheetState,
            selectedOption = selectedCountryName,
            onOptionSelected = {
                selectedCountryName = it
                showCountrySheet = false
            },
            countries = countries
        )
    }

    if (showSuccessPopup) {
        MortgageQuotePopup(
            message = popupMessage,
            sentToEmail = popupEmail,
            navController = navController,
            onDismiss = { showSuccessPopup = false }
        )
    }

    if (quoteState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) { AppLoadingDialog() }
    }

    Scaffold(
        snackbarHost = {
            AppSnackbarHost(hostState = snackbarManager.snackbarHostState, containerColor = snackbarColor)
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = stringResource(R.string.get_mortgage_quote), fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 0) currentStep-- else navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF4F4F4))) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(Color(0xFFF5F5F5))
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 90.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                StepProgress(currentStep)
                Spacer(modifier = Modifier.height(40.dp))

                when (currentStep) {
                    0 -> {
                        AppTextSemiBold(text = stringResource(R.string.loan_type))
                        AppSpacerV(8)
                        Row {
                            loanTypes.take(2).forEachIndexed { index, type ->
                                SelectionButton(
                                    text = type.name.orEmpty(),
                                    selected = selectedLoanType == type.name.orEmpty(),
                                    onClick = { selectedLoanType = type.name.orEmpty() }
                                )
                                if (index == 0) Spacer(modifier = Modifier.width(8.dp))
                            }
                        }
                        AppSpacerV(20)

                        AppTextSemiBold(text = stringResource(R.string.residence_status))
                        AppSpacerV(8)
                        CustomDropdown(
                            items = residenceStatuses.mapNotNull { it.name },
                            selectedItem = selectedResidence,
                            onItemSelected = { selectedResidence = it }
                        )
                        AppSpacerV(20)

                        AppTextSemiBold(text = stringResource(R.string.are_you_in_the))
                        AppSpacerV(8)
                        CustomDropdown(
                            items = buyingProcesses.mapNotNull { it.name },
                            selectedItem = selectedProcess,
                            onItemSelected = { selectedProcess = it }
                        )
                        AppSpacerV(20)

                        OutlinedTextField(
                            value = propertyPrice,
                            onValueChange = { propertyPrice = it.filter(Char::isDigit).take(15) },
                            placeholder = { AppTextMedium(text = "Enter price of the property") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = { AppTextMedium("AED") },
                            textStyle = TextStyle(fontSize = 14.sp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = colorResource(R.color.textprimary),
                                cursorColor = colorResource(R.color.textprimary)
                            )
                        )
                    }

                    1 -> {
                        AppTextSemiBold(text = stringResource(R.string.what_is_your_employment))
                        AppSpacerV(8)
                        CustomDropdown(
                            items = employmentStatuses.mapNotNull { it.name },
                            selectedItem = selectedEmployment,
                            onItemSelected = { selectedEmployment = it }
                        )
                        AppSpacerV(8)
                        val salaried = selectedEmployment.equals("Salaried", ignoreCase = true)
                        if (salaried) {
                            OutlinedTextField(
                                value = monthlySalary,
                                onValueChange = { monthlySalary = it.filter(Char::isDigit).take(15) },
                                placeholder = { AppTextMedium(text = "Enter your salary") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { AppTextMedium("AED") },
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedBorderColor = colorResource(R.color.textprimary),
                                    cursorColor = colorResource(R.color.textprimary)
                                )
                            )
                        }
                    }

                    else -> {
                        AppTextSemiBold(text = stringResource(R.string.name))
                        AppSpacerV(8)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it.take(25) },
                            placeholder = { AppTextMedium(text = "Enter name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Text),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = colorResource(R.color.textprimary),
                                cursorColor = colorResource(R.color.textprimary)
                            )
                        )

                        AppSpacerV(20)
                        AppTextSemiBold(text = stringResource(R.string.email_address))
                        AppSpacerV(8)
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it.take(30) },
                            placeholder = { AppTextMedium(text = "Enter email address") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedBorderColor = colorResource(R.color.textprimary),
                                cursorColor = colorResource(R.color.textprimary)
                            )
                        )

                        AppSpacerV(20)
                        AppTextSemiBold(text = stringResource(R.string.phone_number))
                        AppSpacerV(8)
                        Row {
                            Box(
                                modifier = Modifier
                                    .clickable { showCountrySheet = true }
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .background(color = Color.White, shape = RoundedCornerShape(10.dp))
                                    .height(57.dp)
                                    .width(90.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    val flagUrl = selectedCountryObj?.flag
                                    if (!flagUrl.isNullOrBlank()) {
                                        AppSubComposeAsyncImage(
                                            model = flagUrl,
                                            modifier = Modifier.size(20.dp),
                                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                        )
                                        AppSpacerH(6)
                                    }
                                    AppTextMedium(text = selectedCountryCode, fontSize = 14.sp)
                                }
                            }

                            AppSpacerH(10)
                            OutlinedTextField(
                                value = mobileNumber,
                                onValueChange = { mobileNumber = it.filter(Char::isDigit).take(15) },
                                placeholder = { AppTextMedium(text = "Enter phone number") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Color.White,
                                    focusedContainerColor = Color.White,
                                    unfocusedBorderColor = Color(0xFFE0E0E0),
                                    focusedBorderColor = colorResource(R.color.textprimary),
                                    cursorColor = colorResource(R.color.textprimary)
                                )
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    when (currentStep) {
                        0 -> {
                            if (selectedLoanType.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please select loan type")
                                return@Button
                            }
                            if (selectedResidence.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please select residence status")
                                return@Button
                            }
                            if (selectedProcess.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please select buying process")
                                return@Button
                            }
                            if (propertyPrice.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter property price")
                                return@Button
                            }
                            currentStep++
                        }

                        1 -> {
                            if (selectedEmployment.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please select employment status")
                                return@Button
                            }
                            val salaried = selectedEmployment.equals("Salaried", ignoreCase = true)
                            if (salaried && monthlySalary.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter your salary")
                                return@Button
                            }
                            currentStep++
                        }

                        else -> {
                            if (fullName.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter name")
                                return@Button
                            }
                            if (email.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter email address")
                                return@Button
                            }
                            if (!email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter valid email address")
                                return@Button
                            }
                            if (mobileNumber.isBlank()) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Please enter phone number")
                                return@Button
                            }
                            if (calculatorSnapshot == null) {
                                snackbarColor = PrimaryColor
                                snackbarManager.showError("Mortgage calculator data is missing")
                                return@Button
                            }

                            val loanTypeValue = loanTypes.firstOrNull { it.name == selectedLoanType }?.value.orEmpty()
                            val residenceValue = residenceStatuses.firstOrNull { it.name == selectedResidence }?.value.orEmpty()
                            val buyingProcessValue = buyingProcesses.firstOrNull { it.name == selectedProcess }?.value.orEmpty()
                            val employmentValue = employmentStatuses.firstOrNull { it.name == selectedEmployment }?.value.orEmpty()

                            val request = MortgageQuoteRequest(
                                loanType = loanTypeValue,
                                residenceStatus = residenceValue,
                                residencyStatus = calculatorResidencyStatus.ifBlank { residenceValue },
                                buyingProcess = buyingProcessValue,
                                propertyPrice = propertyPrice.toIntOrNull() ?: 0,
                                employmentStatus = employmentValue,
                                monthlySalary = if (employmentValue == "salaried") monthlySalary.toIntOrNull() else null,
                                name = fullName.trim(),
                                email = email.trim(),
                                countryCode = selectedCountryCode,
                                mobileNumber = mobileNumber,
                                calculatorSnapshot = calculatorSnapshot
                            )

                            mortgageViewModel.getMortgageQuote(request) { result ->
                                when (result) {
                                    is ApiResult.Success -> {
                                        popupMessage = result.body.message ?: "Quote request submitted successfully"
                                        popupEmail = result.body.data?.sentTo.orEmpty()
                                        showSuccessPopup = true
                                    }
                                    is ApiResult.Error -> snackbarManager.showError(result.message)
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
                    .height(54.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.splash_primary))
            ) {
                AppTextMedium(
                    text = if (currentStep == 2) stringResource(R.string.submit) else stringResource(R.string.next),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StepProgress(currentStep: Int) {

    val steps = listOf(stringResource(R.string.loan_details),
        stringResource(R.string.employment),
        stringResource(R.string.submit))

    Column(modifier = Modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            steps.forEachIndexed { index, title ->
                val color = when {
                    index < currentStep -> Color(0xFF00B665) // 🟢 Completed
                    index == currentStep -> Color(0xFF2E00AD) // 🔵 Current
                    else -> Color(0xFFE3E3E3)                 // ⚪ Default
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 10.dp)
                ) {
                    // Top line
                    Box(
                        modifier = Modifier
                            .height(5.dp)
                            .fillMaxWidth()
                            .background(color, RoundedCornerShape(50))
                    )

                    AppSpacerV(10)

                    AppTextLight(text = title, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SelectionButton(text: String, selected: Boolean, onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .background(
                if (selected) Color.Black else Color.White,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() } // 🔴 Handle click
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Black,
            fontSize = 12.sp
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            textStyle = TextStyle(
                fontSize = 14.sp      // 🔴 Reduced input text size
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .height(50.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = colorResource(R.color.textprimary),
                cursorColor = colorResource(R.color.textprimary)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun MortgageQuotePopup(
    message: String,
    sentToEmail: String,
    navController: NavController,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() },
        properties = DialogProperties(
            dismissOnClickOutside = false, // 🔥 Prevent outside click dismiss
            dismissOnBackPress = false     // 🔥 Optional: prevent back press dismiss
        )
    ) {

        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.padding(15.dp)) {
                    // Close Icon
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.TopEnd)
                            .clickable { onDismiss()
                            navController.popBackStack()
                            }
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Spacer(modifier = Modifier.height(20.dp))

                        // Green Circle with Check
                        AppIcon(
                            resId = R.drawable.ic_success_green_tick,
                            contentDescription = null,
                            modifier = Modifier.size(90.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        AppTextBold(
                            text = message,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        AppSpacerV(10)
                        AppTextMedium(text = stringResource(R.string.details_will_be_send),
                            modifier = Modifier, fontSize = 13.sp)
                        AppSpacerV(5)
                        AppTextMedium(text = sentToEmail.ifBlank { stringResource(R.string.demo_mail_id) },
                            modifier = Modifier, fontSize = 13.sp, color = colorResource(R.color.darkBlue))
                        AppSpacerV(10)
                    }
                }
            }
        }
    }
}

