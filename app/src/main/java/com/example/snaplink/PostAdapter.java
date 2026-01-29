package com.example.snaplink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;

    public PostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        holder.tvUsername.setText(post.getUsername());
        holder.tvCaptionUsername.setText(post.getUsername());
        holder.ivUserAvatar.setImageResource(post.getUserAvatar());
        holder.ivPostImage.setImageResource(post.getPostImage());
        holder.tvCaption.setText(post.getCaption());
        holder.tvTimeAgo.setText(post.getTimeAgo());
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView ivUserAvatar;
        TextView tvUsername;
        ImageView ivPostImage;
        TextView tvCaptionUsername;
        TextView tvCaption;
        TextView tvTimeAgo;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivPostUserAvatar);
            tvUsername = itemView.findViewById(R.id.tvPostUsername);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            tvCaptionUsername = itemView.findViewById(R.id.tvCaptionUsername);
            tvCaption = itemView.findViewById(R.id.tvCaption);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }
}
