package com.example.snaplink.models

data class NotificationResponse(
    val success: Boolean,
    val notifications: List<Notification>,
    val followRequests: List<FollowRequest>
)

data class Notification(
    val _id: String,
    val from: NotificationUser,
    val message: String,
    val post: String?,
    val createdAt: String
)

data class FollowRequest(
    val _id: String,
    val username: String,
    val profileImg: String?
)

data class NotificationUser(
    val _id: String,
    val username: String,
    val profileImg: String?
)
