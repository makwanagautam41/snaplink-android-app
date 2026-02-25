package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.models.UserStoryGroup
import com.google.gson.Gson

class MyStoriesFragment : Fragment() {

    private lateinit var storyImage: ImageView
    private lateinit var profileImage: ImageView
    private lateinit var tvStoryUsername: TextView
    private lateinit var btnClose: ImageView
    
    private var storyGroup: UserStoryGroup? = null

    companion object {
        private const val ARG_STORY_GROUP = "story_group"

        fun newInstance(storyGroup: UserStoryGroup): MyStoriesFragment {
            val fragment = MyStoriesFragment()
            val args = Bundle()
            args.putString(ARG_STORY_GROUP, Gson().toJson(storyGroup))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(ARG_STORY_GROUP)?.let {
            storyGroup = Gson().fromJson(it, UserStoryGroup::class.java)
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
        displayStory()
    }

    private fun initViews(view: View) {
        storyImage = view.findViewById(R.id.storyImage)
        profileImage = view.findViewById(R.id.profileImage)
        tvStoryUsername = view.findViewById(R.id.tvStoryUsername)
        btnClose = view.findViewById(R.id.btnClose)
        
        btnClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun displayStory() {
        val group = storyGroup ?: return
        val user = group.user
        val stories = group.stories
        
        if (stories.isEmpty()) return
        
        // Find the most recent story (last in the list based on the response structure)
        val currentStory = stories.last() 
        
        tvStoryUsername.text = user.username

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
}