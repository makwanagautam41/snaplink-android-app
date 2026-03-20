package com.example.snaplink.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages JWT token storage and retrieval using SharedPreferences
 */
object TokenManager {
    private const val PREF_NAME = "snaplink_auth"
    private const val KEY_TOKEN = "jwt_token"
    
    private var prefs: SharedPreferences? = null

    // keys
    private const val KEY_PROFILE_IMAGE = "profile_image"
    private const val KEY_USERNAME = "username"
    private const val KEY_USER_ID = "user_id"   // stored after login to detect own likes

    fun saveProfileImage(url: String) {
        prefs?.edit()?.putString(KEY_PROFILE_IMAGE, url)?.apply()
    }

    fun getProfileImage(): String? {
        return prefs?.getString(KEY_PROFILE_IMAGE, null)
    }

    fun saveUsername(username: String) {
        prefs?.edit()?.putString(KEY_USERNAME, username)?.apply()
    }

    fun getUsername(): String? {
        return prefs?.getString(KEY_USERNAME, null)
    }

    fun saveUserId(id: String) {
        prefs?.edit()?.putString(KEY_USER_ID, id)?.apply()
    }

    fun getUserId(): String? {
        return prefs?.getString(KEY_USER_ID, null)
    }

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Save JWT token to SharedPreferences
     */
    fun saveToken(token: String) {
        prefs?.edit()?.putString(KEY_TOKEN, token)?.apply()
    }
    
    /**
     * Get stored JWT token
     * @return JWT token or null if not found
     */
    fun getToken(): String? {
        return prefs?.getString(KEY_TOKEN, null)
    }
    
    /**
     * Clear stored token (logout)
     */
    fun clearToken() {
        prefs?.edit()
            ?.remove(KEY_TOKEN)
            ?.remove(KEY_PROFILE_IMAGE)
            ?.remove(KEY_USERNAME)
            ?.remove(KEY_USER_ID)
            ?.apply()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
