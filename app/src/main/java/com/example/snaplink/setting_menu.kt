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
    private lateinit var passwordAndSecurity: LinearLayout

    private lateinit var accountVerificationLayout: LinearLayout

    private lateinit var saved: LinearLayout
    private lateinit var notifactions: LinearLayout

    private lateinit var accountPrivacy: LinearLayout
    private lateinit var closeFriends: LinearLayout
    private lateinit var blocked: LinearLayout

    private lateinit var help: LinearLayout
    private lateinit var about: LinearLayout

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
        passwordAndSecurity = findViewById(R.id.passwordAndSecurity)
        accountVerificationLayout = findViewById(R.id.accountVerificationLayout)

        saved = findViewById(R.id.saved)
        notifactions = findViewById(R.id.notifactions)

        accountPrivacy = findViewById(R.id.accountPrivacy)
        closeFriends = findViewById(R.id.closeFriends)
        blocked = findViewById(R.id.blocked)

        help = findViewById(R.id.help)
        about = findViewById(R.id.about)
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

        passwordAndSecurity.setOnClickListener {
            val intent = Intent(this, PasswordAndSecurity::class.java)
            startActivity(intent)
        }
        accountVerificationLayout.setOnClickListener {
            startActivity(Intent(this, AccountVerification::class.java))
        }

        saved.setOnClickListener {
            startActivity(Intent(this, Saved::class.java))
        }

        notifactions.setOnClickListener {
            startActivity(Intent(this, notifications::class.java))
        }

        accountPrivacy.setOnClickListener {
            startActivity(Intent(this, AccountPrivacy::class.java))
        }

        closeFriends.setOnClickListener {
            startActivity(Intent(this, CloseFriends::class.java))
        }

        blocked.setOnClickListener {
            startActivity(Intent(this, Blocked::class.java))
        }

        help.setOnClickListener {
            startActivity(Intent(this, Help::class.java))
        }

        about.setOnClickListener {
            startActivity(Intent(this, About::class.java))
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