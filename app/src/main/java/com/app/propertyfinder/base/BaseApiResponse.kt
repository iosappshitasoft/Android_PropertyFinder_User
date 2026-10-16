package com.app.propertyfinder.base

import com.app.propertyfinder.api.NetworkResult
import com.app.propertyfinder.utils.ApiConstants
import retrofit2.Response
import timber.log.Timber

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T & Any> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
//                    Here handle if status code 401
                    return NetworkResult.Success(body)
                }
            } else if (response.code() == ApiConstants.Companion.UNAUTHORIZED) {
                return unAuthorized(
                    response.code(),
                    "your account has been deleted by admin, Kindly contact to admin!"
                )
            } else if (response.code() == ApiConstants.Companion.DELETED) {
                return unAuthorized(
                    response.code(),
                    "your account has been deleted by admin, Kindly contact to admin!"
                )
            } else if (response.code() == ApiConstants.Companion.DISABLED) {
                return adminDisabled(
                    response.code(),
                    "your account blocked by admin, Kindly contact to admin!"
                )
            }

            Timber.Forest.e("safeApiCall: %s, %s", response.code(), response.message())
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            Timber.Forest.tag(TAG).e("safeApiCall: %s", e.message)
            return error(405, "Server error")
        }
    }

    private fun <T> error(code: Int, errorMessage: String): NetworkResult<T> =
        NetworkResult.Failure(code, "$errorMessage")

    private fun <T> unAuthorized(code: Int, errorMessage: String): NetworkResult<T & Any> =
        NetworkResult.UnAuthorized(code, "$errorMessage")

    private fun <T> adminDisabled(code: Int, errorMessage: String): NetworkResult<T & Any> =
        NetworkResult.AdminDisabled(code, "$errorMessage")

    companion object
    val TAG = BaseApiResponse::class.java.simpleName
}