package com.example.snaplink.models

data class FeedResponse(
    val success: Boolean,
    val posts: List<Post>
)

data class Post(
    val _id: String,
    val caption: String,
    val images: List<PostImage>,
    val postedBy: PostUser,
    val likes: List<String>,
    val comments: List<Comment>,
    val createdAt: String
)

data class PostImage(
    val url: String
)

data class PostUser(
    val _id: String,
    val username: String,
    val profileImg: String
)

data class Comment(
    val commentId: String?,
    val text: String?
)

data class CreatePostResponse(
    val success: Boolean,
    val message: String
)
