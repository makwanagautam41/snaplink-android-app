package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.ResetPasswordRequest
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.ui.activities.MainActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordNewPasswordFragment :
    Fragment(R.layout.fragment_forgot_password_new_password) {

    private var email: String = ""
    private var otp: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email", "") ?: ""
        otp = arguments?.getString("otp", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val togglePassword = view.findViewById<ImageView>(R.id.toggleResetPassword)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvSuccess = view.findViewById<TextView>(R.id.tvSuccess)

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Password toggle
        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etNewPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye)
            } else {
                etNewPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            etNewPassword.setSelection(etNewPassword.text.length)
        }

        // Enable/disable submit button based on password input
        etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val hasText = !s.isNullOrBlank()
                btnSubmit.isEnabled = hasText
                if (hasText) {
                    btnSubmit.setTextColor(0xFFFFFFFF.toInt())
                    btnSubmit.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(0xFF2563EB.toInt())
                } else {
                    btnSubmit.setTextColor(0xFF8E8E93.toInt())
                    btnSubmit.backgroundTintList =
                        android.content.res.ColorStateList.valueOf(0xFF1C1C1E.toInt())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Submit button click - reset password
        btnSubmit.setOnClickListener {
            val newPassword = etNewPassword.text.toString()

            if (newPassword.isEmpty()) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Please enter a new password"
                tvSuccess.visibility = View.GONE
                return@setOnClickListener
            }

            if (newPassword.length < 6) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Password must be at least 6 characters"
                tvSuccess.visibility = View.GONE
                return@setOnClickListener
            }

            // Show loading
            progressBar.visibility = View.VISIBLE
            tvError.visibility = View.GONE
            tvSuccess.visibility = View.GONE
            btnSubmit.isEnabled = false

            ApiClient.api.resetPassword(ResetPasswordRequest(email, otp, newPassword))
                .enqueue(object : Callback<SimpleApiResponse> {
                    override fun onResponse(
                        call: Call<SimpleApiResponse>,
                        response: Response<SimpleApiResponse>
                    ) {
                        if (!isAdded) return
                        progressBar.visibility = View.GONE

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.success == true) {
                                // Show success message
                                tvSuccess.visibility = View.VISIBLE
                                tvSuccess.text = body.message
                                tvError.visibility = View.GONE
                                btnSubmit.isEnabled = false
                                etNewPassword.isEnabled = false

                                // Show toast and navigate to login after a short delay
                                Toast.makeText(
                                    requireContext(),
                                    "Password has been reset successfully!",
                                    Toast.LENGTH_LONG
                                ).show()

                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (!isAdded) return@postDelayed
                                    (activity as? MainActivity)?.navigateWithClearStack(
                                        LoginFragment()
                                    )
                                }, 2000)
                            } else {
                                btnSubmit.isEnabled = true
                                tvError.visibility = View.VISIBLE
                                tvError.text = body?.message ?: "Failed to reset password"
                            }
                        } else {
                            btnSubmit.isEnabled = true
                            val errorMsg = parseErrorMessage(response)
                            tvError.visibility = View.VISIBLE
                            tvError.text = errorMsg
                        }
                    }

                    override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                        if (!isAdded) return
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true
                        tvError.visibility = View.VISIBLE
                        tvError.text = "Network error: ${t.message}"
                    }
                })
        }
    }

    private fun parseErrorMessage(response: Response<SimpleApiResponse>): String {
        return try {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, SimpleApiResponse::class.java)
                errorResponse.message
            } else {
                "Something went wrong"
            }
        } catch (e: Exception) {
            "Something went wrong"
        }
    }
}