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

class BlockedFragment : Fragment() {
    private lateinit var btnBack: ImageView
    private lateinit var rvBlockedUsers: RecyclerView
    private lateinit var adapter: SettingsUserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_blocked, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            initViews(view)
            setupRecyclerView()
            loadBlockedUsers()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "Error initializing blocked list", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        rvBlockedUsers = view.findViewById(R.id.rvBlockedUsers)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = SettingsUserAdapter(emptyList(), isBlockedList = true) { user ->
            // Handle unblock action if needed
            Toast.makeText(requireContext(), "Unblock ${user.username}", Toast.LENGTH_SHORT).show()
        }
        rvBlockedUsers.layoutManager = LinearLayoutManager(requireContext())
        rvBlockedUsers.adapter = adapter
    }

    private fun loadBlockedUsers() {
        val settings = SettingsManager.getSettings()
        settings?.blockedUsers?.let {
            adapter.updateData(it as List<Any>)
        }
    }
}
