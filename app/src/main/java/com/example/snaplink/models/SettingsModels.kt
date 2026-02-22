package com.example.snaplink.models

/**
 * Data models for the /users/settings API response
 */
data class SettingsResponse(
    val success: Boolean,
    val settings: UserSettings,
    val message: String
)

data class UserSettings(
    val profile: SettingsProfile,
    val privacy: SettingsPrivacy,
    val account: SettingsAccount,
    val counts: SettingsCounts,
    val blockedUsers: List<SettingsUser>,
    val closeFriends: List<SettingsUser>
)

data class SettingsProfile(
    val name: String?,
    val username: String?,
    val email: String?,
    val phone: String?,
    val profileImg: String?,
    val bio: String?,
    val gender: String?,
    val dateOfBirth: String?
)

data class SettingsPrivacy(
    val profileVisibility: String?
)

data class SettingsAccount(
    val isVerified: Boolean?,
    val isDeactivated: Boolean?,
    val deactivationMessage: String?,
    val isDeletionScheduled: Boolean?,
    val deletionScheduledAt: String?
)

data class SettingsCounts(
    val posts: Int?,
    val followers: Int?,
    val following: Int?,
    val closeFriends: Int?,
    val pendingFollowRequests: Int?,
    val savedPosts: Int?,
    val notifications: Int?
)

data class SettingsUser(
    val _id: String,
    val name: String?,
    val username: String?,
    val profileImg: String?
)

// Request/Response models for settings update operations
data class UpdateEmailRequest(val email: String)
data class UpdatePhoneRequest(val phone: String)
data class ChangeUsernameRequest(val newUsername: String)
data class UpdateProfileVisibilityRequest(val profileVisibility: String)
data class UpdateDobRequest(val newDateOfBirth: String)

data class SettingsUpdateResponse(
    val success: Boolean,
    val message: String,
    val user: UpdatedUserDob? = null
)

data class UpdatedUserDob(
    val dateOfBirth: String?
)
