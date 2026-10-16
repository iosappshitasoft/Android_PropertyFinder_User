package com.app.propertyfinder.api

sealed class NetworkResult<out H> {
    data class Success<out T>(val body: T) : NetworkResult<T>()
    data class Failure(val status_code: Int,val message: String) : NetworkResult<Nothing>()
    data class UnAuthorized(val status_code: Int, val message: String) : NetworkResult<Nothing>()
    data class AdminDisabled(val status_code: Int, val message: String) : NetworkResult<Nothing>()
    object Pending : NetworkResult<Nothing>()
    object Complete : NetworkResult<Nothing>()
}