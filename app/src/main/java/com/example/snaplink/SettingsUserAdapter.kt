package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.SettingsUser
import de.hdodenhof.circleimageview.CircleImageView

class SettingsUserAdapter(
    private var items: List<Any>,
    private val isBlockedList: Boolean = false,
    private var addedUserIds: Set<String> = emptySet(),
    private val onActionClick: ((SettingsUser) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_USER = 0
        private const val TYPE_HEADER = 1
    }

    fun updateData(newItems: List<Any>, newAddedUserIds: Set<String> = addedUserIds) {
        items = newItems
        addedUserIds = newAddedUserIds
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is SettingsUser) TYPE_USER else TYPE_HEADER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            val layout = if (isBlockedList) R.layout.item_blocked_user else R.layout.item_close_friend
            UserViewHolder(inflater.inflate(layout, parent, false))
        } else {
            HeaderViewHolder(inflater.inflate(R.layout.item_settings_separator, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is UserViewHolder) {
            holder.bind(items[position] as SettingsUser)
        } else if (holder is HeaderViewHolder) {
            holder.bind(items[position] as String)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeaderTitle: TextView = itemView.findViewById(R.id.tvHeaderTitle)
        fun bind(title: String) {
            tvHeaderTitle.text = title
        }
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: CircleImageView = itemView.findViewById(R.id.ivUserAvatar)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val actionBtn: View? = if (isBlockedList) itemView.findViewById(R.id.btnUnblock) else itemView.findViewById(R.id.ivCheck)

        fun bind(user: SettingsUser) {
            tvUsername.text = user.username
            tvName.text = user.name

            Glide.with(itemView.context)
                .load(user.profileImg)
                .placeholder(R.drawable.img_user_placeholder)
                .into(ivAvatar)

            if (!isBlockedList) {
                val ivCheck = itemView.findViewById<ImageView>(R.id.ivCheck)
                if (addedUserIds.contains(user._id)) {
                    ivCheck.setImageResource(R.drawable.ic_check_circle)
                    ivCheck.background = null
                } else {
                    ivCheck.setImageResource(0)
                    ivCheck.setBackgroundResource(R.drawable.circle_outline)
                }
            }

            actionBtn?.setOnClickListener {
                onActionClick?.invoke(user)
            }
        }
    }
}

