package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.snaplink.R

class ForgotPasswordNewPasswordFragment :
    Fragment(R.layout.fragment_forgot_password_new_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Back button
        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Password toggle
        setupPasswordToggle(
            view,
            R.id.etNewPassword,
            R.id.toggleResetPassword
        )
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

            // Keep cursor at end
            editText.setSelection(editText.text.length)
        }
    }
}