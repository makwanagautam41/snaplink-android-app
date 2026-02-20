package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.ui.activities.MainActivity

class ContactInformationFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var btnChangeEmail: RelativeLayout
    private lateinit var btnChangeMobile: RelativeLayout

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
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error loading contact information", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        btnChangeEmail = view.findViewById(R.id.emailField)
        btnChangeMobile = view.findViewById(R.id.mobileField)
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
