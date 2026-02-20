package com.example.snaplink.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.snaplink.R
import com.example.snaplink.ui.fragments.HomeFragment
import com.example.snaplink.ui.fragments.LoginFragment

import com.example.snaplink.network.TokenManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            // Determine starting fragment based on auth state
            val startFragment: Fragment = if (TokenManager.isLoggedIn()) {
                HomeFragment()
            } else {
                LoginFragment()
            }
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container, startFragment)
            }
        }
    }

    /**
     * Navigate to a fragment, optionally adding to backstack.
     * This is the primary navigation mechanism for all screens.
     */
    fun navigateToFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        tag: String? = null
    ) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
        }
    }

    /**
     * Navigate to a fragment and clear the entire backstack.
     * Used for login/logout transitions.
     */
    fun navigateWithClearStack(fragment: Fragment) {
        // Clear all backstack entries
        val fm = supportFragmentManager
        for (i in 0 until fm.backStackEntryCount) {
            fm.popBackStackImmediate()
        }
        fm.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment_container, fragment)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}
