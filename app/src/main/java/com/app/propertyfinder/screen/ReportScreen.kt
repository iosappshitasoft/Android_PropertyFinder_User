package com.app.propertyfinder.screen

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.app.propertyfinder.R
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.viewmodel.ReportViewModel
import com.app.propertyfinder.ui.components.AppIcon
import com.app.propertyfinder.ui.components.AppSpacerH
import com.app.propertyfinder.ui.components.AppSpacerV
import com.app.propertyfinder.ui.components.AppTextBold
import com.app.propertyfinder.ui.components.AppTextLight
import com.app.propertyfinder.ui.components.AppTextRegular
import com.app.propertyfinder.ui.theme.Background
import com.app.propertyfinder.ui.theme.MolumulkBorderDivider
import com.app.propertyfinder.ui.theme.MolumulkCharcoal
import com.app.propertyfinder.ui.theme.MolumulkDimens
import com.app.propertyfinder.ui.theme.MolumulkMutedSlate
import com.app.propertyfinder.ui.theme.MolumulkPrimaryCoralRed
import com.app.propertyfinder.utils.Constants
import com.app.propertyfinder.utils.PrefUtils
import com.app.propertyfinder.utils.rememberSnackbarManager
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class UploadItem(
    val uri: Uri,
    val kind: AttachmentKind,
    progress: Float = 0f,
    isUploading: Boolean = true
) {
    var progress by mutableStateOf(progress)
    var isUploading by mutableStateOf(isUploading)
}

