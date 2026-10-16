package com.app.propertyfinder.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.FilterChipDefaults.filterChipColors
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.app.propertyfinder.R
import com.app.propertyfinder.api.TokenManager
import com.app.propertyfinder.api.viewmodel.NotificationsViewModel
import com.app.propertyfinder.api.viewmodel.PropertyViewModel
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.dto.response.notificationstatus
import com.app.propertyfinder.ui.components.AppImage
import com.app.propertyfinder.ui.components.AppLoadingDialog
import com.app.propertyfinder.ui.components.AppSnackbarHost
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppSubComposeAsyncImage
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.PrimaryColor
import com.app.propertyfinder.ui.theme.TextPrimary
import com.app.propertyfinder.ui.theme.TextSecondary
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import androidx.compose.material3.CheckboxDefaults
import com.app.propertyfinder.navigation.Route
import com.app.propertyfinder.ui.alerts.ConfirmationAlert
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

import com.app.propertyfinder.ui.theme.AppFontFamily
import com.app.propertyfinder.ui.theme.MolumulkAccentCoral
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkLightCoralTint
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkOffWhite
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.ui.theme.MolumulkSuccessGreen

private val NotificationSelectionTopBar = MolumulkOffWhite
private val NotificationSelectionBadgeRed = MolumulkPrimaryCoralRed

