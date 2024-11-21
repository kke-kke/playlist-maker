package com.example.playlistmaker.ui.player.viewModel

data class PlayerScreenState(
    val isPlaying: Boolean = false,
    val currentPosition: String = "00:00",
    val trackLength: String = "00:00",
    val isPrepared: Boolean = false
)