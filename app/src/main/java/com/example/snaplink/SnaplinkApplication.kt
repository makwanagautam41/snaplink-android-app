package com.example.snaplink

import android.app.Application
import android.content.Context
import com.example.snaplink.network.TokenManager

class SnaplinkApplication : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        
        // Initialize TokenManager
        TokenManager.init(applicationContext)
    }
}
