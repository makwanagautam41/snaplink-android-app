package com.example.snaplink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snaplink.R

import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.VerifyReactivateOtpRequest
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReactivateAccountVerifyOtpFragment : Fragment() {

    private var email: String = ""
    private var resendTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        email = arguments?.getString("email") ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reactivate_account_verify_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val btnVerify = view.findViewById<Button>(R.id.btnVerifyOtp)
        val tvResend = view.findViewById<TextView>(R.id.tvResend)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val tvOtpSent = view.findViewById<TextView>(R.id.tvOtpSent)

        val otpFields = listOf(
            view.findViewById<EditText>(R.id.otp1),
            view.findViewById<EditText>(R.id.otp2),
            view.findViewById<EditText>(R.id.otp3),
            view.findViewById<EditText>(R.id.otp4),
            view.findViewById<EditText>(R.id.otp5),
            view.findViewById<EditText>(R.id.otp6)
        )

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupOtpInputs(otpFields)
        startResendTimer(tvResend)

        btnVerify.setOnClickListener {
            val otp = otpFields.joinToString("") { it.text.toString() }
            if (otp.length < 6) {
                tvError.visibility = View.VISIBLE
                tvError.text = "Please enter 6-digit OTP"
                return@setOnClickListener
            }

            btnVerify.isEnabled = false
            btnVerify.text = "Verifying..."
            tvError.visibility = View.GONE

            ApiClient.api.verifyReactivateAccountOtp(VerifyReactivateOtpRequest(email, otp))
                .enqueue(object : Callback<SimpleApiResponse> {
                    override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                        if (!isAdded) return
                        btnVerify.isEnabled = true
                        btnVerify.text = "Verify OTP"

                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(requireContext(), "Account reactivated successfully. You can now login.", Toast.LENGTH_LONG).show()
                            (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
                        } else {
                            tvError.visibility = View.VISIBLE
                            tvError.text = response.body()?.message ?: "Invalid OTP"
                        }
                    }

                    override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                        if (!isAdded) return
                        btnVerify.isEnabled = true
                        btnVerify.text = "Verify OTP"
                        tvError.visibility = View.VISIBLE
                        tvError.text = "Error: ${t.message}"
                    }
                })
        }
    }

    private fun setupOtpInputs(otpFields: List<EditText>) {
        otpFields.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1 && index < otpFields.size - 1) {
                        otpFields[index + 1].requestFocus()
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            editText.setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && editText.text.isEmpty() && index > 0) {
                    otpFields[index - 1].requestFocus()
                    otpFields[index - 1].setSelection(otpFields[index - 1].text.length)
                    return@setOnKeyListener true
                }
                false
            }
        }
    }

    private fun startResendTimer(tvResend: TextView) {
        resendTimer?.cancel()
        resendTimer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (!isAdded) return
                tvResend.text = "Resend in ${millisUntilFinished / 1000}s"
                tvResend.isClickable = false
                tvResend.setTextColor(0xFF8E8E93.toInt())
            }

            override fun onFinish() {
                if (!isAdded) return
                tvResend.text = "Resend OTP"
                tvResend.isClickable = true
                tvResend.setTextColor(0xFF4F6DFF.toInt())
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resendTimer?.cancel()
    }
}