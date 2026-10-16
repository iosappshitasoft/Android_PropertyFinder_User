package com.app.propertyfinder.utils

class ApiConstants {

    companion object{
        const val OK = 200
        const val UNAUTHORIZED = 401
        const val DELETED = 402
        const val INVALID_REQUEST = 400
        const val NOT_FOUND = 404
        const val SERVER_ERROR = 500
        const val DISABLED = 403

        const val TAG_AUTHORIZATION = "Authorization"
        const val TAG_ACCEPT_LANGUAGE = "Accept-Language"
        const val TAG_TRUE = "true"
        const val TAG_FALSE = "false"
        const val TAG_STATUS_CODE = "status_code"
        const val TAG_STATUS = "status"
        const val TAG_MESSAGE = "message"
        const val ACCOUNT_CREATED = "Account created successfully"
        const val LOGIN_SUCCESSFULLY = "Login successfully"
        const val ACCOUNT_DISABLED = "Account is disabled"
        const val ACCOUNT_BLOCKED = "Account has been blocked"
        const val ACCOUNT_DELETED = "Account has been deleted"
        const val EMAIL_ALREADY_EXISTS = "Email Already exists"
        const val EMAIL_ALREADY_REGISTERED = "Email is already registered"
        const val EMAIL_NOT_REGISTERED = "Email is not registered"
        const val ACCOUNT_NOT_EXISTS = "Account does not exists"
        const val INVALID_CREDENTIALS = "Username/Password is not valid"
        const val INVALID_USER_ID = "Invalid User ID"
        const val TAG_TOP = "top"
        const val TAG_BOTTOM = "bottom"
    }
}