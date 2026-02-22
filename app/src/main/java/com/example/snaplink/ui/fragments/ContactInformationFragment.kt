package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.ui.activities.MainActivity

class ContactInformationFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var btnChangeEmail: RelativeLayout
    private lateinit var btnChangeMobile: RelativeLayout
    private lateinit var tvEmail: TextView
    private lateinit var tvMobile: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_contact_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            populateFromSettings()
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading contact information", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data from cache when returning from change email/mobile fragments
        if (::tvEmail.isInitialized) {
            populateFromSettings()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        btnChangeEmail = view.findViewById(R.id.emailField)
        btnChangeMobile = view.findViewById(R.id.mobileField)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvMobile = view.findViewById(R.id.tvMobile)
    }

    /**
     * Populate email and mobile fields from cached settings data
     */
    private fun populateFromSettings() {
        tvEmail.text = SettingsManager.getEmail() ?: "Not set"
        tvMobile.text = SettingsManager.getPhone() ?: "Not set"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnChangeEmail.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ChangeEmailFragment())
        }

        btnChangeMobile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ChangeMobileFragment())
        }
    }
}