private data class NotificationUiModel(
    val id: String,
    val title: String,
    val createdAt: String,
    val isRead: Boolean,
    val imageUrl: String?,
    val propertyId: String?
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NotificationsScreen(navController: NavHostController) {
    val propertyViewModel: PropertyViewModel = hiltViewModel()
    val notificationsViewModel: NotificationsViewModel = hiltViewModel()

    val masterDataState by propertyViewModel.masterDataState.collectAsStateWithLifecycle()
    val notificationsState by notificationsViewModel.notificationsState.collectAsStateWithLifecycle()
    val deleteState by notificationsViewModel.deleteState.collectAsStateWithLifecycle()

    val snackbarManager = rememberSnackbarManager()
    val screenScope = rememberCoroutineScope()
    val token = remember {
        PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").orEmpty().also {
            TokenManager.accessToken = it
        }
    }

    val notificationStatuses = masterDataState.data?.data?.notificationstatus.orEmpty()

    var selectedTabValue by remember { mutableStateOf("") }
    var isSelectionMode by remember { mutableStateOf(false) }
    val selectedIds = remember { mutableStateListOf<String>() }

    var isSearchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    var notifications by remember { mutableStateOf(listOf<NotificationUiModel>()) }
    var showDeleteSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        propertyViewModel.getMasterData("notificationstatus") { }
    }

    LaunchedEffect(notificationStatuses) {
        if (selectedTabValue.isBlank() && notificationStatuses.isNotEmpty()) {
            selectedTabValue = notificationStatuses.first().value.orEmpty()
        }
    }

    LaunchedEffect(token, selectedTabValue, searchQuery) {
        if (token.isBlank() || selectedTabValue.isBlank()) return@LaunchedEffect
        notificationsViewModel.getNotifications(
            token = token,
            tab = selectedTabValue,
            search = searchQuery.takeIf { it.isNotBlank() },
            page = 1,
            limit = 20
        )
    }

    LaunchedEffect(notificationsState.data) {
        val apiItems = notificationsState.data?.data?.notifications.orEmpty()
        notifications = apiItems.mapNotNull { item ->
            val id = item.id ?: return@mapNotNull null
            
            val image = item.metadata?.image
            val finalImageUrl = if (!image.isNullOrBlank()) {
                if (image.startsWith("https://") || image.startsWith("http://")) {
                    image
                } else {
                    val actionUrlStr = item.actionUrl ?: ""
                    if (actionUrlStr.isNotBlank()) {
                        val base = if (actionUrlStr.endsWith("/") && image.startsWith("/")) {
                            actionUrlStr.dropLast(1)
                        } else {
                            actionUrlStr
                        }
                        base + image
                    } else {
                        image
                    }
                }
            } else {
                null
            }

            val propId = item.metadata?.propertyId 
                ?: if (item.relatedItem?.itemType == "property") item.relatedItem.itemId else null

            NotificationUiModel(
                id = id,
                title = item.title.orEmpty(),
                createdAt = item.createdAt.orEmpty(),
                isRead = item.isRead ?: false,
                imageUrl = finalImageUrl,
                propertyId = propId
            )
        }
        val hasUnread = notifications.any { !it.isRead }
        PrefUtils.save(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, hasUnread)
        isSelectionMode = false
        selectedIds.clear()
    }

    val isLoading = notificationsState.isLoading || deleteState.isLoading

    Scaffold(
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        },
        topBar = {
            when {
                isSelectionMode -> {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "${selectedIds.size} Selected",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MolumulkCharcoal,
                                fontFamily = AppFontFamily
                            )
                        },
                        navigationIcon = {
                            val isAllSelected = selectedIds.size == notifications.size && notifications.isNotEmpty()
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.Transparent,
                                border = BorderStroke(1.dp, if (isAllSelected) MolumulkPrimaryCoralRed else MolumulkMutedSlate),
                                modifier = Modifier.padding(start = 16.dp).size(20.dp)
                            ) {
                                Checkbox(
                                    checked = isAllSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            selectedIds.clear()
                                            selectedIds.addAll(notifications.map { it.id })
                                        } else {
                                            selectedIds.clear()
                                        }
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = MolumulkPrimaryCoralRed,
                                        uncheckedColor = Color.Transparent,
                                        checkmarkColor = Color.White
                                    )
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    isSelectionMode = false
                                    selectedIds.clear()
                                }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = MolumulkCharcoal)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = NotificationSelectionTopBar
                        )
                    )
                }

                isSearchMode -> {
                    androidx.compose.material3.TopAppBar(
                        title = {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search...", fontSize = 16.sp, color = MolumulkMutedSlate, fontFamily = AppFontFamily) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = MolumulkCharcoal,
                                    unfocusedTextColor = MolumulkCharcoal
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                isSearchMode = false
                                searchQuery = ""
                            }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                    )
                }

                else -> {
                    CenterAlignedTopAppBar(
                        title = {
                            Text("Notifications", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MolumulkCharcoal, fontFamily = AppFontFamily)
                        },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MolumulkCharcoal)
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchMode = true }) {
                                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = MolumulkCharcoal)
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                    )
                }
            }
        },
        bottomBar = {
            if (isSelectionMode && selectedIds.isNotEmpty()) {
                val deleteAllSelected = selectedIds.size == notifications.size && notifications.isNotEmpty()
                Box(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = {
                            val idsToDelete = selectedIds.toList()
                            notificationsViewModel.deleteNotifications(
                                token = token,
                                ids = idsToDelete
                            ) { result ->
                                when (result) {
                                    is ApiResult.Success -> {
                                        notifications = notifications.filter { it.id !in idsToDelete }
                                        selectedIds.clear()
                                        isSelectionMode = false
                                        showDeleteSuccess = true
                                        val hasUnread = notifications.any { !it.isRead }
                                        PrefUtils.save(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, hasUnread)
                                    }
                                    is ApiResult.Error -> snackbarManager.showError(result.message)
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MolumulkDimens.PrimaryButtonHeight),
                        colors = ButtonDefaults.buttonColors(containerColor = MolumulkPrimaryCoralRed),
                        shape = RoundedCornerShape(MolumulkDimens.PrimaryCtaRadius)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (deleteAllSelected) "Delete all" else "Delete",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            fontFamily = AppFontFamily
                        )
                    }
                }
            }
        },
        containerColor = MolumulkOffWhite
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (isLoading) {
                AppLoadingDialog()
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (!isSearchMode) {
                        NotificationFilters(
                            statuses = notificationStatuses,
                            selectedTabValue = selectedTabValue,
                            onTabSelected = { picked ->
                                selectedTabValue = picked.value.orEmpty()
                            }
                        )
                    }

                    if (notifications.isEmpty()) {
                        EmptyNotificationState(searchQuery)
                        return@Column
                    }

                    val grouped = remember(notifications) { groupNotificationsByHeader(notifications) }
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        grouped.forEach { (header, items) ->
                            item(key = "header_$header") {
                                Text(
                                    header,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = MolumulkMutedSlate,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    fontFamily = AppFontFamily
                                )
                            }
                            items(
                                items,
                                key = { it.id }
                            ) { notification ->
                                if (isSelectionMode) {
                                    SelectableNotificationItem(
                                        notification = notification,
                                        isSelected = selectedIds.contains(notification.id),
                                        onToggle = {
                                            if (selectedIds.contains(notification.id)) selectedIds.remove(notification.id)
                                            else selectedIds.add(notification.id)
                                        }
                                    )
                                } else {
                                    SwipeableNotificationItem(
                                        notification = notification,
                                        onDeleteRequest = { onSuccess, onError ->
                                            notificationsViewModel.deleteNotifications(
                                                token = token,
                                                ids = listOf(notification.id)
                                            ) { result ->
                                                when (result) {
                                                    is ApiResult.Success -> {
                                                        onSuccess()
                                                        screenScope.launch {
                                                            delay(2000)
                                                            notifications = notifications.filter { it.id != notification.id }
                                                            val hasUnread = notifications.any { !it.isRead }
                                                            PrefUtils.save(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, hasUnread)
                                                        }
                                                    }
                                                    is ApiResult.Error -> {
                                                        onError(result.message)
                                                        snackbarManager.showError(result.message)
                                                    }
                                                }
                                            }
                                        },
                                        onLongClick = {
                                            isSelectionMode = true
                                            if (!selectedIds.contains(notification.id)) selectedIds.add(notification.id)
                                        },
                                        onClick = {
                                            if (!notification.propertyId.isNullOrBlank()) {
                                                navController.navigate("property_detail/${notification.propertyId}")
                                            } else {
                                                navController.navigate(Route.Home.route)
                                            }
                                            
                                            if (!notification.isRead) {
                                                notificationsViewModel.markNotificationRead(
                                                    token = token,
                                                    id = notification.id
                                                ) { result ->
                                                    if (result is ApiResult.Success) {
                                                        notifications = notifications.map {
                                                            if (it.id == notification.id) it.copy(isRead = true) else it
                                                        }
                                                        val hasUnread = notifications.any { !it.isRead }
                                                        PrefUtils.save(Constants.TAG_HAS_UNREAD_NOTIFICATIONS, hasUnread)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                }
                                Divider(color = MolumulkBorderDivider, thickness = 1.dp)
                            }
                        }
                    }
                }
            }

            if (showDeleteSuccess) {
                DeleteSuccessToast(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 80.dp)
                )
            }

            LaunchedEffect(showDeleteSuccess) {
                if (!showDeleteSuccess) return@LaunchedEffect
                delay(2000)
                showDeleteSuccess = false
            }
        }
    }
}

