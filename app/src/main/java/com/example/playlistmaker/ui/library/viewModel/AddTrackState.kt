package com.example.playlistmaker.ui.library.viewModel

sealed class AddTrackState {
    data class Success(val playlistName: String) : AddTrackState()
    data class Error(val message: String) : AddTrackState()
}