package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FeedAdapter(
    private val posts: List<PostKt>,
    private val stories: List<StoryKt>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_STORIES = 0
        private const val TYPE_POST = 1
    }

    override fun getItemCount(): Int = posts.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) TYPE_STORIES else TYPE_POST
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
                val post = posts[position - 1]
                holder.tvUsername.text = post.username
                holder.tvCaptionUsername.text = post.username
                holder.ivUserAvatar.setImageResource(post.userAvatar)
                holder.ivPostImage.setImageResource(post.postImage)
                holder.tvCaption.text = post.caption
                holder.tvTimeAgo.text = post.timeAgo
            }
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
