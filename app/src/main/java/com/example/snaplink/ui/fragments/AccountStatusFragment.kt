package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.ui.activities.MainActivity
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.network.SimpleApiResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountStatusFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private var isVerified: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isVerified = SettingsManager.isVerified()
        val layoutId = if (isVerified) R.layout.fragment_account_status_verified else R.layout.fragment_account_status
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            btnBack = view.findViewById(R.id.btnBack)
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            if (isVerified) {
                setupVerifiedUI(view)
            } else {
                setupUnverifiedUI(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing views", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupVerifiedUI(view: View) {
        val tvUsername: TextView = view.findViewById(R.id.tvUsername)
        tvUsername.text = SettingsManager.getUsername() ?: "_user"
        // Note: Image is already set to img_current_user in XML as requested, 
        // but we could also load profile image from SettingsManager if needed.
    }

    private fun setupUnverifiedUI(view: View) {
        val tvUsername: TextView? = view.findViewById(R.id.tvUsername)
        tvUsername?.text = SettingsManager.getUsername() ?: "_user"

        val btnVerifyNow: Button = view.findViewById(R.id.btnVerifyNow)
        btnVerifyNow.setOnClickListener {
            sendOtp()
        }
    }

    private fun sendOtp() {
        val apiService = ApiClient.api
        apiService.sendVerifyUserOtp().enqueue(object : Callback<SimpleApiResponse> {
            override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                if (isAdded) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "OTP sent to your email", Toast.LENGTH_SHORT).show()
                        navigateToOtpFragment()
                    } else {
                        Toast.makeText(requireContext(), response.body()?.message ?: "Failed to send OTP", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun navigateToOtpFragment() {
        (activity as? MainActivity)?.navigateToFragment(AccountStatusVerificationOtpFragment())
    }
}