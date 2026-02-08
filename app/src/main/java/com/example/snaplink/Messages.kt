package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Messages : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages)
        val navHome = findViewById<ImageView>(R.id.navHome)
        val navSearch = findViewById<ImageView>(R.id.navSearch)
        val navAdd = findViewById<ImageView>(R.id.navAdd)
        val navMessage = findViewById<ImageView>(R.id.navMessage)
        val navProfile = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.navProfile)

        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivityKt::class.java))
            finish()
        }

        navSearch.setOnClickListener {
            startActivity(Intent(this, ExploreActivity::class.java))
        }

        navAdd.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        navProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}