package com.app.propertyfinder.api.repository

import com.app.propertyfinder.api.ApiService
import com.app.propertyfinder.api.dto.response.LoginData
import com.app.propertyfinder.api.dto.response.MasterDataResponse
import com.app.propertyfinder.api.dto.response.ProfileData
import com.app.propertyfinder.api.dto.response.ProjectSearchResponse
import com.app.propertyfinder.api.dto.response.ProjectDetailResponse
import com.app.propertyfinder.api.dto.response.PropertyDetailResponse
import com.app.propertyfinder.api.dto.response.PriceInsightsResponse
import com.app.propertyfinder.api.dto.response.PropertyResponse
import com.app.propertyfinder.api.dto.response.RefreshTokenData
import com.app.propertyfinder.api.dto.response.RegisterData
import com.app.propertyfinder.api.dto.response.SavedPropertiesResponse
import com.app.propertyfinder.api.dto.response.SentOtpResponse
import com.app.propertyfinder.api.dto.response.VerifyOtpResponse
import com.app.propertyfinder.api.dto.response.AlertsResponse
import com.app.propertyfinder.api.dto.response.AlertItem
import com.app.propertyfinder.api.dto.response.ContactedPropertiesResponse
import com.app.propertyfinder.api.dto.response.DeveloperSearchResponse
import com.app.propertyfinder.api.dto.response.DeveloperProfileResponse
import com.app.propertyfinder.api.dto.response.HelpFaqsResponse
import com.app.propertyfinder.api.dto.response.InquiryData
import com.app.propertyfinder.api.dto.response.AgencyProfileResponse
import com.app.propertyfinder.api.dto.response.AgencySearchResponse
import com.app.propertyfinder.api.dto.response.AgentProfileResponse
import com.app.propertyfinder.api.dto.response.AgentSearchResponse
import com.app.propertyfinder.api.network.ApiEnvelope
import com.app.propertyfinder.api.network.ApiResult
import com.app.propertyfinder.api.network.ErrorParser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import com.app.propertyfinder.api.dto.response.ReportUploadAttachmentsResponse
import com.app.propertyfinder.api.dto.response.ReportCreateRequest
import com.app.propertyfinder.api.dto.response.ReportCreateResponse
import com.app.propertyfinder.api.dto.response.MortgageCalculateRequest
import com.app.propertyfinder.api.dto.response.MortgageCalculateData
import com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsRequest
import com.app.propertyfinder.api.dto.response.MortgageUpfrontCostsData
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterRequest
import com.app.propertyfinder.api.dto.response.PushNotificationRegisterResponse
import com.app.propertyfinder.api.dto.response.NotificationsResponse
import com.app.propertyfinder.api.dto.response.DeleteNotificationsResponse
import com.app.propertyfinder.api.dto.response.MarkNotificationReadResponse
import com.app.propertyfinder.api.dto.response.UpdateProfileRequest
import com.app.propertyfinder.api.dto.response.UpdateProfileResponse
import com.app.propertyfinder.api.dto.response.UploadProfilePictureResponse
import com.app.propertyfinder.api.dto.response.MortgageQuoteRequest
import com.app.propertyfinder.api.dto.response.MortgageQuoteData
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateData
import com.app.propertyfinder.api.dto.response.RentVsBuyCalculateRequest
import com.app.propertyfinder.api.dto.response.RentVsBuyPaymentBreakdownResponseData
import com.app.propertyfinder.api.dto.response.HistoricalTransactionsResponse

