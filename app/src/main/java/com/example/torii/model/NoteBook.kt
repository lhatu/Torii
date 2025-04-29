package com.example.torii.model

// Model cho Notebook
data class Notebook(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val wordCount: Int = 0,
    val userId: String = ""
)

// Model cho Vocabulary trong Notebook
data class NotebookVocabulary(
    val id: String = "",
    val notebookId: String = "",
    val expression: String = "",
    val reading: String = "",
    val meaning: String = "",
    val addedAt: Long = System.currentTimeMillis()
)