package com.example.playlistmaker.domain.search.api

import com.example.playlistmaker.domain.search.models.Resource
import com.example.playlistmaker.domain.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>

    fun addTrackToHistory(track: Track)

    fun loadTrackHistory(): List<Track>

    fun clearHistory()

}