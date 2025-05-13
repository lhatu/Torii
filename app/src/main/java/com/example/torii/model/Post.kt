package com.example.torii.model

data class Post(
    val postId: String = "",
    val userId: String = "",
    val userName: String = "",
    val tag: String = "",
    val datePosted: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val likedBy: List<String> = emptyList(),
    val comments: List<Comment> = emptyList(),
    val timestamp: Long = 0L
)