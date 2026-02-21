package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.snaplink.R

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        setupPasswordToggle(view, R.id.etOldPassword, R.id.toggleOldPassword)
        setupPasswordToggle(view, R.id.etNewPassword, R.id.toggleNewPassword)
        setupPasswordToggle(view, R.id.etConfirmPassword, R.id.toggleConfirmPassword)
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