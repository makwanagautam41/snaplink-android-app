package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.snaplink.R

import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.DeactivateAccountRequest
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountDeactivationFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var btnCancel: Button
    private lateinit var btnConfirm: Button
    private lateinit var etReason: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_deactivation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.btnBack)
        btnCancel = view.findViewById(R.id.btnCancel)
        btnConfirm = view.findViewById(R.id.btnConfirmDeactivate)
        etReason = view.findViewById(R.id.etReason)
        etPassword = view.findViewById(R.id.etPassword)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnConfirm.setOnClickListener {
            deactivateAccount()
        }
    }

    private fun deactivateAccount() {
        val password = etPassword.text.toString()
        val reason = etReason.text.toString().trim()

        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your password to confirm", Toast.LENGTH_SHORT).show()
            return
        }

        btnConfirm.isEnabled = false
        btnConfirm.text = "Processing..."

        ApiClient.api.deactivateAccount(DeactivateAccountRequest(password, reason))
            .enqueue(object : Callback<SimpleApiResponse> {
                override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                    if (!isAdded) return
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm"

                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Account deactivated successfully", Toast.LENGTH_LONG).show()
                        // Clear token and logout
                        TokenManager.clearToken()
                        (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
                    } else {
                        val msg = response.errorBody()?.string() ?: "Failed to deactivate"
                        Toast.makeText(requireContext(), "Error: $msg", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                    if (!isAdded) return
                    btnConfirm.isEnabled = true
                    btnConfirm.text = "Confirm"
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
