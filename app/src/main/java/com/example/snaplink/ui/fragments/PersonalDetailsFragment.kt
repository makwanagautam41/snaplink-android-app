package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.ui.activities.MainActivity

class PersonalDetailsFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var contactInfoField: RelativeLayout
    private lateinit var usernameField: RelativeLayout
    private lateinit var dobField: RelativeLayout
    private lateinit var accountOwnershipField: RelativeLayout

    // TextViews that display data from cached settings
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvUsername: TextView
    private lateinit var tvDob: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_personal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        populateFromSettings()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data from cache when returning from sub-fragments
        populateFromSettings()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        contactInfoField = view.findViewById(R.id.contactInfoField)
        usernameField = view.findViewById(R.id.usernameField)
        dobField = view.findViewById(R.id.dobField)
        accountOwnershipField = view.findViewById(R.id.accountOwnershipField)

        tvEmail = view.findViewById(R.id.tvEmail)
        tvPhone = view.findViewById(R.id.tvPhone)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvDob = view.findViewById(R.id.tvDob)
    }

    /**
     * Populate the UI fields with cached settings data from SettingsManager
     */
    private fun populateFromSettings() {
        val settings = SettingsManager.getSettings() ?: return

        tvEmail.text = settings.profile.email ?: "Not set"
        tvPhone.text = settings.profile.phone ?: "Not set"
        tvUsername.text = settings.profile.username ?: "Not set"
        tvDob.text = settings.profile.dateOfBirth ?: "Not set"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        contactInfoField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ContactInformationFragment())
        }

        usernameField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ChangeUsernameFragment())
        }
        dobField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ChangeDateOfBirth())
        }
        accountOwnershipField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountOwnershipFragment())
        }
    }
}
