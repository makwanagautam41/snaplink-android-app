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


    //for profile image in bottom navigation
    private const val KEY_PROFILE_IMAGE = "profile_image"
    fun saveProfileImage(url: String) {
        prefs?.edit()?.putString(KEY_PROFILE_IMAGE, url)?.apply()
    }

    fun getProfileImage(): String? {
        return prefs?.getString(KEY_PROFILE_IMAGE, null)
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
        prefs?.edit()?.remove(KEY_TOKEN)?.remove(KEY_PROFILE_IMAGE)?.apply()
    }
    
    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
