package com.example.playlistmaker.ui.library.viewModel

sealed interface PlaylistsScreenState {
    data class Playlist(val playlist: String) : PlaylistsScreenState

    data class Error(val message: String) : PlaylistsScreenState
}