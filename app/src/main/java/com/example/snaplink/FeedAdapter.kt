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
    private val showStories: Boolean = true,
    private val onUserClick: (String) -> Unit
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

                val user = post.postedBy
                val username = user?.username ?: "Unknown User"
                val profileImg = user?.profileImg

                holder.tvUsername.text = username
                holder.tvUsername.setOnClickListener { user?.username?.let { onUserClick(it) } }

                holder.tvCaptionUsername.text = username
                holder.tvCaptionUsername.setOnClickListener { user?.username?.let { onUserClick(it) } }

                holder.tvCaption.text = post.caption ?: ""
                holder.tvTimeAgo.text = getTimeAgo(post.createdAt ?: "")

                if (!profileImg.isNullOrEmpty()) {
                    Glide.with(holder.itemView.context)
                        .load(profileImg)
                        .placeholder(R.drawable.img_current_user)
                        .circleCrop()
                        .into(holder.ivUserAvatar)
                } else {
                    holder.ivUserAvatar.setImageResource(R.drawable.img_current_user)
                }

                holder.ivUserAvatar.setOnClickListener { user?.username?.let { onUserClick(it) } }

                if (!post.images.isNullOrEmpty()) {
                    val imageAdapter = ImageSliderAdapter(post.images)
                    holder.vpPostImages.adapter = imageAdapter

                    setupIndicators(holder.layoutIndicators, post.images.size)

                    if (post.images.size > 1) {
                        holder.layoutIndicators.visibility = View.VISIBLE
                        holder.vpPostImages.registerOnPageChangeCallback(
                            object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                                override fun onPageSelected(position: Int) {
                                    super.onPageSelected(position)
                                    updateIndicators(holder.layoutIndicators, position)
                                }
                            }
                        )
                    } else {
                        holder.layoutIndicators.visibility = View.GONE
                    }
                } else {
                    holder.vpPostImages.adapter = null
                    holder.layoutIndicators.visibility = View.GONE
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

    private fun setupIndicators(layout: android.widget.LinearLayout, count: Int) {
        layout.removeAllViews()
        val indicators = arrayOfNulls<android.widget.ImageView>(count)
        val layoutParams = android.widget.LinearLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(4, 0, 4, 0)
        
        for (i in 0 until count) {
            indicators[i] = android.widget.ImageView(layout.context)
            indicators[i]?.setImageDrawable(
                androidx.core.content.ContextCompat.getDrawable(layout.context, R.drawable.indicator_inactive)
            )
            indicators[i]?.layoutParams = layoutParams
            layout.addView(indicators[i])
        }
        
        if (count > 0) {
            indicators[0]?.setImageDrawable(
                androidx.core.content.ContextCompat.getDrawable(layout.context, R.drawable.indicator_active)
            )
        }
    }
    
    private fun updateIndicators(layout: android.widget.LinearLayout, position: Int) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i)
            if (child is android.widget.ImageView) {
                if (i == position) {
                     child.setImageDrawable(
                        androidx.core.content.ContextCompat.getDrawable(layout.context, R.drawable.indicator_active)
                    )
                } else {
                     child.setImageDrawable(
                        androidx.core.content.ContextCompat.getDrawable(layout.context, R.drawable.indicator_inactive)
                    )
                }
            }
        }
    }
}
