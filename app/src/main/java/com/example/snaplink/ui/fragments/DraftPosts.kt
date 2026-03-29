package com.example.snaplink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.R
import com.example.snaplink.db.AppDatabase
import com.example.snaplink.db.DraftPost
import com.example.snaplink.ui.activities.MainActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DraftPosts : Fragment() {

    private lateinit var btnBack: ImageView
    private lateinit var recyclerDrafts: RecyclerView
    private lateinit var layoutEmpty: View
    private lateinit var draftAdapter: DraftAdapter

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
        loadDrafts()
    }

    private fun initViews(view: View) {
        btnBack = view.findViewById(R.id.btnBack)
        recyclerDrafts = view.findViewById(R.id.recyclerDrafts)
        layoutEmpty = view.findViewById(R.id.layoutEmpty)
    }

    private fun setupRecyclerView() {
        draftAdapter = DraftAdapter(emptyList(), 
            onDraftClick = { draft ->
                val bundle = Bundle().apply {
                    putInt("draft_id", draft.id)
                    putString("caption", draft.caption)
                    putString("media_uris", draft.mediaUris)
                }
                
                // For simplicity, we can use a callback or just let CreatePostFragment handle it
                // Since this app uses a single-activity architecture with navigateToFragment
                // We'll create a new instance and pass arguments
                val createPostFragment = CreatePostFragment().apply {
                    arguments = bundle
                }
                (activity as? MainActivity)?.navigateToFragment(createPostFragment)
            }
        )
        recyclerDrafts.layoutManager = LinearLayoutManager(requireContext())
        recyclerDrafts.adapter = draftAdapter

        // Swipe to delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder) = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val draft = draftAdapter.getDraftAt(position)
                deleteDraft(draft)
            }
        }).attachToRecyclerView(recyclerDrafts)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun loadDrafts() {
        lifecycleScope.launch {
            AppDatabase.getDatabase(requireContext()).draftPostDao().getAllDrafts().collectLatest { drafts ->
                if (drafts.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    draftAdapter.updateData(drafts)
                }
            }
        }
    }

    private fun deleteDraft(draft: DraftPost) {
        lifecycleScope.launch {
            try {
                AppDatabase.getDatabase(requireContext()).draftPostDao().deleteDraft(draft)
                } catch (e: Exception) {
                Toast.makeText(requireContext(), "Failed to delete draft", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmptyState(isEmpty: Boolean) {
        layoutEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerDrafts.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}