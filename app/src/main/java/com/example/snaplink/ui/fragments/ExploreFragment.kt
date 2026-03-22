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

    private var explorePosts: MutableList<com.example.snaplink.models.Post> = mutableListOf()
    private var currentPage = 1
    private var totalPages = 1
    private var isLoadingMore = false
    private var isDataLoaded = false

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
        loadNavProfileImage()

        if (!isDataLoaded) {
            loadExplorePosts(1)
        } else {
            exploreAdapter.updatePosts(explorePosts)
            exploreAdapter.setLoadMoreState(currentPage < totalPages, isLoadingMore)
        }
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
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (exploreAdapter.getItemViewType(position) == ProfilePostAdapter.TYPE_LOAD_MORE) 3 else 1
            }
        }
        rvExplore.layoutManager = gridLayoutManager

        exploreAdapter = ProfilePostAdapter(
            posts = emptyList(),
            onLoadMore = {
                if (!isLoadingMore && currentPage < totalPages) {
                    isLoadingMore = true
                    exploreAdapter.setLoadMoreState(true, true)
                    loadExplorePosts(currentPage + 1)
                }
            }
        ) { position ->
            if (position < explorePosts.size) {
                // To show only THIS post, we pass a list containing only this post
                val singlePostList = mutableListOf(explorePosts[position])
                com.example.snaplink.PostDataHolder.posts = singlePostList
                (activity as? MainActivity)?.navigateToFragment(PostDetailFragment.newInstance(0))
            }
        }
        rvExplore.adapter = exploreAdapter
    }

    private fun loadExplorePosts(page: Int = 1) {
        ApiClient.api.getExplorePosts(page).enqueue(object : Callback<FeedResponse> {
            override fun onResponse(call: Call<FeedResponse>, response: Response<FeedResponse>) {
                if (!isAdded) return
                isLoadingMore = false
                if (response.isSuccessful && response.body()?.success == true) {
                    val body = response.body()!!
                    if (page == 1) explorePosts.clear()
                    explorePosts.addAll(body.posts)
                    body.pagination?.let {
                        currentPage = it.page
                        totalPages = it.totalPages
                    }
                    isDataLoaded = true
                    exploreAdapter.updatePosts(explorePosts)
                    exploreAdapter.setLoadMoreState(currentPage < totalPages, false)
                } else {
                    exploreAdapter.setLoadMoreState(currentPage < totalPages, false)
                }
            }

            override fun onFailure(call: Call<FeedResponse>, t: Throwable) {
                if (!isAdded) return
                isLoadingMore = false
                exploreAdapter.setLoadMoreState(currentPage < totalPages, false)
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
