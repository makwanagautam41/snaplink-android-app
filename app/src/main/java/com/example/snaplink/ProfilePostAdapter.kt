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
    private var onLoadMore: (() -> Unit)? = null,
    private val onPostClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var hasMorePages: Boolean = false
    private var isLoadingMore: Boolean = false

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }

    fun setLoadMoreState(hasMore: Boolean, isLoading: Boolean) {
        val oldHasMore = hasMorePages
        hasMorePages = hasMore
        isLoadingMore = isLoading
        
        if (oldHasMore != hasMore) {
            notifyDataSetChanged()
        } else if (hasMore) {
            notifyItemChanged(itemCount - 1)
        }
    }

    companion object {
        const val TYPE_POST = 0
        const val TYPE_LOAD_MORE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasMorePages && position == posts.size) TYPE_LOAD_MORE else TYPE_POST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_LOAD_MORE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_load_more, parent, false)
            LoadMoreHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile_post, parent, false)
            PostViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostViewHolder) {
            val post = posts[position]
            val thumbUrl = post.media?.firstOrNull()?.url ?: post.images?.firstOrNull()?.url
            if (thumbUrl != null) {
                Glide.with(holder.itemView.context)
                    .load(thumbUrl)
                    .placeholder(R.drawable.img_post_placeholder)
                    .centerCrop()
                    .into(holder.ivPostImage)
            }
            
            holder.itemView.setOnClickListener {
                onPostClick(position)
            }
        } else if (holder is LoadMoreHolder) {
            holder.bind(isLoadingMore, onLoadMore)
        }
    }

    override fun getItemCount(): Int = if (hasMorePages) posts.size + 1 else posts.size

    class LoadMoreHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val btn: View = view.findViewById(R.id.btnLoadMore)
        private val pb: View = view.findViewById(R.id.pbLoadMore)

        fun bind(isLoading: Boolean, onClick: (() -> Unit)?) {
            if (isLoading) {
                btn.visibility = View.GONE
                pb.visibility = View.VISIBLE
            } else {
                btn.visibility = View.VISIBLE
                pb.visibility = View.GONE
                btn.setOnClickListener { onClick?.invoke() }
            }
        }
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
    }
}
