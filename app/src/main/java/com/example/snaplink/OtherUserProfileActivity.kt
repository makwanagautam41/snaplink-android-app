package com.example.snaplink

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.network.UserDetailsResponse
import com.example.snaplink.network.User
import com.example.snaplink.models.MyPostResponse
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUserProfileActivity : AppCompatActivity() {

    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvUsernameTitle: TextView
    private lateinit var tvBio: TextView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvFollowersCount: TextView
    private lateinit var tvFollowingCount: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnMoreOptions: ImageView

    private lateinit var btnFollow: Button
    private lateinit var btnMessage: Button
    private lateinit var btnEmail: Button

    private lateinit var layoutPrivateAccount: LinearLayout
    private lateinit var layoutPublicContent: LinearLayout

    // Bottom Nav
    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navReels: ImageView
    private lateinit var navProfile: CircleImageView

    // Tabs & Grid
    private lateinit var rvProfilePosts: RecyclerView
    private lateinit var profilePostAdapter: ProfilePostAdapter
    private lateinit var tabGrid: ImageView
    private lateinit var tabTags: ImageView
    private lateinit var indicatorGrid: View
    private lateinit var indicatorTags: View

    private var username: String? = null
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_user_profile)

        username = intent.getStringExtra("USERNAME")
        if (username == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        initViews()
        setupNavigation()
        setupRecyclerView()
        
        // Initial state loading
        tvUsernameTitle.text = username

        fetchUserProfile()
    }

    private fun initViews() {
        ivProfile = findViewById(R.id.ivProfile)
        tvName = findViewById(R.id.tvName)
        tvUsernameTitle = findViewById(R.id.tvUsernameTitle)
        tvBio = findViewById(R.id.tvBio)
        tvPostsCount = findViewById(R.id.tvPostsCount)
        tvFollowersCount = findViewById(R.id.tvFollowersCount)
        tvFollowingCount = findViewById(R.id.tvFollowingCount)
        btnBack = findViewById(R.id.btnBack)
        btnMoreOptions = findViewById(R.id.btnMoreOptions)

        btnFollow = findViewById(R.id.btnFollow)
        btnMessage = findViewById(R.id.btnMessage)
        btnEmail = findViewById(R.id.btnEmail)

        layoutPrivateAccount = findViewById(R.id.layoutPrivateAccount)
        layoutPublicContent = findViewById(R.id.layoutPublicContent)

        navHome = findViewById(R.id.navHome)
        navSearch = findViewById(R.id.navSearch)
        navAdd = findViewById(R.id.navAdd)
        navReels = findViewById(R.id.navReels)
        navProfile = findViewById(R.id.navProfile)

        rvProfilePosts = findViewById(R.id.rvProfilePosts)
        tabGrid = findViewById(R.id.tabGrid)
        tabTags = findViewById(R.id.tabTags)
        indicatorGrid = findViewById(R.id.indicatorGrid)
        indicatorTags = findViewById(R.id.indicatorTags)

        btnBack.setOnClickListener { finish() }
        
        btnFollow.setOnClickListener {
             // Toggle follow state logic here (simulated)
             currentUser?.let { user ->
                 val isFollowing = user.isFollowing == true
                 // In a real app, call API here. 
                 // For now, simple UI toggle simulation would need mutable user or local var
                 Toast.makeText(this, if (isFollowing) "Unfollowed" else "Followed", Toast.LENGTH_SHORT).show()
             }
        }
        
        loadNavProfileImage()
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
            val intent = Intent(this, HomeActivityKt::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        
        // Other nav items...
    }

    private fun setupRecyclerView() {
        profilePostAdapter = ProfilePostAdapter(emptyList()) { position ->
             currentPosts?.let { posts ->
                  PostDataHolder.posts = posts
                  val intent = Intent(this, PostDetailActivity::class.java)
                  intent.putExtra("EXTRA_POSITION", position)
                  startActivity(intent)
             }
        }
        rvProfilePosts.layoutManager = GridLayoutManager(this, 3)
        rvProfilePosts.adapter = profilePostAdapter
        
        tabGrid.setOnClickListener { updateTabSelection(true) }
        tabTags.setOnClickListener { updateTabSelection(false) }
    }

    private fun updateTabSelection(isGrid: Boolean) {
        if (isGrid) {
            tabGrid.setColorFilter(Color.WHITE)
            tabTags.setColorFilter(Color.parseColor("#666666"))
            indicatorGrid.setBackgroundColor(Color.WHITE)
            indicatorTags.setBackgroundColor(Color.TRANSPARENT)
            rvProfilePosts.visibility = View.VISIBLE
        } else {
            tabGrid.setColorFilter(Color.parseColor("#666666"))
            tabTags.setColorFilter(Color.WHITE)
            indicatorGrid.setBackgroundColor(Color.TRANSPARENT)
            indicatorTags.setBackgroundColor(Color.WHITE)
            rvProfilePosts.visibility = View.GONE 
        }
    }

    private fun fetchUserProfile() {
        username?.let { user ->
            ApiClient.api.getOtherUserProfile(user).enqueue(object : Callback<com.example.snaplink.network.OtherUserResponse> {
                override fun onResponse(call: Call<com.example.snaplink.network.OtherUserResponse>, response: Response<com.example.snaplink.network.OtherUserResponse>) {
                    if (isDestroyed || isFinishing) return
                    
                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val body = response.body()!!
                            if (body.success && !body.users.isNullOrEmpty()) {
                                val user = body.users[0]
                                currentUser = user
                                updateUI(user)
                            } else {
                                Toast.makeText(this@OtherUserProfileActivity, body.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // ... error handling
                            Toast.makeText(this@OtherUserProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@OtherUserProfileActivity, "Error processing profile data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.example.snaplink.network.OtherUserResponse>, t: Throwable) {
                    if (isDestroyed || isFinishing) return
                    Toast.makeText(this@OtherUserProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateUI(user: User) {
        tvName.text = user.name
        tvUsernameTitle.text = user.username
        tvBio.text = user.bio ?: ""
        tvPostsCount.text = (user.postCount ?: 0).toString()
        tvFollowersCount.text = (user.followers?.size ?: 0).toString()
        tvFollowingCount.text = (user.following?.size ?: 0).toString()

        try {
            Glide.with(this)
                .load(user.profileImg)
                .placeholder(R.drawable.img_current_user)
                .circleCrop()
                .into(ivProfile)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Check legacy isPrivate/isFollowing first, fallback to new profileVisibility 
        // But the new API uses profileVisibility: "public" or "private"
        val isPrivate = user.profileVisibility == "private"
        // isFollowing field might not be in the new user object if it just returns raw user data
        // For now, assuming isFollowing logic needs to be handled if available or defaulted
        val isFollowing = user.isFollowing == true

        if (isFollowing) {
            btnFollow.text = "Following"
            btnFollow.setBackgroundColor(Color.parseColor("#262626")) // Grey
            btnFollow.setTextColor(Color.WHITE)
            
            btnMessage.visibility = View.VISIBLE
            btnEmail.visibility = View.VISIBLE
            
            layoutPrivateAccount.visibility = View.GONE
            layoutPublicContent.visibility = View.VISIBLE
            
            fetchUserPosts(user.username)
             
        } else {
             btnFollow.text = "Follow"
             btnFollow.setBackgroundColor(Color.parseColor("#0095F6")) // Blue
             btnFollow.setTextColor(Color.WHITE)
             
             btnMessage.visibility = View.GONE
             btnEmail.visibility = View.GONE
             
             if (isPrivate) {
                 layoutPrivateAccount.visibility = View.VISIBLE
                 layoutPublicContent.visibility = View.GONE
             } else {
                 layoutPrivateAccount.visibility = View.GONE
                 layoutPublicContent.visibility = View.VISIBLE
                 fetchUserPosts(user.username)
             }
        }
    }
    
    private var currentPosts: List<com.example.snaplink.models.Post>? = null

    private fun fetchUserPosts(username: String) {
        ApiClient.api.getUserPosts(username).enqueue(object : Callback<MyPostResponse> {
            override fun onResponse(call: Call<MyPostResponse>, response: Response<MyPostResponse>) {
                if (isDestroyed || isFinishing) return
                
                try {
                    if (response.isSuccessful && response.body() != null) {
                        val body = response.body()!!
                        if (body.success) {
                            val posts = body.posts
                            if (posts != null) {
                                currentPosts = posts
                                profilePostAdapter.updatePosts(posts)
                                tvPostsCount.text = posts.size.toString()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<MyPostResponse>, t: Throwable) {
                // Fail silently
            }
        })
    }
}
