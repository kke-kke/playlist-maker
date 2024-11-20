package com.example.playlistmaker.presentation.search

import com.example.playlistmaker.domain.search.models.Track

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Success(val tracks: List<Track>) : SearchScreenState()
    object Empty : SearchScreenState()
    data class Error(val message: String) : SearchScreenState()
}