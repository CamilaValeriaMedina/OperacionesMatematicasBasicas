package com.example.opbasicas.utils

import com.example.opbasicas.config.GameLevel
import com.example.opbasicas.models.Question
import kotlin.random.Random

class QuestionGenerator {

    fun generateQuestion(level: GameLevel): Question {
        val (num1, num2, operation) = generateOperationAndNumbers(level)

        val correctAnswer = when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> {
                val dividend = num1 * num2
                dividend / num2
            }
            else -> num1 + num2
        }

        val operationText = when (operation) {
            "÷" -> "$num1 ÷ $num2"
            else -> "$num1 $operation $num2"
        }

        val options = generateOptions(correctAnswer, level)

        return Question(operationText, correctAnswer, options)
    }

    private fun generateOperationAndNumbers(level: GameLevel): Triple<Int, Int, String> {
        val operations = when (level) {
            GameLevel.EASY -> listOf("+", "-")
            GameLevel.MEDIUM -> listOf("+", "-", "×")
            GameLevel.HARD -> listOf("+", "-", "×", "÷")
        }

        val operation = operations.random()

        val range = when (level) {
            GameLevel.EASY -> 0..10
            GameLevel.MEDIUM -> 0..20
            GameLevel.HARD -> 1..50
        }

        var num1 = Random.nextInt(range.first, range.last + 1)
        var num2 = Random.nextInt(range.first, range.last + 1)

        if (operation == "÷") {
            if (num2 == 0) num2 = 1
            num1 = num1 * num2
        }

        if (operation == "-" && level == GameLevel.EASY && num1 < num2) {
            val temp = num1
            num1 = num2
            num2 = temp
        }

        return Triple(num1, num2, operation)
    }

    private fun generateOptions(correctAnswer: Int, level: GameLevel): List<Int> {
        val options = mutableSetOf(correctAnswer)
        val maxOffset = when (level) {
            GameLevel.EASY -> 5
            GameLevel.MEDIUM -> 10
            GameLevel.HARD -> 20
        }

        while (options.size < 4) {
            val offset = Random.nextInt(-maxOffset, maxOffset + 1)
            val distractor = correctAnswer + offset
            if (distractor != correctAnswer && distractor >= 0) {
                options.add(distractor)
            }
        }

        return options.shuffled()
    }
}