package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateData
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateRequest
import com.app.propertyfinder.api.dto.response.RentVsBuyPaymentBreakdownResponseData
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
class RentVsBuyViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _calculateState =
        MutableStateFlow<UiState<ApiEnvelope<RentVsBuyCalculateData>>>(UiState())
    val calculateState = _calculateState.asStateFlow()

    private val _paymentBreakdownState =
        MutableStateFlow<UiState<ApiEnvelope<RentVsBuyPaymentBreakdownResponseData>>>(UiState())
    val paymentBreakdownState = _paymentBreakdownState.asStateFlow()

    fun calculate(
        request: RentVsBuyCalculateRequest,
        onResult: (ApiResult<ApiEnvelope<RentVsBuyCalculateData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _calculateState.value = UiState(isLoading = true)
            when (val result = repository.calculateRentVsBuy(request)) {
                is ApiResult.Success -> {
                    _calculateState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _calculateState.value = UiState(
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

    fun calculatePaymentBreakdown(
        request: RentVsBuyCalculateRequest,
        onResult: (ApiResult<ApiEnvelope<RentVsBuyPaymentBreakdownResponseData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _paymentBreakdownState.value = UiState(isLoading = true)
            when (val result = repository.calculateRentVsBuyPaymentBreakdown(request)) {
                is ApiResult.Success -> {
                    _paymentBreakdownState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _paymentBreakdownState.value = UiState(
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