@Composable
private fun NotificationFilters(
    statuses: List<notificationstatus>,
    selectedTabValue: String,
    onTabSelected: (notificationstatus) -> Unit
) {
    if (statuses.isEmpty()) return
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .background(Color.White)
            .padding(vertical = 10.dp)
    ) {
        items(statuses) { status ->
            val tabValue = status.value.orEmpty()
            val tabName = status.name.orEmpty()
            val selected = tabValue == selectedTabValue

            val backgroundColor = if (selected) MolumulkLightCoralTint else Color.White
            val borderColor = if (selected) MolumulkPrimaryCoralRed else MolumulkBorderDivider
            val textColor = if (selected) MolumulkPrimaryCoralRed else MolumulkCharcoal

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                    .background(backgroundColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                    .border(1.dp, borderColor, RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                    .clickable { onTabSelected(status) }
                    .padding(horizontal = 16.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tabName,
                    color = textColor,
                    fontSize = 13.sp,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                    fontFamily = AppFontFamily
                )
            }
        }
    }
}

@Composable
private fun EmptyNotificationState(searchQuery: String) {
    Column(
        modifier = Modifier.fillMaxSize().background(MolumulkOffWhite),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center) {
            AppImage(resId = R.drawable.ic_notification_empty)
        }
        AppSpacerV(16)
        Text(
            text = if (searchQuery.isNotBlank()) "No results found" else "No notification to show, check again later",
            color = MolumulkMutedSlate,
            fontSize = 14.sp,
            fontFamily = AppFontFamily
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableNotificationItem(
    notification: NotificationUiModel,
    onDeleteRequest: (onSuccess: () -> Unit, onError: (String) -> Unit) -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isDeletedSuccess by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    if (showConfirmDialog) {
        ConfirmationAlert(
            title = "Are you sure you want to delete this notification?",
            yesText = "Delete",
            noText = "Cancel",
            onYesClick = {
                showConfirmDialog = false
                onDeleteRequest(
                    { // onSuccess
                        isDeletedSuccess = true
                    },
                    { _ -> // onError
                        showConfirmDialog = false
                    }
                )
            },
            onNoClick = {
                showConfirmDialog = false
            },
            onDismiss = {
                showConfirmDialog = false
            }
        )
    }

    if (isDeletedSuccess) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(NotificationSelectionBadgeRed)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Successfully deleted", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    } else {
        val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                    showConfirmDialog = true
                    false // reject swipe to avoid internal stuck states, we manually offset below
                } else {
                    false
                }
            },
            positionalThreshold = { totalDistance -> totalDistance * 0.4f }
        )

        val contentOffset by androidx.compose.animation.core.animateDpAsState(
            targetValue = if (showConfirmDialog) (-80).dp else 0.dp,
            label = "contentOffset"
        )

        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MolumulkOffWhite),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(80.dp)
                            .background(MolumulkAccentCoral),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }
            },
            content = {
                Box(modifier = Modifier.offset(x = contentOffset)) {
                    NotificationContent(
                        notification = notification,
                        onLongClick = onLongClick,
                        onClick = onClick
                    )
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationContent(
    notification: NotificationUiModel,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!notification.imageUrl.isNullOrBlank()) {
            AppSubComposeAsyncImage(
                model = notification.imageUrl,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(65.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            AppTextBold(
                text = notification.title,
                fontSize = 13.sp,
                color = if (notification.isRead) MolumulkMutedSlate else MolumulkCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            AppTextLight(
                text = formatNotificationTimeAgo(notification.createdAt),
                color = MolumulkMutedSlate,
                fontSize = 12.sp
            )
        }
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MolumulkPrimaryCoralRed, shape = RoundedCornerShape(percent = 50))
            )
        }
    }
}

