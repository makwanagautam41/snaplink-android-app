package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.network.User
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    private var users: List<User>,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_search, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvUsername.text = user.username
        holder.tvName.text = user.name

        Glide.with(holder.itemView.context)
            .load(user.profileImg)
            .placeholder(R.drawable.img_user_placeholder)
            .into(holder.ivUserAvatar)

        holder.itemView.setOnClickListener {
            onUserClick(user.username)
        }
    }

    override fun getItemCount(): Int = users.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAvatar: CircleImageView = itemView.findViewById(R.id.ivUserAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}
