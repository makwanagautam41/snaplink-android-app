package com.example.snaplink

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.Post

class FeedAdapter(
    private val posts: MutableList<Post>,
    private val stories: List<StoryKt>,
    private val showStories: Boolean = true,
    private val onCommentClick: ((String) -> Unit)? = null,
    private val onAddStoryClick: (() -> Unit)? = null,
    private val onStoryClick: ((StoryKt) -> Unit)? = null,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_STORIES = 0
        private const val TYPE_POST = 1
        private const val TAG = "FeedAdapter"   // Logcat tag for like feature
    }

    // Main-thread handler so we can safely touch Views from Retrofit callbacks
    private val mainHandler = Handler(Looper.getMainLooper())

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

                // Comment button
                holder.ivComment.setOnClickListener {
                    onCommentClick?.invoke(post._id)
                }

                // Options (three-dot) button
                holder.ivPostOptions.setOnClickListener {
                    showPostOptions(holder.itemView.context, post)
                }

                // ── Like state ────────────────────────────────────────────────
                val currentUserId = com.example.snaplink.network.TokenManager.getUserId()
                Log.d(TAG, "Binding post=${post._id} | currentUserId=$currentUserId | likes=${post.likes}")

                val isLiked = currentUserId != null && post.likes.contains(currentUserId)
                Log.d(TAG, "Post ${post._id} isLiked=$isLiked")
                updateLikeUI(holder, isLiked, post.likes.size)

                // Like / Unlike button — always reads the freshest copy from the list
                holder.ivLike.setOnClickListener {
                    // bindingAdapterPosition is used inside callbacks to avoid stale index
                    val pos = holder.bindingAdapterPosition
                    if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
                    val freshRealPos = if (showStories) pos - 1 else pos
                    if (freshRealPos < 0 || freshRealPos >= posts.size) return@setOnClickListener
                    Log.d(TAG, "Like button tapped on post=${posts[freshRealPos]._id}")
                    toggleLike(holder, posts[freshRealPos], freshRealPos)
                }

                // Double-tap on the image container → like only (never unlike)
                // We attach to the FrameLayout wrapper, NOT to VP2, to avoid touch conflicts
                holder.postImageContainer.setOnTouchListener(
                    DoubleTapListener(holder.itemView.context) {
                        val pos = holder.bindingAdapterPosition
                        if (pos == RecyclerView.NO_ID.toInt()) return@DoubleTapListener
                        val freshRealPos = if (showStories) pos - 1 else pos
                        if (freshRealPos < 0 || freshRealPos >= posts.size) return@DoubleTapListener

                        val freshPost = posts[freshRealPos]
                        val uid = com.example.snaplink.network.TokenManager.getUserId()
                        Log.d(TAG, "Double-tap on post=${freshPost._id} | uid=$uid | alreadyLiked=${freshPost.likes.contains(uid)}")

                        // Only like on double-tap, never unlike
                        if (uid != null && !freshPost.likes.contains(uid)) {
                            toggleLike(holder, freshPost, freshRealPos)
                        }
                        // Always play the heart animation regardless of liked state
                        showDoubleTapHeart(holder)
                    }
                )
            }
        }
    }

    // ── Like helpers ──────────────────────────────────────────────────────────

    /**
     * Sets the heart icon (filled red = liked, outline = not liked) and updates the count text.
     * Must be called on the main thread.
     */
    private fun updateLikeUI(holder: PostAdapterKt.PostViewHolder, isLiked: Boolean, count: Int) {
        if (isLiked) {
            holder.ivLike.setImageResource(R.drawable.ic_heart_filled_red)
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_heart_outline)
        }
        holder.tvLikesCount.text = count.toString()
    }

    /**
     * Optimistically toggles like state: updates UI + local list instantly, then calls API.
     * Reverts on failure so the UI always reflects truth.
     */
    private fun toggleLike(
        holder: PostAdapterKt.PostViewHolder,
        post: Post,
        realPosition: Int
    ) {
        // getUserId() must return a value; if null, user not logged in — bail
        val userId = com.example.snaplink.network.TokenManager.getUserId()
        if (userId == null) {
            Log.w(TAG, "toggleLike: userId is null — user not logged in or ID not saved yet")
            return
        }

        val wasLiked = post.likes.contains(userId)
        Log.d(TAG, "toggleLike START | post=${post._id} | userId=$userId | wasLiked=$wasLiked")

        // Build the updated likes list locally
        val updatedLikes = post.likes.toMutableList()
        if (wasLiked) updatedLikes.remove(userId) else updatedLikes.add(userId)

        // Update local list immediately (optimistic)
        val updatedPost = post.copy(likes = updatedLikes)
        posts[realPosition] = updatedPost

        // Reflect in UI immediately
        updateLikeUI(holder, !wasLiked, updatedLikes.size)

        // Small bounce on the heart button
        holder.ivLike.animate().scaleX(1.35f).scaleY(1.35f).setDuration(100)
            .withEndAction {
                holder.ivLike.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            }.start()

        // Hit the API
        com.example.snaplink.network.ApiClient.api.likePost(post._id)
            .enqueue(object : retrofit2.Callback<com.example.snaplink.network.LikeResponse> {

                override fun onResponse(
                    call: retrofit2.Call<com.example.snaplink.network.LikeResponse>,
                    response: retrofit2.Response<com.example.snaplink.network.LikeResponse>
                ) {
                    val body = response.body()
                    Log.d(TAG, "likePost API response | postId=${post._id} | code=${response.code()} | body=$body")

                    mainHandler.post {
                        if (response.isSuccessful && body?.success == true) {
                            val serverCount = body.likesCount
                            val isNowLiked = body.message == "Post liked"
                            Log.d(TAG, "likePost SUCCESS | postId=${post._id} | isNowLiked=$isNowLiked | serverCount=$serverCount")

                            // Sync likes list to match server truth
                            val syncedLikes = updatedLikes.toMutableList()
                            if (isNowLiked && !syncedLikes.contains(userId)) syncedLikes.add(userId)
                            if (!isNowLiked) syncedLikes.remove(userId)

                            posts[realPosition] = updatedPost.copy(likes = syncedLikes)
                            updateLikeUI(holder, isNowLiked, serverCount)
                        } else {
                            // API returned an error — revert optimistic update
                            Log.w(TAG, "likePost FAILED | postId=${post._id} | reverting | errorBody=${response.errorBody()?.string()}")
                            posts[realPosition] = post
                            updateLikeUI(holder, wasLiked, post.likes.size)
                        }
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<com.example.snaplink.network.LikeResponse>,
                    t: Throwable
                ) {
                    Log.e(TAG, "likePost NETWORK ERROR | postId=${post._id} | ${t.message}", t)
                    mainHandler.post {
                        // Network failure — revert
                        posts[realPosition] = post
                        updateLikeUI(holder, wasLiked, post.likes.size)
                    }
                }
            })
    }

    /**
     * Plays the classic Instagram double-tap heart animation:
     * heart fades + scales in from centre, holds briefly, then fades out.
     */
    private fun showDoubleTapHeart(holder: PostAdapterKt.PostViewHolder) {
        val heart = holder.itemView.findViewById<android.widget.ImageView>(R.id.ivDoubleTapHeart)
        heart.visibility = View.VISIBLE
        heart.alpha = 0f
        heart.scaleX = 0.5f
        heart.scaleY = 0.5f

        heart.animate()
            .alpha(1f).scaleX(1f).scaleY(1f)
            .setDuration(250)
            .withEndAction {
                heart.animate()
                    .alpha(0f).scaleX(1.3f).scaleY(1.3f)
                    .setDuration(350)
                    .setStartDelay(400)
                    .withEndAction { heart.visibility = View.GONE }
                    .start()
            }.start()
    }

    // ── Post options (three-dot menu) ─────────────────────────────────────────

    private fun showPostOptions(context: android.content.Context, post: Post) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
        val currentUsername = com.example.snaplink.network.TokenManager.getUsername()
        val isMine = post.postedBy?.username == currentUsername

        val layoutRes = if (isMine) R.layout.layout_post_options_mine else R.layout.layout_post_options_other
        val view = android.view.LayoutInflater.from(context).inflate(layoutRes, null)

        view.findViewById<android.view.View>(R.id.cancelField)?.setOnClickListener {
            dialog.dismiss()
        }

        if (isMine) {
            view.findViewById<android.view.View>(R.id.deleteField)?.setOnClickListener {
                dialog.dismiss()
                confirmAndDeletePost(context, post)
            }
            view.findViewById<android.view.View>(R.id.editField)?.setOnClickListener {
                android.widget.Toast.makeText(context, "Edit clicked", android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        } else {
            view.findViewById<android.view.View>(R.id.reportField)?.setOnClickListener {
                android.widget.Toast.makeText(context, "Report clicked", android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            view.findViewById<android.view.View>(R.id.aboutAccountField)?.setOnClickListener {
                dialog.dismiss()
                navigateToAboutSection(context, post)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    /** Opens the UsersAboutSection fragment for the given post's author. */
    private fun navigateToAboutSection(context: android.content.Context, post: Post) {
        val activity = (context as? androidx.fragment.app.FragmentActivity) ?: return
        val fragment = UsersAboutSection.newInstance(
            username   = post.postedBy?.username ?: "",
            profileImg = post.postedBy?.profileImg,
            createdAt  = post.createdAt
        )
        (activity as? com.example.snaplink.ui.activities.MainActivity)?.navigateToFragment(fragment)
    }

    /** Shows confirmation dialog then calls DELETE /api/posts/{postId}. */
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
                override fun onResponse(
                    call: retrofit2.Call<com.example.snaplink.network.SimpleApiResponse>,
                    response: retrofit2.Response<com.example.snaplink.network.SimpleApiResponse>
                ) {
                    mainHandler.post {
                        if (response.isSuccessful) {
                            val idx = posts.indexOfFirst { it._id == post._id }
                            if (idx >= 0) {
                                posts.removeAt(idx)
                                val notifyIdx = if (showStories) idx + 1 else idx
                                notifyItemRemoved(notifyIdx)
                            }
                            android.widget.Toast.makeText(context, "Post deleted", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(context, "Failed to delete: ${response.message()}", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: retrofit2.Call<com.example.snaplink.network.SimpleApiResponse>, t: Throwable) {
                    mainHandler.post {
                        android.widget.Toast.makeText(context, "Error: ${t.message}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private fun getTimeAgo(createdAt: String): String {
        return try {
            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault())
            sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val time = sdf.parse(createdAt)?.time ?: return "Just now"
            val diff = System.currentTimeMillis() - time
            when {
                diff < 60_000      -> "Just now"
                diff < 3_600_000   -> "${diff / 60_000} minutes ago"
                diff < 86_400_000  -> "${diff / 3_600_000} hours ago"
                diff < 604_800_000 -> "${diff / 86_400_000} days ago"
                else               -> "${diff / 604_800_000} weeks ago"
            }
        } catch (e: Exception) { "Just now" }
    }

    class StoriesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rvStoriesInner: RecyclerView = itemView.findViewById(R.id.rvStoriesInner)

        init {
            rvStoriesInner.layoutManager = LinearLayoutManager(
                itemView.context, LinearLayoutManager.HORIZONTAL, false
            )
        }

        fun bind(stories: List<StoryKt>, onAddStoryClick: (() -> Unit)?, onStoryClick: ((StoryKt) -> Unit)?) {
            rvStoriesInner.adapter = StoryAdapterKt(stories, { onAddStoryClick?.invoke() }, { story -> onStoryClick?.invoke(story) })
        }
    }

    private fun setupIndicators(layout: android.widget.LinearLayout, count: Int) {
        layout.removeAllViews()
        val lp = android.widget.LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(4, 0, 4, 0) }

        for (i in 0 until count) {
            val iv = android.widget.ImageView(layout.context)
            iv.setImageDrawable(
                androidx.core.content.ContextCompat.getDrawable(layout.context,
                    if (i == 0) R.drawable.indicator_active else R.drawable.indicator_inactive)
            )
            iv.layoutParams = lp
            layout.addView(iv)
        }
    }

    private fun updateIndicators(layout: android.widget.LinearLayout, position: Int) {
        for (i in 0 until layout.childCount) {
            val child = layout.getChildAt(i) as? android.widget.ImageView ?: continue
            child.setImageDrawable(
                androidx.core.content.ContextCompat.getDrawable(layout.context,
                    if (i == position) R.drawable.indicator_active else R.drawable.indicator_inactive)
            )
        }
    }
}
