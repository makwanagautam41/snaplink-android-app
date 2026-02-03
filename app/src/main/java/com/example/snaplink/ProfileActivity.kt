package com.example.snaplink

import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.network.UserDetailsResponse
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var ivProfile: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvUsernameTitle: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvBio: TextView
    private lateinit var tvPostsCount: TextView
    private lateinit var tvFollowersCount: TextView
    private lateinit var tvFollowingCount: TextView
    private lateinit var btnBack: ImageView
    private var currentProfileImageUrl: String? = null

    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navReels: ImageView
    private lateinit var navProfile: CircleImageView

    private lateinit var btnEditProfile: Button

    private lateinit var btnSettingMenu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_profile)
        } catch (e: Exception) {
            e.printStackTrace()
            // If layout inflation fails, we can't do much, but at least we log it.
            Toast.makeText(this, "Error loading profile layout", Toast.LENGTH_LONG).show()
            finish()
            return
        }



        try {
            initViews()
            setupNavigation()
            loadNavProfileImage()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing views", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProfile()


        btnBack.setOnClickListener {
            finish()
        }

        ivProfile.setOnClickListener {
            currentProfileImageUrl?.let { url ->
                showFullImageDialog(url)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
    }

    private fun initViews() {
        ivProfile = findViewById(R.id.ivProfile)
        tvName = findViewById(R.id.tvName)
        tvUsernameTitle = findViewById(R.id.tvUsernameTitle)
        tvEmail = findViewById(R.id.tvEmail)
        tvBio = findViewById(R.id.tvBio)
        tvPostsCount = findViewById(R.id.tvPostsCount)
        tvFollowersCount = findViewById(R.id.tvFollowersCount)
        tvFollowingCount = findViewById(R.id.tvFollowingCount)
        btnBack = findViewById(R.id.btnBack)

        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navAdd = findViewById(R.id.navAdd);
        navReels = findViewById(R.id.navReels);
        navProfile = findViewById(R.id.navProfile);

        btnSettingMenu = findViewById(R.id.btnSettingMenu)
        btnEditProfile = findViewById(R.id.btnEditProfile)
    }

    private fun fetchProfile() {
        try {
            ApiClient.api.getUserDetails().enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    if (isDestroyed || isFinishing) return

                    if (response.isSuccessful) {
                        val user = response.body()?.user
                        user?.let {
                            tvName.text = it.name
                            tvUsernameTitle.text = it.username
                            tvEmail.text = it.email
                            tvBio.text = it.bio ?: ""

                            tvPostsCount.text = (it.postCount ?: 0).toString()
                            tvFollowersCount.text = (it.followers?.size ?: 0).toString()
                            tvFollowingCount.text = (it.following?.size ?: 0).toString()

//                            Log.d("API_RESPONSE", "Code: ${response.code()}")
//                            Log.d("API_RESPONSE", "Body: ${response.body()}")
//                            Log.d("API_RESPONSE", "Error: ${response.errorBody()?.string()}")
//
//                            if (response.isSuccessful) {
//                                val user = response.body()?.user
//                                Log.d("API_USER", user.toString())
//                            }

                            // Handle profile image
                            it.profileImg?.let { url ->
                                currentProfileImageUrl = url
                                try {
                                    Glide.with(this@ProfileActivity)
                                        .load(url)
                                        .placeholder(R.drawable.img_current_user)
                                        .circleCrop()
                                        .into(ivProfile)

                                    TokenManager.saveProfileImage(url)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } else if (response.code() == 401) {
                        performLogout()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    if (isDestroyed || isFinishing) return
                    Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performLogout() {
        ApiClient.clearAuth()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
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
            startActivity(intent)
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
            // user on the same page
        }

        btnSettingMenu.setOnClickListener {
            val intent = Intent(this, setting_menu::class.java)
            startActivity(intent)
        }

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun showFullImageDialog(url: String) {
        if (isDestroyed || isFinishing) return
        
        try {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_viewer)
            dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

            val fullImageView = dialog.findViewById<ImageView>(R.id.fullImageView)
            val closeBtn = dialog.findViewById<ImageView>(R.id.btnClose)
            
            if (fullImageView != null) {
                Glide.with(this)
                    .load(url)
                    .into(fullImageView)
            }

            closeBtn?.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error showing image", Toast.LENGTH_SHORT).show()
        }
    }
}