@Composable
private fun SelectableNotificationItem(
    notification: NotificationUiModel,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .clickable { onToggle() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NotificationSelectionLead(
            notification = notification,
            isSelected = isSelected,
            onClick = onToggle
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            AppTextBold(
                text = notification.title,
                fontSize = 13.sp,
                color = if (notification.isRead) MolumulkMutedSlate else MolumulkCharcoal
            )
            Spacer(modifier = Modifier.height(4.dp))
            AppTextLight(
                text = formatNotificationTimeAgo(notification.createdAt),
                color = MolumulkMutedSlate,
                fontSize = 12.sp
            )
        }
        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(MolumulkPrimaryCoralRed, shape = RoundedCornerShape(percent = 50))
            )
        }
    }
}

@Composable
private fun NotificationSelectionLead(
    notification: NotificationUiModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val size = 65.dp
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = Modifier
            .size(size)
            .clip(shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (!notification.imageUrl.isNullOrBlank()) {
            AppSubComposeAsyncImage(
                model = notification.imageUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MolumulkPrimaryCoralRed.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center
            ) {
                AppImage(
                    resId = R.drawable.tick,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun DeleteSuccessToast(modifier: Modifier = Modifier) {
    Surface(
        color = MolumulkSuccessGreen,
        shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Successfully deleted", color = Color.White, fontSize = 14.sp, fontFamily = AppFontFamily)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun groupNotificationsByHeader(
    notifications: List<NotificationUiModel>
): List<Pair<String, List<NotificationUiModel>>> {
    val grouped = linkedMapOf<String, MutableList<NotificationUiModel>>()
    notifications.forEach { notification ->
        val header = formatNotificationHeader(notification.createdAt)
        grouped.getOrPut(header) { mutableListOf() }.add(notification)
    }
    return grouped.entries.map { it.key to it.value }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatNotificationHeader(createdAt: String): String {
    val instant = parseNotificationInstant(createdAt) ?: return "—"
    val zone = ZoneId.systemDefault()
    val date = instant.atZone(zone).toLocalDate()
    val today = LocalDate.now(zone)
    val yesterday = today.minusDays(1)

    return when (date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.US))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatNotificationTimeAgo(createdAt: String): String {
    val instant = parseNotificationInstant(createdAt) ?: return ""
    val now = Instant.now()
    val diff = Duration.between(instant, now)

    if (diff.isNegative) return "Just now"
    val minutes = diff.toMinutes()
    if (minutes < 1L) return "Just now"
    if (minutes < 60L) return "${minutes} min ago"

    val hours = diff.toHours()
    if (hours < 24L) return "${hours} hours ago"

    val days = diff.toDays()
    return if (days <= 1L) "1 day ago" else "${days} days ago"
}

private fun parseNotificationInstant(raw: String): Instant? {
    if (raw.isBlank()) return null

    // First try strict ISO-8601
    try {
        return Instant.parse(raw)
    } catch (_: Exception) {
        // continue to fallback
    }

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSX",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd"
    )

    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                if (pattern.contains("Z") || pattern.contains("X")) {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
            }
            val parsed = sdf.parse(raw) ?: continue
            return parsed.toInstant()
        } catch (_: Exception) {
            // try next pattern
        }
    }
    return null
}

