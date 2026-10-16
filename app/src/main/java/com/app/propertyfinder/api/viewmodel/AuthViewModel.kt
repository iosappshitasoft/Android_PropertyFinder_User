package com.app.propertyfinder.api.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.propertyfinder.api.dto.response.LoginData
import com.app.propertyfinder.api.dto.response.MasterDataResponse
import com.app.propertyfinder.api.dto.response.ProfileData
import com.app.propertyfinder.api.dto.response.RefreshTokenData
import com.app.propertyfinder.api.dto.response.RegisterData
import com.app.propertyfinder.api.dto.response.SentOtpResponse
import com.app.propertyfinder.api.dto.response.VerifyOtpResponse
import com.app.propertyfinder.api.dto.response.UpdateProfileRequest
import com.app.propertyfinder.api.dto.response.UpdateProfileResponse
import com.app.propertyfinder.api.dto.response.UploadProfilePictureResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.UiState
import com.app.propertyfinder.api.repository.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import kotlin.collections.hashMapOf

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: ApiRepository) : ViewModel() {

    /*User Register api*/
    private val _registerState = MutableStateFlow(UiState<ApiEnvelope<RegisterData>>())
    val registerState: StateFlow<UiState<ApiEnvelope<RegisterData>>> = _registerState.asStateFlow()

    fun register(firstName: String, lastName: String, email: String, password: String,
        phone: String, auth: String) {
        val request = hashMapOf<String, Any>(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email,
            "password" to password,
            "phoneNumber" to phone,
            "authProvider" to auth
        )

        viewModelScope.launch {
            _registerState.value = UiState(isLoading = true)

            when (val result = repository.register(request)) {
                is ApiResult.Success -> {
                    _registerState.value = UiState(isLoading = false, data = result.body, code = result.code)
                }

                is ApiResult.Error -> {
                    _registerState.value = UiState(isLoading = false, errorMessage = result.message, code = result.code, rawErrorBody = result.rawBody)
                }
            }
        }
    }

    fun clearRegisterState() {
        _registerState.value = UiState()
    }

    /*User Login api*/
    private val _loginState = MutableStateFlow(UiState<ApiEnvelope<LoginData>>())
    val loginState: StateFlow<UiState<ApiEnvelope<LoginData>>> = _loginState.asStateFlow()

    fun login(email: String,phone: String,password: String,rememberMe: Boolean,
              onResult: (ApiResult<ApiEnvelope<LoginData>>) -> Unit // added for get response button action inside
    ) {
        val request = hashMapOf<String, Any>(
            "email" to email,
            "phoneNumber" to phone,
            "password" to password,
            "rememberMe" to rememberMe
        )

        viewModelScope.launch {
            _loginState.value = UiState(isLoading = true)
            when (val result = repository.login(request)) {
                is ApiResult.Success -> {
                    _loginState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result) // added for get response button action inside
                }

                is ApiResult.Error -> {
                    _loginState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result) // added for get response button action inside
                }
            }
        }
    }

    fun clearLoginState() {
        _loginState.value = UiState()
    }

    /*Google Login Login api*/
    private val _googleLoginState = MutableStateFlow(UiState<ApiEnvelope<LoginData>>())
    val googleLoginState: StateFlow<UiState<ApiEnvelope<LoginData>>> = _googleLoginState.asStateFlow()

    fun googleLogin(token: String,email: String,firstName: String,lastName: String,user: Map<String, Any>,
              onResult: (ApiResult<ApiEnvelope<LoginData>>) -> Unit // added for get response button action inside
    ) {
        val request = hashMapOf<String, Any>(
            "provider" to "google",
            "token" to token,
            "email" to email,
            "firstName" to firstName,
            "lastName" to lastName,
            "user" to user,
        )

        viewModelScope.launch {
            _googleLoginState.value = UiState(isLoading = true)
            when (val result = repository.googleLogin(request)) {
                is ApiResult.Success -> {
                    _googleLoginState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result) // added for get response button action inside
                }

                is ApiResult.Error -> {
                    _googleLoginState.value = UiState(
                        isLoading = false,
                        errorMessage = result.message,
                        code = result.code,
                        rawErrorBody = result.rawBody
                    )
                    onResult(result) // added for get response button action inside
                }
            }
        }
    }

    fun clearGoogleLoginState() {
        _googleLoginState.value = UiState()
    }

    /*Get User Otp api*/
    private val _sendOtpState = MutableStateFlow(UiState<ApiEnvelope<SentOtpResponse>>())
    val sendOtpState: StateFlow<UiState<ApiEnvelope<SentOtpResponse>>> = _sendOtpState.asStateFlow()
    fun sendOtp(request: HashMap<String, Any>,onResult: (ApiResult<ApiEnvelope<SentOtpResponse>>) -> Unit) {
        viewModelScope.launch {
            _sendOtpState.value = UiState(isLoading = true)
            when (val result = repository.sendOtp(request)) {
                is ApiResult.Success -> {
                    _sendOtpState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _sendOtpState.value = UiState(isLoading = false, errorMessage = result.message,
                        code = result.code, rawErrorBody = result.rawBody)
                    onResult(result)
                }
            }
        }
    }
    fun clearSendOtpState() {
        _sendOtpState.value = UiState()
    }

    /*User Otp verification state*/
    private val _verifyOtpState = MutableStateFlow(UiState<ApiEnvelope<VerifyOtpResponse>>())
    val verifyOtpState: StateFlow<UiState<ApiEnvelope<VerifyOtpResponse>>> = _verifyOtpState.asStateFlow()

    fun verifyOtp(request: HashMap<String, Any>,onResult: (ApiResult<ApiEnvelope<VerifyOtpResponse>>) -> Unit) {
        viewModelScope.launch {
            _verifyOtpState.value = UiState(isLoading = true)
            when (val result = repository.verifyOtp(request)) {
                is ApiResult.Success -> {
                    _verifyOtpState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _verifyOtpState.value = UiState(isLoading = false, errorMessage = result.message,
                        code = result.code, rawErrorBody = result.rawBody)
                    onResult(result)
                }
            }
        }
    }
    fun clearVerifyOtpState() {
        _verifyOtpState.value = UiState()
    }

    /*Reset password api*/
    private val _resetPassState = MutableStateFlow(UiState<ApiEnvelope<VerifyOtpResponse>>())
    val resetPasswordState: StateFlow<UiState<ApiEnvelope<VerifyOtpResponse>>> = _resetPassState.asStateFlow()

    fun resetPassword(request: HashMap<String, Any>,onResult: (ApiResult<ApiEnvelope<VerifyOtpResponse>>) -> Unit) {
        viewModelScope.launch {
            _resetPassState.value = UiState(isLoading = true)
            when (val result = repository.resetPass(request)) {
                is ApiResult.Success -> {
                    _resetPassState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _resetPassState.value = UiState(isLoading = false, errorMessage = result.message,
                        code = result.code, rawErrorBody = result.rawBody)
                    onResult(result)
                }
            }
        }
    }
    fun clearResetPassState() {
        _resetPassState.value = UiState()
    }

    /*get user profile data api*/
    private val _profileState = MutableStateFlow(UiState<ApiEnvelope<ProfileData>>())
    val profileState: StateFlow<UiState<ApiEnvelope<ProfileData>>> = _profileState.asStateFlow()

    private val _updateProfileState = MutableStateFlow(UiState<ApiEnvelope<UpdateProfileResponse>>())
    val updateProfileState: StateFlow<UiState<ApiEnvelope<UpdateProfileResponse>>> =
        _updateProfileState.asStateFlow()

    private val _uploadProfilePictureState =
        MutableStateFlow(UiState<ApiEnvelope<UploadProfilePictureResponse>>())
    val uploadProfilePictureState: StateFlow<UiState<ApiEnvelope<UploadProfilePictureResponse>>> =
        _uploadProfilePictureState.asStateFlow()

    fun getProfileData(token: String, onResult: (ApiResult<ApiEnvelope<ProfileData>>) -> Unit) {
        viewModelScope.launch {
            _profileState.value = UiState(isLoading = true)
            when (val result = repository.userProfile(token)) {
                is ApiResult.Success -> {
                    _profileState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _profileState.value = UiState(isLoading = false, errorMessage = result.message,
                        code = result.code, rawErrorBody = result.rawBody)
                    onResult(result)
                }
            }
        }
    }
    fun clearProfileStateState() {
        _profileState.value = UiState()
    }

    fun updateUserProfile(
        token: String,
        request: UpdateProfileRequest,
        onResult: (ApiResult<ApiEnvelope<UpdateProfileResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _updateProfileState.value = UiState(isLoading = true)
            when (val result = repository.updateUserProfile(token, request)) {
                is ApiResult.Success -> {
                    _updateProfileState.value =
                        UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _updateProfileState.value = UiState(
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

    fun clearUpdateProfileState() {
        _updateProfileState.value = UiState()
    }

    fun uploadProfilePicture(
        token: String,
        profilePicture: MultipartBody.Part,
        removeProfilePicture: Boolean = false,
        onResult: (ApiResult<ApiEnvelope<UploadProfilePictureResponse>>) -> Unit = {}
    ) {
        viewModelScope.launch {
            _uploadProfilePictureState.value = UiState(isLoading = true)
            val removeBody = removeProfilePicture
                .toString()
                .toRequestBody("text/plain".toMediaTypeOrNull())
            when (
                val result =
                    repository.uploadProfilePicture(token, profilePicture, removeBody)
            ) {
                is ApiResult.Success -> {
                    _uploadProfilePictureState.value =
                        UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }

                is ApiResult.Error -> {
                    _uploadProfilePictureState.value = UiState(
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

    fun clearUploadProfilePictureState() {
        _uploadProfilePictureState.value = UiState()
    }

    /*refresh user bearer token api*/
    private val _refreshTokenState = MutableStateFlow(UiState<ApiEnvelope<RefreshTokenData>>())
    val refreshTokenState: StateFlow<UiState<ApiEnvelope<RefreshTokenData>>> = _refreshTokenState.asStateFlow()
    fun refreshToken(request: HashMap<String, Any>, onResult: (ApiResult<ApiEnvelope<RefreshTokenData>>) -> Unit) {
        viewModelScope.launch {
            _refreshTokenState.value = UiState(isLoading = true)
            when (val result = repository.refreshToken(request)) {
                is ApiResult.Success -> {
                    _refreshTokenState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _refreshTokenState.value = UiState(isLoading = false, errorMessage = result.message,
                        code = result.code, rawErrorBody = result.rawBody)
                    onResult(result)
                }
            }
        }
    }
    fun clearRefreshTokenState() {
        _refreshTokenState.value = UiState()
    }

    /*Logout api*/
    private val _logoutState = MutableStateFlow(UiState<ApiEnvelope<Nothing>>())
    val logoutState: StateFlow<UiState<ApiEnvelope<Nothing>>> = _logoutState.asStateFlow()

    fun logout(
        refreshToken: String,
        email: String,
        userId: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit
    ) {
        val request = hashMapOf<String, Any>(
            "refreshToken" to refreshToken,
            "email" to email,
            "userId" to userId
        )

        viewModelScope.launch {
            _logoutState.value = UiState(isLoading = true)
            when (val result = repository.logout(request)) {
                is ApiResult.Success -> {
                    _logoutState.value = UiState(isLoading = false, data = result.body, code = result.code)
                    onResult(result)
                }
                is ApiResult.Error -> {
                    _logoutState.value = UiState(
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

    fun clearLogoutState() {
        _logoutState.value = UiState()
    }

    fun deleteUserAccount(
        token: String,
        onResult: (ApiResult<ApiEnvelope<Nothing>>) -> Unit
    ) {
        viewModelScope.launch {
            when (val result = repository.deleteUserAccount(token)) {
                is ApiResult.Success -> onResult(result)
                is ApiResult.Error -> onResult(result)
            }
        }
    }

}