package com.example.snaplink

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.PostMedia

class MediaSliderAdapter(private val mediaList: List<PostMedia>) :
    RecyclerView.Adapter<MediaSliderAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_slider, parent, false)
        return MediaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val media = mediaList[position]
        
        if (media.mediaType == "video") {
            holder.ivSlideImage.visibility = View.GONE
            holder.videoView.visibility = View.VISIBLE
            
            val uri = Uri.parse(media.url)
            holder.videoView.setVideoURI(uri)
            
            // Simple video handling: play when it's ready, loop it.
            holder.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                val videoRatio = mp.videoWidth / mp.videoHeight.toFloat()
                val screenRatio = holder.videoView.width / holder.videoView.height.toFloat()
                val scale = videoRatio / screenRatio
                if (scale >= 1f) {
                    holder.videoView.scaleX = scale
                } else {
                    holder.videoView.scaleY = 1f / scale
                }
                mp.start()
            }
        } else {
            holder.ivSlideImage.visibility = View.VISIBLE
            holder.videoView.visibility = View.GONE
            
            Glide.with(holder.itemView.context)
                .load(media.url)
                .placeholder(R.drawable.img_post_placeholder)
                .centerCrop()
                .into(holder.ivSlideImage)
        }
    }

    override fun getItemCount(): Int = mediaList.size

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSlideImage: ImageView = itemView.findViewById(R.id.ivSlideImage)
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
        val ivPlayIcon: ImageView? = itemView.findViewById(R.id.ivPlayIcon)
    }
}
