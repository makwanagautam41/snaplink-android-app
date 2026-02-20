package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView

class MessagesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHome = view.findViewById<ImageView>(R.id.navHome)
        val navSearch = view.findViewById<ImageView>(R.id.navSearch)
        val navAdd = view.findViewById<ImageView>(R.id.navAdd)
        val navMessage = view.findViewById<ImageView>(R.id.navMessage)
        val navProfile = view.findViewById<CircleImageView>(R.id.navProfile)

        navHome.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(HomeFragment())
        }

        navSearch.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ExploreFragment())
        }

        navAdd.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(CreatePostFragment())
        }

        navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
        }

        // Load nav profile image
        val ctx = context ?: return
        val url = TokenManager.getProfileImage()
        if (!url.isNullOrEmpty()) {
            Glide.with(ctx)
                .load(url)
                .placeholder(R.drawable.img_current_user)
                .into(navProfile)
        }
    }
}
