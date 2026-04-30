package com.example.opbasicas

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.example.opbasicas.config.GameLevel
import com.example.opbasicas.models.GameSession
import com.example.opbasicas.models.Question
import com.example.opbasicas.utils.QuestionGenerator

class GameActivity : AppCompatActivity() {

    private lateinit var tvTimer: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvProgress: TextView
    private lateinit var tvOperation: TextView
    private lateinit var tvFeedback: TextView
    private lateinit var btnNext: Button
    private lateinit var mainToolBar: Toolbar

    private lateinit var cvAnswers: List<CardView>
    private lateinit var tvAnswers: List<TextView>

    private lateinit var questionGenerator: QuestionGenerator
    private lateinit var currentQuestion: Question
    private var currentScore = 0
    private var questionsAnswered = 0
    private var totalQuestions = 0
    private var gameLevel = GameLevel.EASY
    private var timeLimit = 60L
    private var timeRemaining = 60L
    private var isAnswered = false
    private var selectedAnswerIndex = -1

    private var countDownTimer: CountDownTimer? = null
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        val gameSessions = mutableListOf<GameSession>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContentView(R.layout.activity_game)

            val levelName = intent.getStringExtra("level") ?: "EASY"
            gameLevel = try {
                GameLevel.valueOf(levelName)
            } catch (e: IllegalArgumentException) {
                GameLevel.EASY
            }

            sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
            timeLimit = sharedPreferences.getLong("game_time", 60L)
            timeRemaining = timeLimit

            totalQuestions = (timeLimit / 3).toInt().coerceIn(5, 30)

            initViews()
            initToolBar()
            setupClickListeners()

            questionGenerator = QuestionGenerator()

            startGame()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        tvTimer = findViewById(R.id.tvTimer)
        tvScore = findViewById(R.id.tvScore)
        tvProgress = findViewById(R.id.tvProgress)
        tvOperation = findViewById(R.id.tvOperation)
        tvFeedback = findViewById(R.id.tvFeedback)
        btnNext = findViewById(R.id.btnNext)
        mainToolBar = findViewById(R.id.mainToolBar)

        cvAnswers = listOf(
            findViewById(R.id.cvAnswer0),
            findViewById(R.id.cvAnswer1),
            findViewById(R.id.cvAnswer2),
            findViewById(R.id.cvAnswer3)
        )

        tvAnswers = listOf(
            findViewById(R.id.tvAnswer0),
            findViewById(R.id.tvAnswer1),
            findViewById(R.id.tvAnswer2),
            findViewById(R.id.tvAnswer3)
        )

        tvTimer.text = timeRemaining.toString()
        tvScore.text = "0"
        tvProgress.text = "0 / $totalQuestions"
    }

    private fun initToolBar() {
        try {
            setSupportActionBar(mainToolBar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = when (gameLevel) {
                GameLevel.EASY -> "Easy Mode"
                GameLevel.MEDIUM -> "Medium Mode"
                GameLevel.HARD -> "Hard Mode"
            }
            mainToolBar.setTitleTextColor(Color.WHITE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        cvAnswers.forEachIndexed { index, cardView ->
            cardView.setOnClickListener {
                if (!isAnswered) {
                    selectedAnswerIndex = index
                    highlightSelectedAnswer(index)
                    checkAnswer()
                }
            }
        }

        btnNext.setOnClickListener {
            loadNextQuestion()
        }
    }

    private fun highlightSelectedAnswer(selectedIndex: Int) {
        cvAnswers.forEachIndexed { index, cardView ->
            if (index == selectedIndex) {
                cardView.setCardBackgroundColor(Color.parseColor("#FAFAFA"))
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#1A1A1A"))
            }
        }
    }

    private fun startGame() {
        startTimer()
        loadNextQuestion()
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeRemaining * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished / 1000
                tvTimer.text = timeRemaining.toString()
            }

            override fun onFinish() {
                endGame()
            }
        }.start()
    }

    private fun loadNextQuestion() {
        try {
            if (questionsAnswered >= totalQuestions) {
                endGame()
                return
            }

            isAnswered = false
            selectedAnswerIndex = -1
            btnNext.visibility = View.GONE
            tvFeedback.text = ""

            cvAnswers.forEach { it.setCardBackgroundColor(Color.parseColor("#1A1A1A")) }

            currentQuestion = questionGenerator.generateQuestion(gameLevel)
            tvOperation.text = currentQuestion.text

            currentQuestion.options.forEachIndexed { index, option ->
                tvAnswers[index].text = option.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading question: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAnswer() {
        if (selectedAnswerIndex == -1) return

        isAnswered = true

        val selectedAnswer = tvAnswers[selectedAnswerIndex].text.toString().toIntOrNull()
        val isCorrect = selectedAnswer == currentQuestion.correctAnswer

        if (isCorrect) {
            currentScore++
            tvFeedback.text = "✓ Correct!"
            tvFeedback.setTextColor(Color.parseColor("#4CAF50"))
        } else {
            tvFeedback.text = "✗ Wrong! Answer is ${currentQuestion.correctAnswer}"
            tvFeedback.setTextColor(Color.parseColor("#FF0000"))
        }

        questionsAnswered++
        tvScore.text = currentScore.toString()
        tvProgress.text = "$questionsAnswered / $totalQuestions"

        val correctIndex = tvAnswers.indexOfFirst {
            it.text.toString().toIntOrNull() == currentQuestion.correctAnswer
        }
        if (correctIndex != -1) {
            cvAnswers[correctIndex].setCardBackgroundColor(Color.parseColor("#4CAF50"))
        }
        if (!isCorrect) {
            cvAnswers[selectedAnswerIndex].setCardBackgroundColor(Color.parseColor("#FF0000"))
        }

        btnNext.visibility = View.VISIBLE
    }

    private fun endGame() {
        countDownTimer?.cancel()

        val percentage = if (totalQuestions > 0) (currentScore * 100 / totalQuestions) else 0

        val session = GameSession(
            score = currentScore,
            totalQuestions = totalQuestions,
            difficulty = gameLevel.name,
            timestamp = System.currentTimeMillis(),
            timeLimit = timeLimit.toInt()
        )
        gameSessions.add(0, session)

        val dialogMessage = buildString {
            append("Score: $currentScore / $totalQuestions\n")
            append("Percentage: $percentage%\n")
            append("Difficulty: ${gameLevel.name}\n")
            append("Time: ${timeLimit}s\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Game Over!")
            .setMessage(dialogMessage)
            .setPositiveButton("Play Again") { y, x ->
                restartGame()
            }
            .setNegativeButton("Exit") { y, x ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun restartGame() {
        currentScore = 0
        questionsAnswered = 0
        timeRemaining = timeLimit
        isAnswered = false
        selectedAnswerIndex = -1

        tvScore.text = "0"
        tvProgress.text = "0 / $totalQuestions"
        tvTimer.text = timeRemaining.toString()
        tvFeedback.text = ""
        btnNext.visibility = View.GONE

        cvAnswers.forEach { it.setCardBackgroundColor(Color.parseColor("#1A1A1A")) }

        countDownTimer?.cancel()
        startTimer()
        loadNextQuestion()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}