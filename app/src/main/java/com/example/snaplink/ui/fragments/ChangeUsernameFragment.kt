package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.models.ChangeUsernameRequest
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeUsernameFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var etNewUsername: EditText
    private lateinit var btnUpdateUsername: Button
    private lateinit var tvCurrentUsername: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_change_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        populateCurrentUsername()
        setupListeners()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        etNewUsername = view.findViewById(R.id.etNewUsername)
        btnUpdateUsername = view.findViewById(R.id.btnUpdateUsername)
        tvCurrentUsername = view.findViewById(R.id.tvCurrentUsername)
    }

    private fun populateCurrentUsername() {
        val currentUsername = SettingsManager.getUsername()
        tvCurrentUsername.text = currentUsername ?: "Not set"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdateUsername.setOnClickListener {
            val newUsername = etNewUsername.text.toString().trim()

            if (newUsername.isEmpty()) {
                etNewUsername.error = "Username cannot be empty"
                return@setOnClickListener
            }

            if (newUsername.length < 3) {
                etNewUsername.error = "Username must be at least 3 characters"
                return@setOnClickListener
            }

            if (!newUsername.matches(Regex("^[a-zA-Z0-9._]+$"))) {
                etNewUsername.error = "Username can only contain letters, numbers, dots and underscores"
                return@setOnClickListener
            }

            val currentUsername = SettingsManager.getUsername()
            if (newUsername == currentUsername) {
                etNewUsername.error = "New username is the same as current username"
                return@setOnClickListener
            }

            updateUsername(newUsername)
        }
    }

    private fun updateUsername(newUsername: String) {
        btnUpdateUsername.isEnabled = false
        btnUpdateUsername.text = "Updating..."

        ApiClient.api.changeUsername(ChangeUsernameRequest(newUsername)).enqueue(object : Callback<SettingsUpdateResponse> {
            override fun onResponse(call: Call<SettingsUpdateResponse>, response: Response<SettingsUpdateResponse>) {
                if (!isAdded) return

                btnUpdateUsername.isEnabled = true
                btnUpdateUsername.text = "Update Username"

                if (response.isSuccessful && response.body()?.success == true) {
                    // Update the cached settings
                    SettingsManager.updateCachedUsername(newUsername)

                    Toast.makeText(requireContext(), response.body()?.message ?: "Username updated successfully", Toast.LENGTH_SHORT).show()

                    // Update displayed current username
                    tvCurrentUsername.text = newUsername
                    etNewUsername.text.clear()
                } else {
                    val errorMsg = try {
                        response.errorBody()?.string() ?: "Failed to update username"
                    } catch (e: Exception) {
                        "Failed to update username"
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SettingsUpdateResponse>, t: Throwable) {
                if (!isAdded) return

                btnUpdateUsername.isEnabled = true
                btnUpdateUsername.text = "Update Username"
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
