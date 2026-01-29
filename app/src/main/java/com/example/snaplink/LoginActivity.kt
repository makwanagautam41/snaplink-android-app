package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.ApiResponse
import com.example.snaplink.network.LoginRequest
import com.example.snaplink.network.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize TokenManager
        TokenManager.init(applicationContext)
        
        // Check for existing valid token
        if (TokenManager.isLoggedIn()) {
            // Verify token is still valid by making a profile request
            ApiClient.api.getUserDetails().enqueue(object : Callback<com.example.snaplink.network.UserDetailsResponse> {
                override fun onResponse(call: Call<com.example.snaplink.network.UserDetailsResponse>, response: Response<com.example.snaplink.network.UserDetailsResponse>) {
                    if (response.isSuccessful) {
                        // Token is valid, go to home
                        startActivity(Intent(this@LoginActivity, HomeActivityKt::class.java))
                        finish()
                    } else if (response.code() == 401) {
                        // Token expired or invalid, clear it
                        TokenManager.clearToken()
                    }
                }
                override fun onFailure(call: Call<com.example.snaplink.network.UserDetailsResponse>, t: Throwable) {
                    // Network error, let user try to login
                }
            })
        }

        setContentView(R.layout.activity_login)

        val emailOrUsername = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val tvRegister = findViewById<android.widget.TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loginBtn.setOnClickListener {
            val identifier = emailOrUsername.text.toString().trim()
            val pass = password.text.toString()
            
            if (identifier.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email/username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            ApiClient.api.login(
                LoginRequest(identifier, pass)
            ).enqueue(object : Callback<ApiResponse> {

                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val token = apiResponse?.token
                        
                        if (token != null) {
                            // Store JWT token
                            TokenManager.saveToken(token)
                            
                            Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                            
                            val intent = Intent(this@LoginActivity, HomeActivityKt::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Login failed: No token received", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null && errorBody.contains("message")) {
                                val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
                                val msg = match?.groupValues?.get(1) ?: "Login Failed"
                                Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
