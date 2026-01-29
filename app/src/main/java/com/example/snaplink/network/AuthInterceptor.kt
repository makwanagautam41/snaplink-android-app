package com.example.snaplink.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * OkHttp Interceptor that automatically adds JWT token to protected API requests
 */
class AuthInterceptor : Interceptor {
    
    // Public endpoints that don't require authentication
    private val publicEndpoints = listOf(
        "/users/signin",
        "/users/signup"
    )
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath
        
        // Check if this is a public endpoint
        val isPublicEndpoint = publicEndpoints.any { path.contains(it) }
        
        // If public endpoint, proceed without token
        if (isPublicEndpoint) {
            return chain.proceed(originalRequest)
        }
        
        // For protected endpoints, add Authorization header
        val token = TokenManager.getToken()
        
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}
