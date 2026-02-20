package com.example.snaplink.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.PostDataHolder
import com.example.snaplink.ProfilePostAdapter
import com.example.snaplink.R
import com.example.snaplink.models.MyPostResponse
import com.example.snaplink.models.Post
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.network.User
import com.example.snaplink.network.UserDetailsResponse
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtherUserProfileFragment : Fragment() {

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

    companion object {
        private const val ARG_USERNAME = "USERNAME"

        fun newInstance(username: String): OtherUserProfileFragment {
            val fragment = OtherUserProfileFragment()
            val args = Bundle()
            args.putString(ARG_USERNAME, username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_other_user_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        username = arguments?.getString(ARG_USERNAME)
        if (username == null) {
            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        initViews(view)
        setupNavigation()
        setupRecyclerView()

        tvUsernameTitle.text = username

        fetchUserProfile()
    }

    private fun initViews(view: View) {
        ivProfile = view.findViewById(R.id.ivProfile)
        tvName = view.findViewById(R.id.tvName)
        tvUsernameTitle = view.findViewById(R.id.tvUsernameTitle)
        tvBio = view.findViewById(R.id.tvBio)
        tvPostsCount = view.findViewById(R.id.tvPostsCount)
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount)
        tvFollowingCount = view.findViewById(R.id.tvFollowingCount)
        btnBack = view.findViewById(R.id.btnBack)
        btnMoreOptions = view.findViewById(R.id.btnMoreOptions)

        btnFollow = view.findViewById(R.id.btnFollow)
        btnMessage = view.findViewById(R.id.btnMessage)
        btnEmail = view.findViewById(R.id.btnEmail)

        layoutPrivateAccount = view.findViewById(R.id.layoutPrivateAccount)
        layoutPublicContent = view.findViewById(R.id.layoutPublicContent)

        navHome = view.findViewById(R.id.navHome)
        navSearch = view.findViewById(R.id.navSearch)
        navAdd = view.findViewById(R.id.navAdd)
        navReels = view.findViewById(R.id.navReels)
        navProfile = view.findViewById(R.id.navProfile)

        rvProfilePosts = view.findViewById(R.id.rvProfilePosts)
        tabGrid = view.findViewById(R.id.tabGrid)
        tabTags = view.findViewById(R.id.tabTags)
        indicatorGrid = view.findViewById(R.id.indicatorGrid)
        indicatorTags = view.findViewById(R.id.indicatorTags)

        btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        btnFollow.setOnClickListener {
            currentUser?.let { user ->
                val isFollowing = user.isFollowing == true
                val isRequested = user.isRequested == true

                when {
                    isFollowing -> unfollowUser(user.username)
                    isRequested -> cancelFollowRequest(user.username)
                    else -> followUser(user.username)
                }
            }
        }

        loadNavProfileImage()
    }

    private fun followUser(username: String) {
        btnFollow.isEnabled = false

        ApiClient.api.followUser(username)
            .enqueue(object : Callback<com.example.snaplink.network.ApiResponse> {
                override fun onResponse(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    response: Response<com.example.snaplink.network.ApiResponse>
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true

                    if (response.isSuccessful && response.body()?.message != null) {
                        val message = response.body()?.message ?: ""
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                        currentUser?.let { user ->
                            val isPrivate = user.profileVisibility == "private"

                            if (isPrivate) {
                                val updatedUser = user.copy(isRequested = true, isFollowing = false)
                                currentUser = updatedUser
                                updateFollowButton(false, true, true)
                            } else {
                                val updatedUser = user.copy(isFollowing = true, isRequested = false)
                                currentUser = updatedUser
                                updateFollowButton(true, false, false)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to follow", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun unfollowUser(username: String) {
        btnFollow.isEnabled = false

        ApiClient.api.unfollowUser(username)
            .enqueue(object : Callback<com.example.snaplink.network.ApiResponse> {
                override fun onResponse(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    response: Response<com.example.snaplink.network.ApiResponse>
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true

                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(
                            requireContext(),
                            response.body()?.message ?: "Unfollowed",
                            Toast.LENGTH_SHORT
                        ).show()

                        currentUser?.let { user ->
                            val updatedUser = user.copy(isFollowing = false, isRequested = false)
                            currentUser = updatedUser
                            val isPrivate = user.profileVisibility == "private"
                            updateFollowButton(false, isPrivate, false)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to unfollow", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun cancelFollowRequest(username: String) {
        btnFollow.isEnabled = false

        ApiClient.api.unfollowUser(username)
            .enqueue(object : Callback<com.example.snaplink.network.ApiResponse> {
                override fun onResponse(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    response: Response<com.example.snaplink.network.ApiResponse>
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true

                    if (response.isSuccessful && response.body()?.message != null) {
                        Toast.makeText(requireContext(), "Request cancelled", Toast.LENGTH_SHORT).show()

                        currentUser?.let { user ->
                            val updatedUser = user.copy(isRequested = false, isFollowing = false)
                            currentUser = updatedUser
                            val isPrivate = user.profileVisibility == "private"
                            updateFollowButton(false, isPrivate, false)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to cancel request", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(
                    call: Call<com.example.snaplink.network.ApiResponse>,
                    t: Throwable
                ) {
                    if (!isAdded) return
                    btnFollow.isEnabled = true
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
            (activity as? MainActivity)?.navigateToFragment(HomeFragment())
        }

        navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
        }
    }

    private fun setupRecyclerView() {
        profilePostAdapter = ProfilePostAdapter(emptyList()) { position ->
            currentPosts?.let { posts ->
                PostDataHolder.posts = posts
                val fragment = PostDetailFragment.newInstance(position)
                (activity as? MainActivity)?.navigateToFragment(fragment)
            }
        }
        rvProfilePosts.layoutManager = GridLayoutManager(requireContext(), 3)
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
                    if (!isAdded) return

                    try {
                        if (response.isSuccessful && response.body() != null) {
                            val body = response.body()!!
                            if (body.success && !body.users.isNullOrEmpty()) {
                                val user = body.users[0]
                                currentUser = user
                                updateUI(user)
                            } else {
                                Toast.makeText(requireContext(), body.message ?: "Failed to load profile", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "Error processing profile data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<com.example.snaplink.network.OtherUserResponse>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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

        val isPrivate = user.profileVisibility == "private"
        val isFollowing = user.isFollowing == true
        val isRequested = user.isRequested == true

        updateFollowButton(isFollowing, isPrivate, isRequested)

        if (isFollowing) {
            fetchUserPosts(user.username)
        } else if (!isPrivate) {
            fetchUserPosts(user.username)
        }
    }

    private fun updateFollowButton(isFollowing: Boolean, isPrivate: Boolean, isRequested: Boolean) {
        when {
            isFollowing -> {
                btnFollow.text = "Following"
                btnFollow.setBackgroundResource(R.drawable.button_outline)
                btnFollow.setTextColor(Color.WHITE)

                btnMessage.visibility = View.VISIBLE
                btnEmail.visibility = View.VISIBLE

                layoutPrivateAccount.visibility = View.GONE
                layoutPublicContent.visibility = View.VISIBLE
            }

            isRequested -> {
                btnFollow.text = "Requested"
                btnFollow.setBackgroundColor(Color.parseColor("#262626"))
                btnFollow.setTextColor(Color.WHITE)

                btnMessage.visibility = View.GONE
                btnEmail.visibility = View.GONE

                layoutPrivateAccount.visibility = View.VISIBLE
                layoutPublicContent.visibility = View.GONE
            }

            else -> {
                btnFollow.text = "Follow"
                btnFollow.setBackgroundResource(R.drawable.button_primary)
                btnFollow.setTextColor(Color.WHITE)

                btnMessage.visibility = View.GONE
                btnEmail.visibility = View.GONE

                if (isPrivate) {
                    layoutPrivateAccount.visibility = View.VISIBLE
                    layoutPublicContent.visibility = View.GONE
                } else {
                    layoutPrivateAccount.visibility = View.GONE
                    layoutPublicContent.visibility = View.VISIBLE
                }
            }
        }
    }

    private var currentPosts: List<Post>? = null

    private fun fetchUserPosts(username: String) {
        ApiClient.api.getUserPosts(username).enqueue(object : Callback<MyPostResponse> {
            override fun onResponse(call: Call<MyPostResponse>, response: Response<MyPostResponse>) {
                if (!isAdded) return

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
