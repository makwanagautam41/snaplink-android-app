package com.example.snaplink

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PostDetailActivity : AppCompatActivity() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var adapter: FeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val startPosition = intent.getIntExtra("EXTRA_POSITION", 0)
        val posts = PostDataHolder.posts

        initViews()
        setupRecyclerView(posts, startPosition)
        
        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun initViews() {
        rvPosts = findViewById(R.id.rvPosts)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView(posts: List<com.example.snaplink.models.Post>, startPosition: Int) {
        // Reuse FeedAdapter but passing empty list for stories and disabling stories header
        adapter = FeedAdapter(posts, emptyList(), showStories = false) { username ->
            val intent = android.content.Intent(this, OtherUserProfileActivity::class.java)
            intent.putExtra("USERNAME", username)
            startActivity(intent)
        }
        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = adapter
        
        // Scroll to the clicked position
        // Since showStories is false, the adapter position maps 1:1 to the posts list index
        rvPosts.scrollToPosition(startPosition)
    }
}
