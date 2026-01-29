package com.example.snaplink;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivityJava extends AppCompatActivity {

    private RecyclerView rvStories;
    private RecyclerView rvPosts;
    private ImageView navHome, navSearch, navAdd, navReels;
    private CircleImageView navProfile;
    private StoryAdapter storyAdapter;
    private PostAdapter postAdapter;
    private List<Story> storyList;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initViews();
        setupStories();
        setupPosts();
        setupNavigation();
    }

    private void initViews() {
        rvStories = findViewById(R.id.rvStories);
        rvPosts = findViewById(R.id.rvPosts);
        navHome = findViewById(R.id.navHome);
        navSearch = findViewById(R.id.navSearch);
        navAdd = findViewById(R.id.navAdd);
        navReels = findViewById(R.id.navReels);
        navProfile = findViewById(R.id.navProfile);
    }

    private void setupNavigation() {
        navHome.setOnClickListener(v -> {
            // Already on home
        });

        navSearch.setOnClickListener(v -> Toast.makeText(this, "Search coming soon", Toast.LENGTH_SHORT).show());

        navAdd.setOnClickListener(v -> Toast.makeText(this, "Add Post coming soon", Toast.LENGTH_SHORT).show());

        navReels.setOnClickListener(v -> Toast.makeText(this, "Reels coming soon", Toast.LENGTH_SHORT).show());

        navProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupStories() {
        storyList = new ArrayList<>();
        // Add dummy stories
        storyList.add(new Story("Your Story", R.drawable.img_current_user, true));
        storyList.add(new Story("punit_super", R.drawable.img_user_1, false));
        storyList.add(new Story("siko.speed", R.drawable.img_user_2, false));
        storyList.add(new Story("galish...", R.drawable.img_user_3, false));
        storyList.add(new Story("talvin", R.drawable.img_user_4, false));
        storyList.add(new Story("john_doe", R.drawable.img_user_placeholder, false));
        storyList.add(new Story("jane_smith", R.drawable.img_user_placeholder, false));
        storyList.add(new Story("mike_ross", R.drawable.img_user_placeholder, false));

        storyAdapter = new StoryAdapter(storyList);
        rvStories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStories.setAdapter(storyAdapter);
    }

    private void setupPosts() {
        postList = new ArrayList<>();
        // Add dummy posts
        postList.add(new Post("__tushill", R.drawable.img_user_post_1, R.drawable.img_post_1,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor nuntium ex sanior...",
                "10 minutes ago"));
        postList.add(new Post("punit_super", R.drawable.img_user_1, R.drawable.img_post_placeholder,
                "Amazing sunset view from my balcony! 🌅", "1 hour ago"));
        postList.add(new Post("siko.speed", R.drawable.img_user_2, R.drawable.img_post_placeholder,
                "New adventure begins today! Can't wait to share more 🚀", "3 hours ago"));
        postList.add(new Post("talvin", R.drawable.img_user_4, R.drawable.img_post_placeholder,
                "Coffee and code ☕💻 #developerlife", "5 hours ago"));

        postAdapter = new PostAdapter(postList);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postAdapter);
    }
}
