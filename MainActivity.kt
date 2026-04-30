package com.example.opbasicas

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.opbasicas.config.GameLevel

class MainActivity : AppCompatActivity() {

    private lateinit var rgDifficulty: RadioGroup
    private lateinit var btnStart: Button
    private lateinit var mainToolBar: Toolbar
    private var selectedLevel = GameLevel.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
        setupListeners()
        initToolBar()
    }

    private fun initViews() {
        rgDifficulty = findViewById(R.id.rgDifficulty)
        btnStart = findViewById(R.id.btnStart)
        mainToolBar = findViewById(R.id.mainToolBar)
    }

    private fun setupListeners() {
        rgDifficulty.setOnCheckedChangeListener { _, checkedId ->
            selectedLevel = when (checkedId) {
                R.id.rbEasy -> GameLevel.EASY
                R.id.rbMedium -> GameLevel.MEDIUM
                R.id.rbHard -> GameLevel.HARD
                else -> GameLevel.EASY
            }
        }

        btnStart.setOnClickListener {
            if (rgDifficulty.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Please select a difficulty level", Toast.LENGTH_SHORT).show()
            } else {
                startGame()
            }
        }
    }

    private fun initToolBar() {
        setSupportActionBar(mainToolBar)
        supportActionBar?.title = "Brain Test :)"
        mainToolBar.setTitleTextColor(Color.WHITE)
    }

    private fun startGame() {
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("level", selectedLevel.name)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnuHome -> true
            R.id.mnuScores -> {
                startActivity(Intent(this, ScoresActivity::class.java))
                true
            }
            R.id.mnuSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.mnuExit -> {
                confirmExit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmExit() {
        AlertDialog.Builder(this)
            .setTitle("Confirm Exit")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Exit") { _, _ ->
                finishAffinity()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}