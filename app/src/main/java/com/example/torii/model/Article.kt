package com.example.torii.model

data class Article(
    val title: String,
    val publishDate: String,
    val content: String,
    val imageUrl: String?,
    val audioUrl: String?
)
