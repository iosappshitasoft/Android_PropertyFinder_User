package com.app.propertyfinder.api.network

sealed class ApiResult<out T> {
    data class Success<T>(
        val body: T,
        val code: Int
    ) : ApiResult<T>()

    data class Error(
        val message: String,
        val code: Int? = null,
        val rawBody: String? = null
    ) : ApiResult<Nothing>()
}