package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapterKt(private val postList: List<PostKt>) :
    RecyclerView.Adapter<PostAdapterKt.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        
        holder.tvUsername.text = post.username
        holder.tvCaptionUsername.text = post.username
        holder.ivUserAvatar.setImageResource(post.userAvatar)
        
        // Handle fallback for simpler PostKt model
        holder.ivPostImage.visibility = View.VISIBLE
        holder.vpPostImages.visibility = View.GONE
        holder.layoutIndicators.visibility = View.GONE
        
        holder.ivPostImage.setImageResource(post.postImage)
        
        holder.tvCaption.text = post.caption
        holder.tvTimeAgo.text = post.timeAgo

        holder.ivPostOptions.setOnClickListener {
            showPostOptions(holder.itemView.context, post)
        }
    }

    private fun showPostOptions(context: android.content.Context, post: PostKt) {
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(context)
        val currentUsername = com.example.snaplink.network.TokenManager.getUsername()
        val isMine = post.username == currentUsername

        val layoutRes = if (isMine) R.layout.layout_post_options_mine else R.layout.layout_post_options_other
        val view = android.view.LayoutInflater.from(context).inflate(layoutRes, null)

        view.findViewById<android.view.View>(R.id.cancelField)?.setOnClickListener {
            dialog.dismiss()
        }

        if (isMine) {
            view.findViewById<android.view.View>(R.id.deleteField)?.setOnClickListener {
                android.widget.Toast.makeText(context, "Delete clicked", android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
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
                android.widget.Toast.makeText(context, "About account clicked", android.widget.Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun getItemCount(): Int = postList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivUserAvatar: CircleImageView = itemView.findViewById(R.id.ivPostUserAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvPostUsername)
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val vpPostImages: androidx.viewpager2.widget.ViewPager2 = itemView.findViewById(R.id.vpPostImages)
        val layoutIndicators: android.widget.LinearLayout = itemView.findViewById(R.id.layoutIndicators)
        val tvCaptionUsername: TextView = itemView.findViewById(R.id.tvCaptionUsername)
        val tvCaption: TextView = itemView.findViewById(R.id.tvCaption)
        val tvTimeAgo: TextView = itemView.findViewById(R.id.tvTimeAgo)
        val ivComment: ImageView = itemView.findViewById(R.id.ivComment)
        val ivPostOptions: ImageView = itemView.findViewById(R.id.ivPostOptions)
    }
}
