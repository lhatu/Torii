package com.example.torii.model

data class Kanji(
    val character: String,
    val onyomi: List<String>,
    val kunyomi: List<String>,
    val strokes: Int,
    val meaning: String,
    val jlptLevel: String,
    val example: String
)
