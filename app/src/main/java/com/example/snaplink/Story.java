package com.example.snaplink;

public class Story {
    private String username;
    private int avatarResource;
    private boolean isYourStory;

    public Story(String username, int avatarResource, boolean isYourStory) {
        this.username = username;
        this.avatarResource = avatarResource;
        this.isYourStory = isYourStory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAvatarResource() {
        return avatarResource;
    }

    public void setAvatarResource(int avatarResource) {
        this.avatarResource = avatarResource;
    }

    public boolean isYourStory() {
        return isYourStory;
    }

    public void setYourStory(boolean yourStory) {
        isYourStory = yourStory;
    }
}
