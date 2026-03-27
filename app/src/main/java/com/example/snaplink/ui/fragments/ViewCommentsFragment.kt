package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snaplink.CommentsAdapter
import com.example.snaplink.R
import com.example.snaplink.models.Comment
import com.example.snaplink.network.ApiClient
import com.example.snaplink.network.CommentRequest
import com.example.snaplink.network.CommentResponse
import com.example.snaplink.network.SimpleApiResponse
import com.example.snaplink.network.TokenManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ViewCommentsFragment : Fragment() {

    private lateinit var adapter: CommentsAdapter
    private val commentsList = mutableListOf<Comment>()
    private lateinit var etComment: EditText
    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var recycler: RecyclerView
    private var postId: String? = null
    private val gson = Gson()

    companion object {
        private const val ARG_POST_ID = "post_id"
        private const val ARG_COMMENTS_JSON = "comments_json"

        fun newInstance(postId: String, comments: List<Comment>): ViewCommentsFragment {
            val fragment = ViewCommentsFragment()
            val args = Bundle()
            args.putString(ARG_POST_ID, postId)
            args.putString(ARG_COMMENTS_JSON, Gson().toJson(comments))
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

        postId = arguments?.getString(ARG_POST_ID) ?: return
        val commentsJson = arguments?.getString(ARG_COMMENTS_JSON)

        if (commentsJson != null) {
            val type = object : TypeToken<List<Comment>>() {}.type
            val initialComments: List<Comment> = gson.fromJson(commentsJson, type)
            commentsList.clear()
            commentsList.addAll(initialComments)
        }

        recycler = view.findViewById(R.id.recyclerComments)
        etComment = view.findViewById(R.id.etComment)
        val btnSend = view.findViewById<ImageView>(R.id.btnSend)

        adapter = CommentsAdapter(commentsList) { comment ->
            showCommentOptions(comment)
        }
        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        view.findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSend.setOnClickListener {
            val text = etComment.text.toString().trim()
            if (text.isNotEmpty()) {
                postComment(postId!!, text)
            }
        }
    }

    private fun postComment(postId: String, text: String) {
        val request = CommentRequest(text = text)

        ApiClient.api.postComment(postId, request).enqueue(object : Callback<CommentResponse> {
            override fun onResponse(call: Call<CommentResponse>, response: Response<CommentResponse>) {
                mainHandler.post {
                    if (response.isSuccessful && response.body()?.success == true) {
                        etComment.text.clear()
                        
                        val body = response.body()
                        val rawComments = body?.comments
                        
                        // We take the LAST comment because backend seems to return them in ascending order
                        val newestRawComment = if (!rawComments.isNullOrEmpty()) rawComments.last() else null
                        
                        val finalComment = if (newestRawComment != null) {
                            // Map RawComment (string ID) to UI Comment (PostUser object)
                            com.example.snaplink.models.Comment(
                                commentId = newestRawComment.commentId,
                                text = newestRawComment.text,
                                postedBy = com.example.snaplink.models.PostUser(
                                    _id = TokenManager.getUserId() ?: "",
                                    username = TokenManager.getUsername() ?: "Member",
                                    profileImg = TokenManager.getProfileImage() ?: ""
                                ),
                                createdAt = newestRawComment.createdAt
                            )
                        } else {
                            // Backend succeeded but returned no info - create a temporary one
                            com.example.snaplink.models.Comment(
                                commentId = "temp_${System.currentTimeMillis()}",
                                text = text,
                                postedBy = com.example.snaplink.models.PostUser(
                                    _id = TokenManager.getUserId() ?: "",
                                    username = TokenManager.getUsername() ?: "Member",
                                    profileImg = TokenManager.getProfileImage() ?: ""
                                ),
                                createdAt = "Just now"
                            )
                        }

                        // Add to top of client list
                        commentsList.add(0, finalComment)
                        
                        notifyParentOfChange()
                        adapter.updateComments(ArrayList(commentsList))
                        
                        // Reliability fallback
                        adapter.notifyDataSetChanged()
                        
                        recycler.postDelayed({
                            recycler.scrollToPosition(0)
                        }, 100)

                    } else {
                        Toast.makeText(requireContext(), "Failed to post comment", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<CommentResponse>, t: Throwable) {
                mainHandler.post {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun showCommentOptions(comment: Comment) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.layout_comment_options, null)
        
        val isMine = comment.postedBy?.username == TokenManager.getUsername()
        
        if (!isMine) {
            view.findViewById<View>(R.id.deleteField).visibility = View.GONE
        }

        view.findViewById<View>(R.id.deleteField).setOnClickListener {
            dialog.dismiss()
            performDeleteComment(comment)
        }
        
        view.findViewById<View>(R.id.cancelField).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun performDeleteComment(comment: Comment) {
        val cid = comment.commentId
        if (cid == null) {
            Toast.makeText(requireContext(), "Error: Invalid comment ID", Toast.LENGTH_SHORT).show()
            return
        }
        val pid = postId ?: return
        
        ApiClient.api.deleteComment(pid, cid).enqueue(object : Callback<com.example.snaplink.network.SimpleApiResponse> {
            override fun onResponse(call: Call<com.example.snaplink.network.SimpleApiResponse>, response: Response<com.example.snaplink.network.SimpleApiResponse>) {
                mainHandler.post {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "Comment deleted", Toast.LENGTH_SHORT).show()
                        
                        commentsList.remove(comment)
                        notifyParentOfChange()
                        adapter.updateComments(ArrayList(commentsList))
                    } else {
                        Toast.makeText(requireContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<com.example.snaplink.network.SimpleApiResponse>, t: Throwable) {
                mainHandler.post {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun notifyParentOfChange() {
        val pid = postId ?: return
        val bundle = Bundle()
        bundle.putString("postId", pid)
        bundle.putString("commentsJson", gson.toJson(commentsList))
        parentFragmentManager.setFragmentResult("comments_update", bundle)
    }
}