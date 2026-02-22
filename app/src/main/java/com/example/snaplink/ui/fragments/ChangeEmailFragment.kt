package com.example.snaplink.ui.fragments

import android.os.Bundle
import org.json.JSONObject
import android.util.Patterns
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
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.models.UpdateEmailRequest
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeEmailFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var etNewEmail: EditText
    private lateinit var btnUpdateEmail: Button
    private lateinit var tvCurrentEmail: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_change_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        populateCurrentEmail()
        setupListeners()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        etNewEmail = view.findViewById(R.id.etNewEmail)
        btnUpdateEmail = view.findViewById(R.id.btnUpdateEmail)
        tvCurrentEmail = view.findViewById(R.id.tvCurrentEmail)
    }

    private fun populateCurrentEmail() {
        val currentEmail = SettingsManager.getEmail()
        tvCurrentEmail.text = currentEmail ?: "Not set"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdateEmail.setOnClickListener {
            val newEmail = etNewEmail.text.toString().trim()

            if (newEmail.isEmpty()) {
                etNewEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                etNewEmail.error = "Please enter a valid email"
                return@setOnClickListener
            }

            val currentEmail = SettingsManager.getEmail()
            if (newEmail == currentEmail) {
                etNewEmail.error = "New email is the same as current email"
                return@setOnClickListener
            }

            updateEmail(newEmail)
        }
    }

    private fun updateEmail(newEmail: String) {
        btnUpdateEmail.isEnabled = false
        btnUpdateEmail.text = "Updating..."

        ApiClient.api.updateEmail(UpdateEmailRequest(newEmail)).enqueue(object : Callback<SettingsUpdateResponse> {
            override fun onResponse(call: Call<SettingsUpdateResponse>, response: Response<SettingsUpdateResponse>) {
                if (!isAdded) return

                btnUpdateEmail.isEnabled = true
                btnUpdateEmail.text = "Update Email"

                if (response.isSuccessful && response.body()?.success == true) {
                    // Update the cached settings
                    SettingsManager.updateCachedEmail(newEmail)

                    Toast.makeText(requireContext(), response.body()?.message ?: "Email updated successfully", Toast.LENGTH_SHORT).show()

                    // Update displayed current email
                    tvCurrentEmail.text = newEmail
                    etNewEmail.text.clear()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = JSONObject(errorBody)
                            json.optString("message", "Failed to update email")
                        } else {
                            "Failed to update email"
                        }
                    } catch (e: Exception) {
                        "Failed to update email"
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SettingsUpdateResponse>, t: Throwable) {
                if (!isAdded) return

                btnUpdateEmail.isEnabled = true
                btnUpdateEmail.text = "Update Email"
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
