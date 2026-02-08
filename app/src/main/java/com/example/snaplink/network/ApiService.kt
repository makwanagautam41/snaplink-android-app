package com.example.snaplink.network

import com.example.snaplink.models.CreatePostResponse
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.models.MyPostResponse
import com.example.snaplink.models.NotificationResponse
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

// Simple user object for followers/following lists
data class FollowerUser(
    val _id: String,
    val name: String,
    val username: String,
    val profileImg: String?
)

data class User(
    val _id: String,
    val name: String,
    val username: String,
    val email: String?,
    val gender: String?,
    val phone: String?,
    val profileImg: String?,
    val bio: String?,
    val followers: List<FollowerUser>?,
    val following: List<FollowerUser>?,
    val followRequests: List<FollowerUser>?,
    val postCount: Int?,
    val isPrivate: Boolean? = false,
    val isFollowing: Boolean? = false,
    val profileVisibility: String? = "public",
    val isVerified: Boolean? = false
)

data class UserDetailsResponse(
    val success: Boolean,
    val user: User,
    val message: String
)

data class OtherUserResponse(
    val success: Boolean,
    val users: List<User>,
    val message: String
)

data class ImageUpdateResponse(
    val success: Boolean,
    val message: String,
    val imageUrl: String?,
    val DEFAULT_IMG_URL: String?
)

data class RecentSearchResponse(
    val success: Boolean,
    val recentSearches: List<User>?,
    val message: String?
)

interface ApiService {

    @POST("users/signin")
    fun login(@Body body: LoginRequest): Call<ApiResponse>

    @POST("users/signup")
    fun register(@Body body: RegisterRequest): Call<ApiResponse>

    @retrofit2.http.GET("users/profile")
    fun getUserDetails(): Call<UserDetailsResponse>

    @retrofit2.http.GET("users/profile/{query}")
    fun getOtherUserProfile(@retrofit2.http.Path("query") query: String): Call<OtherUserResponse>

    @retrofit2.http.GET("users/past-searched-user")
    fun getPastSearchedUsers(): Call<RecentSearchResponse>

    @retrofit2.http.PUT("users/update")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<UserDetailsResponse>

    @retrofit2.http.GET("posts/feed")
    fun getFeedPosts(): Call<FeedResponse>

    @retrofit2.http.GET("posts/searched-user/{username}")
    fun getUserPosts(@retrofit2.http.Path("username") username: String): Call<MyPostResponse>

    @retrofit2.http.GET("posts/my-posts")
    fun getMyPosts(): Call<MyPostResponse>

    @Multipart
    @POST("posts/create")
    fun createPost(
        @Part images: List<MultipartBody.Part>,
        @Part("caption") caption: RequestBody
    ): Call<CreatePostResponse>

    @Multipart
    @retrofit2.http.PUT("users/update-profile-img")
    fun updateProfileImage(
        @Part image: MultipartBody.Part
    ): Call<ImageUpdateResponse>

    @retrofit2.http.PUT("users/remove-profile-img")
    fun removeProfileImage(): Call<ImageUpdateResponse>

    @retrofit2.http.GET("users/notifications")
    fun getNotifications(): Call<NotificationResponse>

    @retrofit2.http.POST("users/follow/{username}")
    fun followUser(@retrofit2.http.Path("username") username: String): Call<ApiResponse>

    @retrofit2.http.POST("users/unfollow/{username}")
    fun unfollowUser(@retrofit2.http.Path("username") username: String): Call<ApiResponse>

    @retrofit2.http.POST("users/follow/accept/{username}")
    fun acceptFollowRequest(@retrofit2.http.Path("username") username: String): Call<ApiResponse>

    @retrofit2.http.POST("users/follow/reject/{username}")
    fun rejectFollowRequest(@retrofit2.http.Path("username") username: String): Call<ApiResponse>
}
