package com.example.playlistmaker.ui.library.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {
    private lateinit var libraryBinding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        libraryBinding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(libraryBinding.root)

        val trackId = intent.getStringExtra("track") ?: ""
        val playlistId = intent.getStringExtra("playlist") ?: ""

        libraryBinding.libraryViewPager.adapter = LibraryPagerAdapter(supportFragmentManager, lifecycle, trackId, playlistId)

        tabMediator = TabLayoutMediator(libraryBinding.libraryTabLayout, libraryBinding.libraryViewPager) {tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favourite_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()

        libraryBinding.libraryToolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}