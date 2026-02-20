package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ContactInformation : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var btnChangeEmail: RelativeLayout
    private lateinit var btnChangeMobile: RelativeLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        try {
            setContentView(R.layout.activity_contact_information)
            initViews()
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading contact information", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnChangeEmail = findViewById(R.id.emailField)
        btnChangeMobile = findViewById(R.id.mobileField)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }
        
        btnChangeEmail.setOnClickListener {
            val intent = Intent(this, ChangeEmail::class.java)
            startActivity(intent)
        }

        btnChangeMobile.setOnClickListener {
            val intent = Intent(this, ChangeMobile::class.java)
            startActivity(intent)
        }
    }
}