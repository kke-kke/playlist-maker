package com.example.playlistmaker.ui.player.viewModel

import com.example.playlistmaker.utils.Constants.INITIAL_TRACK_TIME

data class PlayerScreenState(
    val isPlaying: Boolean = false,
    val currentPosition: String = INITIAL_TRACK_TIME,
    val trackLength: String = INITIAL_TRACK_TIME,
    val isPrepared: Boolean = false
)