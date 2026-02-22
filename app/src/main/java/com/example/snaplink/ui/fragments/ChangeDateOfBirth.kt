package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.snaplink.R

import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.models.UpdateDobRequest
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeDateOfBirth : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var etDay: EditText
    private lateinit var etMonth: EditText
    private lateinit var etYear: EditText
    private lateinit var btnUpdateDob: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_change_date_of_birth, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        populateCurrentDob()
        setupListeners()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        etDay = view.findViewById(R.id.etDay)
        etMonth = view.findViewById(R.id.etMonth)
        etYear = view.findViewById(R.id.etYear)
        btnUpdateDob = view.findViewById(R.id.btnUpdateDob)
    }

    private fun populateCurrentDob() {
        val currentDob = SettingsManager.getDateOfBirth() ?: return
        
        try {
            // ISO format usually contains 'T' (e.g., 2006-01-04T00:00:00.000Z)
            val datePart = if (currentDob.contains("T")) {
                currentDob.split("T")[0]
            } else {
                currentDob
            }
            
            // The API might return YYYY-MM-DD or DD-MM-YYYY or DD-M-YYYY
            val parts = datePart.split("-")
            if (parts.size >= 3) {
                if (parts[0].length == 4) {
                    // Format: YYYY-MM-DD
                    etYear.setText(parts[0])
                    etMonth.setText(parts[1])
                    etDay.setText(parts[2])
                } else {
                    // Format: DD-MM-YYYY or DD-M-YYYY
                    etDay.setText(parts[0])
                    etMonth.setText(parts[1])
                    etYear.setText(parts[2])
                }
            }
        } catch (e: Exception) {
            Log.e("ChangeDob", "Error parsing current DOB: $currentDob", e)
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnUpdateDob.setOnClickListener {
            val day = etDay.text.toString().trim()
            val month = etMonth.text.toString().trim()
            val year = etYear.text.toString().trim()

            if (day.isEmpty() || month.isEmpty() || year.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simple validation
            val d = day.toIntOrNull() ?: 0
            val m = month.toIntOrNull() ?: 0
            val y = year.toIntOrNull() ?: 0

            if (d < 1 || d > 31) {
                etDay.error = "Invalid day"
                return@setOnClickListener
            }
            if (m < 1 || m > 12) {
                etMonth.error = "Invalid month"
                return@setOnClickListener
            }
            if (y < 1900 || y > 2024) {
                etYear.error = "Invalid year"
                return@setOnClickListener
            }

            // Format as YYYY-MM-DD
            val formattedDate = String.format("%04d-%02d-%02d", y, m, d)
            updateDob(formattedDate)
        }
    }

    private fun updateDob(newDob: String) {
        btnUpdateDob.isEnabled = false
        btnUpdateDob.text = "Updating..."

        ApiClient.api.updateDob(UpdateDobRequest(newDob)).enqueue(object : Callback<SettingsUpdateResponse> {
            override fun onResponse(call: Call<SettingsUpdateResponse>, response: Response<SettingsUpdateResponse>) {
                if (!isAdded) return

                btnUpdateDob.isEnabled = true
                btnUpdateDob.text = "Update Date Of Birth"

                if (response.isSuccessful && response.body()?.success == true) {
                    val returnedDob = response.body()?.user?.dateOfBirth ?: newDob
                    SettingsManager.updateCachedDateOfBirth(returnedDob)
                    Toast.makeText(requireContext(), response.body()?.message ?: "Date of birth updated", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    val errorMsg = try {
                        val errorBody = response.errorBody()?.string()
                        if (errorBody != null) {
                            val json = JSONObject(errorBody)
                            json.optString("message", "Failed to update date of birth")
                        } else {
                            "Failed to update date of birth"
                        }
                    } catch (e: Exception) {
                        "Failed to update date of birth"
                    }
                    Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SettingsUpdateResponse>, t: Throwable) {
                if (!isAdded) return
                btnUpdateDob.isEnabled = true
                btnUpdateDob.text = "Update Date Of Birth"
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
