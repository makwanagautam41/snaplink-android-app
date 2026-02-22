package com.example.snaplink.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.CommentsAdapter
import com.example.snaplink.R

class ViewCommentsFragment : Fragment() {

    companion object {
        private const val ARG_POST_ID = "post_id"

        fun newInstance(postId: String): ViewCommentsFragment {
            val fragment = ViewCommentsFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = arguments?.getString(ARG_POST_ID)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerComments)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = CommentsAdapter()

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}