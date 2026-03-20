package com.example.snaplink.ui.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.snaplink.network.ImageUpdateResponse
import com.example.snaplink.network.TokenManager
import com.example.snaplink.network.UserDetailsResponse
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ProfileFragment : Fragment() {

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

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadProfileImage(it)
        }
    }

    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navMessage: ImageView
    private lateinit var navProfile: CircleImageView

    private lateinit var btnEditProfile: Button

    // Tabs & Grid
    private lateinit var rvProfilePosts: RecyclerView
    private lateinit var profilePostAdapter: ProfilePostAdapter
    private lateinit var tabGrid: ImageView
    private lateinit var tabTags: ImageView
    private lateinit var indicatorGrid: View
    private lateinit var indicatorTags: View
    private lateinit var btnSettingMenu: ImageView
    private lateinit var followersLayoutBtn: LinearLayout
    private lateinit var followingLayoutBtn: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            setupNavigation()
            setupRecyclerView()
            loadNavProfileImage()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing views", Toast.LENGTH_SHORT).show()
            return
        }

        fetchProfile()
        fetchMyPosts()

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        ivProfile.setOnClickListener {
            currentProfileImageUrl?.let { url ->
                showFullImageDialog(url)
            }
        }

        ivProfile.setOnLongClickListener {
            showProfileOptionsDialog()
            true
        }
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
        fetchMyPosts()
    }

    private fun initViews(view: View) {
        ivProfile = view.findViewById(R.id.ivProfile)
        tvName = view.findViewById(R.id.tvName)
        tvUsernameTitle = view.findViewById(R.id.tvUsernameTitle)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvBio = view.findViewById(R.id.tvBio)
        tvPostsCount = view.findViewById(R.id.tvPostsCount)
        tvFollowersCount = view.findViewById(R.id.tvFollowersCount)
        tvFollowingCount = view.findViewById(R.id.tvFollowingCount)
        btnBack = view.findViewById(R.id.btnBack)

        navHome = view.findViewById(R.id.navHome)
        navSearch = view.findViewById(R.id.navSearch)
        navAdd = view.findViewById(R.id.navAdd)
        navMessage = view.findViewById(R.id.navMessage)
        navProfile = view.findViewById(R.id.navProfile)

        btnSettingMenu = view.findViewById(R.id.btnSettingMenu)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)

        rvProfilePosts = view.findViewById(R.id.rvProfilePosts)
        tabGrid = view.findViewById(R.id.tabGrid)
        tabTags = view.findViewById(R.id.tabTags)
        indicatorGrid = view.findViewById(R.id.indicatorGrid)
        indicatorTags = view.findViewById(R.id.indicatorTags)
        followersLayoutBtn = view.findViewById(R.id.followersLayoutBtn)
        followingLayoutBtn = view.findViewById(R.id.followingLayoutBtn)
    }

    private fun setupRecyclerView() {
        profilePostAdapter = ProfilePostAdapter(emptyList()) { position ->
            currentPosts?.let { posts ->
                PostDataHolder.posts = posts.toMutableList()
                val fragment = PostDetailFragment.newInstance(position)
                (activity as? MainActivity)?.navigateToFragment(fragment)
            }
        }
        rvProfilePosts.layoutManager = GridLayoutManager(requireContext(), 3)
        rvProfilePosts.adapter = profilePostAdapter

        tabGrid.setOnClickListener {
            updateTabSelection(true)
            rvProfilePosts.visibility = View.VISIBLE
        }

        tabTags.setOnClickListener {
            updateTabSelection(false)
            rvProfilePosts.visibility = View.GONE
            Toast.makeText(requireContext(), "Tagged posts coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTabSelection(isGrid: Boolean) {
        if (isGrid) {
            tabGrid.setColorFilter(Color.WHITE)
            tabTags.setColorFilter(Color.parseColor("#666666"))
            indicatorGrid.setBackgroundColor(Color.WHITE)
            indicatorTags.setBackgroundColor(Color.TRANSPARENT)
        } else {
            tabGrid.setColorFilter(Color.parseColor("#666666"))
            tabTags.setColorFilter(Color.WHITE)
            indicatorGrid.setBackgroundColor(Color.TRANSPARENT)
            indicatorTags.setBackgroundColor(Color.WHITE)
        }
    }

    private var currentPosts: MutableList<Post>? = null

    private fun fetchMyPosts() {
        ApiClient.api.getMyPosts().enqueue(object : Callback<MyPostResponse> {
            override fun onResponse(call: Call<MyPostResponse>, response: Response<MyPostResponse>) {
                if (!isAdded) return

                if (response.isSuccessful && response.body() != null) {
                    val posts = response.body()!!.posts.toMutableList()
                    currentPosts = posts
                    profilePostAdapter.updatePosts(posts)
                    tvPostsCount.text = posts.size.toString()
                } else {
                    Log.e("ProfileFragment", "Failed to load posts: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<MyPostResponse>, t: Throwable) {
                if (!isAdded) return
                Log.e("ProfileFragment", "Error loading posts", t)
            }
        })
    }

    private fun fetchProfile() {
        try {
            ApiClient.api.getUserDetails().enqueue(object : Callback<UserDetailsResponse> {
                override fun onResponse(call: Call<UserDetailsResponse>, response: Response<UserDetailsResponse>) {
                    if (!isAdded) return

                    if (response.isSuccessful) {
                        val user = response.body()?.user
                        if (user == null) {
                            Log.e("ProfileFragment", "Response was successful but user is null. Raw: ${response.errorBody()?.string()}")
                            Toast.makeText(requireContext(), "Failed to parse profile", Toast.LENGTH_SHORT).show()
                            return
                        }
                        user.let {
                            tvName.text = it.name
                            tvUsernameTitle.text = it.username
                            tvEmail.text = it.email
                            tvBio.text = it.bio ?: ""

                            tvPostsCount.text = (it.postCount ?: 0).toString()
                            tvFollowersCount.text = (it.followers?.size ?: 0).toString()
                            tvFollowingCount.text = (it.following?.size ?: 0).toString()

                            it.profileImg?.let { url ->
                                currentProfileImageUrl = url
                                try {
                                    Glide.with(this@ProfileFragment)
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
                        val errorBody = response.errorBody()?.string()
                        Log.e("ProfileFragment", "Failed to load profile: ${response.code()} - $errorBody")
                        Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserDetailsResponse>, t: Throwable) {
                    if (!isAdded) return
                    Log.e("ProfileFragment", "Network error loading profile", t)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error connecting to server", Toast.LENGTH_SHORT).show()
        }
    }


    private fun performLogout() {
        ApiClient.clearAuth()
        (activity as? MainActivity)?.navigateWithClearStack(LoginFragment())
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
            // Already on profile
        }

        btnSettingMenu.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(SettingMenuFragment())
        }

        btnEditProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(EditProfileFragment())
        }

        followersLayoutBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(FollowersFragment())
        }
        followingLayoutBtn.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(FollowingFragment())
        }
    }

    private fun showFullImageDialog(url: String) {
        if (!isAdded) return

        try {
            val dialog = Dialog(requireContext())
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_image_viewer)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
            Toast.makeText(requireContext(), "Error showing image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProfileOptionsDialog() {
        val options = arrayOf("Change Profile Image", "Remove Profile Image")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Profile Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    pickImageLauncher.launch("image/*")
                }
                1 -> {
                    removeProfileImage()
                }
            }
        }
        builder.show()
    }

    private fun removeProfileImage() {

        ApiClient.api.removeProfileImage().enqueue(object : Callback<ImageUpdateResponse> {
            override fun onResponse(call: Call<ImageUpdateResponse>, response: Response<ImageUpdateResponse>) {
                if (!isAdded) return

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(requireContext(), "Profile image removed", Toast.LENGTH_SHORT).show()
                    val defaultUrl = response.body()!!.DEFAULT_IMG_URL
                    if (!defaultUrl.isNullOrEmpty()) {
                        updateProfileImageUI(defaultUrl)
                        TokenManager.saveProfileImage(defaultUrl)
                    } else {
                        fetchProfile()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to remove image", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ImageUpdateResponse>, t: Throwable) {
                if (!isAdded) return
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadProfileImage(uri: Uri) {
        val file = getFileFromUri(uri)
        if (file != null) {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)


            ApiClient.api.updateProfileImage(body).enqueue(object : Callback<ImageUpdateResponse> {
                override fun onResponse(call: Call<ImageUpdateResponse>, response: Response<ImageUpdateResponse>) {
                    if (!isAdded) return

                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(requireContext(), "Profile image updated", Toast.LENGTH_SHORT).show()
                        val newUrl = response.body()!!.imageUrl
                        if (!newUrl.isNullOrEmpty()) {
                            updateProfileImageUI(newUrl)
                            TokenManager.saveProfileImage(newUrl)
                        } else {
                            fetchProfile()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Failed to update image", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ImageUpdateResponse>, t: Throwable) {
                    if (!isAdded) return
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Error processing image file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfileImageUI(url: String) {
        currentProfileImageUrl = url
        try {
            Glide.with(this@ProfileFragment)
                .load(url)
                .placeholder(R.drawable.img_current_user)
                .circleCrop()
                .into(ivProfile)

            loadNavProfileImage()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("profile_img", ".jpg", requireContext().cacheDir)
            val outputStream = FileOutputStream(tempFile)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
