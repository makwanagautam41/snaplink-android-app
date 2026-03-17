package com.example.snaplink.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.ProfilePostAdapter
import com.example.snaplink.R
import com.example.snaplink.models.FeedResponse
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExploreFragment : Fragment() {

    private lateinit var searchBarContainer: LinearLayout
    private lateinit var rvExplore: RecyclerView
    private lateinit var exploreAdapter: ProfilePostAdapter

    // Bottom Nav
    private lateinit var navHome: ImageView
    private lateinit var navSearch: ImageView
    private lateinit var navAdd: ImageView
    private lateinit var navMessage: ImageView
    private lateinit var navProfile: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_explore, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupNavigation()
        setupRecyclerView()
        loadExplorePosts()
        loadNavProfileImage()
    }

    override fun onResume() {
        super.onResume()
        loadNavProfileImage()
    }

    private fun initViews(view: View) {
        searchBarContainer = view.findViewById(R.id.searchBarContainer)
        rvExplore = view.findViewById(R.id.rvExplore)

        navHome = view.findViewById(R.id.navHome)
        navSearch = view.findViewById(R.id.navSearch)
        navAdd = view.findViewById(R.id.navAdd)
        navMessage = view.findViewById(R.id.navMessage)
        navProfile = view.findViewById(R.id.navProfile)

        searchBarContainer.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(SearchFragment())
        }
    }

    private fun setupRecyclerView() {
        rvExplore.layoutManager = GridLayoutManager(requireContext(), 3)
        exploreAdapter = ProfilePostAdapter(emptyList()) { position ->
            // Handle post click if needed
        }
        rvExplore.adapter = exploreAdapter
    }

    private fun loadExplorePosts() {
        ApiClient.api.getExplorePosts().enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (!isAdded) return
                if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.posts?.let { posts ->
                        exploreAdapter.updatePosts(posts)
                    }
                }
            }

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                // Fail silently
            }
        })
    }

    private fun setupNavigation() {
        navHome.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(HomeFragment())
        }

        navSearch.setOnClickListener {
            // Already here
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
}
