package com.example.snaplink.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.models.UserStoryGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStoriesFragment : Fragment() {

    private lateinit var storyImage: ImageView
    private lateinit var storyVideo: VideoView
    private lateinit var profileImage: ImageView
    private lateinit var tvStoryUsername: TextView
    private lateinit var btnClose: ImageView
    private lateinit var ivPostOptions: ImageView
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
        storyVideo = view.findViewById(R.id.storyVideo)
        profileImage = view.findViewById(R.id.profileImage)
        tvStoryUsername = view.findViewById(R.id.tvStoryUsername)
        btnClose = view.findViewById(R.id.btnClose)
        ivPostOptions = view.findViewById(R.id.ivPostOptions)
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

        ivPostOptions.setOnClickListener {
            showStoryOptions()
        }
    }

    private fun showStoryOptions() {
        val group = storyGroups.getOrNull(currentGroupIndex) ?: return
        val context = requireContext()
        val dialog = BottomSheetDialog(context)
        val currentUsername = TokenManager.getUsername()
        val isMine = group.user.username == currentUsername

        val layoutRes = if (isMine) R.layout.layout_post_options_mine else R.layout.layout_post_options_other
        val view = layoutInflater.inflate(layoutRes, null)

        view.findViewById<View>(R.id.cancelField)?.setOnClickListener {
            dialog.dismiss()
        }

        if (isMine) {
            view.findViewById<View>(R.id.deleteField)?.setOnClickListener {
                dialog.dismiss()
                val currentStory = storyGroups.getOrNull(currentGroupIndex)?.stories?.getOrNull(currentStoryIndex)
                if (currentStory != null) {
                    ApiClient.api.deleteStory(currentStory._id).enqueue(object : Callback<SimpleApiResponse> {
                        override fun onResponse(call: Call<SimpleApiResponse>, response: Response<SimpleApiResponse>) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Story deleted", Toast.LENGTH_SHORT).show()
                                // Remove story from list and navigate
                                val group = storyGroups[currentGroupIndex]
                                val mutableStories = group.stories.toMutableList()
                                mutableStories.removeAt(currentStoryIndex)
                                
                                val newGroup = group.copy(stories = mutableStories)
                                val mutableGroups = storyGroups.toMutableList()
                                mutableGroups[currentGroupIndex] = newGroup
                                
                                if (mutableStories.isEmpty()) {
                                    mutableGroups.removeAt(currentGroupIndex)
                                    storyGroups = mutableGroups
                                    if (storyGroups.isEmpty()) {
                                        parentFragmentManager.popBackStack()
                                    } else {
                                        // Next group
                                        if (currentGroupIndex >= storyGroups.size) currentGroupIndex = storyGroups.size - 1
                                        currentStoryIndex = 0
                                        displayCurrentStory()
                                    }
                                } else {
                                    storyGroups = mutableGroups
                                    if (currentStoryIndex >= mutableStories.size) currentStoryIndex = mutableStories.size - 1
                                    displayCurrentStory()
                                }
                            } else {
                                Toast.makeText(context, "Failed to delete story", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<SimpleApiResponse>, t: Throwable) {
                            Toast.makeText(context, "Error deleting story: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
            }
            view.findViewById<View>(R.id.editField)?.setOnClickListener {
                Toast.makeText(context, "Edit Story clicked", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        } else {
            view.findViewById<View>(R.id.reportField)?.setOnClickListener {
                Toast.makeText(context, "Report Story clicked", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            view.findViewById<View>(R.id.aboutAccountField)?.setOnClickListener {
                Toast.makeText(context, "About account clicked", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.show()
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
            currentStoryIndex++
            displayCurrentStory()
        } else {
            if (currentGroupIndex < storyGroups.size - 1) {
                currentGroupIndex++
                currentStoryIndex = 0
                displayCurrentStory()
            } else {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun navigateToPrevious() {
        if (currentStoryIndex > 0) {
            currentStoryIndex--
            displayCurrentStory()
        } else {
            if (currentGroupIndex > 0) {
                currentGroupIndex--
                val prevGroup = storyGroups[currentGroupIndex]
                currentStoryIndex = prevGroup.stories.size - 1
                displayCurrentStory()
            } else {
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

        setupProgressBars(stories.size, currentStoryIndex)

        Glide.with(this)
            .load(user.profileImg)
            .placeholder(R.drawable.img_current_user)
            .circleCrop()
            .into(profileImage)

        if (currentStory.mediaType == "video") {
            storyImage.visibility = View.GONE
            storyVideo.visibility = View.VISIBLE
            
            storyVideo.setVideoURI(Uri.parse(currentStory.mediaUrl))
            storyVideo.setOnPreparedListener { mp ->
                mp.setVideoScalingMode(android.media.MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                mp.start()
            }
            storyVideo.setOnCompletionListener {
                navigateToNext()
            }
            storyVideo.setOnErrorListener { _, _, _ ->
                Toast.makeText(requireContext(), "Error playing video", Toast.LENGTH_SHORT).show()
                navigateToNext()
                true
            }
        } else {
            storyVideo.stopPlayback()
            storyVideo.visibility = View.GONE
            storyImage.visibility = View.VISIBLE
            
            Glide.with(this)
                .load(currentStory.mediaUrl)
                .placeholder(R.drawable.img_post_placeholder)
                .into(storyImage)
        }
    }

    private fun setupProgressBars(count: Int, currentIndex: Int) {
        layoutProgress.removeAllViews()
        for (i in 0 until count) {
            val v = View(requireContext())
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
            if (i < count - 1) {
                params.marginEnd = 8
            }
            v.layoutParams = params
            
            v.setBackgroundResource(android.R.color.white)
            if (i > currentIndex) {
                v.alpha = 0.3f
            } else {
                v.alpha = 1.0f
            }
            
            layoutProgress.addView(v)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        storyVideo.stopPlayback()
    }
}