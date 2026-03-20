package com.example.snaplink

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.Post
import com.example.snaplink.ui.activities.MainActivity

/**
 * Adapter for the main home feed. Handles both stories and vertical scrolling posts.
 */
class FeedAdapter(
    private val posts: MutableList<Post>,
    private val stories: List<StoryKt>,
    private val showStories: Boolean = true,
    private val onCommentClick: ((String) -> Unit)? = null,
    private val onAddStoryClick: (() -> Unit)? = null,
    private val onStoryClick: ((StoryKt) -> Unit)? = null,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mainHandler = Handler(Looper.getMainLooper())

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
            is StoriesHolder -> holder.bind(stories, onAddStoryClick, onStoryClick)

            is PostAdapterKt.PostViewHolder -> {
                val realPosition = if (showStories) position - 1 else position
                val post = posts[realPosition]

                // Set user info
                val username = post.postedBy?.username ?: "Unknown User"
                holder.tvUsername.text = username
                holder.tvCaptionUsername.text = username
                holder.tvCaption.text = post.caption ?: ""
                holder.tvTimeAgo.text = getTimeFormat(post.createdAt ?: "")

                // Click listeners for navigation to profile
                val navigateToUser = { post.postedBy?.username?.let { onUserClick(it) } }
                holder.tvUsername.setOnClickListener { navigateToUser() }
                holder.tvCaptionUsername.setOnClickListener { navigateToUser() }
                holder.ivUserAvatar.setOnClickListener { navigateToUser() }

                // Load profile avatar with Glide
                Glide.with(holder.itemView.context)
                    .load(post.postedBy?.profileImg)
                    .placeholder(R.drawable.img_current_user)
                    .circleCrop()
                    .into(holder.ivUserAvatar)

                // Set up image slider (ViewPager2)
                if (!post.images.isNullOrEmpty()) {
                    holder.vpPostImages.adapter = ImageSliderAdapter(post.images)
                    setupIndicators(holder.layoutIndicators, post.images.size)
                    
                    if (post.images.size > 1) {
                        holder.layoutIndicators.visibility = View.VISIBLE
                        holder.vpPostImages.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                            override fun onPageSelected(pos: Int) {
                                super.onPageSelected(pos)
                                updateIndicators(holder.layoutIndicators, pos)
                            }
                        })
                    } else {
                        holder.layoutIndicators.visibility = View.GONE
                    }
                } else {
                    holder.vpPostImages.adapter = null
                    holder.layoutIndicators.visibility = View.GONE
                }

                // Interaction button click listeners
                holder.ivComment.setOnClickListener { onCommentClick?.invoke(post._id) }
                holder.ivPostOptions.setOnClickListener { showPostOptions(holder.itemView.context, post) }

                // Like feature (Button tap + Double tap)
                val currentUserId = com.example.snaplink.network.TokenManager.getUserId()
                val isLikedByMe = currentUserId != null && post.likes.contains(currentUserId)
                updateLikeUI(holder, isLikedByMe, post.likes.size)

                // Like button single tap toggle
                holder.ivLike.setOnClickListener {
                    val posInAdapter = holder.bindingAdapterPosition
                    if (posInAdapter == RecyclerView.NO_POSITION) return@setOnClickListener
                    val idx = if (showStories) posInAdapter - 1 else posInAdapter
                    if (idx < 0 || idx >= posts.size) return@setOnClickListener
                    toggleLike(holder, posts[idx], idx)
                }

                // Double tap on image -> always like (Instagram style)
                holder.postImageContainer.setOnTouchListener(DoubleTapListener(holder.itemView.context) {
                    val posInAdapter = holder.bindingAdapterPosition
                    if (posInAdapter == RecyclerView.NO_POSITION) return@DoubleTapListener
                    val idx = if (showStories) posInAdapter - 1 else posInAdapter
                    if (idx < 0 || idx >= posts.size) return@DoubleTapListener

                    val freshPost = posts[idx]
                    val uid = com.example.snaplink.network.TokenManager.getUserId()
                    if (uid != null && !freshPost.likes.contains(uid)) {
                        toggleLike(holder, freshPost, idx)
                    }
                    showDoubleTapHeart(holder)
                })
            }
        }
    }

    private fun updateLikeUI(holder: PostAdapterKt.PostViewHolder, isLiked: Boolean, count: Int) {
        holder.ivLike.setImageResource(if (isLiked) R.drawable.ic_heart_filled_red else R.drawable.ic_heart_outline)
        holder.tvLikesCount.text = count.toString()
    }

    private fun toggleLike(holder: PostAdapterKt.PostViewHolder, post: Post, idx: Int) {
        val userId = com.example.snaplink.network.TokenManager.getUserId() ?: return

        val wasLiked = post.likes.contains(userId)
        val newLikes = post.likes.toMutableList()
        if (wasLiked) newLikes.remove(userId) else newLikes.add(userId)

        // Optimistic UI update and bounce
        val updatedPost = post.copy(likes = newLikes)
        posts[idx] = updatedPost
        updateLikeUI(holder, !wasLiked, newLikes.size)

        holder.ivLike.animate().scaleX(1.35f).scaleY(1.35f).setDuration(120)
            .withEndAction { holder.ivLike.animate().scaleX(1f).scaleY(1f).setDuration(120).start() }
            .start()

        // Call API to sync state
        com.example.snaplink.network.ApiClient.api.likePost(post._id)
            .enqueue(object : retrofit2.Callback<com.example.snaplink.network.LikeResponse> {
                override fun onResponse(call: retrofit2.Call<com.example.snaplink.network.LikeResponse>, response: retrofit2.Response<com.example.snaplink.network.LikeResponse>) {
                    mainHandler.post {
                        val body = response.body()
                        if (response.isSuccessful && body?.success == true) {
                            val serverIsLiked = body.message == "Post liked"
                            val finalLikes = posts[idx].likes.toMutableList().apply {
                                if (serverIsLiked && !contains(userId)) add(userId)
                                else if (!serverIsLiked) remove(userId)
                            }
                            posts[idx] = posts[idx].copy(likes = finalLikes)
                            updateLikeUI(holder, serverIsLiked, body.likesCount)
                        } else {
                            // Revert on failure
                            posts[idx] = post
                            updateLikeUI(holder, wasLiked, post.likes.size)
                        }
                    }
                }
                override fun onFailure(call: retrofit2.Call<com.example.snaplink.network.LikeResponse>, t: Throwable) {
                    mainHandler.post {
                        posts[idx] = post
                        updateLikeUI(holder, wasLiked, post.likes.size)
                    }
                }
            })
    }

    private fun showDoubleTapHeart(holder: PostAdapterKt.PostViewHolder) {
        val heart = holder.itemView.findViewById<android.widget.ImageView>(R.id.ivDoubleTapHeart)
        heart.visibility = View.VISIBLE
        heart.alpha = 0f
        heart.scaleX = 0.5f
        heart.scaleY = 0.5f

        heart.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300)
            .withEndAction {
                heart.animate().alpha(0f).scaleX(1.4f).scaleY(1.4f).setDuration(350).setStartDelay(400)
                    .withEndAction { heart.visibility = View.GONE }.start()
            }.start()
    }

    private fun showPostOptions(context: android.content.Context, post: Post) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
        val isMine = post.postedBy?.username == com.example.snaplink.network.TokenManager.getUsername()
        val layoutRes = if (isMine) R.layout.layout_post_options_mine else R.layout.layout_post_options_other
        val view = android.view.LayoutInflater.from(context).inflate(layoutRes, null)

        view.findViewById<View>(R.id.cancelField)?.setOnClickListener { dialog.dismiss() }

        if (isMine) {
            view.findViewById<View>(R.id.deleteField)?.setOnClickListener {
                dialog.dismiss()
                confirmAndDeletePost(context, post)
            }
        } else {
            view.findViewById<View>(R.id.aboutAccountField)?.setOnClickListener {
                dialog.dismiss()
                val activity = (context as? androidx.fragment.app.FragmentActivity) ?: return@setOnClickListener
                val fragment = UsersAboutSection.newInstance(
                    username = post.postedBy?.username ?: "",
                    profileImg = post.postedBy?.profileImg,
                    createdAt = post.createdAt
                )
                (activity as? MainActivity)?.navigateToFragment(fragment)
            }
        }
        dialog.setContentView(view)
        dialog.show()
    }

    private fun confirmAndDeletePost(context: android.content.Context, post: Post) {
        androidx.appcompat.app.AlertDialog.Builder(context)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ -> performDeletePost(context, post) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performDeletePost(context: android.content.Context, post: Post) {
        com.example.snaplink.network.ApiClient.api.deletePost(post._id)
            .enqueue(object : retrofit2.Callback<com.example.snaplink.network.SimpleApiResponse> {
                override fun onResponse(call: retrofit2.Call<com.example.snaplink.network.SimpleApiResponse>, response: retrofit2.Response<com.example.snaplink.network.SimpleApiResponse>) {
                    mainHandler.post {
                        if (response.isSuccessful) {
                            val i = posts.indexOfFirst { it._id == post._id }
                            if (i >= 0) {
                                posts.removeAt(i)
                                notifyItemRemoved(if (showStories) i + 1 else i)
                            }
                            android.widget.Toast.makeText(context, "Post deleted", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: retrofit2.Call<com.example.snaplink.network.SimpleApiResponse>, t: Throwable) {}
            })
    }

    private fun getTimeFormat(createdAt: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone("UTC") }
            val time = sdf.parse(createdAt)?.time ?: return "Just now"
            val diff = System.currentTimeMillis() - time
            when {
                diff < 60000 -> "Just now"
                diff < 3600000 -> "${diff / 60000}m ago"
                diff < 86400000 -> "${diff / 3600000}h ago"
                else -> "${diff / 86400000}d ago"
            }
        } catch (e: Exception) { "Just now" }
    }

    class StoriesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rv: RecyclerView = itemView.findViewById(R.id.rvStoriesInner)
        init { rv.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false) }
        fun bind(stories: List<StoryKt>, onAdd: (() -> Unit)?, onClick: ((StoryKt) -> Unit)?) {
            rv.adapter = StoryAdapterKt(stories, { onAdd?.invoke() }, { s -> onClick?.invoke(s) })
        }
    }

    private fun setupIndicators(layout: android.widget.LinearLayout, count: Int) {
        layout.removeAllViews()
        for (i in 0 until count) {
            val iv = android.widget.ImageView(layout.context)
            iv.setImageResource(if (i == 0) R.drawable.indicator_active else R.drawable.indicator_inactive)
            layout.addView(iv)
        }
    }

    private fun updateIndicators(layout: android.widget.LinearLayout, position: Int) {
        for (i in 0 until layout.childCount) {
            (layout.getChildAt(i) as? android.widget.ImageView)?.setImageResource(if (i == position) R.drawable.indicator_active else R.drawable.indicator_inactive)
        }
    }
}
