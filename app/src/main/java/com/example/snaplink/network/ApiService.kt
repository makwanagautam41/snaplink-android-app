package com.example.snaplink.network

import com.example.snaplink.models.CreatePostResponse
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.models.MyPostResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class LoginRequest(val identifier: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val username: String, val phone: String, val gender: String)
data class ApiResponse(val message: String, val token: String?, val accessToken: String?, val user: User?)

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

    @retrofit2.http.PUT("users/update")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<UserDetailsResponse>

    @retrofit2.http.GET("posts/feed")
    fun getFeedPosts(): Call<FeedResponse>

    @retrofit2.http.GET("posts/my-posts")
    fun getMyPosts(): Call<MyPostResponse>

    @Multipart
    @POST("posts/create")
    fun createPost(
        @Part images: List<MultipartBody.Part>,
        @Part("caption") caption: RequestBody
    ): Call<CreatePostResponse>
}
