package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.snaplink.R

import android.widget.Button
import android.widget.Toast
import com.example.snaplink.models.ChangePasswordRequest
import com.example.snaplink.models.ChangePasswordResponse
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnCancel: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners(view)
        setupPasswordToggles(view)
    }

    private fun initViews(view: View) {
        etOldPassword = view.findViewById(R.id.etOldPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupListeners(view: View) {
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdatePassword.setOnClickListener {
            validateAndUpdatePassword()
        }
    }

    private fun setupPasswordToggles(view: View) {
        setupPasswordToggle(view, R.id.etOldPassword, R.id.toggleOldPassword)
        setupPasswordToggle(view, R.id.etNewPassword, R.id.toggleNewPassword)
        setupPasswordToggle(view, R.id.etConfirmPassword, R.id.toggleConfirmPassword)
    }

    private fun validateAndUpdatePassword() {
        val oldPass = etOldPassword.text.toString().trim()
        val newPass = etNewPassword.text.toString().trim()
        val confirmPass = etConfirmPassword.text.toString().trim()

        if (oldPass.isEmpty()) {
            etOldPassword.error = "Enter old password"
            return
        }
        if (newPass.isEmpty()) {
            etNewPassword.error = "Enter new password"
            return
        }
        if (newPass.length < 6) {
            etNewPassword.error = "Password must be at least 6 characters"
            return
        }
        if (newPass != confirmPass) {
            etConfirmPassword.error = "Passwords do not match"
            return
        }

        updatePassword(oldPass, newPass)
    }

    private fun updatePassword(oldPass: String, newPass: String) {
        btnUpdatePassword.isEnabled = false
        btnUpdatePassword.text = "Updating..."

        val request = ChangePasswordRequest(oldPass, newPass)
        ApiClient.api.updatePassword(request).enqueue(object : Callback<ChangePasswordResponse> {
            override fun onResponse(call: Call<ChangePasswordResponse>, response: Response<ChangePasswordResponse>) {
                if (!isAdded) return

                btnUpdatePassword.isEnabled = true
                btnUpdatePassword.text = "Update"

                if (response.isSuccessful && response.body()?.success == true) {
                    val newToken = response.body()?.token
                    if (!newToken.isNullOrEmpty()) {
                        TokenManager.saveToken(newToken)
                    }
                    Toast.makeText(requireContext(), response.body()?.message ?: "Password updated successfully", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = JSONObject(errorBody)
                            json.optString("message", "Failed to update password")
                        } else {
                            "Failed to update password"
                        }
                    } catch (e: Exception) {
                        "Failed to update password"
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ChangePasswordResponse>, t: Throwable) {
                if (!isAdded) return
                btnUpdatePassword.isEnabled = true
                btnUpdatePassword.text = "Update"
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupPasswordToggle(view: View, editTextId: Int, toggleId: Int) {
        val editText = view.findViewById<EditText>(editTextId)
        val toggle = view.findViewById<ImageView>(toggleId)

        var isVisible = false

        toggle.setOnClickListener {
            isVisible = !isVisible

            if (isVisible) {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                toggle.setImageResource(R.drawable.ic_eye)
            } else {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
                toggle.setImageResource(R.drawable.ic_eye_off)
            }

            editText.setSelection(editText.text.length)
        }
    }
}