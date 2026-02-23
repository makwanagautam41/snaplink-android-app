package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
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
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.VerifyUserRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountStatusVerificationOtpFragment : Fragment() {

    private lateinit var btnBack: ImageView
    private lateinit var btnVerify: Button
    private lateinit var tvResendOtp: TextView
    private val otpFields = mutableListOf<EditText>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_status_verification_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.btnBack)
        btnVerify = view.findViewById(R.id.btnVerify)
        tvResendOtp = view.findViewById(R.id.tvResendOtp)

        otpFields.add(view.findViewById(R.id.otp1))
        otpFields.add(view.findViewById(R.id.otp2))
        otpFields.add(view.findViewById(R.id.otp3))
        otpFields.add(view.findViewById(R.id.otp4))
        otpFields.add(view.findViewById(R.id.otp5))
        otpFields.add(view.findViewById(R.id.otp6))

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupOtpInputs()

        btnVerify.setOnClickListener {
            verifyOtp()
        }

        tvResendOtp.setOnClickListener {
            resendOtp()
        }
    }

    private fun setupOtpInputs() {
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        otpFields[index - 1].requestFocus()
                        otpFields[index - 1].setText("")
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun verifyOtp() {
        val otp = otpFields.joinToString("") { it.text.toString() }
        if (otp.length < 6) {
            Toast.makeText(requireContext(), "Please enter complete OTP", Toast.LENGTH_SHORT).show()
            return
        }

        btnVerify.isEnabled = false
        btnVerify.text = "Verifying..."

        ApiClient.api.verifyUser(VerifyUserRequest(otp)).enqueue(object : Callback<SimpleApiResponse> {
            override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                if (isAdded) {
                    btnVerify.isEnabled = true
                    btnVerify.text = "Verify Now"

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Account verified successfully!", Toast.LENGTH_SHORT).show()
                        SettingsManager.updateCachedVerifiedStatus(true)
                        // Pop back to AccountStatusFragment which should now show verified state
                        parentFragmentManager.popBackStack()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.message ?: "Verification failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                if (isAdded) {
                    btnVerify.isEnabled = true
                    btnVerify.text = "Verify Now"
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun resendOtp() {
        tvResendOtp.isEnabled = false
        tvResendOtp.text = "Sending..."

        ApiClient.api.sendVerifyUserOtp().enqueue(object : Callback<SimpleApiResponse> {
            override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                if (isAdded) {
                    tvResendOtp.isEnabled = true
                    tvResendOtp.text = "Didn't receive code? Resend"

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.message ?: "Failed to resend OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                if (isAdded) {
                    tvResendOtp.isEnabled = true
                    tvResendOtp.text = "Didn't receive code? Resend"
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}