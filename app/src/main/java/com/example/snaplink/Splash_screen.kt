package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.snaplink.network.TokenManager

class Splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        
        Handler(Looper.getMainLooper()).postDelayed({
            // Check if user is logged in
            val intent = if (TokenManager.isLoggedIn()) {
                // User is logged in, navigate to Home
                Intent(this, HomeActivityKt::class.java)
            } else {
                // User is not logged in, navigate to Login
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, 3000)
    }
}