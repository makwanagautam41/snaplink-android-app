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

class AccountOwnershipFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var accountDeactivationField: RelativeLayout
    private lateinit var accountDeletionField: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account_ownership, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack = view.findViewById(R.id.btnBack)
        accountDeactivationField = view.findViewById(R.id.accountDeactivationField)
        accountDeletionField = view.findViewById(R.id.accountDeletionField)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        accountDeactivationField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountDeactivationFragment())
        }
        accountDeletionField.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountDeletionFragment())
        }
    }
}
