package com.example.snaplink

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class SelectedImageAdapter(
    private val images: MutableList<Uri>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<SelectedImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivSelectedImage: ImageView = itemView.findViewById(R.id.ivSelectedImage)
        private val btnRemoveImage: ImageView = itemView.findViewById(R.id.btnRemoveImage)

        fun bind(uri: Uri, position: Int) {
            ivSelectedImage.setImageURI(uri)
            btnRemoveImage.setOnClickListener {
                onRemove(position)
            }
        }
    }
}
