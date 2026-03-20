package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.FeedAdapter
import com.example.snaplink.PostDataHolder
import com.example.snaplink.R
import com.example.snaplink.ui.activities.MainActivity

class PostDetailFragment : Fragment() {

    private lateinit var rvPosts: RecyclerView
    private lateinit var btnBack: ImageView
    private lateinit var adapter: FeedAdapter

    companion object {
        private const val ARG_POSITION = "EXTRA_POSITION"

        fun newInstance(position: Int): PostDetailFragment {
            val fragment = PostDetailFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_post_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startPosition = arguments?.getInt(ARG_POSITION, 0) ?: 0
        val posts = PostDataHolder.posts

        initViews(view)
        setupRecyclerView(posts, startPosition)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        rvPosts = view.findViewById(R.id.rvPosts)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun setupRecyclerView(posts: MutableList<com.example.snaplink.models.Post>, startPosition: Int) {
        adapter = FeedAdapter(posts, emptyList(), showStories = false, onCommentClick = { postId ->
            val fragment = ViewCommentsFragment.newInstance(postId)
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }) { username ->
            val fragment = OtherUserProfileFragment.newInstance(username)
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }
        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        rvPosts.adapter = adapter

        rvPosts.scrollToPosition(startPosition)
    }
}
