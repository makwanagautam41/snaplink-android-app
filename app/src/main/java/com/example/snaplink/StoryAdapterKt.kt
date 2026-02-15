package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.Glide


class StoryAdapterKt(private val storyList: List<StoryKt>) :
    RecyclerView.Adapter<StoryAdapterKt.StoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = storyList[position]

        holder.tvUsername.text = story.username
        if (!story.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(story.imageUrl)
                .placeholder(R.drawable.img_current_user)
                .into(holder.ivAvatar)
        } else {
            story.avatarResource?.let {
                holder.ivAvatar.setImageResource(it)
            }
        }


        holder.ivAddStory.visibility = if (story.isYourStory) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun getItemCount(): Int = storyList.size

    class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAvatar: CircleImageView = itemView.findViewById(R.id.ivStoryAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvStoryUsername)
        val ivAddStory: ImageView = itemView.findViewById(R.id.ivAddStory)
    }
}
