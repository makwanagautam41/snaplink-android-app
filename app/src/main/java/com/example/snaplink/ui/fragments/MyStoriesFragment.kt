package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.models.UserStoryGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity

class MyStoriesFragment : Fragment() {

    private lateinit var storyImage: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var tvStoryUsername: TextView
    private lateinit var btnClose: ImageView
    private lateinit var layoutProgress: LinearLayout
    private lateinit var viewPrevious: View
    private lateinit var viewNext: View
    
    private var storyGroups: List<UserStoryGroup> = emptyList()
    private var currentGroupIndex = 0
    private var currentStoryIndex = 0

    companion object {
        private const val ARG_STORY_GROUPS = "story_groups"
        private const val ARG_START_INDEX = "start_index"

        fun newInstance(storyGroups: List<UserStoryGroup>, startIndex: Int): MyStoriesFragment {
            val fragment = MyStoriesFragment()
            val args = Bundle()
            args.putString(ARG_STORY_GROUPS, Gson().toJson(storyGroups))
            args.putInt(ARG_START_INDEX, startIndex)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val json = it.getString(ARG_STORY_GROUPS)
            if (json != null) {
                val type = object : TypeToken<List<UserStoryGroup>>() {}.type
                storyGroups = Gson().fromJson(json, type)
            }
            currentGroupIndex = it.getInt(ARG_START_INDEX, 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_my_stories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initViews(view)
        setupNavigation()
        displayCurrentStory()
    }

    private fun initViews(view: View) {
        storyImage = view.findViewById(R.id.storyImage)
        profileImage = view.findViewById(R.id.profileImage)
        tvStoryUsername = view.findViewById(R.id.tvStoryUsername)
        btnClose = view.findViewById(R.id.btnClose)
        layoutProgress = view.findViewById(R.id.layoutProgress)
        viewPrevious = view.findViewById(R.id.viewPrevious)
        viewNext = view.findViewById(R.id.viewNext)
        
        btnClose.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val onProfileClick = View.OnClickListener {
            val group = storyGroups.getOrNull(currentGroupIndex) ?: return@OnClickListener
            val username = group.user.username
            val currentUsername = TokenManager.getUsername()

            val fragment = if (username == currentUsername) {
                ProfileFragment()
            } else {
                OtherUserProfileFragment.newInstance(username)
            }
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }

        profileImage.setOnClickListener(onProfileClick)
        tvStoryUsername.setOnClickListener(onProfileClick)
    }

    private fun setupNavigation() {
        viewNext.setOnClickListener {
            navigateToNext()
        }

        viewPrevious.setOnClickListener {
            navigateToPrevious()
        }
    }

    private fun navigateToNext() {
        val currentGroup = storyGroups.getOrNull(currentGroupIndex) ?: return
        if (currentStoryIndex < currentGroup.stories.size - 1) {
            // Next story in same group
            currentStoryIndex++
            displayCurrentStory()
        } else {
            // Next user group
            if (currentGroupIndex < storyGroups.size - 1) {
                currentGroupIndex++
                currentStoryIndex = 0
                displayCurrentStory()
            } else {
                // End of all stories
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun navigateToPrevious() {
        if (currentStoryIndex > 0) {
            // Previous story in same group
            currentStoryIndex--
            displayCurrentStory()
        } else {
            // Previous user group
            if (currentGroupIndex > 0) {
                currentGroupIndex--
                val prevGroup = storyGroups[currentGroupIndex]
                currentStoryIndex = prevGroup.stories.size - 1
                displayCurrentStory()
            } else {
                // First story of first user, restart it
                currentStoryIndex = 0
                displayCurrentStory()
            }
        }
    }

    private fun displayCurrentStory() {
        val group = storyGroups.getOrNull(currentGroupIndex) ?: return
        val user = group.user
        val stories = group.stories
        
        if (stories.isEmpty()) {
            navigateToNext()
            return
        }

        val currentStory = stories[currentStoryIndex]
        
        tvStoryUsername.text = user.username

        // Update Progress Bars
        setupProgressBars(stories.size, currentStoryIndex)

        Glide.with(this)
            .load(user.profileImg)
            .placeholder(R.drawable.img_current_user)
            .circleCrop()
            .into(profileImage)

        Glide.with(this)
            .load(currentStory.mediaUrl)
            .placeholder(R.drawable.img_post_placeholder)
            .into(storyImage)
    }

    private fun setupProgressBars(count: Int, currentIndex: Int) {
        layoutProgress.removeAllViews()
        for (i in 0 until count) {
            val v = View(requireContext())
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            if (i < count - 1) {
                params.marginEnd = 8 // Space between bars
            }
            v.layoutParams = params
            
            // All bars are white, but upcoming ones are transparent
            v.setBackgroundResource(android.R.color.white)
            if (i > currentIndex) {
                v.alpha = 0.3f
            } else {
                v.alpha = 1.0f
            }
            
            layoutProgress.addView(v)
        }
    }
}