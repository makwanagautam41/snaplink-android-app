package com.example.snaplink.models

data class Pagination(
    val total: Int,
    val page: Int,
    val limit: Int,
    val totalPages: Int
)

data class FeedResponse(
    val success: Boolean,
    val posts: List<Post>,
    val pagination: Pagination? = null
)

data class MyPostResponse(
    val success: Boolean,
    val posts: List<Post>
)

data class Post(
    val _id: String,
    val caption: String? = null,
    val images: List<PostImage>? = null,
    val media: List<PostMedia>? = null,
    val postedBy: PostUser? = null,
    val likes: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val createdAt: String? = null
)

data class PostMedia(
    val _id: String?,
    val url: String,
    val mediaType: String? // "image" or "video"
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
    @com.google.gson.annotations.SerializedName("commentId", alternate = ["id", "_id"])
    val commentId: String?,
    val text: String?,
    val postedBy: PostUser?,
    val createdAt: String?
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
