package com.example.snaplink

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.UpdateProfileRequest
import com.example.snaplink.network.UpdateProfileResponse
import com.example.snaplink.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfile : AppCompatActivity() {

    private lateinit var btnBackFromEditProfile: ImageView
    private lateinit var spGender: Spinner
    private lateinit var etBio: EditText
    private lateinit var btnUpdate: Button

    private val genders = arrayOf("Select Gender", "Male", "Female", "Other")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        btnBackFromEditProfile = findViewById(R.id.btnBackFromEditProfile)
        etBio = findViewById(R.id.etBio)
        btnUpdate = findViewById(R.id.btnUpdateProfile)
        spGender = findViewById(R.id.spGender)

        btnBackFromEditProfile.setOnClickListener { finish() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        spGender.adapter = adapter

        loadExistingProfile()

        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadExistingProfile() {
        ApiClient.api.getUserDetails().enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    if (user != null) {
                        etBio.setText(user.bio ?: "")

                        val genderFromApi = user.gender ?: "Select Gender"
                        val position = genders.indexOfFirst { it.equals(genderFromApi, ignoreCase = true) }
                        spGender.setSelection(if (position >= 0) position else 0)
                    }
                } else {
                    Log.e("EditProfile", "Load failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                Log.e("EditProfile", "Failed to load profile", t)
                Toast.makeText(this@EditProfile, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val bio = etBio.text.toString().trim()
        val gender = spGender.selectedItem.toString()

        if (gender == "Select Gender") {
            Toast.makeText(this, "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        btnUpdate.isEnabled = false
        btnUpdate.text = "Updating..."

        val request = UpdateProfileRequest(bio, gender)

        Log.d("EditProfile", "Request - Bio: $bio, Gender: $gender")

        ApiClient.api.updateProfile(request).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                btnUpdate.isEnabled = true
                btnUpdate.text = "Update Profile"

                Log.d("EditProfile", "Response code: ${response.code()}")
                Log.d("EditProfile", "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@EditProfile, "Profile updated successfully! 🎉", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("EditProfile", "Error: $errorBody")
                        Toast.makeText(this@EditProfile, "Update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(this@EditProfile, "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                btnUpdate.isEnabled = true
                btnUpdate.text = "Update Profile"
                Log.e("EditProfile", "Error: ${t.message}", t)
                Toast.makeText(this@EditProfile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}