package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val searchIntent = Intent(this, SearchActivity::class.java)
        val libraryIntent = Intent(this, LibraryActivity::class.java)
        val settingsIntent = Intent(this, SettingsActivity::class.java)

        mainBinding.searchButton.setOnClickListener{
            startActivity(searchIntent)
        }
        mainBinding.libraryButton.setOnClickListener {
            startActivity(libraryIntent)
        }
        mainBinding.settingsButton.setOnClickListener {
            startActivity(settingsIntent)
        }
    }
}