class ApiRepository @Inject constructor(private val apiService: ApiService) {
    private suspend fun <T> safeApiCallll(apiCall: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body = body, code = response.code())
                } else {
                    ApiResult.Error(message = "Empty response body", code = response.code())
                }
            } else {
                val raw = response.errorBody()?.string()
                ApiResult.Error(message = ErrorParser.parse(raw), code = response.code(), rawBody = raw)
            }
        } catch (e: SocketTimeoutException) {
            ApiResult.Error(message = "Request timeout", rawBody = e.message)
        } catch (e: IOException) {
            ApiResult.Error(message = "No internet connection", rawBody = e.message)
        } catch (e: Exception) {
            ApiResult.Error(message = e.message ?: "Something went wrong", rawBody = e.stackTraceToString())
        }
    }
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<ApiEnvelope<T>>): ApiResult<ApiEnvelope<T>> {
        return try {
            val response = apiCall()

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true) {
                    ApiResult.Success(body, response.code())
                } else {
                    ApiResult.Error(
                        message = body?.message ?: "Something went wrong",
                        code = response.code()
                    )
                }

            } else {
                val rawBody = response.errorBody()?.string()
                ApiResult.Error(
                    message = ErrorParser.parse(rawBody),
                    code = response.code(),
                    rawBody = rawBody
                )
            }

        } catch (e: Exception) {
            ApiResult.Error(e.message ?: "Unknown error")
        }
    }
    suspend fun register(request: Map<String, Any>): ApiResult<ApiEnvelope<RegisterData>> {
        return safeApiCall { apiService.userRegister(request) }
    }
    suspend fun login(request: Map<String, Any>): ApiResult<ApiEnvelope<LoginData>> {
        return safeApiCall { apiService.userLogin(request) }
    }
    suspend fun googleLogin(request: Map<String, Any>): ApiResult<ApiEnvelope<LoginData>> {
        return safeApiCall { apiService.googleLogin(request) }
    }
    suspend fun sendOtp(request: Map<String, Any>): ApiResult<ApiEnvelope<SentOtpResponse>> {
        return safeApiCall { apiService.sendOtp(request) }
    }
    suspend fun verifyOtp(request: Map<String, Any>): ApiResult<ApiEnvelope<VerifyOtpResponse>> {
        return safeApiCall { apiService.verifyOtp(request) }
    }
    suspend fun resetPass(request: Map<String, Any>): ApiResult<ApiEnvelope<VerifyOtpResponse>> {
        return safeApiCall { apiService.resetPassword(request) }
    }
    suspend fun userProfile(token: String): ApiResult<ApiEnvelope<ProfileData>> {
        return safeApiCall { apiService.userProfile("Bearer $token") }
    }

    suspend fun updateUserProfile(
        token: String,
        request: UpdateProfileRequest
    ): ApiResult<ApiEnvelope<UpdateProfileResponse>> {
        return safeApiCall { apiService.updateUserProfile("Bearer $token", request) }
    }

    suspend fun deleteUserAccount(token: String): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.deleteUserAccount("Bearer $token") }
    }

    suspend fun uploadProfilePicture(
        token: String,
        profilePicture: MultipartBody.Part,
        removeProfilePicture: RequestBody
    ): ApiResult<ApiEnvelope<UploadProfilePictureResponse>> {
        return safeApiCall {
            apiService.uploadProfilePicture(
                token = "Bearer $token",
                profilePicture = profilePicture,
                removeProfilePicture = removeProfilePicture
            )
        }
    }
    suspend fun refreshToken(request: Map<String, Any>): ApiResult<ApiEnvelope<RefreshTokenData>> {
        return safeApiCall { apiService.refreshToken(request) }
    }

    suspend fun logout(request: Map<String, Any>): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.logout(request) }
    }
    suspend fun addToSaved(token: String, request: Map<String, Any>): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.addToSaved("Bearer $token", request) }
    }

    suspend fun getSavedProperties(token: String, request: Map<String, Any>): ApiResult<ApiEnvelope<SavedPropertiesResponse>> {
        return safeApiCall { apiService.getSavedProperties("Bearer $token", request) }
    }

    suspend fun removeFromSaved(token: String, propertyId: String): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.removeFromSaved("Bearer $token", propertyId) }
    }




    suspend fun getMasterData(types: String): ApiResult<ApiEnvelope<MasterDataResponse>> {
        return safeApiCall { apiService.getMasterData(types) }
    }
    suspend fun propertyData(
        request: Map<String, Any>,
        token: String? = null
    ): ApiResult<ApiEnvelope<PropertyResponse>> {
        val bearer = token?.trim().takeUnless { it.isNullOrBlank() }?.let { "Bearer $it" }
        return safeApiCall { apiService.getProperty(token = bearer, request = request) }
    }
    suspend fun projectData(request: Map<String, Any>): ApiResult<ApiEnvelope<ProjectSearchResponse>> {
        return safeApiCall { apiService.getProjects(request) }
    }

    suspend fun projectDetails(projectId: String): ApiResult<ApiEnvelope<ProjectDetailResponse>> {
        return safeApiCall { apiService.getProjectDetails(projectId) }
    }

    suspend fun propertyDetails(token: String, propertyId: String): ApiResult<ApiEnvelope<PropertyDetailResponse>> {
        return safeApiCall { apiService.getPropertyDetails("Bearer $token",propertyId) }
    }

    suspend fun getPriceInsights(propertyId: String): ApiResult<ApiEnvelope<PriceInsightsResponse>> {
        return safeApiCall { apiService.getPriceInsights(propertyId) }
    }

    suspend fun getUserAlerts(
        token: String,
        page: Int,
        limit: Int
    ): ApiResult<ApiEnvelope<AlertsResponse>> {
        return safeApiCall { apiService.getUserAlerts("Bearer $token", page, limit) }
    }

    suspend fun createUserAlert(
        token: String,
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<AlertItem>> {
        return safeApiCall { apiService.createUserAlert("Bearer $token", request) }
    }

    suspend fun deleteAllUserAlerts(token: String): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.deleteAllUserAlerts("Bearer $token") }
    }

    suspend fun deleteUserAlert(token: String, id: String): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.deleteUserAlert("Bearer $token", id) }
    }

    suspend fun updateUserAlert(
        token: String,
        id: String,
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<AlertItem>> {
        return safeApiCall { apiService.updateUserAlert("Bearer $token", id, request) }
    }

    suspend fun getContactedProperties(
        token: String,
        page: Int,
        limit: Int
    ): ApiResult<ApiEnvelope<ContactedPropertiesResponse>> {
        return safeApiCall { apiService.getContactedProperties("Bearer $token", page, limit) }
    }

    suspend fun deleteContactedProperties(
        token: String,
        ids: List<String>
    ): ApiResult<ApiEnvelope<Nothing>> {
        val body = mapOf("ids" to ids)
        return safeApiCall { apiService.deleteContactedProperties("Bearer $token", body) }
    }

    suspend fun deleteAllContactedProperties(
        token: String
    ): ApiResult<ApiEnvelope<Nothing>> {
        return safeApiCall { apiService.deleteAllContactedProperties("Bearer $token") }
    }

    suspend fun createInquiry(
        token: String?,
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<InquiryData>> {
        val authHeader = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
        return safeApiCall { apiService.createInquiry(authHeader, request) }
    }

    suspend fun searchAgencies(
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<AgencySearchResponse>> {
        return safeApiCall { apiService.searchAgencies(request) }
    }

    suspend fun getAgencyProfile(
        companyId: String,
        page: Int? = null,
        limit: Int? = null,
        agentPage: Int? = null,
        agentLimit: Int? = null,
        listingType: String? = null,
        sortBy: String? = null
    ): ApiResult<ApiEnvelope<AgencyProfileResponse>> {
        return safeApiCall {
            apiService.getAgencyProfile(
                id = companyId,
                page = page,
                limit = limit,
                agentPage = agentPage,
                agentLimit = agentLimit,
                listingType = listingType,
                sortBy = sortBy
            )
        }
    }

    suspend fun searchAgents(
        token: String? = null,
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<AgentSearchResponse>> {
        val bearer = token?.takeIf { it.isNotBlank() }?.let { "Bearer $it" }
        return safeApiCall { apiService.searchAgents(bearer, request) }
    }

    suspend fun getAgentProfile(
        agentId: String,
        page: Int,
        limit: Int,
        listingType: String? = null,
        sortBy: String? = null
    ): ApiResult<ApiEnvelope<AgentProfileResponse>> {
        return safeApiCall {
            apiService.getAgentProfile(
                id = agentId,
                page = page,
                limit = limit,
                listingType = listingType,
                sortBy = sortBy
            )
        }
    }

    suspend fun searchDevelopers(
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<DeveloperSearchResponse>> {
        return safeApiCall { apiService.searchDevelopers(request) }
    }

    suspend fun getDeveloperProfile(
        developerId: String
    ): ApiResult<ApiEnvelope<DeveloperProfileResponse>> {
        return safeApiCall { apiService.getDeveloperProfile(developerId) }
    }

    /* Reports */

    suspend fun uploadReportAttachments(
        token: String,
        attachments: List<MultipartBody.Part>
    ): ApiResult<ApiEnvelope<ReportUploadAttachmentsResponse>> {
        return safeApiCall { apiService.uploadReportAttachments(token, attachments) }
    }

    suspend fun createReport(
        token: String?,
        request: ReportCreateRequest
    ): ApiResult<ApiEnvelope<ReportCreateResponse>> {
        return safeApiCall { apiService.createReport(token = token, request = request) }
    }

    suspend fun getHelpFaqs(category: String): ApiResult<ApiEnvelope<HelpFaqsResponse>> {
        return safeApiCall { apiService.getHelpFaqs(category) }
    }

    suspend fun calculateMortgage(
        request: MortgageCalculateRequest
    ): ApiResult<ApiEnvelope<MortgageCalculateData>> {
        return safeApiCall { apiService.calculateMortgage(request) }
    }

    suspend fun calculateUpfrontCosts(
        request: MortgageUpfrontCostsRequest
    ): ApiResult<ApiEnvelope<MortgageUpfrontCostsData>> {
        return safeApiCall { apiService.calculateUpfrontCosts(request) }
    }

    suspend fun getMortgageQuote(
        request: MortgageQuoteRequest
    ): ApiResult<ApiEnvelope<MortgageQuoteData>> {
        return safeApiCall { apiService.getMortgageQuote(request) }
    }

    suspend fun calculateRentVsBuy(
        request: RentVsBuyCalculateRequest
    ): ApiResult<ApiEnvelope<RentVsBuyCalculateData>> {
        return safeApiCall { apiService.calculateRentVsBuy(request) }
    }

    suspend fun calculateRentVsBuyPaymentBreakdown(
        request: RentVsBuyCalculateRequest
    ): ApiResult<ApiEnvelope<RentVsBuyPaymentBreakdownResponseData>> {
        return safeApiCall { apiService.calculateRentVsBuyPaymentBreakdown(request) }
    }

    suspend fun registerPushNotification(
        token: String?,
        request: PushNotificationRegisterRequest
    ): ApiResult<ApiEnvelope<PushNotificationRegisterResponse>> {
        return safeApiCall { apiService.registerPushNotification(token = token, request = request) }
    }

    suspend fun getUserNotifications(
        token: String,
        tab: String,
        search: String?,
        page: Int,
        limit: Int
    ): ApiResult<ApiEnvelope<NotificationsResponse>> {
        return safeApiCall { apiService.getUserNotifications("Bearer $token", tab, search, page, limit) }
    }

    suspend fun deleteUserNotifications(
        token: String,
        ids: List<String>
    ): ApiResult<ApiEnvelope<DeleteNotificationsResponse>> {
        val request = mapOf("ids" to ids)
        return safeApiCall { apiService.deleteUserNotifications("Bearer $token", request) }
    }

    suspend fun markNotificationRead(
        token: String,
        id: String
    ): ApiResult<ApiEnvelope<MarkNotificationReadResponse>> {
        return safeApiCall { apiService.markNotificationRead("Bearer $token", id) }
    }

    suspend fun getHistoricalTransactions(
        request: Map<String, Any>
    ): ApiResult<ApiEnvelope<HistoricalTransactionsResponse>> {
        return safeApiCall { apiService.getHistoricalTransactions(request) }
    }

}