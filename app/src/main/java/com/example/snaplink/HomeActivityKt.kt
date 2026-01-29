package com.example.snaplink

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.snaplink.network.TokenManager
import de.hdodenhof.circleimageview.CircleImageView

class HomeActivityKt : AppCompatActivity() {

    private lateinit var rvStories: RecyclerView
    private lateinit var rvPosts: RecyclerView
    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navReels: ImageView
    private lateinit var navProfile: CircleImageView
    private lateinit var storyAdapter: StoryAdapterKt
    private lateinit var postAdapter: PostAdapterKt
    private val storyList = mutableListOf<StoryKt>()
    private val postList = mutableListOf<PostKt>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupStories()
        setupPosts()
        setupNavigation()
        loadNavProfileImage()
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
    }

    private fun initViews() {
        rvStories = findViewById(R.id.rvStories)
        rvPosts = findViewById(R.id.rvPosts)
        navHome = findViewById(R.id.navHome)
        navSearch = findViewById(R.id.navSearch)
        navAdd = findViewById(R.id.navAdd)
        navReels = findViewById(R.id.navReels)
        navProfile = findViewById(R.id.navProfile)
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
            // Navigate to search
            android.widget.Toast.makeText(this, "Search coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        navAdd.setOnClickListener {
            // Navigate to add post
            android.widget.Toast.makeText(this, "Add Post coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }
        
        navReels.setOnClickListener {
            // Navigate to reels
            android.widget.Toast.makeText(this, "Reels coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupStories() {
        // Add dummy stories
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

        rvStories.apply {
            layoutManager = LinearLayoutManager(
                this@HomeActivityKt,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            storyAdapter = StoryAdapterKt(storyList)
            adapter = storyAdapter
        }
    }

    private fun setupPosts() {
        // Add dummy posts
        postList.apply {
            add(
                PostKt(
                    username = "__tushill",
                    userAvatar = R.drawable.img_user_post_1,
                    postImage = R.drawable.img_post_1,
                    caption = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor nuntium ex sanior...",
                    timeAgo = "10 minutes ago"
                )
            )

            add(
                PostKt(
                    username = "punit_super",
                    userAvatar = R.drawable.img_user_1,
                    postImage = R.drawable.img_post_placeholder,
                    caption = "Amazing sunset view from my balcony! 🌅",
                    timeAgo = "1 hour ago"
                )
            )

            add(
                PostKt(
                    username = "siko.speed",
                    userAvatar = R.drawable.img_user_2,
                    postImage = R.drawable.img_post_placeholder,
                    caption = "New adventure begins today! Can't wait to share more 🚀",
                    timeAgo = "3 hours ago"
                )
            )

            add(
                PostKt(
                    username = "talvin",
                    userAvatar = R.drawable.img_user_4,
                    postImage = R.drawable.img_post_placeholder,
                    caption = "Coffee and code ☕💻 #developerlife",
                    timeAgo = "5 hours ago"
                )
            )
        }

        rvPosts.apply {
            layoutManager = LinearLayoutManager(this@HomeActivityKt)
            postAdapter = PostAdapterKt(postList)
            adapter = postAdapter
        }
    }
}