package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapterKt(private val postList: List<PostKt>) :
    RecyclerView.Adapter<PostAdapterKt.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        
        holder.tvUsername.text = post.username
        holder.tvCaptionUsername.text = post.username
        holder.ivUserAvatar.setImageResource(post.userAvatar)
        holder.ivPostImage.setImageResource(post.postImage)
        holder.tvCaption.text = post.caption
        holder.tvTimeAgo.text = post.timeAgo
    }

    override fun getItemCount(): Int = postList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAvatar: CircleImageView = itemView.findViewById(R.id.ivPostUserAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvPostUsername)
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val tvCaptionUsername: TextView = itemView.findViewById(R.id.tvCaptionUsername)
        val tvCaption: TextView = itemView.findViewById(R.id.tvCaption)
        val tvTimeAgo: TextView = itemView.findViewById(R.id.tvTimeAgo)
    }
}
