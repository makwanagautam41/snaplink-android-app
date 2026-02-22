package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SendOtpRequest
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.VerifyOtpRequest
import com.example.snaplink.ui.activities.MainActivity
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ForgotPasswordOtpFragment : Fragment(R.layout.fragment_forgot_password_otp) {

    private var email: String = ""
    private var resendTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email", "") ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnVerifyOtp = view.findViewById<Button>(R.id.btnVerifyOtp)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvResend = view.findViewById<TextView>(R.id.tvResend)
        val tvOtpSent = view.findViewById<TextView>(R.id.tvOtpSent)

        val otpFields = listOf(
            view.findViewById<EditText>(R.id.otp1),
            view.findViewById<EditText>(R.id.otp2),
            view.findViewById<EditText>(R.id.otp3),
            view.findViewById<EditText>(R.id.otp4),
            view.findViewById<EditText>(R.id.otp5),
            view.findViewById<EditText>(R.id.otp6)
        )

        // Back button
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup OTP auto-move inputs
        setupOtpInputs(otpFields)

        // Show initial OTP sent message
        tvOtpSent.visibility = View.VISIBLE

        // Start resend countdown timer
        startResendTimer(tvResend)

        // Resend OTP click
        tvResend.setOnClickListener {
            if (tvResend.text == "Resend OTP") {
                resendOtp(tvResend, tvOtpSent, tvError)
            }
        }

        // Verify OTP button
        btnVerifyOtp.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }

            if (otp.length < 6) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Please enter the complete 6-digit OTP"
                tvOtpSent.visibility = View.GONE
                return@setOnClickListener
            }

            // Disable button and show verifying text
            tvError.visibility = View.GONE
            tvOtpSent.visibility = View.GONE
            btnVerifyOtp.isEnabled = false
            btnVerifyOtp.text = "Verifying..."

            ApiClient.api.verifyPasswordResetOtp(VerifyOtpRequest(email, otp))
                .enqueue(object : Callback<SimpleApiResponse> {
                    override fun onResponse(
                        call: Call<SimpleApiResponse>,
                        response: Response<SimpleApiResponse>
                    ) {
                        if (!isAdded) return
                        btnVerifyOtp.isEnabled = true
                        btnVerifyOtp.text = "Verify OTP"

                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body?.success == true) {
                                // Navigate to new password fragment
                                val newPasswordFragment =
                                    ForgotPasswordNewPasswordFragment().apply {
                                        arguments = Bundle().apply {
                                            putString("email", email)
                                            putString("otp", otp)
                                        }
                                    }
                                (activity as? MainActivity)?.navigateToFragment(
                                    newPasswordFragment
                                )
                            } else {
                                tvError.visibility = View.VISIBLE
                                tvError.text = body?.message ?: "Invalid OTP"
                            }
                        } else {
                            val errorMsg = parseErrorMessage(response)
                            tvError.visibility = View.VISIBLE
                            tvError.text = errorMsg
                        }
                    }

                    override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                        if (!isAdded) return
                        btnVerifyOtp.isEnabled = true
                        btnVerifyOtp.text = "Verify OTP"
                        tvError.visibility = View.VISIBLE
                        tvError.text = "Network error: ${t.message}"
                    }
                })
        }
    }

    private fun setupOtpInputs(otpFields: List<EditText>) {
        otpFields.forEachIndexed { index, editText ->

            // Auto move forward
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            })

            // Backspace move backward
            editText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    editText.text.isEmpty() &&
                    index > 0
                ) {
                    otpFields[index - 1].requestFocus()
                    otpFields[index - 1].setSelection(
                        otpFields[index - 1].text.length
                    )
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    private fun startResendTimer(tvResend: TextView) {
        resendTimer?.cancel()
        tvResend.isClickable = false

        resendTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isAdded) return
                val seconds = millisUntilFinished / 1000
                tvResend.text = "Resend in ${seconds}s"
                tvResend.setTextColor(0xFF8E8E93.toInt())
                tvResend.isClickable = false
            }

            override fun onFinish() {
                if (!isAdded) return
                tvResend.text = "Resend OTP"
                tvResend.setTextColor(0xFF4F6DFF.toInt())
                tvResend.isClickable = true
            }
        }.start()
    }

    private fun resendOtp(
        tvResend: TextView,
        tvOtpSent: TextView,
        tvError: TextView
    ) {
        tvError.visibility = View.GONE
        tvResend.text = "Sending..."

        ApiClient.api.sendPasswordResetOtp(SendOtpRequest(email))
            .enqueue(object : Callback<SimpleApiResponse> {
                override fun onResponse(
                    call: Call<SimpleApiResponse>,
                    response: Response<SimpleApiResponse>
                ) {
                    if (!isAdded) return

                    if (response.isSuccessful && response.body()?.success == true) {
                        tvOtpSent.visibility = View.VISIBLE
                        tvOtpSent.text = "OTP has been resent to your email address"
                        startResendTimer(tvResend)
                    } else {
                        tvResend.text = "Resend OTP"
                        val body = response.body()
                        tvError.visibility = View.VISIBLE
                        tvError.text = body?.message ?: "Failed to resend OTP"
                    }
                }

                override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                    if (!isAdded) return
                    tvResend.text = "Resend OTP"
                    tvError.visibility = View.VISIBLE
                    tvError.text = "Network error: ${t.message}"
                }
            })
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

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel()
    }
}