enum class AttachmentKind {
    IMAGE,
    PDF,
    VIDEO,
    OTHER
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController){
    val context = LocalContext.current
    var email by remember { mutableStateOf( "") }
    var description by remember { mutableStateOf("") }

    val uploadItems = remember { mutableStateListOf<UploadItem>() }
    var showSuccessPopup by remember { mutableStateOf(false) }
    var successPopupMessage by remember { mutableStateOf("") }
    val reportViewModel: ReportViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val snackbarManager = rememberSnackbarManager()

    val reportNavState = navController.previousBackStackEntry?.savedStateHandle
    val reportTypeFromNav = reportNavState?.get<String>("reportType")
    val reportedItemIdFromNav = reportNavState?.get<String>("reportedItemId")
    val reasonFromNav = reportNavState?.get<String>("reason")
    val userTypeFromNav = reportNavState?.get<String>("userType")

    // Multi image picker
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris
            .take(10 - uploadItems.size)
            .forEach { uri ->
                if (uri.isSupportedAttachment(context)) {
                    uploadItems.add(
                        UploadItem(
                            uri = uri,
                            kind = uri.getAttachmentKind(context),
                            progress = 0f,
                            isUploading = false
                        )
                    )
                }
            }
        }

    if (showSuccessPopup) {
        ReportSuccessPopup(
            message = successPopupMessage.ifBlank { stringResource(R.string.report_send_success) },
            onDismiss = {
                showSuccessPopup = false
                successPopupMessage = ""
                email = ""
                description = ""
                uploadItems.clear()
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    AppTextBold(text = stringResource(R.string.report_problem), fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MolumulkCharcoal
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val emailTrim = email.trim()
                        val descTrim = description.trim()

                        if (emailTrim.isBlank()) {
                            snackbarManager.showError("Enter email")
                            return@IconButton
                        }
                        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(emailTrim).matches()
                        if (!emailValid) {
                            snackbarManager.showError("Incorrect email")
                            return@IconButton
                        }
                        if (descTrim.isBlank()) {
                            snackbarManager.showError("Enter description")
                            return@IconButton
                        }
                        if (descTrim.length < 15) {
                            snackbarManager.showError("Description must be at least 15 characters")
                            return@IconButton
                        }

                        val isLoggedIn =
                            PrefUtils.getString(Constants.TAG_LOGIN, "") == Constants.LOGIN_USER
                        val accessToken = PrefUtils.getString(Constants.TAG_ACCESS_TOKEN, "").orEmpty()
                        val bearer = accessToken.takeIf { it.isNotBlank() }?.let { "Bearer $it" }.orEmpty()
                        val createAuthToken = if (isLoggedIn && bearer.isNotBlank()) bearer else null

                        fun createWithUploadedUrls(attachmentUrls: List<String>) {
                            if (isLoggedIn) {
                                val finalReportType = reportTypeFromNav ?: "property"
                                val finalReportedItemId = reportedItemIdFromNav
                                val finalReason = reasonFromNav ?: "incorrect-info"
                                val finalUserType = userTypeFromNav ?: "user"

                                if (finalReportedItemId.isNullOrBlank()) {
                                    // Fallback to guest payload when report item metadata is missing
                                    val request = com.app.propertyfinder.api.dto.response.ReportCreateRequest(
                                        reportType = null,
                                        reportedItemId = null,
                                        userType = null,
                                        reason = null,
                                        email = emailTrim,
                                        description = descTrim,
                                        attachments = attachmentUrls
                                    )

                                    reportViewModel.createReport(
                                        token = null,
                                        request = request
                                    ) { createResult ->
                                        when (createResult) {
                                            is ApiResult.Success -> {
                                                successPopupMessage = createResult.body.message.orEmpty()
                                                showSuccessPopup = true
                                            }

                                            is ApiResult.Error -> {
                                                uploadItems.forEach { it.isUploading = false }
                                                snackbarManager.showError(createResult.message)
                                            }
                                        }
                                    }
                                    return
                                }

                                val request = com.app.propertyfinder.api.dto.response.ReportCreateRequest(
                                    reportType = finalReportType,
                                    reportedItemId = finalReportedItemId,
                                    userType = finalUserType,
                                    reason = finalReason,
                                    email = emailTrim,
                                    description = descTrim,
                                    attachments = attachmentUrls
                                )

                                reportViewModel.createReport(
                                    token = createAuthToken,
                                    request = request
                                ) { createResult ->
                                    when (createResult) {
                                        is ApiResult.Success -> {
                                            successPopupMessage = createResult.body.message.orEmpty()
                                            showSuccessPopup = true
                                        }

                                        is ApiResult.Error -> {
                                            uploadItems.forEach { it.isUploading = false }
                                            snackbarManager.showError(createResult.message)
                                        }
                                    }
                                }
                            } else {
                                val request = com.app.propertyfinder.api.dto.response.ReportCreateRequest(
                                    reportType = null,
                                    reportedItemId = null,
                                    userType = null,
                                    reason = null,
                                    email = emailTrim,
                                    description = descTrim,
                                    attachments = attachmentUrls
                                )

                                reportViewModel.createReport(
                                    token = null,
                                    request = request
                                ) { createResult ->
                                    when (createResult) {
                                        is ApiResult.Success -> {
                                            successPopupMessage = createResult.body.message.orEmpty()
                                            showSuccessPopup = true
                                        }

                                        is ApiResult.Error -> {
                                            uploadItems.forEach { it.isUploading = false }
                                            snackbarManager.showError(createResult.message)
                                        }
                                    }
                                }
                            }
                        }

                        // If no files, still create a report with empty attachments.
                        if (uploadItems.isEmpty()) {
                            createWithUploadedUrls(emptyList())
                            return@IconButton
                        }

                        if (bearer.isBlank()) {
                            snackbarManager.showError("Session expired. Please login again.")
                            return@IconButton
                        }

                        uploadItems.forEach { item ->
                            item.progress = 0f
                            item.isUploading = true
                        }

                        coroutineScope.launch {
                            uploadItems.forEach { item -> simulateUpload(item) }

                            val (parts, tempFiles) = withContext(Dispatchers.IO) {
                                buildAttachmentParts(context, uploadItems)
                            }

                            if (parts.isEmpty()) {
                                tempFiles.forEach { runCatching { it.delete() } }
                                uploadItems.forEach { it.isUploading = false }
                                snackbarManager.showError("Failed to prepare attachments")
                                return@launch
                            }

                            reportViewModel.uploadAttachments(
                                token = bearer,
                                attachments = parts,
                            ) { uploadResult ->
                                tempFiles.forEach { runCatching { it.delete() } }

                                when (uploadResult) {
                                    is ApiResult.Success -> {
                                        uploadItems.forEach { it.isUploading = false }
                                        val uploads = uploadResult.body.data?.uploads
                                        val urls = buildList {
                                            addAll(uploads?.images?.mapNotNull { it.url }.orEmpty())
                                            addAll(uploads?.videos?.mapNotNull { it.url }.orEmpty())
                                            addAll(uploads?.documents?.mapNotNull { it.url }.orEmpty())
                                        }
                                        createWithUploadedUrls(urls)
                                    }

                                    is ApiResult.Error -> {
                                        uploadItems.forEach { it.isUploading = false }
                                        snackbarManager.showError(uploadResult.message)
                                    }
                                }
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_report_send),
                            contentDescription = "send",
                            tint = MolumulkPrimaryCoralRed
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        bottomBar = {
            BottomAttachBar {
                launcher.launch("*/*")
            }
        },
        snackbarHost = {
            snackbarManager.CustomSnackbarHost()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.screen_bg_color))
                .padding(horizontal = 20.dp, vertical = 15.dp)
        ) {

            AppTextRegular(text = stringResource(R.string.email_address),
                color = MolumulkCharcoal)
            AppSpacerV(6)

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { AppTextLight(text = stringResource(R.string.enter_email_address),
                    color = MolumulkMutedSlate) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    // 👇 Background color
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,

                    focusedBorderColor = MolumulkPrimaryCoralRed,   // 👈 Focus border color
                    unfocusedBorderColor = MolumulkBorderDivider,
                    cursorColor = MolumulkPrimaryCoralRed
                )
            )

            AppSpacerV(16)
            AppTextRegular(text = stringResource(R.string.description), color = MolumulkCharcoal)

            AppSpacerV(6)

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { AppTextLight(text = stringResource(R.string.report_hint),
                    color = MolumulkMutedSlate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(MolumulkDimens.InputCornerRadius),
                colors = OutlinedTextFieldDefaults.colors(
                    // 👇 Background color
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,

                    focusedBorderColor = MolumulkPrimaryCoralRed,   // 👈 Focus border color
                    unfocusedBorderColor = MolumulkBorderDivider,
                    cursorColor = MolumulkPrimaryCoralRed
                )
            )
            AppSpacerV(20)

            Row (
                verticalAlignment = Alignment.Bottom
            ){
                AppTextRegular(text = stringResource(R.string.attach_files),
                    color = MolumulkCharcoal)
                AppSpacerH(5)
                AppTextLight(text = stringResource(R.string.optional),
                    color = MolumulkMutedSlate, fontSize = 11.sp,
                    modifier = Modifier
                )

            }
            AppSpacerV(10)

            if (uploadItems.isEmpty()) {
                EmptyAttachView()
            } else {
                LazyRow {
                    items(uploadItems) { item ->
                        UploadItemView(
                            item = item,
                            onRemove = { uploadItems.remove(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UploadItemView(item: UploadItem, onRemove: () -> Unit) {
    Box(modifier = Modifier.padding(end = 12.dp).size(90.dp)
        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
    ) {
        when (item.kind) {
            AttachmentKind.PDF -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextBold(
                        text = "PDF",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }

            AttachmentKind.VIDEO -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AppIcon(
                        resId = R.drawable.ic_video_play,
                        modifier = Modifier.size(34.dp),
                        tint = Color.White
                    )
                }
            }

            AttachmentKind.IMAGE -> {
                Image(
                    painter = rememberAsyncImagePainter(item.uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp, end = 8.dp)
                        .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius)),
                    contentScale = ContentScale.Crop
                )
            }

            AttachmentKind.OTHER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    AppTextBold(
                        text = "FILE",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        if (item.isUploading) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(top = 8.dp, end = 8.dp)
                    .clip(RoundedCornerShape(MolumulkDimens.InputCornerRadius))
                .background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(resId = R.drawable.ic_upload,
                    modifier = Modifier.size(20.dp))
                CircularProgressIndicator(
                    progress = item.progress,
                    color = MolumulkPrimaryCoralRed,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(35.dp)
                )
            }
        } else {
            // Remove button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(MolumulkPrimaryCoralRed)
                    .clickable { onRemove() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyAttachView() {
    Column(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppIcon(
            resId = R.drawable.ic_folder,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        AppTextLight(text = "No files attached", color = MolumulkMutedSlate)
    }
}

@Composable
fun BottomAttachBar(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Color.Black).clickable { onClick() }
            .navigationBarsPadding().padding(vertical = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Column (horizontalAlignment = Alignment.CenterHorizontally) {
            AppIcon(
                resId = R.drawable.ic_attach_files,
                contentDescription = null,
                tint = Color.White
            )
            AppSpacerV(5)
            AppTextRegular(text = stringResource(R.string.attach_files), color = Color.White)
        }
    }
}

fun simulateUpload(item: UploadItem) {
    CoroutineScope(Dispatchers.Main).launch {
        for (i in 1..100) {
            delay(30)
            item.progress = i / 100f
        }
    }
}

private fun Uri.getDisplayName(context: Context): String? {
    return try {
        val cursor = context.contentResolver.query(
            this,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) it.getString(0) else null
        }
    } catch (_: Exception) {
        null
    }
}

private fun Uri.isSupportedAttachment(context: Context): Boolean {
    val fileName = getDisplayName(context).orEmpty()
    val ext = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
    val allowedExt = setOf("jpg", "jpeg", "png", "webp", "pdf", "doc", "docx", "mp4", "webm")

    if (ext.isNotBlank()) return allowedExt.contains(ext)

    val mimeType = context.contentResolver.getType(this).orEmpty().lowercase()
    return mimeType.startsWith("image/") ||
        mimeType == "application/pdf" ||
        mimeType == "application/msword" ||
        mimeType == "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ||
        mimeType == "video/mp4" ||
        mimeType == "video/webm"
}

private fun Uri.getAttachmentKind(context: Context): AttachmentKind {
    val fileName = getDisplayName(context).orEmpty()
    val ext = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()

    val mimeType = context.contentResolver.getType(this).orEmpty().lowercase()

    return when {
        mimeType.startsWith("image/") || ext in setOf("jpg", "jpeg", "png", "webp") -> AttachmentKind.IMAGE
        mimeType == "application/pdf" || ext == "pdf" -> AttachmentKind.PDF
        mimeType.startsWith("video/") || ext in setOf("mp4", "webm") -> AttachmentKind.VIDEO
        else -> AttachmentKind.OTHER
    }
}

private suspend fun buildAttachmentParts(
    context: Context,
    items: List<UploadItem>
): Pair<List<MultipartBody.Part>, List<File>> {
    val parts = mutableListOf<MultipartBody.Part>()
    val tempFiles = mutableListOf<File>()

    for (item in items) {
        val fileName = item.uri.getDisplayName(context).orEmpty()
            .ifBlank { "attachment_${System.currentTimeMillis()}" }

        val ext = fileName.substringAfterLast('.', missingDelimiterValue = "").lowercase()
        val mimeType = context.contentResolver.getType(item.uri)
            ?: MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext).orEmpty()

        val tempFile = File(context.cacheDir, "report_upload_${System.currentTimeMillis()}_${fileName}")

        val ok = context.contentResolver.openInputStream(item.uri)?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        } != null

        if (!ok) {
            runCatching { tempFile.delete() }
            continue
        }

        tempFiles.add(tempFile)

        val requestBody = tempFile.asRequestBody(mimeType.toMediaTypeOrNull())
        parts.add(MultipartBody.Part.createFormData("attachments", fileName, requestBody))
    }

    return parts to tempFiles
}

@Composable
fun ReportSuccessPopup(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { /* outside click disabled */ }) {

        Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Card(
                shape = RoundedCornerShape(MolumulkDimens.CardCornerRadius),
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
                            .clickable { onDismiss() }
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
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReportProblemPreview(){
    ReportScreen(navController = rememberNavController())
}