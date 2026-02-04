package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.Post

class FeedAdapter(
    private val posts: List<Post>,
    private val stories: List<StoryKt>,
    private val showStories: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_STORIES = 0
        private const val TYPE_POST = 1
    }

    override fun getItemCount(): Int = if (showStories) posts.size + 1 else posts.size

    override fun getItemViewType(position: Int): Int {
        return if (showStories && position == 0) TYPE_STORIES else TYPE_POST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_STORIES) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stories_feed, parent, false)
            StoriesHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post, parent, false)
            PostAdapterKt.PostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is StoriesHolder -> holder.bind(stories)
            is PostAdapterKt.PostViewHolder -> {
                val realPosition = if (showStories) position - 1 else position
                val post = posts[realPosition]
                
                // Bind username
                holder.tvUsername.text = post.postedBy.username
                holder.tvCaptionUsername.text = post.postedBy.username
                
                // Bind caption
                holder.tvCaption.text = post.caption
                
                // Calculate time ago (simplified - you can enhance this)
                holder.tvTimeAgo.text = getTimeAgo(post.createdAt)
                
                // Load profile image using Glide
                Glide.with(holder.itemView.context)
                    .load(post.postedBy.profileImg)
                    .placeholder(R.drawable.img_current_user)
                    .circleCrop()
                    .into(holder.ivUserAvatar)
                
                // Load post image using Glide
                if (post.images.isNotEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(post.images[0].url)
                        .placeholder(R.drawable.img_post_placeholder)
                        .into(holder.ivPostImage)
                }
            }
        }
    }

    private fun getTimeAgo(createdAt: String): String {
        // Simple time ago calculation - you can enhance this with a library
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val time = sdf.parse(createdAt)?.time ?: return "Just now"
            val now = System.currentTimeMillis()
            val diff = now - time
            
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000} minutes ago"
                diff < 86400000 -> "${diff / 3600000} hours ago"
                diff < 604800000 -> "${diff / 86400000} days ago"
                else -> "${diff / 604800000} weeks ago"
            }
        } catch (e: Exception) {
            "Just now"
        }
    }

    class StoriesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rvStoriesInner: RecyclerView = itemView.findViewById(R.id.rvStoriesInner)

        init {
            rvStoriesInner.layoutManager = LinearLayoutManager(
                itemView.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }

        fun bind(stories: List<StoryKt>) {
            rvStoriesInner.adapter = StoryAdapterKt(stories)
        }
    }
}
