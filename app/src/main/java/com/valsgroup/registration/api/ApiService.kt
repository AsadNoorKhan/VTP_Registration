package com.valsgroup.registration.api

import com.valsgroup.registration.RegistrationPayload
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.Path

// API Interface for Retrofit
interface ApiService {
    
    // Route 1: IMEI Registration
    @POST(com.valsgroup.registration.config.Config.endpointUserRegister)
    suspend fun registerImei(
        @Header("Authorization") authorization: String,
        @Query("imei_id") imeiId: Long,
        @Query("user_id") userId: String,
        @Query("user_status") userStatus: String,
        @Query("tagging") tagging: String
    ): Response<ApiResponse>
    
    // Route 2: User Status
    @GET(com.valsgroup.registration.config.Config.endpointUserStatus + "/{userId}")
    suspend fun getUserStatus(
        @Header("Authorization") authorization: String,
        @Path("userId") userId: String
    ): Response<UserStatusResponse>
}

// Base URL configuration - now using Config object
object ApiConfig {
    val BASE_URL: String get() = com.valsgroup.registration.config.Config.apiBaseUrl
    val BEARER_TOKEN: String get() = "Bearer vtpliveviewvwep" // Bearer token with proper format
}

    // Response data classes
    data class ApiResponse(
        val status: String,
        val imei_id: Long? = null,
        val operation: String? = null,
        val affected_rows: Int? = null,
        val message: String? = null
    )

    data class UserStatusResponse(
    val status: String,
    val records: List<UserRecord>
)

data class UserRecord(
    val user_id: String,
    val imei_id: Long,
    val user_status: String,
    val tagging: String?
)

// API Repository for handling API calls
class ApiRepository(private val apiService: ApiService) {
    
    suspend fun registerImei(payload: RegistrationPayload): Result<Pair<Int, ApiResponse>> {
        return try {
            val response = apiService.registerImei(ApiConfig.BEARER_TOKEN, payload.imei_id, payload.user_id, payload.user_status, payload.tagging)
            if (response.isSuccessful) {
                Result.success(Pair(response.code(), response.body() ?: ApiResponse("error", message = "Empty response")))
            } else {
                Result.failure(Exception("Registration failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserStatus(userId: String): Result<Pair<Int, UserStatusResponse>> {
        return try {
            val response = apiService.getUserStatus(ApiConfig.BEARER_TOKEN, userId)
            if (response.isSuccessful) {
                Result.success(Pair(response.code(), response.body() ?: UserStatusResponse("error", emptyList())))
            } else {
                Result.failure(Exception("Failed to get user status: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
} 