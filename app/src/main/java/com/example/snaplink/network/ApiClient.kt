package com.example.snaplink.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
//    private const val BASE_URL = "http://10.0.2.2:5000/api/"
    private const val BASE_URL = "https://snaplink-android-app-backend.vercel.app/api/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Clear authentication token (logout)
     */
    fun clearAuth() {
        TokenManager.clearToken()
    }
}
