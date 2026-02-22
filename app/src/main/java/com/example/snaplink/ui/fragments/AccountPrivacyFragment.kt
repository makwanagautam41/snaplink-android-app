package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.models.SettingsUpdateResponse
import com.example.snaplink.models.UpdateProfileVisibilityRequest
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountPrivacyFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var switchPublicAccount: SwitchCompat

    // Track if we are programmatically setting the switch to avoid triggering listener
    private var isUpdatingSwitch = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_account_privacy, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            populateFromSettings()
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing views", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        switchPublicAccount = view.findViewById(R.id.switchPublicAccount)
    }

    /**
     * Set the switch state based on cached profile visibility
     */
    private fun populateFromSettings() {
        val visibility = SettingsManager.getProfileVisibility()
        isUpdatingSwitch = true
        switchPublicAccount.isChecked = (visibility == "public")
        isUpdatingSwitch = false
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Use the CardView click to toggle the switch
        view?.findViewById<androidx.cardview.widget.CardView>(R.id.cardPublicAccount)?.setOnClickListener {
            switchPublicAccount.isChecked = !switchPublicAccount.isChecked
        }

        switchPublicAccount.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingSwitch) return@setOnCheckedChangeListener

            val newVisibility = if (isChecked) "public" else "private"
            updateProfileVisibility(newVisibility)
        }
    }

    private fun updateProfileVisibility(newVisibility: String) {
        switchPublicAccount.isEnabled = false

        ApiClient.api.updateProfileVisibility(UpdateProfileVisibilityRequest(newVisibility))
            .enqueue(object : Callback<SettingsUpdateResponse> {
                override fun onResponse(call: Call<SettingsUpdateResponse>, response: Response<SettingsUpdateResponse>) {
                    if (!isAdded) return

                    switchPublicAccount.isEnabled = true

                    if (response.isSuccessful && response.body()?.success == true) {
                        // Update cached settings
                        SettingsManager.updateCachedProfileVisibility(newVisibility)

                        val msg = if (newVisibility == "public") "Account is now public" else "Account is now private"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    } else {
                        // Revert the switch on failure
                        isUpdatingSwitch = true
                        switchPublicAccount.isChecked = !switchPublicAccount.isChecked
                        isUpdatingSwitch = false

                        Toast.makeText(requireContext(), "Failed to update privacy settings", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SettingsUpdateResponse>, t: Throwable) {
                    if (!isAdded) return

                    switchPublicAccount.isEnabled = true

                    // Revert the switch on failure
                    isUpdatingSwitch = true
                    switchPublicAccount.isChecked = !switchPublicAccount.isChecked
                    isUpdatingSwitch = false

                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
