package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.ui.activities.MainActivity

class PersonalDetailsFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var contactInfoField: RelativeLayout
    private lateinit var usernameField: RelativeLayout
    private lateinit var dobField: RelativeLayout
    private lateinit var accountOwnershipField: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_personal_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.btnBack)
        contactInfoField = view.findViewById(R.id.contactInfoField)
        usernameField = view.findViewById(R.id.usernameField)
        dobField = view.findViewById(R.id.dobField)
        accountOwnershipField = view.findViewById(R.id.accountOwnershipField)

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
