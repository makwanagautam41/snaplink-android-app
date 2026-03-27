package com.example.snaplink.ui.fragments

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView

class MessagesFragment : Fragment() {

    // ── Data models ──────────────────────────────────────

    data class Chat(
        val name: String,
        val message: String,
        val time: String,
        val unread: Int,
        val isOnline: Boolean = false
    )

    data class Story(
        val name: String,
        val color: Int
    )

    // ── Sample data ──────────────────────────────────────

    private val stories = listOf(
        Story("You",     0xFF3D9EFF.toInt()),
        Story("Liam",    0xFFFF6B6B.toInt()),
        Story("Michael", 0xFFFFD93D.toInt()),
        Story("James",   0xFF6BCB77.toInt()),
        Story("David",   0xFFFF922B.toInt()),
        Story("Thomas",  0xFFDA77FF.toInt()),
        Story("Chris",   0xFFFF5CAD.toInt())
    )

    private val chats = listOf(
        Chat("Liam Murphy",     "Hey thank you! How can I assist...", "23m",       3, isOnline = true),
        Chat("Michael Johnson", "Hello bro!",                         "10m",       1, isOnline = true),
        Chat("James Brown",     "See you soon 👋",                    "1h",        0),
        Chat("David Wilson",    "Ok done 👍",                         "2h",        0),
        Chat("Thomas Lee",      "Send me the file when you can",      "Yesterday", 2, isOnline = true),
        Chat("Chris Evans",     "Sounds good, let's do it!",          "Yesterday", 0),
        Chat("Sarah Connor",    "Thanks for the update 🙌",           "2d",        0, isOnline = true)
    )

    private val avatarColors = listOf(
        0xFFFF6B6B.toInt(), 0xFF3D9EFF.toInt(), 0xFF6BCB77.toInt(),
        0xFFFFD93D.toInt(), 0xFFDA77FF.toInt(), 0xFFFF922B.toInt(),
        0xFFFF5CAD.toInt()
    )

