package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.ApiResponse
import com.example.snaplink.network.RegisterRequest
import com.example.snaplink.network.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<android.widget.TextView>(R.id.tvLogin).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        view.findViewById<Button>(R.id.registerBtn).setOnClickListener {
            val name = view.findViewById<EditText>(R.id.name).text.toString().trim()
            val email = view.findViewById<EditText>(R.id.email).text.toString().trim()
            val password = view.findViewById<EditText>(R.id.password).text.toString()
            val username = view.findViewById<EditText>(R.id.username).text.toString().trim()
            val phone = view.findViewById<EditText>(R.id.phone).text.toString().trim()
            val gender = view.findViewById<EditText>(R.id.gender).text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() ||
                username.isEmpty() || phone.isEmpty() || gender.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.api.register(
                RegisterRequest(name, email, password, username, phone, gender)
            ).enqueue(object : Callback<ApiResponse> {

                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (!isAdded) return
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val token = apiResponse?.token

                        if (token != null) {
                            TokenManager.saveToken(token)
                        }

                        Toast.makeText(requireContext(), "Account created successfully", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null && errorBody.contains("message")) {
                                val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
                                val msg = match?.groupValues?.get(1) ?: "Registration Failed"
                                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "Registration Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        setupPasswordToggle(view, R.id.password, R.id.toggleRegisterPassword)
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
