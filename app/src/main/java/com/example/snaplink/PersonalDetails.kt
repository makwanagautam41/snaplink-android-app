package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PersonalDetails : AppCompatActivity() {
    private lateinit var btnBack: ImageView
    private lateinit var contactInfoField: RelativeLayout
    private lateinit var usernameField: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_details)

        btnBack = findViewById(R.id.btnBack)
        contactInfoField = findViewById(R.id.contactInfoField)
        usernameField = findViewById(R.id.usernameField)

        btnBack.setOnClickListener {
            finish()
        }

        contactInfoField.setOnClickListener {
            startActivity(Intent(this, ContactInformation::class.java))
        }

        usernameField.setOnClickListener {
            startActivity(Intent(this, ChangeUsername::class.java))
        }
    }
}