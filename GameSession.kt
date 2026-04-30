package com.example.opbasicas.models

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GameSession(
    val score : Int,
    val totalQuestions : Int,
    val difficulty : String,
    val timestamp : Long,
    val timeLimit : Int
) {
    val percentage: Int
        get() = if (totalQuestions > 0) (score * 100 / totalQuestions) else 0

    val formattedDate: String
        get() {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return format.format(Date(timestamp))
        }
}