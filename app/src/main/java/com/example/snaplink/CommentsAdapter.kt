package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.Comment
import com.example.snaplink.network.TokenManager

class CommentsAdapter(
    initialComments: List<Comment>,
    private val onOptionClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private var comments: List<Comment> = initialComments.toList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProfile: ImageView = view.findViewById(R.id.imgProfile)
        val commentText: TextView = view.findViewById(R.id.tvCommentText)
        val time: TextView = view.findViewById(R.id.tvTime)
        val btnOptions: ImageView = view.findViewById(R.id.btnOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = comments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        
        val username = comment.postedBy?.username ?: "Unknown"
        val text = comment.text ?: ""
        val fullContent = "$username  $text"
        val spannable = android.text.SpannableString(fullContent)
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0, username.length,
            android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        holder.commentText.text = spannable
        
        holder.time.text = getTimeFormat(comment.createdAt ?: "")
        
        Glide.with(holder.itemView.context)
            .load(comment.postedBy?.profileImg)
            .placeholder(R.drawable.img_current_user)
            .circleCrop()
            .into(holder.imgProfile)

        // Show options only if the comment belongs to the current user
        val currentUsername = TokenManager.getUsername()
        if (username == currentUsername) {
            holder.btnOptions.visibility = View.VISIBLE
            holder.btnOptions.setOnClickListener {
                onOptionClick(comment)
            }
        } else {
            holder.btnOptions.visibility = View.GONE
        }
    }

    fun updateComments(newComments: List<Comment>) {
        val diffResult = androidx.recyclerview.widget.DiffUtil.calculateDiff(object : androidx.recyclerview.widget.DiffUtil.Callback() {
            override fun getOldListSize(): Int = comments.size
            override fun getNewListSize(): Int = newComments.size
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return comments[oldItemPosition].commentId == newComments[newItemPosition].commentId
            }
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return comments[oldItemPosition] == newComments[newItemPosition]
            }
        })
        this.comments = newComments.toList() // Use a copy
        diffResult.dispatchUpdatesTo(this)
    }

    private fun getTimeFormat(createdAt: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).apply { 
                timeZone = java.util.TimeZone.getTimeZone("UTC") 
            }
            val time = sdf.parse(createdAt)?.time ?: return "Just now"
            val diff = System.currentTimeMillis() - time
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m"
                diff < 86400000 -> "${diff / 3600000}h"
                else -> "${diff / 86400000}d"
            }
        } catch (e: Exception) { "Just now" }
    }
}