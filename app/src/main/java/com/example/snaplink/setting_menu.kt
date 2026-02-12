package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.example.snaplink.network.ApiClient

class setting_menu : AppCompatActivity() {

    private lateinit var btnBackFromSetting: ImageView
    private lateinit var layoutLogout: LinearLayout
    private lateinit var personalDetailsLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContentView(R.layout.activity_setting_menu)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading settings layout", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            initViews()
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        btnBackFromSetting = findViewById(R.id.btnBackFromSetting)
        layoutLogout = findViewById(R.id.layoutLogout)
        personalDetailsLayout = findViewById(R.id.personalDetailsLayout)
    }

    private fun setupListeners() {
        btnBackFromSetting.setOnClickListener {
            finish()
        }

        layoutLogout.setOnClickListener {
            performLogout()
        }

        personalDetailsLayout.setOnClickListener {
            val intent = Intent(this, personal_details::class.java)
            startActivity(intent)
        }
    }

    private fun performLogout() {
        try {
            ApiClient.clearAuth()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show()
        }
    }
}