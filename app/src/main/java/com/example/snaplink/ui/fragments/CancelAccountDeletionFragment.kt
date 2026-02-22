package com.example.snaplink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snaplink.R

import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.CancelDeletionRequest
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelAccountDeletionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cancel_account_deletion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        val etUsername = view.findViewById<EditText>(R.id.etUsername)
        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val etPassword = view.findViewById<EditText>(R.id.etPassword)
        val togglePassword = view.findViewById<ImageView>(R.id.togglePassword)
        val cbConfirm = view.findViewById<CheckBox>(R.id.cbConfirm)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val tvWarning = view.findViewById<TextView>(R.id.tvWarning)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Setup password toggle
        var isPasswordVisible = false
        togglePassword.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye)
            } else {
                etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                togglePassword.setImageResource(R.drawable.ic_eye_off)
            }
            etPassword.setSelection(etPassword.text.length)
        }

        // Enable/disable submit button based on inputs (checkbox checked happens in click listener for better UX)
        val checkInputs = {
            val u = etUsername.text.toString().trim()
            val e = etEmail.text.toString().trim()
            val p = etPassword.text.toString()
            
            val isEnabled = u.isNotEmpty() && e.isNotEmpty() && p.isNotEmpty()
            btnSubmit.isEnabled = isEnabled
            if (isEnabled) {
                btnSubmit.setTextColor(0xFFFFFFFF.toInt())
                btnSubmit.setBackgroundResource(0) // Clear the outline background
                btnSubmit.backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF2563EB.toInt())
            } else {
                btnSubmit.setTextColor(0xFF8E8E93.toInt())
                btnSubmit.backgroundTintList = null
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { checkInputs() }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etUsername.addTextChangedListener(textWatcher)
        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)
        cbConfirm.setOnCheckedChangeListener { _, _ -> checkInputs() }

        btnSubmit.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            val isConfirmed = cbConfirm.isChecked
            if (!isConfirmed) {
                Toast.makeText(requireContext(), "Please confirm that you want to cancel deletion", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSubmit.isEnabled = false
            btnSubmit.text = "Processing..."

            ApiClient.api.cancelAccountDeletion(CancelDeletionRequest(username, email, password, isConfirmed))
                .enqueue(object : Callback<SimpleApiResponse> {
                    override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                        if (!isAdded) return
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Submit"

                        if (response.isSuccessful && response.body()?.success == true) {
                            Toast.makeText(requireContext(), "Account deletion cancelled successfully. You can now login.", Toast.LENGTH_LONG).show()
                            (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
                        } else {
                            val msg = response.body()?.message ?: "Failed to cancel deletion"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                        if (!isAdded) return
                        btnSubmit.isEnabled = true
                        btnSubmit.text = "Submit"
                        Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}