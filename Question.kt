package com.example.opbasicas.models

data class Question(
    val text: String,
    val correctAnswer: Int,
    val options: List<Int>
)