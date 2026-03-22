package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R
import com.example.snaplink.UserAdapter
import com.example.snaplink.network.FollowerUser
import com.example.snaplink.ui.activities.MainActivity

class FollowingFragment : Fragment() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter

    companion object {
        private const val ARG_FOLLOWING = "arg_following"

        fun newInstance(following: List<FollowerUser>): FollowingFragment {
            val fragment = FollowingFragment()
            val args = Bundle()
            args.putSerializable(ARG_FOLLOWING, ArrayList(following))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_following_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnBack = view.findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        rvUsers = view.findViewById(R.id.rvUsers)
        
        val following = arguments?.getSerializable(ARG_FOLLOWING) as? ArrayList<FollowerUser> ?: arrayListOf()
        
        setupRecyclerView(following)
    }

    private fun setupRecyclerView(following: List<FollowerUser>) {
        val userList = following.map { userItem ->
            com.example.snaplink.network.User(
                _id = userItem._id,
                name = userItem.name,
                username = userItem.username,
                profileImg = userItem.profileImg,
                email = null,
                gender = null,
                phone = null,
                bio = null,
                followers = null,
                following = null,
                followRequests = null,
                savedPosts = null,
                dateOfBirth = null,
                postCount = null
            )
        }
        
        userAdapter = UserAdapter(userList) { username ->
            val fragment = OtherUserProfileFragment.newInstance(username)
            (activity as? MainActivity)?.navigateToFragment(fragment)
        }
        
        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.adapter = userAdapter
    }
}
