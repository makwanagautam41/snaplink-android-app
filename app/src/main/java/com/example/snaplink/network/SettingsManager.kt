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
}
