package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class setting_menu : AppCompatActivity() {

    private lateinit var btnBackFromSetting: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContentView(R.layout.activity_setting_menu)
        } catch (e: Exception) {
            e.printStackTrace()
            // If layout inflation fails, we can't do much, but at least we log it.
            Toast.makeText(this, "Error loading profile layout", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            initViews()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
    }
    private fun initViews() {
        btnBackFromSetting = findViewById(R.id.btnBackFromSetting)

        btnBackFromSetting.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}