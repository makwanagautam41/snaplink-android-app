package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R
import com.example.snaplink.SettingsUserAdapter
import com.example.snaplink.models.SettingsUser
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.SettingsManager
import com.example.snaplink.network.ToggleCloseFriendResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CloseFriendsFragment : Fragment() {
    private val TAG = "CloseFriendsFragment"
    private lateinit var btnBack: ImageView
    private lateinit var rvCloseFriends: RecyclerView
    private lateinit var adapter: SettingsUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_close_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            setupRecyclerView()
            loadCloseFriends()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing close friends list", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        rvCloseFriends = view.findViewById(R.id.rvCloseFriends)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = SettingsUserAdapter(emptyList(), isBlockedList = false) { user ->
            toggleCloseFriend(user)
        }
        rvCloseFriends.layoutManager = LinearLayoutManager(requireContext())
        rvCloseFriends.adapter = adapter
    }

    private fun toggleCloseFriend(user: SettingsUser) {
        val username = user.username ?: return
            
        ApiClient.api.toggleCloseFriend(username).enqueue(object : Callback<ToggleCloseFriendResponse> {
            override fun onResponse(call: Call<ToggleCloseFriendResponse>, response: Response<ToggleCloseFriendResponse>) {
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    val message = responseBody?.message ?: "Action successful"
                    
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                    
                    // Update cache and UI
                    SettingsManager.toggleCloseFriend(user._id)
                    loadCloseFriends()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(requireContext(), "Failed to update close friend status", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ToggleCloseFriendResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadCloseFriends() {
        val settings = SettingsManager.getSettings()
        settings?.closeFriends?.let { data ->
            val items = mutableListOf<Any>()
            items.addAll(data.closeFriendsAdded)
            
            if (data.closeFriendsNotAdded.isNotEmpty()) {
                items.add("Suggestions")
                items.addAll(data.closeFriendsNotAdded)
            }
            
            val addedIds = data.closeFriendsAdded.map { it._id }.toSet()
            adapter.updateData(items, addedIds)
        }
    }
}
