package com.example.snaplink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class UsersAboutSection : Fragment() {

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_PROFILE_IMG = "profile_img"
        private const val ARG_CREATED_AT = "created_at"

        /**
         * Creates a new instance of [UsersAboutSection] with the given user data.
         *
         * @param username  The account's username to display.
         * @param profileImg URL of the account's profile image (may be null/empty).
         * @param createdAt ISO-8601 timestamp string of when the account was created.
         */
        fun newInstance(
            username: String,
            profileImg: String?,
            createdAt: String?
        ): UsersAboutSection {
            return UsersAboutSection().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                    putString(ARG_PROFILE_IMG, profileImg)
                    putString(ARG_CREATED_AT, createdAt)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_users_about_section, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username   = arguments?.getString(ARG_USERNAME)   ?: ""
        val profileImg = arguments?.getString(ARG_PROFILE_IMG)
        val createdAt  = arguments?.getString(ARG_CREATED_AT)

        // ── Back button ──────────────────────────────────────────────────────
        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            (activity as? MainActivity)?.onBackPressed()
        }

        // ── Profile image ────────────────────────────────────────────────────
        val imgProfile = view.findViewById<CircleImageView>(R.id.imgProfile)
        if (!profileImg.isNullOrEmpty()) {
            Glide.with(this)
                .load(profileImg)
                .placeholder(R.drawable.img_current_user)
                .circleCrop()
                .into(imgProfile)
        } else {
            imgProfile.setImageResource(R.drawable.img_current_user)
        }

        // ── Username ─────────────────────────────────────────────────────────
        view.findViewById<TextView>(R.id.tvUsername).text = username

        // ── Date joined ──────────────────────────────────────────────────────
        val tvDateJoined = view.findViewById<TextView>(R.id.tvDateJoined)
        tvDateJoined.text = formatJoinDate(createdAt)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Converts an ISO-8601 UTC string (e.g. "2024-06-15T09:30:00.000Z")
     * into a human-readable "Month Year" string (e.g. "June 2024").
     * Returns "Unknown" if the string is null or unparseable.
     */
    private fun formatJoinDate(createdAt: String?): String {
        if (createdAt.isNullOrBlank()) return "Unknown"
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(createdAt) ?: return "Unknown"
            val out  = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
            out.format(date)
        } catch (e: Exception) {
            "Unknown"
        }
    }
}