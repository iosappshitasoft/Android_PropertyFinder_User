package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.DeleteNotificationsResponse
import com.app.propertyfinder.api.dto.response.MarkNotificationReadResponse
import com.app.propertyfinder.api.dto.response.NotificationsResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _notificationsState =
        MutableStateFlow(UiState<ApiEnvelope<NotificationsResponse>>())
    val notificationsState: StateFlow<UiState<ApiEnvelope<NotificationsResponse>>> =
        _notificationsState.asStateFlow()

    private val _deleteState =
        MutableStateFlow(UiState<ApiEnvelope<DeleteNotificationsResponse>>())
    val deleteState: StateFlow<UiState<ApiEnvelope<DeleteNotificationsResponse>>> =
        _deleteState.asStateFlow()

    private val _markReadState =
        MutableStateFlow(UiState<ApiEnvelope<MarkNotificationReadResponse>>())
    val markReadState: StateFlow<UiState<ApiEnvelope<MarkNotificationReadResponse>>> =
        _markReadState.asStateFlow()

    fun getNotifications(
        token: String,
        tab: String,
        search: String?,
        page: Int,
        limit: Int,
        onResult: (ApiResult<ApiEnvelope<NotificationsResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _notificationsState.value = UiState(isLoading = true)

            when (val result = repository.getUserNotifications(token, tab, search, page, limit)) {
                is ApiResult.Success -> {
                    _notificationsState.value =
                        UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _notificationsState.value =
                        UiState(
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

    fun deleteNotifications(
        token: String,
        ids: List<String>,
        onResult: (ApiResult<ApiEnvelope<DeleteNotificationsResponse>>) -> Unit = {}
    ) {
        if (ids.isEmpty()) return

        viewModelScope.launch {
            _deleteState.value = UiState(isLoading = true)

            when (val result = repository.deleteUserNotifications(token, ids)) {
                is ApiResult.Success -> {
                    _deleteState.value =
                        UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _deleteState.value =
                        UiState(
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

    fun markNotificationRead(
        token: String,
        id: String,
        onResult: (ApiResult<ApiEnvelope<MarkNotificationReadResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _markReadState.value = UiState(isLoading = true)

            when (val result = repository.markNotificationRead(token, id)) {
                is ApiResult.Success -> {
                    _markReadState.value =
                        UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _markReadState.value =
                        UiState(
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

