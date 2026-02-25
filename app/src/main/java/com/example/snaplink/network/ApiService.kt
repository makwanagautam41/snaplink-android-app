package com.example.snaplink.network

import com.example.snaplink.models.ChangeUsernameRequest
import com.example.snaplink.models.ChangePasswordRequest
import com.example.snaplink.models.ChangePasswordResponse
import com.example.snaplink.models.CreatePostResponse
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.models.MyPostResponse
import com.example.snaplink.models.NotificationResponse
import com.example.snaplink.models.SettingsResponse
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.models.UpdateDobRequest
import com.example.snaplink.models.UpdateEmailRequest
import com.example.snaplink.models.UpdatePhoneRequest
import com.example.snaplink.models.UpdateProfileVisibilityRequest
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
    val savedPosts: List<String>?,
    val dateOfBirth: String?,
    val postCount: Int?,
    val isFollowing: Boolean? = false,
    val isRequested: Boolean? = false,
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

// Forgot Password flow
data class SendOtpRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val otp: String, val newPassword: String)
data class SimpleApiResponse(val success: Boolean, val message: String)

// Account Deactivation/Deletion
data class DeactivateAccountRequest(val password: String, val reason: String? = null)
data class DeleteAccountRequest(val password: String, val reason: String? = null)

// Reactivation/Cancel Deletion
data class ReactivateRequest(val username: String, val email: String, val password: String)
data class VerifyReactivateOtpRequest(val email: String, val otp: String)
data class CancelDeletionRequest(val username: String, val email: String, val password: String, val confirmCancel: Boolean)

// Verify User
data class VerifyUserRequest(val otp: String)


// Toggle Close Friend
data class ToggleCloseFriendResponse(val message: String)


// Story
data class StoryResponse(val success: Boolean, val message: String, val story: StoryData? = null)
data class StoryData(val _id: String, val media: String, val caption: String?)

interface ApiService {

    @POST("users/signin")
    fun login(@Body body: LoginRequest): Call<ApiResponse>

    @POST("users/signup")
    fun register(@Body body: RegisterRequest): Call<ApiResponse>

    @POST("users/send-password-rest-otp")
    fun sendPasswordResetOtp(@Body body: SendOtpRequest): Call<SimpleApiResponse>

    @POST("users/verify-password-reset-otp")
    fun verifyPasswordResetOtp(@Body body: VerifyOtpRequest): Call<SimpleApiResponse>

    @POST("users/reset-password")
    fun resetPassword(@Body body: ResetPasswordRequest): Call<SimpleApiResponse>

    @retrofit2.http.GET("users/profile")
    fun getUserDetails(): Call<UserDetailsResponse>

    @retrofit2.http.GET("users/profile/{query}")
    fun getOtherUserProfile(@retrofit2.http.Path("query") query: String): Call<OtherUserResponse>

    @retrofit2.http.GET("users/past-searched-user")
    fun getPastSearchedUsers(): Call<RecentSearchResponse>

    @retrofit2.http.PUT("users/update")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<UpdateProfileResponse>

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

    // Settings endpoints
    @retrofit2.http.GET("users/settings")
    fun getUserSettings(): Call<SettingsResponse>

    @retrofit2.http.PUT("users/update-email")
    fun updateEmail(@Body body: UpdateEmailRequest): Call<SettingsUpdateResponse>

    @retrofit2.http.PUT("users/update-phone")
    fun updatePhone(@Body body: UpdatePhoneRequest): Call<SettingsUpdateResponse>

    @retrofit2.http.PUT("users/change-username")
    fun changeUsername(@Body body: ChangeUsernameRequest): Call<SettingsUpdateResponse>

    @retrofit2.http.POST("users/update-profile-visibility")
    fun updateProfileVisibility(@Body body: UpdateProfileVisibilityRequest): Call<SettingsUpdateResponse>

    @retrofit2.http.PUT("users/change-date-of-birth")
    fun updateDob(@Body body: UpdateDobRequest): Call<SettingsUpdateResponse>

    @retrofit2.http.PUT("users/update-password")
    fun updatePassword(@Body body: ChangePasswordRequest): Call<ChangePasswordResponse>

    @POST("users/deactivate-account")
    fun deactivateAccount(@Body body: DeactivateAccountRequest): Call<SimpleApiResponse>

    @POST("users/delete-account")
    fun deleteAccount(@Body body: DeleteAccountRequest): Call<SimpleApiResponse>

    @POST("users/send-reactivate-account-otp")
    fun sendReactivateAccountOtp(@Body body: ReactivateRequest): Call<SimpleApiResponse>

    @POST("users/verify-otp-and-reactivate-account")
    fun verifyReactivateAccountOtp(@Body body: VerifyReactivateOtpRequest): Call<SimpleApiResponse>

    @POST("users/cancel-account-deletion")
    fun cancelAccountDeletion(@Body body: CancelDeletionRequest): Call<SimpleApiResponse>

    @POST("users/send-verify-user-otp")
    fun sendVerifyUserOtp(): Call<SimpleApiResponse>

    @POST("users/verify-user")
    fun verifyUser(@Body body: VerifyUserRequest): Call<SimpleApiResponse>

    @retrofit2.http.PUT("users/add-close-friend/{username}")
    fun toggleCloseFriend(@retrofit2.http.Path("username") username: String): Call<ToggleCloseFriendResponse>

    @Multipart
    @POST("story/upload")
    fun uploadStory(
        @Part media: MultipartBody.Part,
        @Part("caption") caption: RequestBody?
    ): Call<StoryResponse>
}

