package com.example.opbasicas

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var mainToolBar: Toolbar
    private lateinit var etGameTime: EditText
    private lateinit var btnSaveSettings: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        initViews()
        initToolBar()
        loadSettings()
        setupListeners()
    }

    private fun initViews() {
        mainToolBar = findViewById(R.id.mainToolBar)
        etGameTime = findViewById(R.id.etGameTime)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
        sharedPreferences = getSharedPreferences("game_prefs", Context.MODE_PRIVATE)
    }

    private fun initToolBar() {
        setSupportActionBar(mainToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        mainToolBar.setTitleTextColor(Color.WHITE)
    }

    private fun loadSettings() {
        val savedTime = sharedPreferences.getLong("game_time", 60L)
        etGameTime.setText(savedTime.toString())
    }

    private fun setupListeners() {
        btnSaveSettings.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val timeText = etGameTime.text.toString()

        if (timeText.isEmpty()) {
            Toast.makeText(this, "Please enter a time value", Toast.LENGTH_SHORT).show()
            return
        }

        val time = timeText.toLongOrNull()

        if (time == null) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        if (time < 10) {
            Toast.makeText(this, "Minimum time is 10 seconds", Toast.LENGTH_SHORT).show()
            return
        }

        if (time > 300) {
            Toast.makeText(this, "Maximum time is 300 seconds", Toast.LENGTH_SHORT).show()
            return
        }

        sharedPreferences.edit().putLong("game_time", time).apply()
        Toast.makeText(this, "Settings saved! Game time: ${time}s", Toast.LENGTH_SHORT).show()
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