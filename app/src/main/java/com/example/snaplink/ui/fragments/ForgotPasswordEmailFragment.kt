package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SendOtpRequest
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.ui.activities.MainActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordEmailFragment : Fragment(R.layout.fragment_forgot_password_email) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Enable/disable submit button based on email input
        etEmail.addTextChangedListener(object : TextWatcher {
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

        // Submit button click
        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                showMessage(tvMessage, "Please enter your email", isError = true)
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage(tvMessage, "Please enter a valid email address", isError = true)
                return@setOnClickListener
            }

            // Show loading, hide messages, disable button
            progressBar.visibility = View.VISIBLE
            tvMessage.visibility = View.GONE
            btnSubmit.isEnabled = false

            ApiClient.api.sendPasswordResetOtp(SendOtpRequest(email))
                .enqueue(object : Callback<SimpleApiResponse> {
                    override fun onResponse(
                        call: Call<SimpleApiResponse>,
                        response: Response<SimpleApiResponse>
                    ) {
                        if (!isAdded) return
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.success == true) {
                                // Navigate to OTP verification fragment
                                val otpFragment = ForgotPasswordOtpFragment().apply {
                                    arguments = Bundle().apply {
                                        putString("email", email)
                                    }
                                }
                                (activity as? MainActivity)?.navigateToFragment(otpFragment)
                            } else {
                                showMessage(
                                    tvMessage,
                                    body?.message ?: "Failed to send OTP",
                                    isError = true
                                )
                            }
                        } else {
                            val errorMsg = parseErrorMessage(response)
                            showMessage(tvMessage, errorMsg, isError = true)
                        }
                    }

                    override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                        if (!isAdded) return
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true
                        showMessage(
                            tvMessage,
                            "Network error: ${t.message}",
                            isError = true
                        )
                    }
                })
        }
    }

    private fun showMessage(tvMessage: TextView, message: String, isError: Boolean) {
        tvMessage.visibility = View.VISIBLE
        tvMessage.text = message
        tvMessage.setTextColor(
            if (isError) 0xFFFF5252.toInt() else 0xFF00C853.toInt()
        )
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