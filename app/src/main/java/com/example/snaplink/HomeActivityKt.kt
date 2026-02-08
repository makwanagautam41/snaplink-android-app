package com.example.snaplink

import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.models.Post
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivityKt : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupStories()
        setupFeed()
        setupNavigation()
        loadNavProfileImage()
        
        // Load feed data from API
        loadFeedOnce()
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
    }

    private fun initViews() {
        rvFeed = findViewById(R.id.rvFeed)
        navHome = findViewById(R.id.navHome)
        navSearch = findViewById(R.id.navSearch)
        navAdd = findViewById(R.id.navAdd)
        navMessage = findViewById(R.id.navMessage)
        navProfile = findViewById(R.id.navProfile)
        btnNotification = findViewById(R.id.btnNotification)
    }

    private fun loadNavProfileImage() {
        val url = TokenManager.getProfileImage()
        if (!url.isNullOrEmpty()) {
            Glide.with(this)
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
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }
        
        navAdd.setOnClickListener {
            startActivity(Intent(this, CreatePostActivity::class.java))
        }

        navMessage.setOnClickListener {
            Toast.makeText(this, "Message coming soon", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnNotification.setOnClickListener {
            val intent = Intent(this, notifications::class.java)
            startActivity(intent)
        }
    }

    private fun setupStories() {
        // Keep stories as mock data for now
        storyList.apply {
            add(StoryKt("Your Story", R.drawable.img_current_user, true))
            add(StoryKt("punit_super", R.drawable.img_user_1, false))
            add(StoryKt("siko.speed", R.drawable.img_user_2, false))
            add(StoryKt("galish...", R.drawable.img_user_3, false))
            add(StoryKt("talvin", R.drawable.img_user_4, false))
            add(StoryKt("john_doe", R.drawable.img_user_placeholder, false))
            add(StoryKt("jane_smith", R.drawable.img_user_placeholder, false))
            add(StoryKt("mike_ross", R.drawable.img_user_placeholder, false))
        }
    }

    private fun setupFeed() {
        rvFeed.layoutManager = LinearLayoutManager(this)
        feedAdapter = FeedAdapter(postList, storyList, true) { username ->
             if (username != "current_user_username_placeholder") { // Check if it's not me? (Optional)
                 val intent = Intent(this, OtherUserProfileActivity::class.java)
                 intent.putExtra("USERNAME", username)
                 startActivity(intent)
             }
        }
        rvFeed.adapter = feedAdapter
    }

    private fun loadFeedOnce() {
        ApiClient.api.getFeedPosts().enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    postList.clear()
                    postList.addAll(response.body()!!.posts)
                    feedAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@HomeActivityKt,
                        "Failed to load feed: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                Toast.makeText(
                    this@HomeActivityKt,
                    "Failed to load feed: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
