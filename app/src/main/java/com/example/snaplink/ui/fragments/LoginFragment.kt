package com.example.snaplink.ui.fragments

import android.content.Intent
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
import com.example.snaplink.network.LoginRequest
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check for existing valid token
        if (TokenManager.isLoggedIn()) {
            ApiClient.api.getUserDetails().enqueue(object : Callback<com.example.snaplink.network.UserDetailsResponse> {
                override fun onResponse(call: Call<com.example.snaplink.network.UserDetailsResponse>, response: Response<com.example.snaplink.network.UserDetailsResponse>) {
                    if (!isAdded) return
                    if (response.isSuccessful) {
                        val u = response.body()?.user
                        u?.username?.let { TokenManager.saveUsername(it) }
                        u?._id?.let    { TokenManager.saveUserId(it) }
                        u?.profileImg?.let { TokenManager.saveProfileImage(it) }
                        (activity as? MainActivity)?.navigateWithClearStack(HomeFragment())
                    } else if (response.code() == 401) {
                        TokenManager.clearToken()
                    }
                }
                override fun onFailure(call: Call<com.example.snaplink.network.UserDetailsResponse>, t: Throwable) {
                    // Network error, let user try to login
                }
            })
        }

        val emailOrUsername = view.findViewById<EditText>(R.id.email)
        val password = view.findViewById<EditText>(R.id.password)
        val loginBtn = view.findViewById<Button>(R.id.loginBtn)
        val tvRegister = view.findViewById<android.widget.TextView>(R.id.tvRegister)

        tvRegister.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(RegisterFragment())
        }

        val tvForgotPassword = view.findViewById<android.widget.TextView>(R.id.tvForgotPassword)
        tvForgotPassword.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ForgotPasswordEmailFragment())
        }

        loginBtn.setOnClickListener {
            val identifier = emailOrUsername.text.toString().trim()
            val pass = password.text.toString()

            if (identifier.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter email/username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClient.api.login(
                LoginRequest(identifier, pass)
            ).enqueue(object : Callback<ApiResponse> {

                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (!isAdded) return
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        
                        // Check for both HTTP success and API success flag
                        if (apiResponse?.success == true && apiResponse.token != null) {
                            val token = apiResponse.token
                            TokenManager.saveToken(token)

                            // Save user id from top-level response field
                            apiResponse._id?.let {
                                TokenManager.saveUserId(it)
                            }

                            // Note: username & profileImg are no longer provided in the login/signup response
                            // They can be fetched subsequently via getUserDetails() if needed.

                            Toast.makeText(requireContext(), apiResponse.message ?: "Login successful", Toast.LENGTH_SHORT).show()
                            (activity as? MainActivity)?.navigateWithClearStack(HomeFragment())
                        } else {
                            val msg = apiResponse?.message ?: "Login failed"
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null && errorBody.contains("message")) {
                                val match = "\"message\":\"(.*?)\"".toRegex().find(errorBody)
                                val msg = match?.groupValues?.get(1) ?: "Login Failed"
                                
                                if (msg.contains("deactivated", ignoreCase = true)) {
                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                                    (activity as? MainActivity)?.navigateToFragment(ReactivateAccountFragment())
                                } else if (msg.contains("deletion", ignoreCase = true)) {
                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
                                    (activity as? MainActivity)?.navigateToFragment(CancelAccountDeletionFragment())
                                } else {
                                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(requireContext(), "Login Failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        setupPasswordToggle(view, R.id.password, R.id.toggleLoginPassword)
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
