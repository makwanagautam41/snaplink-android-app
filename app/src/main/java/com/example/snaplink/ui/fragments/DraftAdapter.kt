package com.example.snaplink.ui.fragments

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.R
import com.example.snaplink.db.DraftPost
import java.text.SimpleDateFormat
import java.util.*

class DraftAdapter(
    private var drafts: List<DraftPost>,
    private val onDraftClick: (DraftPost) -> Unit
) : RecyclerView.Adapter<DraftAdapter.DraftViewHolder>() {

    fun updateData(newDrafts: List<DraftPost>) {
        this.drafts = newDrafts
        notifyDataSetChanged()
    }

    fun getDraftAt(position: Int): DraftPost {
        return drafts[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DraftViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_draft_post, parent, false)
        return DraftViewHolder(view)
    }

    override fun onBindViewHolder(holder: DraftViewHolder, position: Int) {
        val draft = drafts[position]
        holder.bind(draft)
    }

    override fun getItemCount() = drafts.size

    inner class DraftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivThumbnail: ImageView = itemView.findViewById(R.id.ivDraftThumbnail)
        private val tvCaption: TextView = itemView.findViewById(R.id.tvDraftCaption)
        private val tvMediaCount: TextView = itemView.findViewById(R.id.tvDraftMediaCount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDraftDate)

        fun bind(draft: DraftPost) {
            tvCaption.text = if (draft.caption.isNullOrEmpty()) "No caption" else draft.caption
            
            val uris = draft.mediaUris.split(";").filter { it.isNotEmpty() }
            tvMediaCount.text = "${uris.size} items"
            
            if (uris.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(Uri.parse(uris[0]))
                    .placeholder(R.drawable.img_post_placeholder)
                    .into(ivThumbnail)
            } else {
                ivThumbnail.setImageResource(R.drawable.img_post_placeholder)
            }

            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            tvDate.text = "Saved on ${sdf.format(Date(draft.timestamp))}"

            itemView.setOnClickListener { onDraftClick(draft) }
        }
    }
}
