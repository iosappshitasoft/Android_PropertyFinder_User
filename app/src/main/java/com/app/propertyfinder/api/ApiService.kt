package com.app.propertyfinder.api

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
import com.app.propertyfinder.api.dto.response.SentOtpResponse
import com.app.propertyfinder.api.dto.response.SavedPropertiesResponse
import com.app.propertyfinder.api.dto.response.VerifyOtpResponse
import com.app.propertyfinder.api.dto.response.AlertsResponse
import com.app.propertyfinder.api.dto.response.AlertItem
import com.app.propertyfinder.api.dto.response.ContactedPropertiesResponse
import com.app.propertyfinder.api.dto.response.DeveloperSearchResponse
import com.app.propertyfinder.api.dto.response.DeveloperProfileResponse
import com.app.propertyfinder.api.dto.response.InquiryData
import com.app.propertyfinder.api.dto.response.AgencyProfileResponse
import com.app.propertyfinder.api.dto.response.AgencySearchResponse
import com.app.propertyfinder.api.dto.response.AgentProfileResponse
import com.app.propertyfinder.api.dto.response.AgentSearchResponse
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
import com.app.propertyfinder.api.dto.response.HelpFaqsResponse
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
import com.app.propertyfinder.api.network.ApiEnvelope
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.PUT

interface ApiService {

