package com.example.snaplink

data class StoryKt(
    val username: String,
    val imageUrl: String? = null,
    val avatarResource: Int? = null,
    val isYourStory: Boolean = false
)

