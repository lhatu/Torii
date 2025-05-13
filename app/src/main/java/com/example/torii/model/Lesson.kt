package com.example.torii.model

import kotlin.time.Duration

data class Lesson(
    val id: String,
    val title: String,
    val datePost: String,
    val duration: String,
    val videoUrl: String,
    val imageUrl: String,
    val listVocab: List<LessonVocabulary>,
    val listGrammar: List<LessonGrammar>
)

data class LessonVocabulary(
    val word: String,
    val reading: String,
    val meaning: String
)

data class LessonGrammar(
    val structure: String,
    val explanation: String
)