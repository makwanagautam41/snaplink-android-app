package com.example.snaplink.models

data class FeedResponse(
    val success: Boolean,
    val posts: List<Post>
)

data class MyPostResponse(
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

// Detailed Story Models
data class UserStoryResponse(
    val success: Boolean,
    val stories: List<UserStoryGroup>
)

data class UserStoryGroup(
    val user: StoryUser,
    val stories: List<StoryDetail>
)

data class StoryUser(
    val _id: String,
    val username: String,
    val profileImg: String?
)

data class StoryDetail(
    val _id: String,
    val mediaUrl: String,
    val mediaType: String,
    val caption: String?,
    val postedBy: StoryUser,
    val viewers: List<String>,
    val isArchived: Boolean,
    val createdAt: String
)
