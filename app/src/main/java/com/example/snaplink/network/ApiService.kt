package com.example.snaplink.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val identifier: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val username: String, val phone: String, val gender: String)
data class ApiResponse(val message: String, val token: String?, val accessToken: String?)

data class User(
    val _id: String,
    val name: String,
    val username: String,
    val email: String,
    val gender: String?,
    val phone: String?,
    val profileImg: String?,
    val bio: String?,
    val followers: List<String>?,
    val following: List<String>?,
    val postCount: Int?
)

data class UserDetailsResponse(
    val success: Boolean,
    val user: User,
    val message: String
)

interface ApiService {

    @POST("users/signin")
    fun login(@Body body: LoginRequest): Call<ApiResponse>

    @POST("users/signup")
    fun register(@Body body: RegisterRequest): Call<ApiResponse>

    @retrofit2.http.GET("users/profile")
    fun getUserDetails(): Call<UserDetailsResponse>
}
