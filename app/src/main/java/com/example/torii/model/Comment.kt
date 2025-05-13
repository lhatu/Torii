package com.example.torii.model

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val userAvatar: String = ""
)
