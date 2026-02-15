package com.example.snaplink.network

data class UpdateProfileResponse(
    val success: Boolean,
    val message: String,
    val user: UpdatedUserData
)

data class UpdatedUserData(
    val bio: String?,
    val gender: String?
)

data class UpdateProfileRequest(
    val bio: String,
    val gender: String
)
