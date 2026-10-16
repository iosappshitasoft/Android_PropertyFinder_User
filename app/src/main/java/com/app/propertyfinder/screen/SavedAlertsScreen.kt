package com.app.propertyfinder.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.propertyfinder.R
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextMedium
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.PropertyFinderTheme
import com.app.propertyfinder.ui.theme.SuccessColor
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.SnackbarManager
import com.app.propertyfinder.utils.rememberSnackbarManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.dto.response.AlertItem
import com.app.propertyfinder.api.dto.response.alertFrequencies
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.viewmodel.UserViewModel
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextSemiBold
import com.app.propertyfinder.ui.theme.FocusBorderColor
import com.app.propertyfinder.ui.theme.UnfocusBorderColor
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import java.time.Duration
import java.time.Instant


data class SavedAlert(
    val id: String,
    val title: String,
    val time: String,
    var frequency: String = "Every 3 days"
)

fun AlertItem.toSavedAlert(): SavedAlert? {
    val id = id ?: return null
    val title = alertName ?: return null
    val createdText = createdAt ?: ""
    val frequencyText = frequency ?: "daily"

    return SavedAlert(
        id = id,
        title = title,
        time = createdText,
        frequency = frequencyText.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedAlertsScreen(navController: NavHostController, userViewModel: UserViewModel = hiltViewModel()) {
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val alertsState by userViewModel.alertsState.collectAsStateWithLifecycle()
    val updateAlertState by userViewModel.updateAlertState.collectAsStateWithLifecycle()
    val deleteAlertState by userViewModel.deleteAlertState.collectAsStateWithLifecycle()
    val deleteAllAlertsState by userViewModel.deleteAllAlertsState.collectAsStateWithLifecycle()
    var savedAlerts by remember { mutableStateOf<List<SavedAlert>>(emptyList()) }

    var showRenameDialog by remember { mutableStateOf<SavedAlert?>(null) }
    var showDeleteSuccess by remember { mutableStateOf(false) }
    val snackbarManager = rememberSnackbarManager()
    var snackbarColor by remember { mutableStateOf(SuccessColor) }
    var alertFrequencyOptions by remember { mutableStateOf<List<alertFrequencies>>(emptyList()) }

    LaunchedEffect(Unit) {
        TokenManager.accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").toString()
        TokenManager.refreshToken = PrefUtils.getString(Constants.TAG_REFRESH_TOKEN, "").toString()
        if (masterDataState.data == null && masterDataState.isLoading != true) {
            propertyViewModel.getMasterData("alertfrequencies") { _ -> }
        }
        userViewModel.getUserAlerts(TokenManager.accessToken, page = 1, limit = 20)
    }

    LaunchedEffect(alertsState.data) {
        val items = alertsState.data?.data?.items.orEmpty()
        savedAlerts = items.mapNotNull { it.toSavedAlert() }
    }

    LaunchedEffect(masterDataState.data) {
        alertFrequencyOptions = masterDataState.data?.data?.alertFrequencies ?: emptyList()
    }

    if (showRenameDialog != null) {
        RenameBottomSheet(
            alert = showRenameDialog!!,
            onDismiss = { showRenameDialog = null },
            onSave = { updatedAlert ->
                userViewModel.updateUserAlert(
                    token = TokenManager.accessToken,
                    id = updatedAlert.id,
                    request = mapOf(
                        "alertName" to updatedAlert.title,
                        "isActive" to true
                    )
                ) { result ->
                    when (result) {
                        is ApiResult.Success -> {
                            savedAlerts = savedAlerts.map { if (it.id == updatedAlert.id) updatedAlert else it }
                            snackbarManager.showSuccess(result.body.message.toString())
                        }

                        is ApiResult.Error -> {
                            snackbarManager.showError(result.message)
                        }
                    }
                    showRenameDialog = null
                }
            }
        )
    }

    // Simulate delete success message/toast if needed, for now just a state
    if (showDeleteSuccess) {
       // Could be a toast or a temporary overlay
    }

    if (alertsState.isLoading || updateAlertState.isLoading|| deleteAlertState.isLoading|| deleteAllAlertsState.isLoading){
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center) {
//            AppLoader()
            AppLoadingDialog()
        }
    }
    Scaffold(

        snackbarHost = {
            AppSnackbarHost(
                hostState = snackbarManager.snackbarHostState,
                containerColor = snackbarColor
            )
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { AppTextBold(text = "Saved Alerts", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            if (savedAlerts.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .background(Background)
                ) {
                    Button(
                        onClick = {
                            userViewModel.deleteAllUserAlerts(
                                token = TokenManager.accessToken
                            ) { result ->
                                when (result) {
                                    is ApiResult.Success -> {
                                        savedAlerts = emptyList()
                                        showDeleteSuccess = true
                                        snackbarManager.showSuccess(result.body.message.toString())
                                    }

                                    is ApiResult.Error -> {
                                        snackbarManager.showError(result.message)
                                    }
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MolumulkDimens.PrimaryButtonHeight),
                        shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                    ) {
                         Icon(
                             imageVector = Icons.Default.Delete,
                             contentDescription = null, 
                             tint = Color.White,
                             modifier = Modifier.size(18.dp)
                         )
                        AppSpacerH(8)
                        AppTextBold(text = "Delete all", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(paddingValues)
        ) {
            if (savedAlerts.isEmpty()) {
                EmptyAlertsState(showSuccess = showDeleteSuccess,snackbarManager)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(savedAlerts) { alert ->
                        SavedAlertItem(
                            alert = alert,
                            frequencyOptions = alertFrequencyOptions,
                            onRename = { showRenameDialog = alert },
                            onDelete = {
                                userViewModel.deleteUserAlert(
                                    token = TokenManager.accessToken,
                                    id = alert.id
                                ) { result ->
                                    when (result) {
                                        is ApiResult.Success -> {
                                            savedAlerts = savedAlerts.filter { it.id != alert.id }
                                            snackbarManager.showSuccess(result.body.message.toString())
                                        }

                                        is ApiResult.Error -> {
                                            snackbarManager.showError(result.message)
                                        }
                                    }
                                }
                            },
                            onFrequencyChange = { newFreq ->
                                userViewModel.updateUserAlert(
                                    token = TokenManager.accessToken,
                                    id = alert.id,
                                    request = mapOf(
                                        "frequency" to newFreq.lowercase(),
                                        "isActive" to true
                                    )
                                ) { result ->
                                    when (result) {
                                        is ApiResult.Success -> {
                                            val displayFrequency =
                                                newFreq.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                                            savedAlerts = savedAlerts.map {
                                                if (it.id == alert.id) it.copy(frequency = displayFrequency) else it
                                            }
                                            snackbarManager.showSuccess(result.body.message.toString())
                                        }

                                        is ApiResult.Error -> {
                                            snackbarManager.showError(result.message)
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SavedAlertItem(
    alert: SavedAlert,
    frequencyOptions: List<alertFrequencies>,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onFrequencyChange: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var frequencyExpanded by remember { mutableStateOf(false) }
    var itemWidth by remember { mutableStateOf(0) }

    Surface(
        shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                itemWidth = coordinates.size.width
            }
    ) {
        Column(modifier = Modifier.padding(5.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 15.dp, end = 15.dp, top = 15.dp)
                ) {
                    AppTextBold(
                        text = alert.title,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    AppSpacerV(4)
                    AppTextLight(text = formatCreatedTime(alert.time))
                }

                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, end = 10.dp)
                ) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More",
                            tint = TextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(White).width(165.dp)
                    ) {
                        DropdownMenuItem(
                            text = { AppTextMedium(text = "Rename") },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextSecondary
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { AppTextMedium(text = "Delete") },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = TextSecondary
                                )
                            }
                        )
                    }
                }
            }

            AppSpacerV(16)

            var rowWidth by remember { mutableStateOf(0) }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                    .background(Background)
                    .onGloballyPositioned { rowWidth = it.size.width }
                    .clickable { frequencyExpanded = true }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppTextRegular(
                    text = "Receive updates",
                    fontSize = 12.sp,
                    modifier = Modifier.weight(1f)
                )

                Box {
                    val selectedFrequencyName = frequencyOptions
                        .firstOrNull { it.value.orEmpty().equals(alert.frequency, ignoreCase = true) }
                        ?.name
                        .orEmpty()
                        .ifBlank { alert.frequency }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppTextSemiBold(text = selectedFrequencyName, fontSize = 13.sp)
                        AppSpacerH(4)
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = frequencyExpanded,
                    onDismissRequest = { frequencyExpanded = false },
                    modifier = Modifier
                        .width(with(androidx.compose.ui.platform.LocalDensity.current) { rowWidth.toDp() })
                        .background(White)
                ) {
                    frequencyOptions.forEach { option ->
                        val optionName = option.name.orEmpty()
                        val optionValue = option.value.orEmpty()
                        val isSelected = optionValue.equals(alert.frequency, ignoreCase = true) || 
                                         optionName.equals(alert.frequency, ignoreCase = true)

                        DropdownMenuItem(
                            text = { AppTextMedium(text = optionName) },
                            trailingIcon = {
                                if (isSelected) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            onClick = {
                                frequencyExpanded = false
                                onFrequencyChange(
                                    optionValue.ifBlank { optionName.lowercase() }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAlertsState(showSuccess: Boolean, snackbarManager: SnackbarManager) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (showSuccess) {
            snackbarManager.showSuccess("Alerts deleted successfully")
        }
        
        AppSpacerV(40)
        
        // Placeholder image for empty state
        AppImage(
             // Using a generic placeholder or one from the project if available. 
             // Assuming R.drawable.no_alerts_placeholder exists or just use a generic warning/alert icon big
             resId = R.drawable.alerts_deleted,
             modifier = Modifier.size(300.dp).padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RenameBottomSheet(
    alert: SavedAlert,
    onDismiss: () -> Unit,
    onSave: (SavedAlert) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var text by remember { mutableStateOf(alert.title) }
    var error by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = White,
        shape = RoundedCornerShape(topStart = 33.64.dp, topEnd = 33.64.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            AppTextBold(
                text = "Rename",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = text,
                onValueChange = { 
                    text = it
                    if (it.isNotBlank()) error = ""
                },
                modifier = Modifier.fillMaxWidth(),
                isError = error.isNotEmpty(),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = MolumulkPrimaryCoralRed,
                    unfocusedBorderColor = MolumulkBorderDivider,
                    cursorColor = MolumulkPrimaryCoralRed
                )
            )

            if (error.isNotEmpty()) {
                AppSpacerV(height = 4)
                AppTextRegular(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AppSpacerV(24)

            Button(
                onClick = { 
                    if (text.isNotBlank()) {
                        onSave(alert.copy(title = text))
                    } else {
                        error = "Kindly enter the alert name"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MolumulkDimens.PrimaryButtonHeight),
                colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
            ) {
                AppTextBold(text = "Save", color = White, fontSize = 16.sp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatCreatedTime(isoDate: String): String {
    return try {
        val createdInstant = Instant.parse(isoDate)
        val nowInstant = Instant.now()
        val duration = Duration.between(createdInstant, nowInstant)

        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()
        val weeks = days / 7

        when {
            minutes < 1 -> "Created just now"
            minutes < 60 -> "Created $minutes minute${if (minutes > 1) "s" else ""} ago"
            hours < 24 -> "Created $hours hour${if (hours > 1) "s" else ""} ago"
            days < 7 -> "Created $days day${if (days > 1) "s" else ""} ago"
            weeks < 5 -> "Created $weeks week${if (weeks > 1) "s" else ""} ago"
            days < 365 -> {
                val months = days / 30
                "Created $months month${if (months > 1) "s" else ""} ago"
            }
            else -> {
                val years = days / 365
                "Created $years year${if (years > 1) "s" else ""} ago"
            }
        }
    } catch (e: Exception) {
        ""
    }
}

@Preview
@Composable
fun SavedAlertsPreview() {
    PropertyFinderTheme {
        SavedAlertsScreen(rememberNavController())
    }
}
