package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.searchButton)
        val libraryButton = findViewById<Button>(R.id.libraryButton)
        val settingsButton = findViewById<Button>(R.id.settingsButton)

        val searchIntent = Intent(this, SearchActivity::class.java)
        val libraryIntent = Intent(this, LibraryActivity::class.java)
        val settingsIntent = Intent(this, SettingsActivity::class.java)

        searchButton.setOnClickListener{
            startActivity(searchIntent)
        }
        libraryButton.setOnClickListener {
            startActivity(libraryIntent)
        }
        settingsButton.setOnClickListener {
            startActivity(settingsIntent)
        }
    }
}