    // ── Lifecycle ─────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.activity_messages, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildStories(view)
        buildChatList(view)
        setupNavigation(view)
    }

    // ── Stories slider ────────────────────────────────────

    private fun buildStories(view: View) {
        val container = view.findViewById<LinearLayout>(R.id.storiesContainer)

        stories.forEachIndexed { index, story ->

            val wrapper = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.marginEnd = dp(16) }
            }

            // Ring frame
            val ringFrame = FrameLayout(requireContext()).apply {
                val s = dp(64)
                layoutParams = LinearLayout.LayoutParams(s, s)
                background = GradientDrawable().apply {
                    shape = GradientDrawable.OVAL
                    setColor(Color.TRANSPARENT)
                    setStroke(
                        dp(2),
                        if (index == 0) Color.parseColor("#2E2E2E") else story.color
                    )
                }
            }

            // Avatar
            val avatar = CircleImageView(requireContext()).apply {
                val s = dp(54)
                layoutParams = FrameLayout.LayoutParams(s, s, Gravity.CENTER)
                setImageDrawable(initialDrawable(story.name[0].toString(), story.color))
            }
            ringFrame.addView(avatar)

            // "+" badge on your own story
            if (index == 0) {
                ringFrame.addView(TextView(requireContext()).apply {
                    text = "+"
                    textSize = 13f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    typeface = Typeface.DEFAULT_BOLD
                    val bs = dp(20)
                    layoutParams = FrameLayout.LayoutParams(bs, bs).also {
                        it.gravity = Gravity.BOTTOM or Gravity.END
                        it.bottomMargin = dp(1)
                        it.rightMargin = dp(1)
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor("#3D9EFF"))
                        setStroke(dp(2), Color.BLACK)
                    }
                })
            }

            // Name label
            val label = TextView(requireContext()).apply {
                text = if (index == 0) "You" else story.name
                textSize = 11f
                setTextColor(Color.parseColor("#AAAAAA"))
                gravity = Gravity.CENTER
                maxWidth = dp(64)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = dp(6) }
            }

            wrapper.addView(ringFrame)
            wrapper.addView(label)
            container.addView(wrapper)
        }
    }

    // ── Chat list ─────────────────────────────────────────

    private fun buildChatList(view: View) {
        val parent = view.findViewById<LinearLayout>(R.id.mainContainer)

        chats.forEachIndexed { index, chat ->
            val color = avatarColors[index % avatarColors.size]

            // Row
            val row = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    dp(72)
                )
                setPadding(dp(16), 0, dp(16), 0)
                isClickable = true
                isFocusable = true
                background = rowRipple()
            }

            // Avatar + online dot
            val avatarFrame = FrameLayout(requireContext()).apply {
                val s = dp(52)
                layoutParams = LinearLayout.LayoutParams(s, s)
            }
            val avatar = CircleImageView(requireContext()).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                setImageDrawable(initialDrawable(chat.name[0].toString(), color))
            }
            avatarFrame.addView(avatar)

            if (chat.isOnline) {
                avatarFrame.addView(View(requireContext()).apply {
                    val ds = dp(13)
                    layoutParams = FrameLayout.LayoutParams(ds, ds).also {
                        it.gravity = Gravity.BOTTOM or Gravity.END
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor("#4CD964"))
                        setStroke(dp(2), Color.BLACK)
                    }
                })
            }

            // Name + message column
            val textCol = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                ).also { it.setMargins(dp(14), 0, dp(8), 0) }
            }

            val nameView = TextView(requireContext()).apply {
                text = chat.name
                textSize = 15f
                setTextColor(Color.WHITE)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
            }

            val msgView = TextView(requireContext()).apply {
                text = chat.message
                textSize = 13f
                setTextColor(
                    if (chat.unread > 0) Color.parseColor("#CCCCCC")
                    else Color.parseColor("#666666")
                )
                if (chat.unread > 0) typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                maxLines = 1
                ellipsize = android.text.TextUtils.TruncateAt.END
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).also { it.topMargin = dp(3) }
            }

            textCol.addView(nameView)
            textCol.addView(msgView)

            // Time + badge column
            val rightCol = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    dp(54), LinearLayout.LayoutParams.MATCH_PARENT
                )
                setPadding(0, dp(16), 0, dp(16))
            }

            val timeView = TextView(requireContext()).apply {
                text = chat.time
                textSize = 11.5f
                gravity = Gravity.END
                setTextColor(
                    if (chat.unread > 0) Color.parseColor("#3D9EFF")
                    else Color.parseColor("#555555")
                )
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            rightCol.addView(timeView)

            if (chat.unread > 0) {
                rightCol.addView(TextView(requireContext()).apply {
                    text = chat.unread.toString()
                    textSize = 11f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
                    val bs = dp(20)
                    layoutParams = LinearLayout.LayoutParams(bs, bs).also {
                        it.topMargin = dp(6)
                        it.gravity = Gravity.END
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(Color.parseColor("#3D9EFF"))
                    }
                })
            }

            row.addView(avatarFrame)
            row.addView(textCol)
            row.addView(rightCol)
            parent.addView(row)

            // Inset divider
            if (index < chats.size - 1) {
                parent.addView(View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1
                    ).also { it.setMargins(dp(82), 0, 0, 0) }
                    setBackgroundColor(Color.parseColor("#181818"))
                })
            }
        }
    }

    // ── Navigation ────────────────────────────────────────

    private fun setupNavigation(view: View) {
        val navProfile = view.findViewById<CircleImageView>(R.id.navProfile)

        view.findViewById<ImageView>(R.id.navHome).setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(HomeFragment())
        }
        view.findViewById<ImageView>(R.id.navSearch).setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ExploreFragment())
        }
        view.findViewById<ImageView>(R.id.navAdd).setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(CreatePostFragment())
        }
        navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
        }

        TokenManager.getProfileImage()?.takeIf { it.isNotEmpty() }?.let { url ->
            Glide.with(requireContext())
                .load(url)
                .placeholder(R.drawable.img_current_user)
                .into(navProfile)
        }
    }

    // ── Helpers ───────────────────────────────────────────

    private fun dp(value: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            resources.displayMetrics
        ).toInt()

    private fun initialDrawable(letter: String, bgColor: Int): android.graphics.drawable.Drawable {
        return object : android.graphics.drawable.Drawable() {
            private val bgPaint  = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = bgColor; alpha = 200 }
            private val txtPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            }
            override fun draw(c: Canvas) {
                val cx = bounds.exactCenterX()
                val cy = bounds.exactCenterY()
                val r  = minOf(bounds.width(), bounds.height()) / 2f
                c.drawCircle(cx, cy, r, bgPaint)
                txtPaint.textSize = r * 0.72f
                val fm = txtPaint.fontMetrics
                c.drawText(letter.uppercase(), cx, cy - (fm.ascent + fm.descent) / 2f, txtPaint)
            }
            override fun setAlpha(a: Int) { bgPaint.alpha = a }
            override fun setColorFilter(cf: ColorFilter?) { bgPaint.colorFilter = cf }
            @Suppress("OVERRIDE_DEPRECATION")
            override fun getOpacity() = PixelFormat.TRANSLUCENT
        }
    }

    private fun rowRipple(): android.graphics.drawable.Drawable {
        val pressed = GradientDrawable().apply { setColor(Color.parseColor("#141414")) }
        return StateListDrawable().also {
            it.addState(intArrayOf(android.R.attr.state_pressed), pressed)
            it.addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
        }
    }
}