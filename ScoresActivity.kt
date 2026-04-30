package com.example.opbasicas

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.opbasicas.models.GameSession

class ScoresActivity : AppCompatActivity() {

    private lateinit var mainToolBar: Toolbar
    private lateinit var llScoresList: LinearLayout
    private lateinit var tvOverallAverage: TextView
    private lateinit var tvEasyAverage: TextView
    private lateinit var tvMediumAverage: TextView
    private lateinit var tvHardAverage: TextView
    private lateinit var tvNoGames: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scores)

        initViews()
        initToolBar()
        displayScores()
    }

    private fun initViews() {
        mainToolBar = findViewById(R.id.mainToolBar)
        llScoresList = findViewById(R.id.llScoresList)
        tvOverallAverage = findViewById(R.id.tvOverallAverage)
        tvEasyAverage = findViewById(R.id.tvEasyAverage)
        tvMediumAverage = findViewById(R.id.tvMediumAverage)
        tvHardAverage = findViewById(R.id.tvHardAverage)
        tvNoGames = findViewById(R.id.tvNoGames)
    }

    private fun initToolBar() {
        setSupportActionBar(mainToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Game History"
        mainToolBar.setTitleTextColor(Color.WHITE)
    }

    private fun displayScores() {
        val sessions = GameActivity.gameSessions

        if (sessions.isEmpty()) {
            tvNoGames.visibility = View.VISIBLE
            tvOverallAverage.text = "0%"
            tvEasyAverage.text = "Easy: 0%"
            tvMediumAverage.text = "Medium: 0%"
            tvHardAverage.text = "Hard: 0%"
            return
        }

        tvNoGames.visibility = View.GONE

        val overallAvg = sessions.map { it.percentage }.average()
        val easySessions = sessions.filter { it.difficulty == "EASY" }
        val mediumSessions = sessions.filter { it.difficulty == "MEDIUM" }
        val hardSessions = sessions.filter { it.difficulty == "HARD" }

        val easyAvg = if (easySessions.isNotEmpty()) easySessions.map { it.percentage }.average() else 0.0
        val mediumAvg = if (mediumSessions.isNotEmpty()) mediumSessions.map { it.percentage }.average() else 0.0
        val hardAvg = if (hardSessions.isNotEmpty()) hardSessions.map { it.percentage }.average() else 0.0

        tvOverallAverage.text = String.format("%.1f%%", overallAvg)
        tvEasyAverage.text = String.format("Easy: %.1f%%", easyAvg)
        tvMediumAverage.text = String.format("Medium: %.1f%%", mediumAvg)
        tvHardAverage.text = String.format("Hard: %.1f%%", hardAvg)

        llScoresList.removeAllViews()
        for (session in sessions) {
            val scoreView = createScoreItemView(session)
            llScoresList.addView(scoreView)
        }
    }

    private fun createScoreItemView(session: GameSession): View {
        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.item_score, llScoresList, false)

        val tvScoreValue = view.findViewById<TextView>(R.id.tvScoreValue)
        val tvDifficulty = view.findViewById<TextView>(R.id.tvDifficulty)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvTimeLimit = view.findViewById<TextView>(R.id.tvTimeLimit)

        tvScoreValue.text = "${session.score}/${session.totalQuestions} (${session.percentage}%)"
        tvDifficulty.text = session.difficulty
        tvDate.text = session.formattedDate
        tvTimeLimit.text = "Time: ${session.timeLimit}s"

        val color = when (session.difficulty) {
            "EASY" -> "#33FF00"
            "MEDIUM" -> "#CCFF00"
            else -> "#FF0000"
        }
        tvDifficulty.setTextColor(Color.parseColor(color))

        return view
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