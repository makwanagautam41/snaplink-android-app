package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.snaplink.R
import com.example.snaplink.models.SettingsResponse
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingMenuFragment : Fragment() {

    private lateinit var btnBackFromSetting: ImageView
    private lateinit var layoutLogout: LinearLayout
    private lateinit var personalDetailsLayout: LinearLayout
    private lateinit var passwordAndSecurity: LinearLayout
    private lateinit var accountStatusLayout: LinearLayout
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
            fetchUserSettings()
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
        accountStatusLayout = view.findViewById(R.id.accountStatusLayout)
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

        accountStatusLayout.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(AccountStatusFragment())
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

    /**
     * Fetches all user settings from /users/settings and caches them in SettingsManager.
     * This is called every time the settings page is opened.
     */
    private fun fetchUserSettings() {
        ApiClient.api.getUserSettings().enqueue(object : Callback<SettingsResponse> {
            override fun onResponse(call: Call<SettingsResponse>, response: Response<SettingsResponse>) {
                if (!isAdded) return

                if (response.isSuccessful && response.body()?.success == true) {
                    val settings = response.body()!!.settings
                    SettingsManager.setSettings(settings)
                    Log.d("SettingMenuFragment", "Settings fetched and cached successfully")
                } else {
                    Log.e("SettingMenuFragment", "Failed to fetch settings: ${response.code()}")
                    Toast.makeText(requireContext(), "Failed to load settings", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SettingsResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("SettingMenuFragment", "Error fetching settings", t)
                Toast.makeText(requireContext(), "Network error loading settings", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun performLogout() {
        SettingsManager.clearSettings()
        ApiClient.clearAuth()
        (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
    }
}
