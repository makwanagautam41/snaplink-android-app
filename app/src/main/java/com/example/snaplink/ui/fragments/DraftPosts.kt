package com.example.snaplink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R

class DraftPosts : Fragment() {

    private lateinit var btnBack: ImageView
    private lateinit var recyclerDrafts: RecyclerView
    private lateinit var layoutEmpty: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_draft_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupListeners()
        
        // Initial state: show empty for now as we don't have a database yet
        showEmptyState(true)
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        recyclerDrafts = view.findViewById(R.id.recyclerDrafts)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        recyclerDrafts.layoutManager = LinearLayoutManager(requireContext())
        // Adapter would be set here
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerDrafts.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}