package com.example.snaplink.network

import com.example.snaplink.models.UserSettings

/**
 * Singleton manager that caches the user settings data fetched from /users/settings.
 * All settings-related fragments should use this to read cached data instead of
 * making separate API calls.
 */
object SettingsManager {

    private var cachedSettings: UserSettings? = null

    /**
     * Store the fetched settings data
     */
    fun setSettings(settings: UserSettings) {
        cachedSettings = settings
    }

    /**
     * Get cached settings. Returns null if not yet fetched.
     */
    fun getSettings(): UserSettings? = cachedSettings

    /**
     * Clear cached settings (e.g., on logout)
     */
    fun clearSettings() {
        cachedSettings = null
    }

    // Convenience getters for common fields
    fun getEmail(): String? = cachedSettings?.profile?.email
    fun getPhone(): String? = cachedSettings?.profile?.phone
    fun getUsername(): String? = cachedSettings?.profile?.username
    fun getName(): String? = cachedSettings?.profile?.name
    fun getBio(): String? = cachedSettings?.profile?.bio
    fun getGender(): String? = cachedSettings?.profile?.gender
    fun getProfileImg(): String? = cachedSettings?.profile?.profileImg
    fun getDateOfBirth(): String? = cachedSettings?.profile?.dateOfBirth
    fun getProfileVisibility(): String? = cachedSettings?.privacy?.profileVisibility
    fun isVerified(): Boolean = cachedSettings?.account?.isVerified ?: false

    /**
     * Update cached email after a successful API call
     */
    fun updateCachedEmail(newEmail: String) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                profile = it.profile.copy(email = newEmail)
            )
        }
    }

    /**
     * Update cached phone after a successful API call
     */
    fun updateCachedPhone(newPhone: String) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                profile = it.profile.copy(phone = newPhone)
            )
        }
    }

    /**
     * Update cached username after a successful API call
     */
    fun updateCachedUsername(newUsername: String) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                profile = it.profile.copy(username = newUsername)
            )
        }
    }

    /**
     * Update cached profile visibility after a successful API call
     */
    fun updateCachedProfileVisibility(visibility: String) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                privacy = it.privacy.copy(profileVisibility = visibility)
            )
        }
    }

    /**
     * Update cached date of birth after a successful API call
     */
    fun updateCachedDateOfBirth(newDob: String) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                profile = it.profile.copy(dateOfBirth = newDob)
            )
        }
    }

    /**
     * Update cached verification status after a successful API call
     */
    fun updateCachedVerifiedStatus(isVerified: Boolean) {
        cachedSettings?.let {
            cachedSettings = it.copy(
                account = it.account.copy(isVerified = isVerified)
            )
        }
    }

    /**
     * Update cached close friends after a successful toggle
     */
    fun toggleCloseFriend(userId: String) {
        cachedSettings?.let { settings ->
            val currentData = settings.closeFriends
            val isCurrentlyAdded = currentData.closeFriendsAdded.any { it._id == userId }
            
            val newAddedList = currentData.closeFriendsAdded.toMutableList()
            val newNotAddedList = currentData.closeFriendsNotAdded.toMutableList()
            
            if (isCurrentlyAdded) {
                // Remove from added, move to not added
                val user = newAddedList.find { it._id == userId }
                if (user != null) {
                    newAddedList.remove(user)
                    newNotAddedList.add(0, user)
                }
            } else {
                // Remove from not added, move to added
                val user = newNotAddedList.find { it._id == userId }
                if (user != null) {
                    newNotAddedList.remove(user)
                    newAddedList.add(user)
                }
            }
            
            cachedSettings = settings.copy(
                closeFriends = currentData.copy(
                    closeFriendsAdded = newAddedList,
                    closeFriendsNotAdded = newNotAddedList
                )
            )
        }
    }
}
