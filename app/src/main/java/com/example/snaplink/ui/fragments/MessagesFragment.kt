package com.example.snaplink.ui.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.network.TokenManager
import com.example.snaplink.ui.activities.MainActivity
import de.hdodenhof.circleimageview.CircleImageView

class MessagesFragment : Fragment() {

    data class Chat(
        val name: String,
        val message: String,
        val time: String,
        val unread: Int
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.activity_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.findViewById<LinearLayout>(R.id.mainContainer)

        val chats = listOf(
            Chat("Liam Murphy", "Hey thank you! How can I assist...", "23 mins", 3),
            Chat("Michael Johnson", "Hello bro!", "10 mins", 1),
            Chat("James Brown", "See you soon", "1 hr", 0),
            Chat("David Wilson", "Ok done 👍", "2 hr", 0),
            Chat("Thomas Lee", "Send me file", "Yesterday", 2)
        )

        chats.forEach { chat ->

            val item = LinearLayout(requireContext())
            item.orientation = LinearLayout.HORIZONTAL
            item.setPadding(24, 24, 24, 24)

            val image = CircleImageView(requireContext())
            image.layoutParams = LinearLayout.LayoutParams(120, 120)
            image.setImageResource(R.drawable.img_current_user)

            val textContainer = LinearLayout(requireContext())
            textContainer.orientation = LinearLayout.VERTICAL
            val params = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            params.setMargins(20, 0, 0, 0)
            textContainer.layoutParams = params

            val name = TextView(requireContext())
            name.text = chat.name
            name.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            name.textSize = 15f

            val message = TextView(requireContext())
            message.text = chat.message
            message.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
            message.textSize = 13f

            textContainer.addView(name)
            textContainer.addView(message)

            val right = LinearLayout(requireContext())
            right.orientation = LinearLayout.VERTICAL

            val time = TextView(requireContext())
            time.text = chat.time
            time.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))

            val unread = TextView(requireContext())
            unread.text = chat.unread.toString()
            unread.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            unread.setBackgroundColor(0xFF1DB954.toInt())
            unread.setPadding(10, 5, 10, 5)

            if (chat.unread == 0) unread.visibility = View.GONE

            right.addView(time)
            right.addView(unread)

            item.addView(image)
            item.addView(textContainer)
            item.addView(right)

            parent.addView(item)
        }

        val navHome = view.findViewById<ImageView>(R.id.navHome)
        val navSearch = view.findViewById<ImageView>(R.id.navSearch)
        val navAdd = view.findViewById<ImageView>(R.id.navAdd)
        val navProfile = view.findViewById<CircleImageView>(R.id.navProfile)

        navHome.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(HomeFragment())
        }

        navSearch.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ExploreFragment())
        }

        navAdd.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(CreatePostFragment())
        }

        navProfile.setOnClickListener {
            (activity as? MainActivity)?.navigateToFragment(ProfileFragment())
        }

        val url = TokenManager.getProfileImage()
        if (!url.isNullOrEmpty()) {
            Glide.with(requireContext())
                .load(url)
                .placeholder(R.drawable.img_current_user)
                .into(navProfile)
        }
    }
}