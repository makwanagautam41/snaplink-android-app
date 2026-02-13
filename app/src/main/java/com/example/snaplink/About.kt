package com.example.snaplink

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class About : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        try {
            setContentView(R.layout.activity_about)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading about layout", Toast.LENGTH_LONG).show()
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
        btnBack = findViewById(R.id.btnBack)
    }
    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
    }
}