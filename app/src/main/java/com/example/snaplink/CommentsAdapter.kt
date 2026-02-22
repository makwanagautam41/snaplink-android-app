package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentsAdapter : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {

    private val fakeComments = listOf(
        "john_doe" to "This is amazing 🔥",
        "alex_23" to "Wow bro nice post!",
        "gautam_dev" to "Love this UI 😍",
        "snap_user123" to "Clean design!",
        "android_master" to "Instagram vibes!",
        "frontend_guy" to "Looks smooth 👌",
        "designer_01" to "Very nice layout!",
        "test_user" to "Comment section working!",
        "dev_ninja" to "This is exactly what I was looking for 💯",
        "code_wizard" to "Great work on this feature!",
        "pixel_perfect" to "The attention to detail here is incredible",
        "ui_lover" to "Can you share the design file? 🎨",
        "mobile_dev" to "Smooth animations, love it!",
        "react_fan" to "This looks better than most apps out there",
        "kotlin_pro" to "Clean code, clean design 🧹",
        "night_owl" to "Scrolling through this at 3am 😅",
        "photo_king" to "The image quality is stunning 📸",
        "travel_girl" to "Where was this taken? Looks beautiful!",
        "foodie_life" to "This makes me hungry 🍕",
        "fitness_bro" to "Motivation right here 💪",
        "music_vibes" to "What song is this? 🎵",
        "art_studio" to "Pure art, nothing less 🖼️",
        "wanderlust" to "Adding this to my bucket list ✈️",
        "coffee_addict" to "Need my coffee to appreciate this ☕",
        "sunset_chaser" to "Golden hour hits different 🌅",
        "bookworm" to "This caption though 📖",
        "gamer_zone" to "GG! This is fire 🎮",
        "nature_lover" to "Mother nature at her best 🌿",
        "street_style" to "Outfit check! 👗",
        "tech_geek" to "What camera did you use? 📷"
    )

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val commentText: TextView = view.findViewById(R.id.tvCommentText)
        val time: TextView = view.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = fakeComments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (username, comment) = fakeComments[position]
        holder.commentText.text = "$username  $comment"
        val hours = listOf("1m", "5m", "12m", "30m", "1h", "2h", "3h", "5h", "8h", "12h", "1d", "2d", "3d", "5d", "1w")
        holder.time.text = hours[position % hours.size]
    }
}