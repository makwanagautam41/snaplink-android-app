package com.example.snaplink;

public class Post {
    private String username;
    private int userAvatar;
    private int postImage;
    private String caption;
    private String timeAgo;

    public Post(String username, int userAvatar, int postImage, String caption, String timeAgo) {
        this.username = username;
        this.userAvatar = userAvatar;
        this.postImage = postImage;
        this.caption = caption;
        this.timeAgo = timeAgo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    public int getPostImage() {
        return postImage;
    }

    public void setPostImage(int postImage) {
        this.postImage = postImage;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }
}
