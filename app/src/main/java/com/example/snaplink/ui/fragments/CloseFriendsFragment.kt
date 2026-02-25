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
import com.example.snaplink.network.SettingsManager

class CloseFriendsFragment : Fragment() {
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
            // Handle close friend click
        }
        rvCloseFriends.layoutManager = LinearLayoutManager(requireContext())
        rvCloseFriends.adapter = adapter
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
