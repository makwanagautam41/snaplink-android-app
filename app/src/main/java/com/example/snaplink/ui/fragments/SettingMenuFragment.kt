package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity

class SettingMenuFragment : Fragment() {

    private lateinit var btnBackFromSetting: ImageView
    private lateinit var layoutLogout: LinearLayout
    private lateinit var personalDetailsLayout: LinearLayout
    private lateinit var passwordAndSecurity: LinearLayout
    private lateinit var accountVerificationLayout: LinearLayout
    private lateinit var saved: LinearLayout
    private lateinit var notifactions: LinearLayout
    private lateinit var accountPrivacy: LinearLayout
    private lateinit var closeFriends: LinearLayout
    private lateinit var blocked: LinearLayout
    private lateinit var help: LinearLayout
    private lateinit var about: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_setting_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            setupListeners()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews(view: View) {
        btnBackFromSetting = view.findViewById(R.id.btnBackFromSetting)
        layoutLogout = view.findViewById(R.id.layoutLogout)
        personalDetailsLayout = view.findViewById(R.id.personalDetailsLayout)
        passwordAndSecurity = view.findViewById(R.id.passwordAndSecurity)
        accountVerificationLayout = view.findViewById(R.id.accountVerificationLayout)
        saved = view.findViewById(R.id.saved)
        notifactions = view.findViewById(R.id.notifactions)
        accountPrivacy = view.findViewById(R.id.accountPrivacy)
        closeFriends = view.findViewById(R.id.closeFriends)
        blocked = view.findViewById(R.id.blocked)
        help = view.findViewById(R.id.help)
        about = view.findViewById(R.id.about)
    }

    private fun setupListeners() {
        btnBackFromSetting.setOnClickListener { parentFragmentManager.popBackStack() }

        layoutLogout.setOnClickListener { performLogout() }

        personalDetailsLayout.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PersonalDetailsFragment())
        }

        passwordAndSecurity.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(PasswordAndSecurityFragment())
        }

        accountVerificationLayout.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountVerificationFragment())
        }

        saved.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(SavedFragment())
        }

        notifactions.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(NotificationsFragment())
        }

        accountPrivacy.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountPrivacyFragment())
        }

        closeFriends.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(CloseFriendsFragment())
        }

        blocked.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(BlockedFragment())
        }

        help.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(HelpFragment())
        }

        about.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AboutFragment())
        }
    }

    private fun performLogout() {
        ApiClient.clearAuth()
        (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
    }
}
