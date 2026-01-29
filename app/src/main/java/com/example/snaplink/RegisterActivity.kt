package com.example.snaplink

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.ApiResponse
import com.example.snaplink.network.RegisterRequest
import com.example.snaplink.network.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        // Initialize TokenManager
        TokenManager.init(applicationContext)

        findViewById<android.widget.TextView>(R.id.tvLogin).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.registerBtn).setOnClickListener {
            val name = findViewById<EditText>(R.id.name).text.toString().trim()
            val email = findViewById<EditText>(R.id.email).text.toString().trim()
            val password = findViewById<EditText>(R.id.password).text.toString()
            val username = findViewById<EditText>(R.id.username).text.toString().trim()
            val phone = findViewById<EditText>(R.id.phone).text.toString().trim()
            val gender = findViewById<EditText>(R.id.gender).text.toString().trim()
            
            // Basic validation
            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || 
                username.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.api.register(
                RegisterRequest(name, email, password, username, phone, gender)
            ).enqueue(object : Callback<ApiResponse> {

                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val token = apiResponse?.token
                        
                        if (token != null) {
                            // Store JWT token
                            TokenManager.saveToken(token)
                        }
                        
                        Toast.makeText(this@RegisterActivity, "Account created successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null && errorBody.contains("message")) {
                                val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
                                val msg = match?.groupValues?.get(1) ?: "Registration Failed"
                                Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@RegisterActivity, "Registration Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@RegisterActivity, "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
