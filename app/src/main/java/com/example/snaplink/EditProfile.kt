package com.example.snaplink

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snaplink.network.ApiClient
//import com.example.snaplink.network.UpdateProfileRequest
import com.example.snaplink.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {

    private lateinit var btnBackFromEditProfile: ImageView

    private lateinit var etName: EditText
    private lateinit var etBio: EditText
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnBackFromEditProfile = findViewById(R.id.btnBackFromEditProfile)
        etName = findViewById(R.id.etName)
        etBio = findViewById(R.id.etBio)
        btnUpdate = findViewById(R.id.btnUpdateProfile)

        btnBackFromEditProfile.setOnClickListener {
            finish()
        }

        loadExistingProfile()

//        btnUpdate.setOnClickListener {
//            updateProfile()
//        }
    }

    private fun loadExistingProfile() {
        ApiClient.api.getUserDetails().enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    etName.setText(user?.name ?: "")
                    etBio.setText(user?.bio ?: "")
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                Toast.makeText(this@EditProfile, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

//    private fun updateProfile() {
//        val name = etName.text.toString().trim()
//        val bio = etBio.text.toString().trim()
//
//        if (name.isEmpty()) {
//            etName.error = "Name required"
//            return
//        }
//
//        val request = UpdateProfileRequest(name, bio)
//
//        ApiClient.api.updateProfile(request).enqueue(object : Callback<UserDetailsResponse> {
//            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
//                if (response.isSuccessful) {
//                    Toast.makeText(this@EditProfile, "Profile updated", Toast.LENGTH_SHORT).show()
//                    finish()
//                } else {
//                    Toast.makeText(this@EditProfile, "Update failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
//                Toast.makeText(this@EditProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
}
