package com.example.torii.model

data class NewWord(
    val word: String,      // Từ vựng
    val category: String,  // Chủ đề
    val ipa: String,       // Phiên âm IPA
    val meaning: String,   // Nghĩa
    val example: String,   // Ví dụ
    val imageUrl: String
)