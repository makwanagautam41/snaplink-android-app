package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.FollowRequest
import de.hdodenhof.circleimageview.CircleImageView

class FollowRequestAdapter(
    private var followRequests: List<FollowRequest>,
    private val onAcceptClick: (FollowRequest) -> Unit,
    private val onRejectClick: (FollowRequest) -> Unit
) : RecyclerView.Adapter<FollowRequestAdapter.FollowRequestViewHolder>() {

    fun updateFollowRequests(newRequests: List<FollowRequest>) {
        followRequests = newRequests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowRequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_follow_request, parent, false)
        return FollowRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowRequestViewHolder, position: Int) {
        holder.bind(followRequests[position])
    }

    override fun getItemCount(): Int = followRequests.size

    inner class FollowRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivAvatar: CircleImageView = itemView.findViewById(R.id.ivFollowRequestAvatar)
        private val tvUsername: TextView = itemView.findViewById(R.id.tvFollowRequestUsername)
        private val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        private val btnReject: Button = itemView.findViewById(R.id.btnReject)

        fun bind(request: FollowRequest) {
            tvUsername.text = request.username

            Glide.with(itemView.context)
                .load(request.profileImg)
                .placeholder(R.drawable.img_user_placeholder)
                .into(ivAvatar)

            btnAccept.setOnClickListener {
                onAcceptClick(request)
            }

            btnReject.setOnClickListener {
                onRejectClick(request)
            }
        }
    }
}

