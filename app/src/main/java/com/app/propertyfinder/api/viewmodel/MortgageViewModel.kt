package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.MortgageCalculateData
import com.app.propertyfinder.api.dto.response.MortgageCalculateRequest
import com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsData
import com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsRequest
import com.app.propertyfinder.api.dto.response.MortgageQuoteData
import com.app.propertyfinder.api.dto.response.MortgageQuoteRequest
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
class MortgageViewModel @Inject constructor(
    private val repository: ApiRepository
) : ViewModel() {

    private val _calculateState =
        MutableStateFlow<UiState<ApiEnvelope<MortgageCalculateData>>>(UiState())
    val calculateState = _calculateState.asStateFlow()

    private val _upfrontCostsState =
        MutableStateFlow<UiState<ApiEnvelope<MortgageUpfrontCostsData>>>(UiState())
    val upfrontCostsState = _upfrontCostsState.asStateFlow()

    private val _quoteState =
        MutableStateFlow<UiState<ApiEnvelope<MortgageQuoteData>>>(UiState())
    val quoteState = _quoteState.asStateFlow()

    fun calculate(
        request: MortgageCalculateRequest,
        onResult: (ApiResult<ApiEnvelope<MortgageCalculateData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _calculateState.value = UiState(isLoading = true)
            when (val result = repository.calculateMortgage(request)) {
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

    fun calculateUpfrontCosts(
        request: MortgageUpfrontCostsRequest,
        onResult: (ApiResult<ApiEnvelope<MortgageUpfrontCostsData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _upfrontCostsState.value = UiState(isLoading = true)

            when (val result = repository.calculateUpfrontCosts(request)) {
                is ApiResult.Success -> {
                    _upfrontCostsState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _upfrontCostsState.value = UiState(
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

    fun getMortgageQuote(
        request: MortgageQuoteRequest,
        onResult: (ApiResult<ApiEnvelope<MortgageQuoteData>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _quoteState.value = UiState(isLoading = true)
            when (val result = repository.getMortgageQuote(request)) {
                is ApiResult.Success -> {
                    _quoteState.value = UiState(
                        isLoading = false,
                        data = result.body,
                        code = result.code
                    )
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _quoteState.value = UiState(
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

