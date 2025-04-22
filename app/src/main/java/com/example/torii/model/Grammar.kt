package com.example.torii.model

data class GrammarExample(
    val sentence: String,
    val translation: String
)

data class Grammar(
    val phrase: String,
    val structure: String,
    val meaning: String,
    val explanation: String,
    val jlptLevel: String,
    val examples: List<GrammarExample>
)

