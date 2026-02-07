package com.example.snaplink

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreActivity : AppCompatActivity() {

    private lateinit var searchBarContainer: LinearLayout
    private lateinit var rvExplore: RecyclerView
    private lateinit var exploreAdapter: ProfilePostAdapter

    // Bottom Nav
    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navMessage: ImageView
    private lateinit var navProfile: CircleImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        initViews()
        setupNavigation()
        setupRecyclerView()
        loadExplorePosts()
        loadNavProfileImage()
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
        // Highlight logic could go here if icons update
    }

    private fun initViews() {
        searchBarContainer = findViewById(R.id.searchBarContainer)
        rvExplore = findViewById(R.id.rvExplore)

        navHome = findViewById(R.id.navHome)
        navSearch = findViewById(R.id.navSearch)
        navAdd = findViewById(R.id.navAdd)
        navMessage = findViewById(R.id.navMessage)
        navProfile = findViewById(R.id.navProfile)

        searchBarContainer.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        rvExplore.layoutManager = GridLayoutManager(this, 3)
        exploreAdapter = ProfilePostAdapter(emptyList()) { position ->
            // Handle post click if needed, maybe open post detail?
            // For now, placeholder behavior
        }
        rvExplore.adapter = exploreAdapter
    }

    private fun loadExplorePosts() {
        // Using getFeedPosts as placeholder
        ApiClient.api.getFeedPosts().enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.posts?.let { posts ->
                        exploreAdapter.updatePosts(posts)
                    }
                }
            }

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                // Fail silently or toast
            }
        })
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivityKt::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        navSearch.setOnClickListener {
            // Already here
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
}
