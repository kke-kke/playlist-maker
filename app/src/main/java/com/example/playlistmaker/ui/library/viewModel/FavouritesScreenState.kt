package com.example.playlistmaker.ui.library.viewModel

import com.example.playlistmaker.domain.search.models.Track

sealed interface FavouritesScreenState {
    data class FavouriteTracks(val tracks: List<Track>) : FavouritesScreenState

    data class Error(val message : String) : FavouritesScreenState
}