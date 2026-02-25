package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.FeedAdapter
import com.example.snaplink.R
import com.example.snaplink.StoryKt
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.models.Post
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var rvFeed: RecyclerView
    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navMessage: ImageView
    private lateinit var navProfile: CircleImageView
    private lateinit var feedAdapter: FeedAdapter

    private lateinit var btnNotification: ImageView

    private val storyList = mutableListOf<StoryKt>()
    private val postList = mutableListOf<Post>()

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            uploadStory(uri)
        }
    }

    private fun uploadStory(uri: android.net.Uri) {
        val context = requireContext()
        val contentResolver = context.contentResolver
        
        // Create temporary file to upload
        val fileDescriptor = contentResolver.openFileDescriptor(uri, "r") ?: return
        val inputStream = java.io.FileInputStream(fileDescriptor.fileDescriptor)
        val file = File(context.cacheDir, "story_upload_${System.currentTimeMillis()}")
        val outputStream = FileOutputStream(file)
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = file.asRequestBody(contentResolver.getType(uri)?.toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("media", file.name, requestFile)
        val caption = "New Story".toRequestBody("text/plain".toMediaTypeOrNull())

        Toast.makeText(context, "Uploading story...", Toast.LENGTH_SHORT).show()

        ApiClient.api.uploadStory(body, caption).enqueue(object : Callback<com.example.snaplink.network.StoryResponse> {
            override fun onResponse(call: Call<com.example.snaplink.network.StoryResponse>, response: Response<com.example.snaplink.network.StoryResponse>) {
                if (!isAdded) return
                if (response.isSuccessful) {
                    Toast.makeText(context, "Story uploaded successfully!", Toast.LENGTH_SHORT).show()
                    loadFeedOnce() // Refresh feed to potentially show new story
                } else {
                    Toast.makeText(context, "Failed to upload story", Toast.LENGTH_SHORT).show()
                }
                file.delete()
            }

            override fun onFailure(call: Call<com.example.snaplink.network.StoryResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(context, "Error uploading story: ${t.message}", Toast.LENGTH_SHORT).show()
                file.delete()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupStories()
        setupFeed()
        setupNavigation()
        loadNavProfileImage()

        // Load feed data from API
        loadFeedOnce()
        loadStories()
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
        setupStories()
        loadStories()
        feedAdapter.notifyDataSetChanged()
    }

    private fun initViews(view: View) {
        rvFeed = view.findViewById(R.id.rvFeed)
        navHome = view.findViewById(R.id.navHome)
        navSearch = view.findViewById(R.id.navSearch)
        navAdd = view.findViewById(R.id.navAdd)
        navMessage = view.findViewById(R.id.navMessage)
        navProfile = view.findViewById(R.id.navProfile)
        btnNotification = view.findViewById(R.id.btnNotification)
    }

    private fun loadNavProfileImage() {
        val ctx = context ?: return
        val url = TokenManager.getProfileImage()
        if (!url.isNullOrEmpty()) {
            Glide.with(ctx)
                .load(url)
                .placeholder(R.drawable.img_current_user)
                .into(navProfile)
        }
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            // Already on home
        }

        navSearch.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ExploreFragment())
        }

        navAdd.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(CreatePostFragment())
        }

        navMessage.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(MessagesFragment())
        }

        navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
        }

        btnNotification.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(NotificationsFragment())
        }
    }

    private fun setupStories() {
        val profileUrl = TokenManager.getProfileImage()

        storyList.clear()

        // Your Story (Dynamic profile image)
        storyList.add(
            StoryKt(
                username = "Your Story",
                imageUrl = profileUrl,
                avatarResource = null,
                isYourStory = true
            )
        )
    }

    private fun setupFeed() {
        rvFeed.layoutManager = LinearLayoutManager(requireContext())
        feedAdapter = FeedAdapter(
            postList,
            storyList,
            true,
            onCommentClick = { postId ->
                val fragment = ViewCommentsFragment.newInstance(postId)
                (activity as? MainActivity)?.navigateToFragment(fragment)
            },
            onAddStoryClick = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
            },
            onStoryClick = { story ->
                val allStoryGroups = storyList.mapNotNull { it.storyGroup }
                val currentGroupIndex = allStoryGroups.indexOf(story.storyGroup)
                
                if (currentGroupIndex != -1) {
                    val fragment = MyStoriesFragment.newInstance(allStoryGroups, currentGroupIndex)
                    (activity as? MainActivity)?.navigateToFragment(fragment)
                } else {
                    if (story.isYourStory) {
                        Toast.makeText(context, "No stories yet", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) { username ->
            if (username != "current_user_username_placeholder") {
                val fragment = OtherUserProfileFragment.newInstance(username)
                (activity as? MainActivity)?.navigateToFragment(fragment)
            }
        }
        rvFeed.adapter = feedAdapter
    }

    private fun loadStories() {
        // 1. Load My Story
        ApiClient.api.getMyStories().enqueue(object : Callback<com.example.snaplink.models.UserStoryResponse> {
            override fun onResponse(call: Call<com.example.snaplink.models.UserStoryResponse>, response: Response<com.example.snaplink.models.UserStoryResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val groups = response.body()?.stories ?: emptyList()
                    if (groups.isNotEmpty()) {
                        val myGroup = groups[0]
                        updateMyStoryInList(myGroup)
                    }
                }
                // 2. Load Other Stories (nested to ensure order or just parallel)
                loadOtherStories()
            }
            override fun onFailure(call: Call<com.example.snaplink.models.UserStoryResponse>, t: Throwable) {
                loadOtherStories()
            }
        })
    }

    private fun loadOtherStories() {
        ApiClient.api.getOtherStories().enqueue(object : Callback<com.example.snaplink.models.UserStoryResponse> {
            override fun onResponse(call: Call<com.example.snaplink.models.UserStoryResponse>, response: Response<com.example.snaplink.models.UserStoryResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val otherGroups = response.body()?.stories ?: emptyList()
                    updateOtherStoriesInList(otherGroups)
                }
            }
            override fun onFailure(call: Call<com.example.snaplink.models.UserStoryResponse>, t: Throwable) {
                // Ignore failure for stories
            }
        })
    }

    private fun updateMyStoryInList(group: com.example.snaplink.models.UserStoryGroup) {
        val index = storyList.indexOfFirst { it.isYourStory }
        if (index != -1) {
            val old = storyList[index]
            storyList[index] = old.copy(storyGroup = group)
            feedAdapter.notifyItemChanged(0)
        }
    }

    private fun updateOtherStoriesInList(groups: List<com.example.snaplink.models.UserStoryGroup>) {
        // Keep "Your Story" as first
        val yourStory = if (storyList.isNotEmpty()) storyList[0] else null
        storyList.clear()
        if (yourStory != null) storyList.add(yourStory)
        
        groups.forEach { group ->
            storyList.add(
                StoryKt(
                    username = group.user.username,
                    imageUrl = group.user.profileImg,
                    storyGroup = group
                )
            )
        }
        feedAdapter.notifyItemChanged(0)
    }

    private fun loadFeedOnce() {
        ApiClient.api.getFeedPosts().enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (!isAdded) return
                if (response.isSuccessful && response.body()?.success == true) {
                    postList.clear()
                    postList.addAll(response.body()!!.posts)
                    feedAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load feed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(
                    requireContext(),
                    "Failed to load feed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
