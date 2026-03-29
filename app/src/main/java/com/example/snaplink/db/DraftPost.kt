package com.example.snaplink.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "draft_posts")
data class DraftPost(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val caption: String?,
    val mediaUris: String, // Semicolon separated string of URIs
    val timestamp: Long = System.currentTimeMillis()
)
