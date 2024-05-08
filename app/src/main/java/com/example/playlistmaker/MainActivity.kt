package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search)
        val libraryButton = findViewById<Button>(R.id.library)
        val settingsButton = findViewById<Button>(R.id.settings)

        searchButton.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажали на поиск", Toast.LENGTH_SHORT).show()
        }
        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на библиотеку", Toast.LENGTH_SHORT).show()
        }
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на настройки", Toast.LENGTH_SHORT).show()
        }
    }
}