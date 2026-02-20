package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.UpdateProfileRequest
import com.example.snaplink.network.UpdateProfileResponse
import com.example.snaplink.network.UserDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var btnBackFromEditProfile: ImageView
    private lateinit var spGender: Spinner
    private lateinit var etBio: EditText
    private lateinit var btnUpdate: Button

    private val genders = arrayOf("Select Gender", "Male", "Female", "Other")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBackFromEditProfile = view.findViewById(R.id.btnBackFromEditProfile)
        etBio = view.findViewById(R.id.etBio)
        btnUpdate = view.findViewById(R.id.btnUpdateProfile)
        spGender = view.findViewById(R.id.spGender)

        btnBackFromEditProfile.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genders)
        spGender.adapter = adapter

        loadExistingProfile()

        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadExistingProfile() {
        ApiClient.api.getUserDetails().enqueue(object : Callback<UserDetailsResponse> {
            override fun onResponse(
                call: Call<UserDetailsResponse>,
                response: Response<UserDetailsResponse>
            ) {
                if (!isAdded) return
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
                if (!isAdded) return
                Log.e("EditProfile", "Failed to load profile", t)
                Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProfile() {
        val bio = etBio.text.toString().trim()
        val gender = spGender.selectedItem.toString()

        if (gender == "Select Gender") {
            Toast.makeText(requireContext(), "Please select gender", Toast.LENGTH_SHORT).show()
            return
        }

        btnUpdate.isEnabled = false
        btnUpdate.text = "Updating..."

        val request = UpdateProfileRequest(bio, gender)

        ApiClient.api.updateProfile(request).enqueue(object : Callback<UpdateProfileResponse> {
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (!isAdded) return
                btnUpdate.isEnabled = true
                btnUpdate.text = "Update Profile"

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully! 🎉", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        Log.e("EditProfile", "Error: $errorBody")
                        Toast.makeText(requireContext(), "Update failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                if (!isAdded) return
                btnUpdate.isEnabled = true
                btnUpdate.text = "Update Profile"
                Log.e("EditProfile", "Error: ${t.message}", t)
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
