package com.example.snaplink

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snaplink.models.PostImage

class ImageSliderAdapter(private val images: List<PostImage>) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView.context)
            .load(image.url)
            .placeholder(R.drawable.img_post_placeholder)
            .centerCrop()
            .into(holder.ivSlideImage)
    }

    override fun getItemCount(): Int = images.size

    class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivSlideImage: ImageView = itemView.findViewById(R.id.ivSlideImage)
    }
}
