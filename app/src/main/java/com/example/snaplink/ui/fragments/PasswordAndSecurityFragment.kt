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

class PasswordAndSecurityFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var changePasswordField: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_password_and_security, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            btnBack = view.findViewById(R.id.btnBack)
            changePasswordField = view.findViewById(R.id.changePasswordField)
            btnBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }

            changePasswordField.setOnClickListener {
                (activity as? MainActivity)?.navigateToFragment(ChangePasswordFragment())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing views", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }
}
