package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.ReportUploadAttachmentsResponse
import com.app.propertyfinder.api.dto.response.ReportCreateRequest
import com.app.propertyfinder.api.dto.response.ReportCreateResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _uploadAttachmentsState =
        MutableStateFlow<UiState<ApiEnvelope<ReportUploadAttachmentsResponse>>>(UiState())
    val uploadAttachmentsState = _uploadAttachmentsState.asStateFlow()

    private val _createReportState = MutableStateFlow<UiState<ApiEnvelope<ReportCreateResponse>>>(UiState())
    val createReportState = _createReportState.asStateFlow()

    fun uploadAttachments(
        token: String,
        attachments: List<MultipartBody.Part>,
        onResult: (ApiResult<ApiEnvelope<ReportUploadAttachmentsResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uploadAttachmentsState.value = UiState(isLoading = true)
            when (val result = repository.uploadReportAttachments(token, attachments)) {
                is ApiResult.Success -> {
                    _uploadAttachmentsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _uploadAttachmentsState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }

    fun createReport(
        token: String?,
        request: ReportCreateRequest,
        onResult: (ApiResult<ApiEnvelope<ReportCreateResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _createReportState.value = UiState(isLoading = true)
            when (val result = repository.createReport(token = token, request = request)) {
                is ApiResult.Success -> {
                    _createReportState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _createReportState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result)
                }
            }
        }
    }
}

