package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterRequest
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PushNotificationViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _registerState =
        MutableStateFlow(UiState<ApiEnvelope<PushNotificationRegisterResponse>>())
    val registerState = _registerState.asStateFlow()

    fun registerPush(
        bearerToken: String?,
        request: PushNotificationRegisterRequest,
        onResult: (ApiResult<ApiEnvelope<PushNotificationRegisterResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _registerState.value = UiState(isLoading = true)
            when (val result = repository.registerPushNotification(bearerToken, request)) {
                is ApiResult.Success -> {
                    _registerState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _registerState.value = UiState(
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

