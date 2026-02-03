package com.example.snaplink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_STORIES = 0;
    private static final int TYPE_POST = 1;

    private List<Post> posts;
    private List<Story> stories;

    public FeedAdapter(List<Post> posts, List<Story> stories) {
        this.posts = posts;
        this.stories = stories;
    }

    @Override
    public int getItemCount() {
        return posts.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_STORIES : TYPE_POST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_STORIES) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_stories_feed, parent, false);
            return new StoriesHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_post, parent, false);
            return new PostAdapter.PostViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof StoriesHolder) {
            ((StoriesHolder) holder).bind(stories);
        } else {
            Post post = posts.get(position - 1);
            PostAdapter.PostViewHolder vh = (PostAdapter.PostViewHolder) holder;
            vh.tvUsername.setText(post.getUsername());
            vh.tvCaptionUsername.setText(post.getUsername());
            vh.ivUserAvatar.setImageResource(post.getUserAvatar());
            vh.ivPostImage.setImageResource(post.getPostImage());
            vh.tvCaption.setText(post.getCaption());
            vh.tvTimeAgo.setText(post.getTimeAgo());
        }
    }

    static class StoriesHolder extends RecyclerView.ViewHolder {
        RecyclerView rvStoriesInner;

        StoriesHolder(View itemView) {
            super(itemView);
            rvStoriesInner = itemView.findViewById(R.id.rvStoriesInner);
            rvStoriesInner.setLayoutManager(
                    new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }

        void bind(List<Story> stories) {
            rvStoriesInner.setAdapter(new StoryAdapter(stories));
        }
    }
}
