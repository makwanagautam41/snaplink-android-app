package com.example.snaplink.ui.fragments

import android.os.Bundle
import org.json.JSONObject
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
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.models.UpdatePhoneRequest
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeMobileFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var etNewMobile: EditText
    private lateinit var btnUpdateMobile: Button
    private lateinit var tvCurrentMobile: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_change_mobile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        populateCurrentMobile()
        setupListeners()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        etNewMobile = view.findViewById(R.id.etNewMobile)
        btnUpdateMobile = view.findViewById(R.id.btnUpdateMobile)
        tvCurrentMobile = view.findViewById(R.id.tvCurrentMobile)
    }

    private fun populateCurrentMobile() {
        val currentPhone = SettingsManager.getPhone()
        tvCurrentMobile.text = currentPhone ?: "Not set"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdateMobile.setOnClickListener {
            val newMobile = etNewMobile.text.toString().trim()

            if (newMobile.isEmpty()) {
                etNewMobile.error = "Mobile number cannot be empty"
                return@setOnClickListener
            }

            if (newMobile.length < 10) {
                etNewMobile.error = "Please enter a valid mobile number"
                return@setOnClickListener
            }

            val currentPhone = SettingsManager.getPhone()
            if (newMobile == currentPhone) {
                etNewMobile.error = "New mobile is the same as current mobile"
                return@setOnClickListener
            }

            updateMobile(newMobile)
        }
    }

    private fun updateMobile(newMobile: String) {
        btnUpdateMobile.isEnabled = false
        btnUpdateMobile.text = "Updating..."

        ApiClient.api.updatePhone(UpdatePhoneRequest(newMobile)).enqueue(object : Callback<SettingsUpdateResponse> {
            override fun onResponse(call: Call<SettingsUpdateResponse>, response: Response<SettingsUpdateResponse>) {
                if (!isAdded) return

                btnUpdateMobile.isEnabled = true
                btnUpdateMobile.text = "Update Mobile"

                if (response.isSuccessful && response.body()?.success == true) {
                    // Update the cached settings
                    SettingsManager.updateCachedPhone(newMobile)

                    Toast.makeText(requireContext(), response.body()?.message ?: "Mobile updated successfully", Toast.LENGTH_SHORT).show()

                    // Update displayed current mobile
                    tvCurrentMobile.text = newMobile
                    etNewMobile.text.clear()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = JSONObject(errorBody)
                            json.optString("message", "Failed to update mobile")
                        } else {
                            "Failed to update mobile"
                        }
                    } catch (e: Exception) {
                        "Failed to update mobile"
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SettingsUpdateResponse>, t: Throwable) {
                if (!isAdded) return

                btnUpdateMobile.isEnabled = true
                btnUpdateMobile.text = "Update Mobile"
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
