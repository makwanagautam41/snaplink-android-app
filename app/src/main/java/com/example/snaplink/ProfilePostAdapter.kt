package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.Post

class ProfilePostAdapter(
    private var posts: List<Post>,
    private val onPostClick: (Int) -> Unit
) : RecyclerView.Adapter<ProfilePostAdapter.PostViewHolder>() {

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_profile_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        if (post.images.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(post.images[0].url)
                .placeholder(R.drawable.img_post_placeholder)
                .centerCrop()
                .into(holder.ivPostImage)
        }
        
        holder.itemView.setOnClickListener {
            onPostClick(position)
        }
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
    }
}
