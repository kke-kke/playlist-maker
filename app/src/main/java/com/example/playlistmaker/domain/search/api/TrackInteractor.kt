package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackInteractor {

    fun searchTracks(expression: String) : Flow<Pair<List<Track>?, String?>>

    fun addToHistory(track: Track)

    fun getHistory(): List<Track>

    fun clearHistory()

}