package com.example.torii.model

data class Vocabulary(
    val expression: String,
    val reading: String,
    val meaning: String,
    val tags: String,
    val imgUrl: String?,
    val exSentence: String?,
    val category: String?,
)
