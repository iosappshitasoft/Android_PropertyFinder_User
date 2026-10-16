package com.app.propertyfinder.api.network

data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val errorMessage: String? = null,
    val code: Int? = null,
    val rawErrorBody: String? = null
)