    @POST("auth/users/register")
    suspend fun userRegister(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<RegisterData>>

    @POST("auth/users/login")
    suspend fun userLogin(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<LoginData>>

    @POST("auth/users/social-login")
    suspend fun googleLogin(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<LoginData>>

    @POST("auth/users/send-otp")
    suspend fun sendOtp(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<SentOtpResponse>>

    @POST("auth/users/verify-otp")
    suspend fun verifyOtp(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<VerifyOtpResponse>>

    @POST("auth/users/reset-password")
    suspend fun resetPassword(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<VerifyOtpResponse>>

    @GET("users/profile")
    suspend fun userProfile(@Header("Authorization") token: String): Response<ApiEnvelope<ProfileData>>

    @PUT("users/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): Response<ApiEnvelope<UpdateProfileResponse>>

    @DELETE("users/account")
    suspend fun deleteUserAccount(
        @Header("Authorization") token: String
    ): Response<ApiEnvelope<Nothing>>

    @Multipart
    @POST("users/upload-profile-picture")
    suspend fun uploadProfilePicture(
        @Header("Authorization") token: String,
        @Part profilePicture: MultipartBody.Part,
        @Part("removeProfilePicture") removeProfilePicture: RequestBody
    ): Response<ApiEnvelope<UploadProfilePictureResponse>>

    @POST("auth/users/refresh")
    suspend fun refreshToken(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<RefreshTokenData>>

    @POST("auth/users/logout")
    suspend fun logout(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<Nothing>>


    /*Saved Properties Api's*/
    @POST("users/saved-properties/add")
    suspend fun addToSaved(@Header("Authorization") token: String,
                           @Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<Nothing>>

    @GET("users/saved-properties")
    suspend fun getSavedProperties(@Header("Authorization") token: String,
                                   @QueryMap request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<SavedPropertiesResponse>>

    @DELETE("users/saved-properties/remove/{propertyId}")
    suspend fun removeFromSaved(
        @Header("Authorization") token: String,
        @Path("propertyId") propertyId: String
    ): Response<ApiEnvelope<Nothing>>


    @GET("master-data")
    suspend fun getMasterData(@Query("types") types: String): Response<ApiEnvelope<MasterDataResponse>>

    @POST("properties/search")
    suspend fun getProperty(
        @Header("Authorization") token: String? = null,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<PropertyResponse>>

    @POST("projects/search")
    suspend fun getProjects(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiEnvelope<ProjectSearchResponse>>

    @GET("projects/{id}")
    suspend fun getProjectDetails(@Path("id") id: String): Response<ApiEnvelope<ProjectDetailResponse>>

    @GET("properties/{id}")
    suspend fun getPropertyDetails(@Header("Authorization") token: String,@Path("id") id: String):
            Response<ApiEnvelope<PropertyDetailResponse>>

    @GET("properties/price-insights/{propertyId}")
    suspend fun getPriceInsights(
        @Path("propertyId") propertyId: String
    ): Response<ApiEnvelope<PriceInsightsResponse>>

    /* Alerts Api's */

    @GET("users/alerts")
    suspend fun getUserAlerts(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiEnvelope<AlertsResponse>>

    @POST("users/alerts/create")
    suspend fun createUserAlert(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<AlertItem>>

    @DELETE("users/alerts/delete-all")
    suspend fun deleteAllUserAlerts(
        @Header("Authorization") token: String
    ): Response<ApiEnvelope<Nothing>>

    @DELETE("users/alerts/{id}")
    suspend fun deleteUserAlert(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<Nothing>>

    @PUT("users/alerts/{id}")
    suspend fun updateUserAlert(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<AlertItem>>

    /* Notifications */

    @GET("users/notifications")
    suspend fun getUserNotifications(
        @Header("Authorization") token: String,
        @Query("tab") tab: String,
        @Query("search") search: String? = null,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiEnvelope<NotificationsResponse>>

    @HTTP(method = "DELETE", path = "users/notifications", hasBody = true)
    suspend fun deleteUserNotifications(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<DeleteNotificationsResponse>>

    @PUT("users/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<ApiEnvelope<MarkNotificationReadResponse>>

    /* Contacted properties */

    @GET("users/contacted-properties")
    suspend fun getContactedProperties(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): Response<ApiEnvelope<ContactedPropertiesResponse>>

   /* @DELETE("users/contacted-properties")
    suspend fun deleteContactedProperties(
        @Header("Authorization") token: String,
        @QueryMap request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<Nothing>>*/

    @HTTP(method = "DELETE", path = "users/contacted-properties", hasBody = true)
    suspend fun deleteContactedProperties(
        @Header("Authorization") token: String,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<Nothing>>

    @DELETE("users/contacted-properties/delete-all")   // Unused
    suspend fun deleteAllContactedProperties(
        @Header("Authorization") token: String
    ): Response<ApiEnvelope<Nothing>>

    /* Inquiries */

    @POST("inquiries/create")
    suspend fun createInquiry(
        @Header("Authorization") token: String? = null,
        @Body request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<InquiryData>>

    /* Agencies */
    @GET("agency/search")
    suspend fun searchAgencies(
        @QueryMap request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<AgencySearchResponse>>

    @GET("agency/{id}")
    suspend fun getAgencyProfile(
        @Path("id") id: String,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("agentPage") agentPage: Int? = null,
        @Query("agentLimit") agentLimit: Int? = null,
        @Query("listingType") listingType: String? = null,
        @Query("sortBy") sortBy: String? = null
    ): Response<ApiEnvelope<AgencyProfileResponse>>

    @GET("agents/search")
    suspend fun searchAgents(
        @Header("Authorization") token: String? = null,
        @QueryMap request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<AgentSearchResponse>>

    @GET("agents/{id}")
    suspend fun getAgentProfile(
        @Path("id") id: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("listingType") listingType: String? = null,
        @Query("sortBy") sortBy: String? = null
    ): Response<ApiEnvelope<AgentProfileResponse>>

    @GET("developers/search")
    suspend fun searchDevelopers(
        @QueryMap request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<DeveloperSearchResponse>>

    @GET("developers/{id}")
    suspend fun getDeveloperProfile(
        @Path("id") id: String
    ): Response<ApiEnvelope<DeveloperProfileResponse>>

    /* Mortgage Calculator */

    @POST("mortgages/calculate")
    suspend fun calculateMortgage(
        @Body request: MortgageCalculateRequest
    ): Response<ApiEnvelope<MortgageCalculateData>>

    @POST("mortgages/upfront-costs")
    suspend fun calculateUpfrontCosts(
        @Body request: MortgageUpfrontCostsRequest
    ): Response<ApiEnvelope<MortgageUpfrontCostsData>>

    @POST("mortgages/get-quote")
    suspend fun getMortgageQuote(
        @Body request: MortgageQuoteRequest
    ): Response<ApiEnvelope<MortgageQuoteData>>

    @POST("rent-vs-buy/calculate")
    suspend fun calculateRentVsBuy(
        @Body request: RentVsBuyCalculateRequest
    ): Response<ApiEnvelope<RentVsBuyCalculateData>>

    @POST("rent-vs-buy/payment-breakdown")
    suspend fun calculateRentVsBuyPaymentBreakdown(
        @Body request: RentVsBuyCalculateRequest
    ): Response<ApiEnvelope<RentVsBuyPaymentBreakdownResponseData>>

    /* Push notification register */
    @POST("users/notifications/push/register")
    suspend fun registerPushNotification(
        @Header("Authorization") token: String?,
        @Body request: PushNotificationRegisterRequest
    ): Response<ApiEnvelope<PushNotificationRegisterResponse>>

    /* Report attachments */

    @Multipart
    @POST("reports/upload-attachments")
    suspend fun uploadReportAttachments(
        @Header("Authorization") token: String,
        @Part attachments: List<MultipartBody.Part>,
    ): Response<ApiEnvelope<ReportUploadAttachmentsResponse>>

    /* Reports create */

    @POST("reports/create")
    suspend fun createReport(
        @Header("Authorization") token: String? = null,
        @Body request: ReportCreateRequest
    ): Response<ApiEnvelope<ReportCreateResponse>>

    /* Help / FAQ */

    @GET("help/faqs")
    suspend fun getHelpFaqs(
        @Query("category") category: String
    ): Response<ApiEnvelope<HelpFaqsResponse>>

    @GET("transactions/historical")
    suspend fun getHistoricalTransactions(
        @QueryMap request: Map<String, @JvmSuppressWildcards Any>
    ): Response<ApiEnvelope<HistoricalTransactionsResponse>>

}